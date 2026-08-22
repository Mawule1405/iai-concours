import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'tx-right-modal-template',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './right-modal-template.component.html',
  styleUrl: './right-modal-template.component.css'
})
export class RightModalTemplateComponent {
  @Input() isOpen = false;
  @Input() title = 'Système';
  @Input() description = '';

  // Événement de fermeture unique
  @Output() close = new EventEmitter<void>();

  onClose() {
    this.close.emit();
  }


}
