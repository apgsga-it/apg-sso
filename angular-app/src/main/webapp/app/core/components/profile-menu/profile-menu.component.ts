import {Component} from '@angular/core';
import {Principal} from '../../../auth/services/principal.service';
import {LoginService} from '../../../auth/services/login.service';

@Component({
    selector: 'apg-profile-menu',
    templateUrl: './profile-menu.component.html',
    styleUrls: ['./profile-menu.component.scss']
})
export class ProfileMenuComponent {

    constructor(public principal: Principal,
                private loginService: LoginService) {
    }

    login() {
        this.loginService.login();
    }

    logout() {
        this.loginService.logout();
    }
}
