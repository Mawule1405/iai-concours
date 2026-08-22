import { Pipe, PipeTransform, inject } from '@angular/core';
import { AuthService } from '../services/auth.service'; // Ton service d'auth/permissions

@Pipe({
  name: 'hasPermission',
  standalone: true,
  pure: true // Performant : recalculé uniquement si l'argument change
})
export class HasPermissionPipe implements PipeTransform {
  private authService = inject(AuthService);

  transform(permission: string | string[]): boolean {
    if (!permission) return false;
    return this.authService.hasPermission(permission);
  }
}
