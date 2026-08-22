import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {  Cycle, Serie, Status } from '../enums/enum';
import {CandidateDto, CandidateStatisticsDto} from '../models/candidate.model';
import {environment} from '../../../environments/environment';

export interface Pagination<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  page: number;
}

export interface CandidateFilterParams {
  numero?: string;
  gender?: string;
  status?: Status;
  serie?: Serie;
  option?: Cycle;
  page: number;
  size: number;
}

@Injectable({
  providedIn: 'root'
})
export class CandidateService {
  private apiUrl = `${environment.apiUrl}/candidates`;

  constructor(private http: HttpClient) {}

  getCandidates(params: CandidateFilterParams): Observable<Pagination<CandidateDto>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString());

    if (params.numero) httpParams = httpParams.set('numero', params.numero);
    if (params.gender) httpParams = httpParams.set('gender', params.gender);
    if (params.status) httpParams = httpParams.set('status', params.status);
    if (params.serie) httpParams = httpParams.set('serie', params.serie);
    if (params.option) httpParams = httpParams.set('option', params.option);

    return this.http.get<Pagination<CandidateDto>>(this.apiUrl, { params: httpParams });
  }

  getPendingCandidate(){
    return this.http.get<string[]>(`${this.apiUrl}/pending-candidate`);
  }

  /**
   * Mettre à jour les informations d'un candidat
   */
  updateCandidate(candidateId: string, candidate: CandidateDto): Observable<CandidateDto> {
    return this.http.put<CandidateDto>(`${this.apiUrl}/${candidateId}`, candidate);
  }

  /**
   * Supprimer un candidat par son identifiant
   */
  deleteCandidate(candidateId: string): Observable<boolean | void> {
    return this.http.delete<boolean | void>(`${this.apiUrl}/${candidateId}`);
  }

  export(format: 'csv' | 'excel' | 'pdf', params: CandidateFilterParams): Observable<Blob> {
    let httpParams = new HttpParams();
    if (params.numero) httpParams = httpParams.set('numero', params.numero);
    if (params.gender) httpParams = httpParams.set('gender', params.gender);
    if (params.status) httpParams = httpParams.set('status', params.status);
    if (params.serie) httpParams = httpParams.set('serie', params.serie);
    if (params.option) httpParams = httpParams.set('option', params.option);

    return this.http.get(`${this.apiUrl}/export/${format}`, {
      params: httpParams,
      responseType: 'blob'
    });
  }

  getGlobalCandidateStatistics(): Observable<CandidateStatisticsDto> {
    return this.http.get<CandidateStatisticsDto>(`${this.apiUrl}/statistics/global`);
  }

  /**
   * Récupère les statistiques filtrées
   */
  getCandidateStatistics(filters?: {
    numero?: string;
    gender?: string;
    status?: string;
    serie?: string;
    option?: string;
  }): Observable<CandidateStatisticsDto> {

    let params = new HttpParams();

    if (filters) {
      if (filters.numero) params = params.set('numero', filters.numero);
      if (filters.gender) params = params.set('gender', filters.gender);
      if (filters.status) params = params.set('status', filters.status);
      if (filters.serie) params = params.set('serie', filters.serie);
      if (filters.option) params = params.set('option', filters.option);
    }

    return this.http.get<CandidateStatisticsDto>(`${this.apiUrl}/statistics`, { params });
  }
}
