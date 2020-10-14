import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {Router} from '@angular/router';
import {KeycloakService} from 'keycloak-angular';
import {UserApiService} from './user-api.service';

@Injectable({providedIn: 'root'})
export class Principal {
    public accountDTO?: AccountDTO;
    private authenticated = false;
    private authenticationState = new Subject<AccountDTO>();

    constructor(private keycloakAngular: KeycloakService, private userApiService: UserApiService, private router: Router) {
    }

    hasAnyAuthority(authorities: AuthorityConstant[]): boolean {
        if (!this.authenticated || !this.accountDTO || !this.accountDTO.authorities) {
            return false;
        }

        for (const authority of authorities) {
            if (this.accountDTO.authorities.includes(authority)) {
                return true;
            }
        }

        return false;
    }

    identity(force?: boolean): Promise<AccountDTO | undefined> {
        if (force === true) {
            this.accountDTO = undefined;
        }

        return this.keycloakAngular.isLoggedIn()
                   .then((isLoggedIn: boolean) => {
                       if (isLoggedIn) {
                           return this.userApiService
                                      .get()
                                      .toPromise()
                                      .then((accountDTO: AccountDTO) => {
                                          if (accountDTO) {
                                              this.accountDTO = accountDTO;
                                              this.authenticated = true;
                                          } else {
                                              this.accountDTO = undefined;
                                              this.authenticated = false;
                                          }
                                          this.authenticationState.next(this.accountDTO);

                                          return this.accountDTO!;
                                      });
                       } else {
                           this.accountDTO = undefined;
                           this.authenticated = false;
                           this.authenticationState.next(this.accountDTO);
                           return this.accountDTO;
                       }
                   })
                   .catch(() => {
                       this.accountDTO = undefined;
                       this.authenticated = false;
                       this.authenticationState.next(this.accountDTO);
                       return undefined;
                   });
    }

    isAuthenticated(): boolean {
        return this.authenticated;
    }

    getAuthenticationState(): Observable<AccountDTO> {
        return this.authenticationState.asObservable();
    }
}
