import { inject, Injectable, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable, of, tap } from 'rxjs';
import {environment} from '../../../environments/environment.development';


@Injectable({
  providedIn: 'root'
})
export class FileStorageService implements OnDestroy {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/files`;

  // Map pour stocker les URLs créées : [fileName -> objectURL]
  private urlCache = new Map<string, string>();

  /**
   * Récupère un fichier, le cache et gère la reconstruction si nécessaire
   */
  getPublicFileUrl(fileName: string): Observable<string> {
    // 1. Vérifier si l'image est déjà en mémoire
    if (this.urlCache.has(fileName)) {
      return of(this.urlCache.get(fileName)!);
    }

    // 2. Sinon, télécharger et créer l'URL
    return this.http.get(`${this.apiUrl.replace('/files', '')}/public/download?fileName=${fileName}`, {
      responseType: 'blob'
    }).pipe(
      map(blob => {
        const objectUrl = URL.createObjectURL(blob);
        this.urlCache.set(fileName, objectUrl);
        return objectUrl;
      })
    );
  }
  getFileUrl(fileName: string): Observable<string> {
    // 1. Vérifier si l'image est déjà en mémoire
    if (this.urlCache.has(fileName)) {
      return of(this.urlCache.get(fileName)!);
    }

    // 2. Sinon, télécharger et créer l'URL
    return this.http.get(`${this.apiUrl}/download?fileName=${fileName}`, {
      responseType: 'blob'
    }).pipe(
      map(blob => {
        const objectUrl = URL.createObjectURL(blob);
        this.urlCache.set(fileName, objectUrl);
        return objectUrl;
      })
    );
  }

  /**
   * Force la suppression d'une URL spécifique (utile après une modification)
   */
  revokeFileUrl(fileName: string): void {
    const url = this.urlCache.get(fileName);
    if (url) {
      URL.revokeObjectURL(url);
      this.urlCache.delete(fileName);
    }
  }

  /**
   * Nettoyage complet pour éviter les fuites de mémoire
   */
  ngOnDestroy(): void {
    this.urlCache.forEach(url => URL.revokeObjectURL(url));
    this.urlCache.clear();
  }
}
