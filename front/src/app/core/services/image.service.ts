import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/images`;

  /**
   * Upload d'une image (POST /iai-concours-api/images)
   */
  uploadImage(file: File): Observable<boolean> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<boolean>(this.apiUrl, formData);
  }

  /**
   * Récupère l'image sous forme de Blob (GET /iai-concours-api/images)
   */
  getImageBlob(): Observable<Blob> {
    return this.http.get(this.apiUrl, { responseType: 'blob' });
  }
}
