import {Component} from '@angular/core';
import {Principal} from '../../../auth/services/principal.service';
import {LoginService} from '../../../auth/services/login.service';

@Component({
    selector: 'apg-public-page',
    templateUrl: './public-page.component.html',
    styleUrls: ['./public-page.component.scss']
})
export class PublicPageComponent {
    static readonly PAGE_TITLE = 'Public Page';

    readonly baseAuthority = 'BASISROLLE' as AuthorityConstant;

    constructor(public principal: Principal,
                private loginService: LoginService) {
    }

    getPageTitle() {
        return PublicPageComponent.PAGE_TITLE;
    }

    login() {
        this.loginService.login();
    }

    logout() {
        this.loginService.logout();
    }

    getAuthorities(): string {
        if (!this.principal.accountDTO || !this.principal.accountDTO.authorities || this.principal.accountDTO.authorities.length === 0) {
            return 'keinen Berechtigungen';
        } else {
            return `den Berechtigungen ${this.principal.accountDTO.authorities.join(', ')}`;
        }
    }
}
