import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  ModalTemplateComponent
} from '../../../../../shared/templates/modal-template/modal-template.component';
import {AppUser, Role} from '../../../../../core/models/auth.model';


@Component({
  selector: 'app-utilisateur-manage',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalTemplateComponent],
  templateUrl: './utilisateur-manage.component.html'
})
export class UtilisateurManageComponent implements OnInit {
  @Input() isOpen = false;
  @Input() user: AppUser | null = null;
  @Input() allRoles: Role[] = []; // Liste de tous les rôles dispo en base

  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<AppUser>();

  userForm!: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit() {
    this.initForm();
  }

  initForm() {
    this.userForm = this.fb.group({
      id: [this.user?.id || null],
      username: [this.user?.username || '', [Validators.required, Validators.minLength(3)]],
      email: [this.user?.email || '', [Validators.required, Validators.email]],
      firstName: [this.user?.firstName || '', Validators.required],
      lastName: [this.user?.lastName || '', Validators.required],
      enabled: [this.user ? this.user.enabled : true],
      roles: [this.user?.roles || []] // On stocke les objets Role sélectionnés
    });
  }

  // Gestion des rôles (Toggle)
  isRoleSelected(roleId: string): boolean {
    const selectedRoles = this.userForm.value.roles as Role[];
    return selectedRoles.some(r => r.id === roleId);
  }

  toggleRole(role: Role) {
    let currentRoles = [...this.userForm.value.roles];
    if (this.isRoleSelected(role.id!)) {
      currentRoles = currentRoles.filter(r => r.id !== role.id);
    } else {
      currentRoles.push(role);
    }
    this.userForm.patchValue({ roles: currentRoles });
  }

  onSubmit() {
    if (this.userForm.valid) {
      this.save.emit(this.userForm.value);
    }
  }
}
