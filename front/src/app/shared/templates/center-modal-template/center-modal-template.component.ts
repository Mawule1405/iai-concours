import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'tx-center-modal-template',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './center-modal-template.component.html',
  styleUrl: './center-modal-template.component.css'
})
export class CenterModalTemplateComponent {
  @Input() isOpen = false;
  @Input() title = 'Système';
  @Input() description = '';

  // Événement de fermeture unique
  @Output() close = new EventEmitter<void>();

  onClose() {
    this.close.emit();
  }


}
