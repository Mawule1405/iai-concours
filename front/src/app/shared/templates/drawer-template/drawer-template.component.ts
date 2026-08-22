import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'tx-drawer-template',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './drawer-template.component.html'
})
export class DrawerTemplateComponent {
  @Input() isCollapsed = false;
  @Input() isMobileVisible = false; // Remplacé isOpen par isMobileVisible
  @Input() isMobile = false;
  @Input() title = 'Menu';
  @Output() toggle = new EventEmitter<void>();
}
