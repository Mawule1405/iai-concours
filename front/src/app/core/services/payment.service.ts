import {inject, Injectable} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../../environments/environment';
import {PaymentDto} from '../models/payment.model'; // Adaptez le chemin selon votre structure

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  // Remplacez l'URL de base selon la configuration de votre environnement (ex: environment.apiUrl)
  private readonly apiUrl = `${environment.apiUrl}/payments`;
  private readonly http = inject(HttpClient)


  /**
   * Crée un nouveau paiement.
   * POST /tsc-api/payments
   */
  createPaymentDto(payment: PaymentDto): Observable<PaymentDto> {
    return this.http.post<PaymentDto>(this.apiUrl, payment);
  }

  /**
   * Récupère un paiement par son ID.
   * GET /tsc-api/payments/{id}
   */
  getPaymentDtoById(id: string): Observable<PaymentDto> {
    return this.http.get<PaymentDto>(`${this.apiUrl}/${id}`);
  }


  /**
   * Met à jour un paiement existant.
   * PUT /tsc-api/payments/{id}
   */
  updatePaymentDto(id: string, payment: PaymentDto): Observable<PaymentDto> {
    return this.http.put<PaymentDto>(`${this.apiUrl}/${id}`, payment);
  }

  /**
   * Supprime un paiement par son ID.
   * DELETE /tsc-api/payments/{id}
   */
  deletePaymentDto(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
