import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableTemplateComponent } from '../../../../shared/templates/table-template/table-template.component';
import {ApiError, AppUser, Pagination, Role} from '../../../../core/models/auth.model';
import {UserService} from '../../../../core/services/user.service';
import {UtilisateurManageComponent} from './utilisateur-manage/utilisateur-manage.component';
import {RoleService} from '../../../../core/services/role.service';
import {NotificationService} from "../../../../core/services/notification.service";

@Component({
  selector: 'app-utilisateur',
  standalone: true,
  imports: [CommonModule, TableTemplateComponent, UtilisateurManageComponent],
  templateUrl: './utilisateur.component.html'
})
export class UtilisateurComponent implements OnInit {

  cdr = inject(ChangeDetectorRef)
  userService = inject(UserService)
  roleService = inject(RoleService)
  notifyService = inject(NotificationService)

  // Data
  users: AppUser[] = [];
  roles: Role[]=[]
  totalElements = 0;
  totalPages = 0;

  // State
  keyword = '';
  currentPage = 1;
  pageSize = 10;
  expandedUserId: string | null = null; // Pour le dropdown des rôles

  showCreateModal: boolean = false;
  showUpdateModal: boolean = false
  selectedUser!: AppUser;

  ngOnInit() {
    this.loadUsers();
    this.loadRoles();
  }

  loadUsers() {
    this.userService.getAllUsers(this.keyword, this.currentPage, this.pageSize)
      .subscribe((res: Pagination<AppUser>) => {
        this.users = res.content;
        this.totalElements = res.totalElements;
        this.totalPages = res.totalPages;
        this.cdr.detectChanges()
      });
  }

  loadRoles(){
    this.roleService.getAllRoles().subscribe((data)=>
    {this.roles=data
      this.cdr.detectChanges()
    })
  }

  // Handlers pour le Template
  onSearch() {

    this.currentPage = 1;
    this.loadUsers();
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadUsers();
  }

  onPageSizeChange(size: number) {
    this.pageSize = size;
    this.currentPage = 1;
    this.loadUsers();
  }

  // Actions Utilisateurs
  toggleRoles(userId: string) {
    this.expandedUserId = this.expandedUserId === userId ? null : userId;
    this.cdr.detectChanges()
  }

  onToggleStatus(user: AppUser) {
    if (!user.id) return;

    // 1. Définition des messages selon l'état de l'utilisateur
    let message = user.enabled
        ? `ATTENTION: Vous êtes sur le point de désactiver le compte de ${user.username}. L'accès lui sera instantanément refusé.`
        : `Vous allez réactiver le compte de ${user.username}. L'utilisateur pourra de nouveau se connecter au système.`;

    let title = user.enabled ? 'DÉSACTIVATION COMPTE' : 'ACTIVATION COMPTE';

    // 2. Utilisation de ta structure .then()
    this.notifyService.confirm(message, title).then((result) => {
      if (result) {
        // Attention à l'orthographe de toggleStatus ici selon ton UserService
        this.userService.toggleStatus(user.id!).subscribe({
          next: () => {
            this.notifyService.success("L'état de l'utilisateur a été mis à jour avec succès.", "OPÉRATION REUSSIE");
            this.loadUsers(); // Recharge la table pour actualiser les badges
          },
          error: (err) => {
            this.notifyService.error("Une erreur est survenue lors du changement de statut.", "ÉCHEC OPÉRATION");
          }
        });
      }
    });
  }

  onToggleLock(user: AppUser) {
    if (!user.id) return;

    // 1. Définition des messages selon l'état de verrouillage de l'utilisateur
    let message = user.locked
        ? `Vous allez déverrouiller le compte de ${user.username}. L'utilisateur récupérera ses droits d'accès immédiats.`
        : `ATTENTION: Vous êtes sur le point de bloquer le compte de ${user.username}. Ses sessions actives seront suspendues.`;

    let title = user.locked ? 'DÉVERROUILLAGE COMPTE' : 'VERROUILLAGE COMPTE';

    // 2. Utilisation de ta structure .then()
    this.notifyService.confirm(message, title).then((result) => {
      if (result) {
        this.userService.toggleLock(user.id!).subscribe({
          next: () => {
            this.notifyService.success("Le statut de sécurité de l'utilisateur a été mis à jour.", "OPÉRATION REUSSIE");
            this.loadUsers(); // Recharge la table pour actualiser l'icône de cadenas
          },
          error: (err) => {
            this.notifyService.error("Une erreur est survenue lors de la modification du verrouillage.", "ÉCHEC OPÉRATION");
          }
        });
      }
    });
  }

  onResetPassword(user: AppUser) {
    if (!user.id) return;

    // 1. Définition des messages pour le protocole de réinitialisation
    let message = `Vous êtes sur le point de réinitialiser le mot de passe de ${user.username}. Le nouveau mot de passe lui sera envoyé par mail.`;
    let title = 'RÉINITIALISATION MOT DE PASSE';

    // 2. Utilisation de ta structure .then()
    this.notifyService.confirm(message, title).then((result) => {
      if (result) {
        this.userService.resetPassword(user.id!).subscribe({
          next: () => {
            this.notifyService.success("Les instructions de réinitialisation ont été envoyées à l'utilisateur.", "OPÉRATION REUSSIE");
            // Pas besoin de recharger la table ici car aucune donnée visuelle du tableau ne change
          },
          error: (err) => {
            this.notifyService.error("Une erreur est survenue lors de la réinitialisation du mot de passe.", "ÉCHEC OPÉRATION");
          }
        });
      }
    });
  }


  onUpdate(user: AppUser) {

    this.selectedUser = user;
    this.showUpdateModal = true;
    this.cdr.detectChanges()
  }



  onDelete(id: string) {
    // 1. On attend le verdict de l'opérateur (bloque l'exécution de la méthode, pas le thread)
    this.notifyService.confirm(
      'SUPPRESSION_UTILISATEUR',
       'ÊTES-VOUS SÛR DE VOULOIR SUPPRIMER DÉFINITIVEMENT CET ACCÈS ? CETTE ACTION EST IRRÉVERSIBLE.',
    ).then((result)=>{
      if(result){
        this.userService.deleteUser(id).subscribe({
          next: () => {
            this.notifyService.success('L\'UTILISATEUR A ÉTÉ SUPPRIMÉ DE LA BASE DE DONNÉES.', 'PROTOCOLE_OK');
            this.loadUsers(); // Rafraîchit le tx-table-template
          },
          error: (httpError) => {
            const apiError = httpError.error;
            this.notifyService.error(apiError?.message || 'IMPOSSIBLE_DE_SUPPRIMER_L_UTILISATEUR', 'ECHEC_OPERATIONAL');
          }
        });
      }
    })

  }

  onCreate() { this.showCreateModal = true; }


  onSaveCreate($event: AppUser) {
      this.showCreateModal = false;
        this.userService.createUser($event).subscribe({
          next: () => {
            this.loadUsers();
            this.notifyService.success("LE COMPTE DE L'UTILISATEUR A BIEN ÉTÉ CRÉÉ.", "CRÉATION DE COMPTE");},
          error: (httpError) => {

            const apiError = httpError.error as ApiError;
            this.notifyService.error(
                apiError.message,
              `HTTP_ERROR_${apiError.code}`,
            );
          }
        })
    }

  onSaveUpdate($event: AppUser) {
    this.showUpdateModal = false;
    if(this.selectedUser && this.selectedUser.id){
      this.userService.updateUser(this.selectedUser.id ,$event).subscribe({
        next: () => {
          this.loadUsers();
          this.cdr.detectChanges()
          this.notifyService.success("LE COMPTE DE L'UTILISATEUR A BIEN ÉTÉ MODIFIER.", "MODIFICATION DE COMPTE");},
        error: (httpError) => {

          const apiError = httpError.error as ApiError;
          this.notifyService.error(
              apiError.message,
              `HTTP_ERROR_${apiError.code}`,
          );
        }
      })
    }
  }

  onClose() {
    this.showUpdateModal = false;
    this.cdr.detectChanges()
  }
}
