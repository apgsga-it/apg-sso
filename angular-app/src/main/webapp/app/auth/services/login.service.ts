import {Injectable} from '@angular/core';
import {Location} from '@angular/common';
import {KeycloakService} from 'keycloak-angular';
import {Router} from '@angular/router';

@Injectable({providedIn: 'root'})
export class LoginService {
    constructor(
        private keycloakAngular: KeycloakService,
        private location: Location,
        private router: Router
    ) {
    }

    login(requestedPath?: string) {
        const path = requestedPath ? requestedPath : this.router.routerState.snapshot.url;
        this.keycloakAngular.login({
            redirectUri: `${location.origin}/${this.location.prepareExternalUrl(path)}`
        });
    }

    logout(): void {
        const redirectUri = `${location.origin}${this.location.prepareExternalUrl('/')}`;
        this.keycloakAngular.logout(redirectUri);
    }
}
