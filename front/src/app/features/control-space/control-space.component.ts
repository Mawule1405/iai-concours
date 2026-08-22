import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet, RouterModule, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';
import { APP_ICONS } from '../../core/constants/app-icons';
import { HasPermissionDirective } from '../../core/directives/permission.directive';
import { AppUser } from '../../core/models/auth.model';
import { UserService } from '../../core/services/user.service';
import { SecureImgPipe } from '../../core/pipes/secure-img.pipe';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-control-space',
  standalone: true,
  imports: [RouterOutlet, RouterModule, HasPermissionDirective, SecureImgPipe, AsyncPipe],
  templateUrl: './control-space.component.html',
  styleUrl: './control-space.component.css',
})
export class ControlSpaceComponent implements OnInit {

  authService = inject(AuthService);
  private readonly userService = inject(UserService);
  router = inject(Router);
  notifyService = inject(NotificationService);
  user = signal<AppUser | null>(null);
  readonly icons = APP_ICONS;

  ngOnInit() {
    this.loadProfile();
  }

  logout() {
    this.notifyService.confirm("Voulez-vous vous déconnecter ?", "DÉCONNEXION").then((result) => {
      if (result) {
        this.authService.logout();
      }
    });
  }

  loadProfile(): void {
    this.userService.getUser().subscribe({
      next: (userData) => {
        this.user.set(userData);
      },
      error: () => {
        console.log('Erreur lors du chargement du profil.');
      }
    });
  }
}
