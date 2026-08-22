import {inject, Injectable, NgZone} from '@angular/core';
import { Client, Message } from '@stomp/stompjs';

import SockJS from 'sockjs-client';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private client: Client;
  public zone = inject(NgZone)

  constructor() {
    this.client = new Client({
      // Configuration compatible avec Spring + SockJS
      webSocketFactory: () => new SockJS(`${environment.apiUrl.replace('/iai-concours-api','/ws-iai-concours-api')}`),

      // Logs de débogage pour voir les frames STOMP (très utile !)
      debug: (str) => console.log('STOMP: ' + str),

      // Tentative de reconnexion automatique toutes le 5 secondes
      reconnectDelay: 5000,
    });

    this.client.onConnect = (frame) => {
      console.log('Connecté avec succès au serveur STOMP ✅');
    };

    this.client.onStompError = (frame) => {
      console.error('Erreur de protocole STOMP ❌', frame.headers['message']);
    };

    this.client.activate(); // Lance la connexion
  }

  // Méthode de "Listen" inspirée de l'article
  listen(topic: string, callback: (payload: string) => void) {
    // Si déjà connecté, on s'abonne
    if (this.client.connected) {
      this.client.subscribe(topic, (message: Message) => {
        callback(message.body);
      });
    } else {
      // Sinon, on attend l'événement de connexion
      const originalOnConnect = this.client.onConnect;
      this.client.onConnect = (frame) => {
        if (originalOnConnect) originalOnConnect(frame);
        this.client.subscribe(topic, (message: Message) => {
          callback(message.body);
        });
      };
    }
  }
}
