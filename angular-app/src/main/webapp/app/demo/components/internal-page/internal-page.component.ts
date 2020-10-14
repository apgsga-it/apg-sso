import {Component} from '@angular/core';
import {ConfigurationApiService} from '../../../core/services/configuration-api.service';

@Component({
    selector: 'apg-internal-page',
    templateUrl: './internal-page.component.html',
    styleUrls: ['./internal-page.component.scss']
})
export class InternalPageComponent {
    static readonly PAGE_TITLE = 'Internal Page';

    internalConfiguration: InternalConfigurationDTO;

    readonly internalAuthority = 'BASISROLLE' as AuthorityConstant;

    constructor(private configurationApiService: ConfigurationApiService) {
        this.configurationApiService.getInternalConfiguration()
            .toPromise()
            .then(internalConfiguration => {
                this.internalConfiguration = internalConfiguration;
            });
    }

    getPageTitle() {
        return InternalPageComponent.PAGE_TITLE;
    }
}
