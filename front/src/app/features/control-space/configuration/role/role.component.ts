import {Component, OnInit, inject, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import {RoleService} from '../../../../core/services/role.service';
import {Permission, Role} from '../../../../core/models/auth.model';
import {RoleManageComponent} from './role-manage/role-manage.component';
import {PermissionService} from '../../../../core/services/permission.service';
import {NotificationService} from '../../../../core/services/notification.service';


@Component({
  selector: 'app-role',
  standalone: true,
  imports: [CommonModule, RoleManageComponent],
  templateUrl: './role.component.html',
  styleUrl: './role.component.css',
})
export class RoleComponent implements OnInit {
  private roleService = inject(RoleService);
  private permissionService = inject(PermissionService)
  private cdr = inject(ChangeDetectorRef);
  private notifyService = inject(NotificationService);

  roles: Role[] = [];
  permissions: Permission[] = [];
  expandedRoleId: string | null = null; // Pour gérer le dropdown des permissions

  showCreateModal: boolean = false;
  showUpdateModal: boolean = false;
  selectedRole!: Role;


  ngOnInit(): void {
    this.loadRoles();
    this.loadPermissions();
  }

  loadRoles(): void {
    this.roleService.getAllRoles().subscribe({
      next: (data) => {
        this.roles = data
        this.cdr.detectChanges()
      },
      error: (err) => console.error('Erreur chargement rôles', err)
    });
  }

  loadPermissions(): void {
    this.permissionService.getAllPermissions().subscribe({
      next: (data) => {
        this.permissions = data
          this.cdr.detectChanges()
      },error: err => {
        this.cdr.detectChanges()
      }
    })
  }

  togglePermissions(roleId: any): void {
    this.expandedRoleId = this.expandedRoleId === roleId ? null : roleId;
  }

  onCreate(): void {
    this.showCreateModal = true;
  }

  onUpdate(role: Role): void {
    this.selectedRole = role;
    this.showUpdateModal = true;
  }

  onDelete(id: any): void {
    this.notifyService.confirm("Souhaitez-vous supprimer ce rôle", "SUPPRESSION ROLE")
        .then((result)=>{
        if (result) {
          this.roleService.deleteRole(id).subscribe(() => this.loadRoles());
        }
      })
  }

  onSaveCreate($event: any) {
    this.roleService.createRole($event).subscribe({
      next: (data) => {
        this.showCreateModal = false;
        this.loadRoles();
      },error: err => {
        this.showCreateModal = false;
        this.notifyService.error(err.message, `ERREUR ${err.statut}`)
      }
    })
  }

  onSaveUpdate($event: any) {
    if(this.selectedRole && this.selectedRole.id) {
      this.roleService.updateRole(this.selectedRole.id,$event).subscribe({
        next: (data) => {
          this.showUpdateModal = false;
          this.loadRoles();
        },error: err => {
          this.showUpdateModal = false;
          this.notifyService.error(err.message, `ERREUR ${err.statut}`)
        }
      })
    }
  }
}
