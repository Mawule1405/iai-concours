import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import {APP_ICONS} from '../../../core/constants/app-icons';

@Component({
  selector: 'app-configuration',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './configuration.component.html',
  styleUrl: './configuration.component.css',
})
export class ConfigurationComponent {
  isCollapsed = false; // Gère l'état réduit/étendu
  readonly icons = APP_ICONS;
  toggleSidebar() {
    this.isCollapsed = !this.isCollapsed;
  }
}
