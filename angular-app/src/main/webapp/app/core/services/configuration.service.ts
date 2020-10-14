import {Injectable} from '@angular/core';
import {ConfigurationApiService} from './configuration-api.service';

@Injectable({
    providedIn: 'root'
})
export class ConfigurationService {

    configuration: ConfigurationDTO;

    constructor(private configurationApiService: ConfigurationApiService) {
    }

    load() {
        return this.configurationApiService.getConfiguration()
                   .toPromise()
                   .then(configuration => {
                       this.configuration = configuration;
                   });
    }
}
