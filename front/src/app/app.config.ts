import {
  ApplicationConfig, inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {authInterceptor} from './core/interceptors/auth.interceptor';
import {globalErrorInterceptor} from './core/interceptors/global.interceptors';
import {AuthService} from './core/services/auth.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([
          authInterceptor,
          globalErrorInterceptor,
        ]
      )
    ),
    provideAppInitializer(() => {
      const authService = inject(AuthService);
      // Si on n'a pas de token du tout, on ne tente même pas le refresh
      if (!authService.getAccessToken()) {
        return Promise.resolve();
      }
      return authService.silentRefresh();
    }),
  ]
};
