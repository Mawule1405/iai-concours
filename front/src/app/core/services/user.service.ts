import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {AppUser, Pagination} from "../models/auth.model";
import {PasswordChangeRequest} from '../models/password-change.model';


@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  /**
   * Récupère la liste de tous les utilisateurs
   * Requis: PERM_READ_USER
   */
  getAllUsers(keyword: string, page: number, size: number): Observable<Pagination<AppUser>> {
    let params = new HttpParams()
        .set('keyword', keyword)
        .set('page', (page-1).toString())
        .set('size', size.toString());

    return this.http.get<Pagination<AppUser>>(this.apiUrl, {params: params});
  }

  /**
   * Récupère un utilisateur par son ID
   * Requis: PERM_READ_USER
   */
  getUserById(id: string): Observable<AppUser> {
    return this.http.get<AppUser>(`${this.apiUrl}/${id}`);
  }

  /**
   * Crée un nouvel utilisateur
   * Requis: PERM_CREATE_USER
   */
  createUser(user: AppUser): Observable<AppUser> {
    return this.http.post<AppUser>(this.apiUrl, user);
  }

  /**
   * Met à jour un utilisateur existant
   * Requis: PERM_UPDATE_USER
   */
  updateUser(id: string, user: AppUser): Observable<AppUser> {
    return this.http.put<AppUser>(`${this.apiUrl}/${id}`, user);
  }

  /**
   * Supprime un utilisateur
   * Requis: PERM_DELETE_USER
   */
  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Réinitialise le mot de passe d'un utilisateur
   * Requis: PERM_UPDATE_USER
   */
  resetPassword(id: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/reset-password`, {});
  }

  toggleStatus(id: string) {
    return this.http.patch(`${this.apiUrl}/toggle-status`, id);
  }

  toggleLock(id: string) {
    return this.http.patch(`${this.apiUrl}/toggle-lock`, id);
  }

  changePassword(request: PasswordChangeRequest): Observable<boolean> {
    return this.http.post<boolean>(`${this.apiUrl}/new-password`, request);
  }

  getUser() {
    return this.http.get<AppUser>(`${this.apiUrl}/my-account`);
  }
}
