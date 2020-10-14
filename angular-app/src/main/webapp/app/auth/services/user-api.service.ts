import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

import {SERVER_API_URL} from '../../app.constants';

@Injectable({providedIn: 'root'})
export class UserApiService {
    private accountBaseUrl = `${SERVER_API_URL}api/account`;

    constructor(private http: HttpClient) {
    }

    get(): Observable<AccountDTO> {
        return this.http.get<AccountDTO>(this.accountBaseUrl);
    }
}
