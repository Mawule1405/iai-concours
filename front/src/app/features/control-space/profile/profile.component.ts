import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {SpaceTemplateComponent} from '../../../shared/templates/space-template/space-template.component';
import {SecureImgPipe} from '../../../core/pipes/secure-img.pipe';
import {UserService} from '../../../core/services/user.service';
import {AppUser} from '../../../core/models/auth.model';
import {PasswordChangeRequest} from '../../../core/models/password-change.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, SpaceTemplateComponent, SecureImgPipe, SecureImgPipe],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  private readonly userService = inject(UserService);
  private readonly fb = inject(FormBuilder);

  // States
  user = signal<AppUser | null>(null);
  loading = signal<boolean>(false);
  activeTab = signal<'profile' | 'password'>('profile');

  // Feedbacks
  successMessage = signal<string | null>(null);
  errorMessage = signal<string | null>(null);

  // Formulaires
  profileForm!: FormGroup;
  passwordForm!: FormGroup;

  ngOnInit(): void {
    this.initForms();
    this.loadProfile();
  }

  private initForms(): void {
    this.profileForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      username: [{ value: '', disabled: true }]
    });

    this.passwordForm = this.fb.group({
      oldPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  private passwordMatchValidator(g: FormGroup) {
    const newPass = g.get('newPassword')?.value;
    const confirmPass = g.get('confirmPassword')?.value;
    return newPass === confirmPass ? null : { mismatch: true };
  }

  loadProfile(): void {
    this.loading.set(true);
    this.userService.getUser().subscribe({
      next: (userData) => {
        this.user.set(userData);
        this.profileForm.patchValue({
          firstName: userData.firstName,
          lastName: userData.lastName,
          email: userData.email,
          username: userData.username
        });
        this.loading.set(false);
      },
      error: () => {
        this.setError('Erreur lors du chargement du profil.');
        this.loading.set(false);
      }
    });
  }

  updateProfile(): void {
    if (this.profileForm.invalid || !this.user()?.id) return;

    this.clearMessages();
    this.loading.set(true);

    const currentUser = this.user()!;
    const updatedUser: AppUser = {
      ...currentUser,
      firstName: this.profileForm.value.firstName,
      lastName: this.profileForm.value.lastName,
      email: this.profileForm.value.email
    };

    this.userService.updateUser(currentUser.id!, updatedUser).subscribe({
      next: (res) => {
        this.user.set(res);
        this.setSuccess('Informations personnelles mises à jour avec succès.');
        this.loading.set(false);
      },
      error: (err) => {
        this.setError(err?.error?.message || 'Échec de la mise à jour du profil.');
        this.loading.set(false);
      }
    });
  }

  updatePassword(): void {
    if (this.passwordForm.invalid) return;

    this.clearMessages();
    this.loading.set(true);

    const request: PasswordChangeRequest = {
      oldPassword: this.passwordForm.value.oldPassword,
      newPassword: this.passwordForm.value.newPassword
    };

    this.userService.changePassword(request).subscribe({
      next: () => {
        this.setSuccess('Mot de passe modifié avec succès.');
        this.passwordForm.reset();
        this.loading.set(false);
      },
      error: (err) => {
        this.setError(err?.error?.message || 'Erreur lors du changement de mot de passe.');
        this.loading.set(false);
      }
    });
  }

  private clearMessages(): void {
    this.successMessage.set(null);
    this.errorMessage.set(null);
  }

  private setSuccess(msg: string): void {
    this.successMessage.set(msg);
    setTimeout(() => this.successMessage.set(null), 5000);
  }

  private setError(msg: string): void {
    this.errorMessage.set(msg);
    setTimeout(() => this.errorMessage.set(null), 5000);
  }
}
