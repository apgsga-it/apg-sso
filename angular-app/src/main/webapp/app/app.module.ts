import {BrowserModule} from '@angular/platform-browser';
import {ApplicationRef, DoBootstrap, NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HttpClientModule} from '@angular/common/http';
import {SharedModule} from './shared/shared.module';
import {CoreModule} from './core/core.module';
import {AuthModule} from './auth/auth.module';
import {LayoutModule} from '@angular/cdk/layout';
import {KeycloakAngularModule, KeycloakService} from 'keycloak-angular';

const keycloakAngular = new KeycloakService();

@NgModule({
    declarations: [
        AppComponent
    ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        HttpClientModule,
        LayoutModule,
        KeycloakAngularModule,
        // Order of feature modules imports matters for the router
        SharedModule,
        CoreModule,
        AuthModule,
        // Main routing module should be the last in the list
        AppRoutingModule
    ],
    providers: [
        {
            provide: KeycloakService,
            useValue: keycloakAngular
        }
    ]
})
export class AppModule implements DoBootstrap {
    ngDoBootstrap(appRef: ApplicationRef) {
        keycloakAngular
            .init({
                config: 'keycloak.json',
                initOptions: {
                    onLoad: 'check-sso',
                    silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
                    checkLoginIframe: true
                },
                loadUserProfileAtStartUp: true,
                enableBearerInterceptor: true
            })
            .then(() => {
                console.log('[ngDoBootstrap] bootstrap app');

                appRef.bootstrap(AppComponent);
            })
            .catch(error => console.error('[ngDoBootstrap] init Keycloak failed', error));
    }
}
