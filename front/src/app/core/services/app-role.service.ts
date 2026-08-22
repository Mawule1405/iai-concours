import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Role } from '../models/auth.model';


@Injectable({
  providedIn: 'root'
})
export class AppRoleService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/roles`;

  /** Récupère tous les rôles (@GetMapping("/all")) */
  fetchAll(): Observable<Role[]> {
    return this.http.get<Role[]>(`${this.API_URL}/all`);
  }

  /** Trouve un rôle par ID (@GetMapping("/find/{id}")) */
  fetchById(id: string): Observable<Role> {
    return this.http.get<Role>(`${this.API_URL}/find/${id}`);
  }

  /** Crée un nouveau rôle (@PostMapping("/create")) */
  create(role: Role): Observable<Role> {
    return this.http.post<Role>(`${this.API_URL}/create`, role);
  }
}
