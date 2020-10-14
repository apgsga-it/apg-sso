import {Injectable, isDevMode} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';

import {Principal} from './principal.service';
import {LoginService} from './login.service';
import {ERROR_FORBIDDEN_PATH} from '../../shared/constants/route.constants';
import {KeycloakService} from 'keycloak-angular';

@Injectable({providedIn: 'root'})
export class UserRouteAccessService implements CanActivate {
    constructor(
        private router: Router,
        private loginService: LoginService,
        private keycloakAngular: KeycloakService,
        private principal: Principal
    ) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | Promise<boolean> {
        const authorities = route.data.authorities;
        // We need to call the checkLogin / and so the principal.identity() function, to ensure,
        // that the client has a principal too, if they already logged in by the server.
        // This could happen on a page refresh.
        return this.checkLogin(authorities, state.url);
    }

    checkLogin(authorities: AuthorityConstant[], requestedPath: string): Promise<boolean> {
        const principal = this.principal;

        return Promise.resolve(
            principal.identity().then(accountDTO => {
                if (!authorities || authorities.length === 0) {
                    return true;
                }

                if (accountDTO) {
                    const hasAnyAuthority = principal.hasAnyAuthority(authorities);
                    if (hasAnyAuthority) {
                        return true;
                    }
                    if (isDevMode()) {
                        console.error(`User cannot access the path ${requestedPath} because he/she has not any of the required authorities: `, authorities);
                    }
                    this.redirect(requestedPath, true);
                    return false;
                } else {
                    this.redirect(requestedPath, false);
                    return false;
                }
            })
        );
    }

    private redirect(requestedPath: string, accessDenied: boolean) {
        if (accessDenied) {
            this.router.navigate(['/', ERROR_FORBIDDEN_PATH]);
        } else {
            this.loginService.login(requestedPath);
        }
    }
}
