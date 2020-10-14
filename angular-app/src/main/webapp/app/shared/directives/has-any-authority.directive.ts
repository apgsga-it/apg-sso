import {Directive, Input, TemplateRef, ViewContainerRef} from '@angular/core';
import {Principal} from '../../auth/services/principal.service';

/**
 * @whatItDoes Conditionally includes an HTML element if current user has any
 * of the authorities passed as the `expression`.
 *
 * @howToUse
 * ```
 *     <some-element *apgHasAnyAuthority="'BASISROLLE'">...</some-element>
 *
 *     <some-element *apgHasAnyAuthority="['BASISROLLE', 'VERKAUF']">...</some-element>
 * ```
 */
@Directive({
    selector: '[apgHasAnyAuthority]'
})
export class ApgHasAnyAuthorityDirective {
    private authorities: AuthorityConstant[];

    constructor(private principal: Principal, private templateRef: TemplateRef<any>, private viewContainerRef: ViewContainerRef) {
    }

    @Input()
    set apgHasAnyAuthority(value: AuthorityConstant | AuthorityConstant[]) {
        this.authorities = !Array.isArray(value) ? [value] : value;
        this.updateView();
        // Get notified each time authentication state changes.
        this.principal.getAuthenticationState().subscribe(() => this.updateView());
    }

    private updateView(): void {
        const hasAnyAuthority = this.principal.hasAnyAuthority(this.authorities);
        this.viewContainerRef.clear();
        if (hasAnyAuthority) {
            this.viewContainerRef.createEmbeddedView(this.templateRef);
        }
    }
}
