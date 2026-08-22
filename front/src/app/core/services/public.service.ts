import {Injectable, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Observable} from 'rxjs';


@Injectable({
  providedIn: 'root',
})

export class PublicService {
  http = inject(HttpClient)
  private readonly apiUrl = `${environment.apiUrl}/public`;


  getNewPassword(usernameOrEmail: string){
    return this.http.post<boolean>(`${this.apiUrl}/new-password`, usernameOrEmail);
  }


  getCardDetails(cardNum: string) {
    return this.http.get<any>(`${this.apiUrl}/check-card/${cardNum}`);
  }

  getContratDetails(cNum: string) {
    return this.http.get<any>(`${this.apiUrl}/check-contrat/${cNum}`);
  }

  getHomeStats() {
    return this.http.get<any>(`${this.apiUrl}/home-stats`)
  }


}
