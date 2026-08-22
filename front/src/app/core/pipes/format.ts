import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'statutFormat'
})
export class FormatPipe implements PipeTransform {

  transform(content: string | null | undefined): string {
    if (!content) return ''; // ← Gère null, undefined, chaîne vide

    // Votre logique de transformation reste la même
    content = content.replace(
      /(TITRE\s+[IVX]+[:：]\s*.*?)(?=\n\n|\nTITRE|\nArticle|\n$)/gi,
      '<div class="statut-title">$1</div>'
    );

    content = content.replace(
      /(Article\s+\d+[:：]\s*.*?)(?=\n\n|\nTITRE|\nArticle|\n$)/gi,
      '<div class="statut-article"><strong>$1</strong></div>'
    );

    content = content.replace(/^[ \t]*•\s+/gm, '<li class="bullet-point">');
    content = content.replace(/^[ \t]*-\s+/gm, '<li class="bullet-point">');
    content = content.replace(/^[ \t]*❖\s+/gm, '<li class="bullet-point">');
    content = content.replace(/^[ \t]*\d+\.\s+/gm, '<li class="numbered-point">');

    content = content.replace(/<\/li>/g, '');
    content = content.replace(/<li([^>]*?)>/g, '<li$1>');

    content = content.replace(/(<li[^>]*>.*?<\/li>\s*)+/g, match => {
      return `<ul class="statut-list">${match}</ul>`;
    });

    content = content.replace(/\n\n/g, '</p><p>');
    content = content.replace(/\n/g, '<br>');

    content = `<p>${content}</p>`;
    content = content.replace(/<p><\/p>/g, '');

    content = content.replace(/caducée/gi, '<span class="icon-caducée">🪡</span> Caducée');
    content = content.replace(/ailes/gi, '<span class="icon-wings">🕊️</span> Ailes');
    content = content.replace(/barre rose/gi, '<span class="icon-pink-bar">🌸</span> Barre rose');
    content = content.replace(/logo/gi, '<span class="icon-logo">🖼️</span> Logo');

    content = content.replace(/<p><p>/g, '<p>');
    content = content.replace(/<\/p><\/p>/g, '</p>');

    return content;
  }
}
