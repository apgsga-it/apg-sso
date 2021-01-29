import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MainLayoutComponent} from './core/components/main-layout/main-layout.component';
import {ERROR_ROUTES} from './core/components/error/error.route';
import {ROOT_PATH} from './shared/constants/route.constants';

const APP_ROUTES: Routes = [
    {
        path: ROOT_PATH,
        component: MainLayoutComponent,
        children: [
            // Lazy loaded modules
            {
                path: ROOT_PATH,
                loadChildren: () => import('./demo/demo.module').then(m => m.DemoModule)
            }
        ]
    },
    ...ERROR_ROUTES,
];

@NgModule({
    imports: [RouterModule.forRoot(APP_ROUTES, {
        useHash: true,
        scrollPositionRestoration: 'disabled',
        enableTracing: false,
        relativeLinkResolution: 'legacy'
    })],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
