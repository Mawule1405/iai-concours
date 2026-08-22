import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'tx-modal-template',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal-template.component.html',
  styleUrl: './modal-template.component.css'
})
export class ModalTemplateComponent {
  @Input() isOpen = false;
  @Input() title = 'Système';
  @Input() description = '';

  // Configuration du Footer
  @Input() showCancel: boolean = true;
  @Input() showClear: boolean = false;
  @Input() showValidate: boolean = true;

  // Libellés personnalisables
  @Input() validateLabel: string = 'Valider';
  @Input() cancelLabel: string = 'Annuler';
  @Input() clearLabel: string = 'Effacer';

  // Événements
  @Output() close = new EventEmitter<void>();
  @Output() validate = new EventEmitter<void>();
  @Output() clear = new EventEmitter<void>();

  onClose() {
    this.close.emit();
  }

  onValidate() {
    this.validate.emit();
  }

  onClear() {
    this.clear.emit();
  }
}
