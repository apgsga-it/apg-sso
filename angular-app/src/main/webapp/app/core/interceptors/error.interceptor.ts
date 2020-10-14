import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {EMPTY, Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {ERROR_NOT_FOUND_PATH, ERROR_PATH} from '../../shared/constants/route.constants';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    private readonly IGNORED_HTTP_CODES = [
        401,
        403
    ];

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(
            catchError((error: any) => {
                if ((error instanceof HttpErrorResponse) && error.status >= 400 && error.status <= 600 && this.IGNORED_HTTP_CODES.indexOf(error.status) < 0) {
                    if (error.status === 404) {
                        window.location.href = `/#/${ERROR_NOT_FOUND_PATH}`;
                        return EMPTY;
                    }

                    if (error.error == null || error.error.type == null) {
                        console.error(`An error occurred on the server. Received a response with status code ${error.status}`);
                        if (error.error) {
                            console.error(error.error.title);
                        }
                        window.location.href = `/#/${ERROR_PATH}`;
                        return EMPTY;
                    }
                }

                return throwError(error);
            })
        );
    }
}
