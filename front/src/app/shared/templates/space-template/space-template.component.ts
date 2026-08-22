import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'tx-space-template',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './space-template.component.html'
})
export class SpaceTemplateComponent {
  // Config Header
  @Input() title: string = 'Gestion';
  @Input() subtitle: string = 'Security Control';
  @Input() icon: string = 'fa-shield-halved';
  @Input() createLabel: string = 'Nouveau';


  // Visibilité des boutons
  @Input() showCreate: boolean = true;
  @Input() showExport: boolean = false;


  // Événements
  @Output() create = new EventEmitter<void>();
  @Output() export = new EventEmitter<void>();


}
