import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {CandidateService} from '../../../core/services/candidate.service';
import {CandidateStatisticsDto} from '../../../core/models/candidate.model';
import {WebsocketService} from '../../../core/services/websocket.service';
import {SpaceTemplateComponent} from '../../../shared/templates/space-template/space-template.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  private readonly candidateService = inject(CandidateService);

  // Signaux pour l'état de l'interface
  readonly stats = signal<CandidateStatisticsDto | null>(null);
  readonly isLoading = signal<boolean>(true);
  readonly error = signal<string | null>(null);
  private readonly ws = inject(WebsocketService);

  ngOnInit(): void {
    this.fetchGlobalStatistics();

    this.ws.listen('/topic/candidates', (status) => {
      const cleanStatus = status?.replace(/^"|"$/g, '').trim();
      if (cleanStatus === 'REFRESH') {
        this.ws.zone.run(() => {
          this.fetchGlobalStatistics()
        });
      }
    });
  }

  fetchGlobalStatistics(): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.candidateService.getGlobalCandidateStatistics().subscribe({
      next: (data) => {
        this.stats.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des statistiques :', err);
        this.error.set('Impossible de charger les statistiques globales.');
        this.isLoading.set(false);
      }
    });
  }
}
