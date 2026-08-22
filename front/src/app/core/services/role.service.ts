import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import {Role} from '../models/auth.model';


@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private http = inject(HttpClient);

  // L'URL correspond au @RequestMapping("/tsc-api/roles")
  private readonly API_URL = `${environment.apiUrl}/roles`;

  /**
   * Récupère la liste de tous les rôles (@GetMapping)
   */
  getAllRoles(): Observable<Role[]> {
    return this.http.get<Role[]>(this.API_URL);
  }

  /**
   * Récupère un rôle par son ID (@GetMapping("/{id}"))
   */
  getRoleById(id: string | number): Observable<Role> {
    return this.http.get<Role>(`${this.API_URL}/${id}`);
  }

  /**
   * Crée un nouveau rôle (@PostMapping)
   */
  createRole(role: Role): Observable<Role> {
    return this.http.post<Role>(this.API_URL, role);
  }

  /**
   * Met à jour un rôle existant (@PutMapping("/{id}"))
   */
  updateRole(id: string , role: Role): Observable<Role> {
    return this.http.put<Role>(`${this.API_URL}/${id}`, role);
  }

  /**
   * Supprime un rôle (@DeleteMapping("/{id}"))
   */
  deleteRole(id: string | number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
