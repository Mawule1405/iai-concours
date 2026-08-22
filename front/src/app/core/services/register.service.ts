import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {CandidateDto} from '../models/candidate.model';
import {PaymentDto} from '../models/payment.model';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/register`;

  /**
   * Étape 1 : Enregistrement des informations du candidat
   * POST /iai-concours/register/candidate
   */
  registerCandidate(candidateDto: CandidateDto): Observable<CandidateDto> {
    return this.http.post<CandidateDto>(`${this.baseUrl}/candidate`, candidateDto);
  }

  /**
   * Étape 2 : Enregistrement du paiement associé au candidat
   * POST /iai-concours/register/candidates/{candidateId}/payment
   */
  registerPayment(candidateId: string, paymentDto: PaymentDto): Observable<PaymentDto> {
    return this.http.post<PaymentDto>(`${this.baseUrl}/candidates/${candidateId}/payment`, paymentDto);
  }

  getCandidateByNumero(query: string) {
    return this.http.get<CandidateDto>(`${this.baseUrl}/candidate/${query}`);
  }
}
