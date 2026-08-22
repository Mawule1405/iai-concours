import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'tx-table-template',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './table-template.component.html'
})
export class TableTemplateComponent {
  // Config Header
  @Input() title: string = 'Gestion';
  @Input() subtitle: string = 'Security Control';
  @Input() icon: string = 'fa-shield-halved';
  @Input() createLabel: string = 'Nouveau';
  @Input() showPagination: boolean = true;
  @Input() showSearch: boolean = true;

  // Recherche
  @Input() keyword: string = '';
  @Output() keywordChange = new EventEmitter<string>();
  @Output() search = new EventEmitter<void>();
  @Output() refresh = new EventEmitter<void>();

  // Visibilité des boutons
  @Input() showCreate: boolean = true;
  @Input() showExport: boolean = false;

  // Pagination
  @Input() pageSizeOptions: number[] = [5, 10, 20, 50, 100];
  @Input() currentPage: number = 1;
  @Input() totalElements: number = 0;
  @Input() totalPages: number = 1;
  @Input() pageSize: number = 10;

  // Événements
  @Output() create = new EventEmitter<void>();
  @Output() export = new EventEmitter<void>();
  @Output() pageChange = new EventEmitter<number>();
  @Output() pageSizeChange = new EventEmitter<number>();


  onPageSizeChange(newSize: number) {
    this.pageSize = newSize;
    this.pageSizeChange.emit(newSize);
  }

  onKeywordChange() {
    this.keywordChange.emit(this.keyword);
  }

  onClear() {
    this.keyword = '';
    this.keywordChange.emit('');
    this.refresh.emit();
  }
}
