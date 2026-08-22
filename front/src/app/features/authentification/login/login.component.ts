import {ChangeDetectorRef, Component, inject} from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import {AuthService} from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef)

  loginForm: FormGroup;
  errorMessage: string | null = null;
  isLoading = false;
  showPassword = false; // Gestion de l'aperçu du mot de passe

  constructor() {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.errorMessage = 'Veuillez remplir correctement tous les champs obligatoires.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/control-space/dashboard']); // Redirection après succès
        this.cdr.detectChanges()
      },
      error: (err) => {
        this.isLoading = false;
        // Gestion des erreurs selon le retour de ton backend
        if (err.status === 401) {
          this.errorMessage = 'Identifiants ou mot de passe incorrects.';
        } else {
          this.errorMessage = 'Une erreur technique est survenue. Veuillez réessayer.';
        }
      }
    });
  }
}
