import {HttpInterceptorFn, HttpErrorResponse, HttpEvent, HttpRequest, HttpHandlerFn} from '@angular/common/http'; // 💡 Ajoute HttpEvent ici
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, throwError, BehaviorSubject, switchMap, filter, take, Observable, of } from 'rxjs';

// Gestion de la file d'attente des requêtes pendant le rafraîchissement
let isRefreshing = false;
const refreshTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);
    const token = authService.getAccessToken();

    // On ajoute le token si disponible
    const authReq = token
        ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` }, withCredentials: true })
        : req.clone({ withCredentials: true });

    return next(authReq).pipe(
        catchError((error) => {
            // On ne tente le refresh que si c'est une 401 et que ce n'est pas déjà une requête d'auth
            if (
                error instanceof HttpErrorResponse &&
                error.status === 401 &&
                !req.url.includes('/login') &&
                !req.url.includes('/auth/refresh')
            ) {

                return handle401Error(req, next, authService);
            }

            return throwError(() => error);
        })
    );
};


function handle401Error(
    req: HttpRequest<any>,
    next: HttpHandlerFn, // Utilise HttpHandlerFn pour les interceptors fonctionnels
    authService: AuthService
): Observable<HttpEvent<any>> { // On précise le retour de la fonction
    if (!isRefreshing) {
        isRefreshing = true;
        refreshTokenSubject.next(null);

        return authService.silentRefresh().pipe(
            switchMap((res: any): Observable<HttpEvent<any>> => { // Typer le retour du switchMap
                isRefreshing = false;

                const newToken = authService.getAccessToken();

                refreshTokenSubject.next(newToken);

                return next(req.clone({
                    setHeaders: { Authorization: `Bearer ${newToken}` },
                    withCredentials: true
                }));
            }),
            catchError((err) => {
                isRefreshing = false;
                authService.logout();
                return throwError(() => err);
            })
        );
    } else {
        return refreshTokenSubject.pipe(
            filter((token): token is string => token !== null), // Type guard pour TS
            take(1),
            switchMap((token: string): Observable<HttpEvent<any>> => { // Typer le retour ici aussi
                return next(req.clone({
                    setHeaders: { Authorization: `Bearer ${token}` },
                    withCredentials: true
                }));
            })
        );
    }
}
