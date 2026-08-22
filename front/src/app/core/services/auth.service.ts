import { Injectable, inject } from '@angular/core';
import { HttpBackend, HttpClient } from '@angular/common/http';
import { tap, catchError, Observable, throwError } from 'rxjs';
import { environment } from '../../../environments/environment.development';

import { UserService } from './user.service';
import { AppUser } from '../models/auth.model';
import { ACCESS_TOKEN } from '../constants/auth.constants';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private handler = inject(HttpBackend);
  private userService = inject(UserService);
  router = inject(Router);

  // Client HTTP bypassing les intercepteurs pour le Silent Refresh
  private silentHttp!: HttpClient;
  private readonly API_URL = `${environment.apiUrl}/auth`;

  currentUser: AppUser | null = null;

  constructor() {
    this.silentHttp = new HttpClient(this.handler);
  }

  /**
   * Enregistre le token JWT dans le localStorage
   */
  setAccessToken(token: string): void {
    localStorage.setItem(ACCESS_TOKEN, token);
  }

  /**
   * Récupère le token JWT du localStorage
   */
  getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN);
  }

  /**
   * Extrait le rôle principal de l'utilisateur
   */
  getUserRole(): string | null {
    const decoded = this.decodeToken();
    return decoded ? (decoded.role || decoded.authorities?.[0] || null) : null;
  }

  /**
   * Extrait le nom d'utilisateur (sub)
   */
  getUsername(): string {
    const decoded = this.decodeToken();
    return decoded ? (decoded.sub || '') : '';
  }

  /**
   * Extrait l'identifiant unique de l'utilisateur
   */
  getUserId(): number {
    const decoded = this.decodeToken();
    return decoded ? (decoded.userId || decoded.id || 0) : 0;
  }

  /**
   * Décodage sécurisé du payload JWT
   */
  private decodeToken(): any {
    const token = this.getAccessToken();
    if (!token) return null;

    try {
      const parts = token.split('.');
      if (parts.length !== 3) return null;

      const payload = parts[1];
      // Support du décodage basique UTF-8 / Base64
      const decodedJson = decodeURIComponent(
        atob(payload)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(decodedJson);
    } catch (e) {
      console.error('Erreur de décodage du token JWT :', e);
      return null;
    }
  }

  /**
   * Rafraîchissement silencieux du token JWT via le Cookie de Session
   */
  silentRefresh(): Observable<{ access: string }> {
    return this.silentHttp
      .post<{ access: string }>(
        `${this.API_URL}/refresh`,
        {},
        { withCredentials: true }
      )
      .pipe(
        tap((res) => this.setAccessToken(res.access)),
        catchError((err) => {
          this.clearSession();
          return throwError(() => err);
        })
      );
  }

  /**
   * Connexion au backend
   */
  login(data: any): Observable<{ access: string }> {
    const loginUrl = environment.apiUrl.replace('/iai-concours-api', '') + '/login';
    return this.http.post<{ access: string }>(loginUrl, data).pipe(
      tap((res) => this.setAccessToken(res.access))
    );
  }

  /**
   * Déconnexion complète et réinitialisation de la session
   */
  logout(): void {
    this.clearSession();
    this.router.navigate(['/login']);
  }

  /**
   * Nettoyage du stockage local et des variables en mémoire
   */
  private clearSession(): void {
    this.currentUser = null;
    localStorage.removeItem(ACCESS_TOKEN);
  }

  /**
   * Extrait la liste de toutes les permissions et autorités associées au token JWT
   */
  getPermissions(): string[] {
    const decoded = this.decodeToken();
    if (!decoded) return [];

    const authorities = decoded.authorities || decoded.permissions || decoded.roles || [];

    if (Array.isArray(authorities)) {
      return authorities;
    } else if (typeof authorities === 'string') {
      return [authorities];
    }

    return [];
  }

  /**
   * Vérifie si l'utilisateur détient une permission ou un rôle spécifique.
   * Si un tableau est fourni, renvoie true s'il détient AU MOINS UNE des permissions.
   */
  hasPermission(permission: string | string[]): boolean {
    const userPermissions = this.getPermissions();

    if (!userPermissions || userPermissions.length === 0) {
      return false;
    }

    // Octroi automatique de tous les droits pour l'administrateur système
    if (
      userPermissions.includes('ROLE_ADMINISTRATOR') ||
      userPermissions.includes('ROLE_ADMIN') ||
      userPermissions.includes('ROLE_SUPER_ADMIN')
    ) {
      return true;
    }

    if (Array.isArray(permission)) {
      return permission.some((perm) => userPermissions.includes(perm));
    }

    return userPermissions.includes(permission);
  }
}
