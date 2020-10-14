import {NgModule} from '@angular/core';
import {SharedModule} from '../shared/shared.module';
import {DemoRoutingModule} from './demo.route';
import {PublicPageComponent} from './components/public-page/public-page.component';
import {InternalPageComponent} from './components/internal-page/internal-page.component';

@NgModule({
    imports: [
        SharedModule,
        DemoRoutingModule
    ],
    declarations: [
        PublicPageComponent,
        InternalPageComponent
    ]
})
export class DemoModule {
}
