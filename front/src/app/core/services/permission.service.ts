import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {Permission} from '../models/auth.model'; // Adapte le chemin


@Injectable({
  providedIn: 'root'
})
export class PermissionService {

  // L'URL de base correspond au @RequestMapping de ton contrôleur Java
  private readonly API_URL = `${environment.apiUrl}/permissions`;

  constructor(private http: HttpClient) {}

  /**
   * Récupère toutes les permissions
   * @PreAuthorize("hasAuthority('PERM_MANAGE_ROLES')") requis côté serveur
   */
  getAllPermissions(): Observable<Permission[]> {
    return this.http.get<Permission[]>(this.API_URL);
  }

  /**
   * Récupère une permission par son identifiant
   * @param id L'identifiant technique de la permission
   */
  getPermissionById(id: string): Observable<Permission> {
    return this.http.get<Permission>(`${this.API_URL}/${id}`);
  }
}
