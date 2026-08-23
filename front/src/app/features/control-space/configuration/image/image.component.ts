import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ImageService} from '../../../../core/services/image.service';

@Component({
  selector: 'app-image',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './image.component.html',
  styleUrl: './image.component.css',
})
export class ImageComponent implements OnInit {
  private readonly imageService = inject(ImageService);

  imageUrl = signal<string | null>(null);
  selectedFile = signal<File | null>(null);
  previewUrl = signal<string | null>(null);
  isLoading = signal<boolean>(false);
  isUploading = signal<boolean>(false);

  ngOnInit(): void {
    this.fetchImage();
  }

  /**
   * Récupère l'image actuelle via ImageService
   */
  fetchImage(): void {
    this.isLoading.set(true);
    this.imageService.getImageBlob().subscribe({
      next: (blob) => {
        if (this.imageUrl()) {
          URL.revokeObjectURL(this.imageUrl()!);
        }
        const objectUrl = URL.createObjectURL(blob);
        this.imageUrl.set(objectUrl);
        this.isLoading.set(false);
      },
      error: () => {
        this.imageUrl.set(null);
        this.isLoading.set(false);
      }
    });
  }

  /**
   * Capture du fichier et création d'une URL de prévisualisation
   */
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      this.selectedFile.set(file);

      if (this.previewUrl()) {
        URL.revokeObjectURL(this.previewUrl()!);
      }
      this.previewUrl.set(URL.createObjectURL(file));
    }
  }

  /**
   * Envoi de la nouvelle image via ImageService
   */
  uploadImage(): void {
    const file = this.selectedFile();
    if (!file) return;

    this.isUploading.set(true);
    this.imageService.uploadImage(file).subscribe({
      next: (success) => {
        this.isUploading.set(false);
        if (success) {
          this.cancelSelection();
          this.fetchImage();
        }
      },
      error: () => {
        this.isUploading.set(false);
      }
    });
  }

  /**
   * Annulation de la sélection en cours
   */
  cancelSelection(): void {
    if (this.previewUrl()) {
      URL.revokeObjectURL(this.previewUrl()!);
    }
    this.selectedFile.set(null);
    this.previewUrl.set(null);
  }
}
