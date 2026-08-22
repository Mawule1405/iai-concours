import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import {environment} from '../../../../environments/environment';
import {UserService} from '../../../core/services/user.service';
import {PublicService} from '../../../core/services/public.service';


@Component({
  selector: 'app-password-forget',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule],
  templateUrl: './password-forget.component.html',
  styleUrl: './password-forget.component.css'
})
export class PasswordForgetComponent {
  private fb = inject(FormBuilder);
  publicService = inject(PublicService);


  forgotForm: FormGroup;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  isLoading = false;

  constructor() {
    this.forgotForm = this.fb.group({
      // Un seul champ pour l'identifiant ou l'email (selon ce que ton backend accepte)
      usernameOrEmail: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.forgotForm.invalid) {
      this.errorMessage = 'Veuillez renseigner votre identifiant ou adresse email.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.successMessage = null;

    // Appel vers ton endpoint Spring Boot (ajuste l'URL si nécessaire selon ton contrôleur)
    const payload = { login: this.forgotForm.value.usernameOrEmail };

    this.publicService.getNewPassword(payload.login).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'L\'initialisation a été lancée. Si le compte existe, un nouveau mot de passe vient de vous être envoyé par email.';
        this.forgotForm.reset();
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 404) {
          this.errorMessage = 'Aucun utilisateur ne correspond à cet identifiant ou cet email.';
        } else {
          this.errorMessage = 'Une erreur technique est survenue. Veuillez réessayer ultérieurement.';
        }
      }
    });
  }
}
