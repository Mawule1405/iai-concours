import { inject, Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { map, Observable, of, catchError } from 'rxjs';
import { FileStorageService } from '../services/file-storage-service';

@Pipe({
  name: 'secureImg',
  standalone: true
})
export class SecureImgPipe implements PipeTransform {
  private fileService = inject(FileStorageService);
  private sanitizer = inject(DomSanitizer);

  // Chemin vers ton image par défaut
  private readonly PLACEHOLDER = 'assets/images/default-avatar.png';

  transform(fileName: string | null | undefined): Observable<SafeUrl> {
    // 1. Gestion des cas vides
    if (!fileName || fileName.trim() === '') {
      return of(this.sanitizer.bypassSecurityTrustUrl(this.PLACEHOLDER));
    }

    // 2. Appel au service (qui gère déjà le cache en interne)
    return this.fileService.getFileUrl(fileName).pipe(
      map(url => this.sanitizer.bypassSecurityTrustUrl(url)),
      catchError((error) => {
         return of(this.sanitizer.bypassSecurityTrustUrl(this.PLACEHOLDER));
      })
    );
  }
}
