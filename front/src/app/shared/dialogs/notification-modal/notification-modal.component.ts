import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import {NotificationConfig} from '../../../core/models/notification.model';

@Component({
  selector: 'app-notification-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-modal.component.html',

})
export class NotificationModalComponent {
  @Input() config!: NotificationConfig;
  @Output() confirmed = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  getStyles() {
    switch (this.config.type) {
      case 'SUCCESS':
        return {
          icon: 'fa-check-double',
          color: 'text-green-600',
          border: 'border-green-600',
          bg: 'bg-green-600 hover:bg-green-700 text-white' // Ajout de text-white pour le contraste sur le fond vert
        };
      case 'ERROR':
        return {
          icon: 'fa-triangle-exclamation',
          color: 'text-red-600',
          border: 'border-red-600',
          bg: 'bg-red-600 hover:bg-red-700'
        };
      case 'WARNING':
        // Utilisation du Orange Brand pour les avertissements système
        return {
          icon: 'fa-radiation',
          color: 'text-brand-orange-500',
          border: 'border-brand-orange-500',
          bg: 'bg-brand-orange-500 hover:bg-brand-orange-600'
        };
      case 'PERMISSION':
        // Utilisation du Bleu Brand pour les accès et la sécurité
        return {
          icon: 'fa-user-shield',
          color: 'text-brand-blue-900',
          border: 'border-brand-blue-900',
          bg: 'bg-brand-blue-900 hover:bg-brand-blue-800'
        };
      case 'TOP_PERMISSION':
        // Utilisation d'un Noir/Ardoise profond pour un look sobre, premium et sécuritaire
        return {
          icon: 'fa-user-shield',
          color: 'text-slate-900',
          border: 'border-slate-900',
          bg: 'bg-slate-900 hover:bg-slate-800'
        };
      default:
        return {
          icon: 'fa-circle-info',
          color: 'text-slate-500',
          border: 'border-slate-500',
          bg: 'bg-slate-600 hover:bg-slate-700'
        };
    }
  }


}
