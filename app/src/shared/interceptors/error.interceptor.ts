import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthenticationService } from '../services';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(
    private _router: Router,
    private _authenticationService: AuthenticationService
  ) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((err) => {
        if ([401, 403].indexOf(err.status) !== -1) {
          // try refresh token
          this._authenticationService.refreshtoken().subscribe({
            next: (data) => {
              debugger;
              if (data && data.access_token) {
                return next.handle(
                  request.clone({
                    setHeaders: {
                      Authorization: `Bearer ${data.access_token}`,
                    },
                  })
                );
              } else {
                this._router.navigate(['/login']);
              }
            },
            error: () => {
              this._router.navigate(['/login']);
            },
          });
        }
        // throwError
        const error = err.error.message || err.statusText;
        return throwError(error);
      })
    );
  }
}
