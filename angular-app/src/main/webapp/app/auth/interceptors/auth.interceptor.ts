import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {LoginService} from '../services/login.service';
import {Router} from '@angular/router';
import {ERROR_FORBIDDEN_PATH} from '../../shared/constants/route.constants';
import {Injector, Type} from '@angular/core';

export class AuthInterceptor implements HttpInterceptor {
    constructor(private injector: Injector) {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(
            tap(
                () => {
                },
                (err: any) => {
                    if ((err instanceof HttpErrorResponse) && err.url && !err.url.includes('/api/account')) {
                        const router: Router = this.injector.get(Router as Type<Router>);
                        if (err.status === 401) {
                            const loginService: LoginService = this.injector.get(LoginService as Type<LoginService>);
                            loginService.login();
                        } else if (err.status === 403) {
                            router.navigate(['/', ERROR_FORBIDDEN_PATH]);
                        }
                    }
                }
            )
        );
    }
}
