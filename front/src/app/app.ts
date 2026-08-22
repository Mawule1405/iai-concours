import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {NotificationContainerComponent} from './shared/dialogs/notification-container/notification-container.component';

@Component({
  imports: [RouterOutlet, NotificationContainerComponent],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('front');
}
