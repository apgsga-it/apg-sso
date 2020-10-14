import {NgModule} from '@angular/core';
import {MAT_FORM_FIELD_DEFAULT_OPTIONS} from '@angular/material/form-field';
import {RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {ApgHasAnyAuthorityDirective} from './directives/has-any-authority.directive';
import {MatToolbarModule} from '@angular/material/toolbar';
import {CardLayoutComponent} from './components/card-layout/card-layout.component';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatListModule} from '@angular/material/list';
import {MatDividerModule} from '@angular/material/divider';
import {MatMenuModule} from '@angular/material/menu';

@NgModule({
    imports: [
        CommonModule,
        RouterModule,
        MatCardModule,
        MatButtonModule,
        MatToolbarModule,
        MatIconModule,
        MatSidenavModule,
        MatSlideToggleModule,
        MatMenuModule,
        MatDividerModule,
        MatListModule
    ],
    providers: [
        {
            provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
            useValue: {float: 'auto'}
        }
    ],
    declarations: [
        ApgHasAnyAuthorityDirective,
        CardLayoutComponent
    ],
    exports: [
        CommonModule,
        RouterModule,
        ApgHasAnyAuthorityDirective,
        CardLayoutComponent,
        MatCardModule,
        MatButtonModule,
        MatToolbarModule,
        MatIconModule,
        MatSidenavModule,
        MatSlideToggleModule,
        MatMenuModule,
        MatDividerModule,
        MatListModule
    ]
})
export class SharedModule {
}
