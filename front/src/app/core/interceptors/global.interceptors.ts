import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';

export const globalErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const notifService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // 1. PRIORITÉ : Laisser l'AuthInterceptor gérer le rafraîchissement
      // On ne traite pas la 401 ici, sinon on va afficher une erreur
      // alors que le système est peut-être en train de réparer la session en arrière-plan.
      if (error.status === 401) {
        return throwError(() => error);
      }

      // 2. Ignorer les erreurs sur les endpoints d'authentification
      // (On gère souvent les erreurs de login directement dans le composant Login)
      if (req.url.includes('/login') || req.url.includes('/refresh')) {
        return throwError(() => error);
      }

      let message = 'An unexpected system error occurred.';
      let title = 'CRITICAL_FAILURE';

      // 3. Traitement des autres codes d'erreur
      switch (error.status) {
        case 403:
          title = 'ACCESS_DENIED';
          message = 'You do not have the required clearance for this operation.';
          break;
        case 404:
          title = 'RESOURCE_NOT_FOUND';
          message = 'The requested data entity does not exist on the server.';
          break;
        case 400:
          title = 'BAD_REQUEST';
          // On essaie de récupérer le message précis du backend s'il existe
          message = error.error?.message || 'Invalid data submitted to the protocol.';
          break;
        case 500:
          title = 'SERVER_ERROR';
          message = 'The remote server encountered an internal failure.';
          break;
        case 0:
          title = 'NETWORK_ERROR';
          message = 'Unable to reach the server. Please check your connection.';
          break;
      }

      // 4. Notification à l'utilisateur (décommenter pour activer)
      //notifService.error(message, title);

      // 5. On propage toujours l'erreur pour que le composant appelant soit au courant
      return throwError(() => error);
    })
  );
};
