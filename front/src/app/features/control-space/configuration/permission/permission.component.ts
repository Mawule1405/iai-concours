import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {Permission} from '../../../../core/models/auth.model';
import {PermissionService} from '../../../../core/services/permission.service';


@Component({
  selector: 'app-permission',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './permission.component.html',
  styleUrl: './permission.component.css',
})
export class PermissionComponent implements OnInit {
  permissions: Permission[] = [];
  permissionService = inject(PermissionService);
  cdr = inject(ChangeDetectorRef);
  isLoading = true;


  ngOnInit(): void {
    this.permissionService.getAllPermissions().subscribe({
      next: (data) => {
        this.permissions = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
