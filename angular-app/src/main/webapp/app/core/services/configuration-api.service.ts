import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SERVER_API_URL} from '../../app.constants';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ConfigurationApiService {

    private resourceUrl = `${SERVER_API_URL}api/configuration`;

    constructor(private httpClient: HttpClient) {
    }

    getConfiguration(): Observable<ConfigurationDTO> {
        return this.httpClient.get<ConfigurationDTO>(this.resourceUrl);
    }

    getInternalConfiguration(): Observable<InternalConfigurationDTO> {
        return this.httpClient.get<InternalConfigurationDTO>(`${this.resourceUrl}/internal`);
    }
}
