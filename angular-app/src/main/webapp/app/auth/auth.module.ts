import {Injector, NgModule} from '@angular/core';
import {SharedModule} from '../shared/shared.module';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {AuthInterceptor} from './interceptors/auth.interceptor';

@NgModule({
    imports: [
        SharedModule
    ],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true,
            deps: [Injector]
        }
    ]
})
export class AuthModule {
}
