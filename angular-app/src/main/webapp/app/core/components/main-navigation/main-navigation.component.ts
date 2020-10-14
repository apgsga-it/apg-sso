import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {PublicPageComponent} from '../../../demo/components/public-page/public-page.component';
import {NavigationItemModel} from '../../models/NavigationItem.model';
import {Principal} from '../../../auth/services/principal.service';
import {INTERNAL_PATH, PUBLIC_PATH} from '../../../shared/constants/route.constants';
import {InternalPageComponent} from '../../../demo/components/internal-page/internal-page.component';

@Component({
    selector: 'apg-main-navigation',
    templateUrl: './main-navigation.component.html',
    styleUrls: ['./main-navigation.component.scss']
})
export class MainNavigationComponent implements OnInit {

    @Output()
    private navigationItemClicked = new EventEmitter();

    accessibleNavigationItems: NavigationItemModel[] = [];

    private readonly NAVIGATION_ITEMS: NavigationItemModel[] = [
        new NavigationItemModel(PUBLIC_PATH, PublicPageComponent.PAGE_TITLE, []),
        new NavigationItemModel(INTERNAL_PATH, InternalPageComponent.PAGE_TITLE, ['BASISROLLE'])
    ];

    constructor(private principal: Principal) {
    }

    ngOnInit() {
        this.updateAccessibleNavigationItems();
        this.principal.getAuthenticationState().subscribe(() => this.updateAccessibleNavigationItems());
    }

    private updateAccessibleNavigationItems() {
        this.accessibleNavigationItems = this.NAVIGATION_ITEMS.filter(navItem => {
            if (navItem.authorities.length === 0) {
                return true;
            }
            return this.principal.hasAnyAuthority(navItem.authorities);
        });
    }

    handleClick() {
        this.navigationItemClicked.emit();
    }
}
