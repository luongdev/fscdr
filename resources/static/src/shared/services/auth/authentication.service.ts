import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, map, Observable, throwError } from 'rxjs';
import { AuthBaseResponse, UserModel } from '@shared/models';
import { ConfigService } from '@shared/ultils/config.service';
import Swal from 'sweetalert2';

const headers = new HttpHeaders().set('Content-Type', 'application/json');

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  public currentUser: Observable<UserModel>;

  private currentUserSubject: BehaviorSubject<UserModel>;

  constructor(
    private _http: HttpClient,
    private readonly configService: ConfigService
  ) {
    this.currentUserSubject = new BehaviorSubject<UserModel>(
      JSON.parse(localStorage.getItem('currentUser'))
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): UserModel {
    return this.currentUserSubject.value;
  }

  login(username: string, password: string) {
    return this._http
      .post<AuthBaseResponse<UserModel>>(
        `${this.configService.keycloakUrl}/v1.0/login`,
        JSON.stringify({
          username: username,
          password: password,
        }),
        { headers: headers }
      )
      .pipe(
        map((response) => {
          // login successful if there's a jwt token in the response
          if (response.statusCode == 200 && response.data.access_token) {
            // store user details and jwt token in local storage to keep user logged in between page refreshes
            localStorage.setItem('currentUser', JSON.stringify(response.data));
            // notify
            this.currentUserSubject.next(response.data);
          } else if (response.statusCode == 401) {
            Swal.fire({
              icon: 'error',
              title: 'Lỗi...',
              text: `Tên tài khoản hoặc mật khẩu không chính xác!`,
            }).then();
          }

          return response.data;
        })
      );
  }

  refreshtoken() {
    const currentUser = this.currentUserValue;
    if (currentUser && currentUser.refresh_token) {
      return this._http
        .post<AuthBaseResponse<UserModel>>(
          `${this.configService.keycloakUrl}/v1.0/refresh-token`,
          JSON.stringify({
            refresh_token: currentUser.refresh_token,
          }),
          { headers: headers }
        )
        .pipe(
          map((response) => {
            // login successful if there's a jwt token in the response
            if (response.statusCode == 200 && response.data.access_token) {
              // store user details and jwt token in local storage to keep user logged in between page refreshes
              localStorage.setItem(
                'currentUser',
                JSON.stringify(response.data)
              );
              // notify
              this.currentUserSubject.next(response.data);
            }

            return response.data;
          })
        );
    }
    return throwError('null accesstoken!');
  }

  logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('currentUser');
    // notify
    this.currentUserSubject.next(null);
  }
}
