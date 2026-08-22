import { Component, signal, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CandidateService } from '../../../core/services/candidate.service';
import {CandidateDto, compareTwoCandidate} from '../../../core/models/candidate.model';
import { Pagination } from '../../../core/models/auth.model';
import { Cycle, Serie, Status, PaymentMethod } from '../../../core/enums/enum';
import {PaginationComponent} from '../../../shared/pagination/pagination.component';
import {WebsocketService} from '../../../core/services/websocket.service';
import {EditCandidateComponent} from './edit-candidate/edit-candidate.component';
import {NotificationService} from '../../../core/services/notification.service';
import {compareTwoPayment, PaymentDto} from '../../../core/models/payment.model';
import {PaymentService} from '../../../core/services/payment.service';
import {RegisterPaymentComponent} from './register-payment/register-payment.component';
import {RegisterService} from '../../../core/services/register.service';
import {EditPaymentComponent} from './edit-payment/edit-payment.component';
import {HasPermissionDirective} from '../../../core/directives/permission.directive';

@Component({
  selector: 'app-candidate-management',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    PaginationComponent,
    EditCandidateComponent,
    RegisterPaymentComponent,
    EditPaymentComponent,
    HasPermissionDirective
  ],
  templateUrl: './candidate-management.component.html',
  styleUrl: './candidate-management.component.css'
})
export class CandidateManagementComponent implements OnInit {
  private candidateService = inject(CandidateService);
  private notifyService = inject(NotificationService)
  private readonly ws = inject(WebsocketService);
  private paymentService = inject(PaymentService);
  private readonly registerService = inject(RegisterService);

  // Enums exposés au template HTML
  readonly Cycle = Cycle;
  readonly Serie = Serie;
  readonly Status = Status;

  // State des candidats et chargement
  candidates = signal<CandidateDto[]>([]);
  loading = signal<boolean>(false);

  // Filtres
  searchNumero = signal<string>('');
  filterGender = signal<string>('');
  filterStatus = signal<string>('');
  filterSerie = signal<string>('');
  filterOption = signal<string>('');

  // Pagination dynamique liée à `Pagination<T>`
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);
  totalElements = signal<number>(0);
  totalPages = signal<number>(1);

  selectedCandidate = signal<CandidateDto|null>(null)
  selectedPayment = signal<PaymentDto|null>(null)
  isCandidateEditModal = signal(false)
  isPaymentRegisterModal = signal(false)
  isEditPaymentModal = signal(false)
  ngOnInit(): void {
    this.loadCandidates();

    this.ws.listen('/topic/candidates', (status) => {
      const cleanStatus = status?.replace(/^"|"$/g, '').trim();
      if (cleanStatus === 'REFRESH') {
        this.ws.zone.run(() => {
          this.loadCandidates();
        });
      }
    });
  }

  loadCandidates(): void {
    this.loading.set(true);

    this.candidateService.getCandidates({
      numero: this.searchNumero() || undefined,
      gender: this.filterGender() || undefined,
      status: (this.filterStatus() as Status) || undefined,
      serie: (this.filterSerie() as Serie) || undefined,
      option: (this.filterOption() as Cycle) || undefined,
      page: this.currentPage() - 1, // Décalage index 0 pour le Backend
      size: this.pageSize()
    }).subscribe({
      next: (res: Pagination<CandidateDto>) => {
        this.candidates.set(res.content);
        this.totalElements.set(res.totalElements);
        this.totalPages.set(res.totalPages);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Erreur lors du chargement des candidats :', err);
        this.loading.set(false);
      }
    });
  }

  onFilterChange(): void {
    this.currentPage.set(1);
    this.loadCandidates();
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
    this.loadCandidates();
  }

  onPageSizeChange(size: number): void {
    this.pageSize.set(size);
    this.currentPage.set(1);
    this.loadCandidates();
  }

  resetFilters(): void {
    this.searchNumero.set('');
    this.filterGender.set('');
    this.filterStatus.set('');
    this.filterSerie.set('');
    this.filterOption.set('');
    this.currentPage.set(1);
    this.loadCandidates();
  }

  exportFile(format: 'csv' | 'excel' | 'pdf'): void {
    this.candidateService.export(format, {
      numero: this.searchNumero() || undefined,
      gender: this.filterGender() || undefined,
      status: (this.filterStatus() as Status) || undefined,
      serie: (this.filterSerie() as Serie) || undefined,
      option: (this.filterOption() as Cycle) || undefined,
      page: 0,
      size: this.totalElements()
    }).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `candidats_${Date.now()}.${format === 'excel' ? 'xlsx' : format}`;
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }


  editPayment(candidat: CandidateDto) {
    this.selectedCandidate.set(candidat);
    this.paymentService.getPaymentDtoById(candidat.paymentId||"").subscribe(payment => {
      this.selectedPayment.set(payment);
      this.isEditPaymentModal.set(true);
    })
  }

  savePayment(candidat: CandidateDto) {
    this.selectedCandidate.set(candidat)
    this.isPaymentRegisterModal.set(true)
  }

  editCandidate(candidat: CandidateDto) {
    this.selectedCandidate.set(candidat);
    this.isCandidateEditModal.set(true)
  }

  onSave($event: CandidateDto) {

    // @ts-ignore
    if(compareTwoCandidate($event, this.selectedCandidate())){
      this.notifyService.warning("Aucune information n'a été modifié")
    }else{
      this.notifyService.confirm("Souhaitez-vous enrégistrer la modification effectuée?",
        `Information du candidat`).then((result) => {
          if(result){
            this.candidateService.updateCandidate(this.selectedCandidate()?.id||'', $event).subscribe({
              next: event => {
                this.notifyService.success("Modification réussie");
                this.loadCandidates()
                this.onClose()
              },
              error: err=>{
                this.notifyService.error(`Une erreur s'est produite: ${err}}`);
              }
            })
          }
      })
    }

  }



  onClose() {
    this.isCandidateEditModal.set(false);
    this.isPaymentRegisterModal.set(false)
    this.isEditPaymentModal.set(false)
    this.selectedPayment.set(null)
    this.selectedCandidate.set(null);
  }

  onRegisterPayment($event: PaymentDto) {
    this.notifyService.confirm("Souhaitez-vous enregistrer ce payment?",
      "Payment des frais de concours").then((result) => {
        if(result){
          this.registerService.registerPayment(this.selectedCandidate()?.id||'', $event).subscribe({
            next: event => {
              this.notifyService.success("Payment effectué avec succès")
              this.onClose()
              this.loadCandidates()
            },
            error:err => {
              this.notifyService.error(`Une erreur s'est produite: ${err}}`);
            }
          })
        }
    })
  }

  onEditPayment($event: PaymentDto) {
    // @ts-ignore
    if(compareTwoPayment($event, this.selectedPayment())){
      this.notifyService.warning("Aucune modification n'a été effectuée.")
    }else{
      this.notifyService.confirm("Souhaitez-vous enregistrer la modification de payment?",
        "Modification du payment").then((result) => {
          if(result){
            this.paymentService.updatePaymentDto(this.selectedPayment()?.id||'', $event).subscribe({
              next: event => {
                this.notifyService.success("Modification effectuée avec succès")
                this.onClose()
              },
              error: err => {
                this.notifyService.error(`Une erreur s'est produite: ${err}}`);
              }
            })
          }
      })
    }
  }

  delete(candidat: CandidateDto) {
  this.notifyService.confirm("Souhaitez-vous supprimer ce candidat?",
    "Suppression d'un candidat").then((result) => {
      if(result){
        this.candidateService.deleteCandidate(candidat.id||'').subscribe({
          next: event => {
            this.notifyService.success("Suppression réussie")
            this.loadCandidates()
          },
          error: err => {
            this.notifyService.error(`Une erreur s'est produite: ${err}}`);
          }
        })
      }
  })
  }
}
