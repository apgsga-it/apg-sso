import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {APP_INITIALIZER, LOCALE_ID, NgModule} from '@angular/core';
import {LOCALE} from '../app.constants';
import locale from '@angular/common/locales/de-CH';
import {registerLocaleData} from '@angular/common';
import {ConfigurationService} from './services/configuration.service';
import {ErrorComponent} from './components/error/error.component';
import {ErrorInterceptor} from './interceptors/error.interceptor';
import {SharedModule} from '../shared/shared.module';
import {MainLayoutComponent} from './components/main-layout/main-layout.component';
import {MainNavigationComponent} from './components/main-navigation/main-navigation.component';
import {ProfileMenuComponent} from './components/profile-menu/profile-menu.component';

export function ConfigurationLoader(configurationService: ConfigurationService) {
    return () => configurationService.load();
}

@NgModule({
    imports: [
        SharedModule
    ],
    declarations: [
        MainLayoutComponent,
        MainNavigationComponent,
        ProfileMenuComponent,
        ErrorComponent
    ],
    providers: [
        {
            provide: LOCALE_ID,
            useValue: LOCALE
        },
        {
            provide: APP_INITIALIZER,
            useFactory: ConfigurationLoader,
            deps: [ConfigurationService],
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorInterceptor,
            multi: true
        },
    ]
})
export class CoreModule {
    constructor() {
        registerLocaleData(locale);
    }
}
