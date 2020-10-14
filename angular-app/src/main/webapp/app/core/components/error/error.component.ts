import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs';
import {LoginService} from '../../../auth/services/login.service';

@Component({
    selector: 'apg-error',
    templateUrl: './error.component.html',
    styleUrls: ['./error.component.scss']
})
export class ErrorComponent implements OnInit, OnDestroy {
    errorTitle: string;
    errorDescription: string;
    showLogoutButton: boolean;

    private subscription: Subscription;

    constructor(private route: ActivatedRoute, private loginService: LoginService) {
    }

    ngOnInit() {
        this.subscription = this.route.data.subscribe(routeData => {
            this.errorTitle = routeData.errorTitle;
            this.errorDescription = routeData.errorDescription;
            this.showLogoutButton = routeData.showLogoutButton;
        });
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    logout() {
        this.loginService.logout();
    }
}
