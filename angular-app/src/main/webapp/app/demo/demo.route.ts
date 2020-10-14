import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {PublicPageComponent} from './components/public-page/public-page.component';
import {INTERNAL_PATH, PUBLIC_PATH, ROOT_PATH} from '../shared/constants/route.constants';
import {UserRouteAccessService} from '../auth/services/user-route-access-service';
import {InternalPageComponent} from './components/internal-page/internal-page.component';

export const DEMO_ROUTES: Routes = [
    {
        path: ROOT_PATH,
        redirectTo: PUBLIC_PATH
    },
    {
        path: PUBLIC_PATH,
        component: PublicPageComponent,
        data: {
            authorities: [],
            pageTitle: PublicPageComponent.PAGE_TITLE
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: INTERNAL_PATH,
        component: InternalPageComponent,
        data: {
            authorities: ['BASISROLLE' as AuthorityConstant],
            pageTitle: InternalPageComponent.PAGE_TITLE
        },
        canActivate: [UserRouteAccessService]
    }
];

@NgModule({
    imports: [RouterModule.forChild(DEMO_ROUTES)],
    exports: [RouterModule]
})
export class DemoRoutingModule {
}
