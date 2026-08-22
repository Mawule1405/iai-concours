import { Routes } from '@angular/router';
import {LoginComponent} from './features/authentification/login/login.component';
import {PasswordForgetComponent} from './features/authentification/password-forget/password-forget.component';
import {ControlSpaceComponent} from './features/control-space/control-space.component';
import {DashboardComponent} from './features/control-space/dashboard/dashboard.component';
import {
  CandidateManagementComponent
} from './features/control-space/candidate-management/candidate-management.component';
import {ProfileComponent} from './features/control-space/profile/profile.component';
import {ConfigurationComponent} from './features/control-space/configuration/configuration.component';
import {InscriptionComponent} from './features/control-space/inscription/inscription.component';
import {PermissionComponent} from './features/control-space/configuration/permission/permission.component';
import {RoleComponent} from './features/control-space/configuration/role/role.component';
import {UtilisateurComponent} from './features/control-space/configuration/utilisateur/utilisateur.component';

export const routes: Routes = [
  {path: '', redirectTo: 'login', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path:'forgot-password',component: PasswordForgetComponent},
  {path: 'control-space', component: ControlSpaceComponent, children: [
      {path: '', redirectTo: 'dashboard', pathMatch: 'full'},
      {path:'dashboard', component:DashboardComponent},
      {path:'register', component: InscriptionComponent},
      {path: 'candidates', component: CandidateManagementComponent, children: [

        ]},
      {path: 'profile', component: ProfileComponent},
      {path:'configuration', component: ConfigurationComponent, children: [
          {path:'', redirectTo:'permissions', pathMatch: 'full'},
          {path: 'permissions', component: PermissionComponent},
          {path: 'roles', component: RoleComponent},
          {path: 'users', component: UtilisateurComponent},
        ]},
      ]},

];
