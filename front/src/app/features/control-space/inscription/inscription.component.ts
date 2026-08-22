import {ChangeDetectorRef, Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {RegisterService} from '../../../core/services/register.service';
import {CandidateDto} from '../../../core/models/candidate.model';
import {Cycle, PaymentMethod, Serie, Status} from '../../../core/enums/enum';
import {PaymentDto} from '../../../core/models/payment.model';
import {WebsocketService} from '../../../core/services/websocket.service';
import {CandidateService} from '../../../core/services/candidate.service';

@Component({
  selector: 'app-inscription',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './inscription.component.html',
  styleUrl: './inscription.component.css'
})
export class InscriptionComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly registerService = inject(RegisterService);
  private readonly ws = inject(WebsocketService);
  private readonly candidateService = inject(CandidateService)
  private readonly cdr = inject(ChangeDetectorRef);

  // Étape courante : 1 = Candidat, 2 = Paiement, 3 = Confirmation
  activeStep = signal<number>(1);
  loading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  // Champ de recherche pour reprise du workflow
  searchNumero = signal<string>('');
  isSearchMode = signal<boolean>(false);

  // Candidat sélectionné ou créé
  createdCandidate = signal<CandidateDto | null>(null);

  pendingCandidates = signal<string[]>([]);

  // Options d'énumération
  optionsList = Object.values(Cycle);
  seriesList = Object.values(Serie);
  paymentMethods = Object.values(PaymentMethod);

  // Formulaire Étape 1 : Candidat
  candidateForm: FormGroup = this.fb.group({
    id: [null],
    lastName: ['', [Validators.required, Validators.minLength(2)]],
    firstName: ['', [Validators.required, Validators.minLength(2)]],
    birthDate: ['', [Validators.required]],
    gender: ['M', [Validators.required]],
    email: [''],
    phone: ['', [Validators.required]],
    tutorPhone:[''],
    enrolmentDate:[new Date(), [Validators.required]],
    nationality: ['Gabonaise', [Validators.required]],
    serie: [Serie.C, [Validators.required]],
    option: [Cycle.WORKS_ENGINEERING, [Validators.required]]
  });

  // Formulaire Étape 2 : Paiement
  paymentForm: FormGroup = this.fb.group({
    numberOfTransactions: ["", [Validators.required]],
    amount: [25000, [Validators.required, Validators.min(1)]],
    transferPhone: [""],
    paymentDate: [new Date(), [Validators.required]],
    transferHour:["", [Validators.required]],
    paymentMethod: [PaymentMethod.MOBILE_MONEY, [Validators.required]]
  });

  ngOnInit() {

    this.loadPendingCandidate()

    this.ws.listen('/topic/candidates', (status) => {
      const cleanStatus = status?.replace(/^"|"$/g, '').trim();
      if (cleanStatus === 'REFRESH') {
        this.ws.zone.run(() => {
          this.loadPendingCandidate()
        });
      }
    });
  }

  loadPendingCandidate() {
    this.candidateService.getPendingCandidate().subscribe({
      next:data=>{
        this.pendingCandidates.set(data);
        this.cdr.detectChanges();
      },
      error:error=>{
        console.log(error);
      }
    })
  }

  /**
   * Recherche un candidat existant par son numéro d'enregistrement/table
   */
  searchCandidate(): void {
    const query = this.searchNumero().trim();
    if (!query) return;

    this.loading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    // Nécessite une méthode getCandidateByNumero dans RegisterService
    this.registerService.getCandidateByNumero(query).subscribe({
      next: (candidate) => {
        this.loading.set(false);
        if (!candidate) {
          this.errorMessage.set("Aucun candidat trouvé avec ce numéro.");
          return;
        }

        this.createdCandidate.set(candidate);
        this.candidateForm.patchValue(candidate);

        // Si le paiement est déjà effectué, on redirige vers l'étape 3
        if (candidate.status === Status.REGISTERED_AND_PAYMENT ) {
          this.activeStep.set(3);
          this.successMessage.set("Ce dossier est déjà finalisé.");
        } else {
          // Sinon on bascule directement à l'étape Paiement (Étape 2)
          this.activeStep.set(2);
          this.successMessage.set("Dossier trouvé ! Vous pouvez poursuivre le règlement.");
        }
        this.loadPendingCandidate()
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err?.error?.message || "Candidat introuvable avec ce numéro.");
      }
    });
  }

  // Enregistrement / MÀJ du Candidat
  submitCandidate(): void {
    if (this.candidateForm.invalid) {
      this.candidateForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    const candidateData: CandidateDto = this.candidateForm.value;

    this.registerService.registerCandidate(candidateData).subscribe({
      next: (res) => {
        this.createdCandidate.set(res);
        this.loading.set(false);
        this.activeStep.set(2);
        this.loadPendingCandidate()
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err?.error?.message || "Erreur lors de l'enregistrement du candidat.");
      }
    });
  }

  // Validation du Paiement
  submitPayment(): void {
    const candidate = this.createdCandidate();
    if (!candidate || !candidate.id) {
      this.errorMessage.set("Identifiant du candidat introuvable.");
      return;
    }

    if (this.paymentForm.invalid) {
      this.paymentForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    const paymentData: PaymentDto = this.paymentForm.value;

    this.registerService.registerPayment(candidate.id, paymentData).subscribe({
      next: () => {
        this.loading.set(false);
        this.successMessage.set("Inscription et paiement enregistrés avec succès !");
        this.activeStep.set(3);
        this.loadPendingCandidate()
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err?.error?.message || "Erreur lors du traitement du paiement.");
      }
    });
  }

  resetWorkflow(): void {
    this.candidateForm.reset({ gender: 'M', nationality: 'Gabonaise', serie: Serie.C, option: Cycle.WORKS_ENGINEERING });
    this.paymentForm.reset({ amount: 25000, numberOfTransactions: 1, paymentMethod: PaymentMethod.MOBILE_MONEY });
    this.createdCandidate.set(null);
    this.searchNumero.set('');
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.activeStep.set(1);
  }
}
