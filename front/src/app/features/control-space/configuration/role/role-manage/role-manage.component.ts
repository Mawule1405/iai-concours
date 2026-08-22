import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  ModalTemplateComponent
} from '../../../../../shared/templates/modal-template/modal-template.component';
import {Permission, Role} from '../../../../../core/models/auth.model';

@Component({
  selector: 'app-role-manage',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalTemplateComponent],
  templateUrl: './role-manage.component.html'
})
export class RoleManageComponent {
  @Input() isOpen = false;
  @Input() role: Role = { name: '', description: '', permissions: [] };
  @Input() allPermissions: Permission[] = [];

  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<any>();

  // Vérifie si une permission est déjà attribuée au rôle
  isPermissionChecked(permId: string): boolean {
    return this.role.permissions.some((p: any) => p.id === permId);
  }

  // Bascule l'état d'une permission
  togglePermission(perm: any) {
    const index = this.role.permissions.findIndex((p: any) => p.id === perm.id);
    if (index > -1) {
      this.role.permissions.splice(index, 1);
    } else {
      this.role.permissions.push(perm);
    }
  }

  onSave() {
    this.save.emit(this.role);
  }
}
