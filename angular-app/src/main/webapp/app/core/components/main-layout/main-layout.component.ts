import {Component, ViewChild} from '@angular/core';
import {Observable} from 'rxjs';
import {BreakpointObserver} from '@angular/cdk/layout';
import {map} from 'rxjs/operators';
import {Router} from '@angular/router';
import {Title} from '@angular/platform-browser';
import {MatButton} from '@angular/material/button';
import {ConfigurationService} from '../../services/configuration.service';

@Component({
    selector: 'apg-main-layout',
    templateUrl: './main-layout.component.html',
    styleUrls: ['./main-layout.component.scss']
})
export class MainLayoutComponent {
    isSmallDevice: boolean;
    isSmallDevice$: Observable<boolean>;
    apgApplicationVersion?: string;

    @ViewChild('sidenavToggleButton', {static: false})
    private sidenavToggleButton: MatButton;

    constructor(private router: Router,
                private titleService: Title,
                breakpointObserver: BreakpointObserver,
                configurationService: ConfigurationService) {
        this.apgApplicationVersion = configurationService.configuration.version;

        this.isSmallDevice$ = breakpointObserver.observe(
            '(max-width: 799px)'
        ).pipe(map(result => {
            this.isSmallDevice = result.matches;
            return result.matches;
        }));
    }

    blurSidenavToggleButton() {
        this.sidenavToggleButton._elementRef.nativeElement.blur();
    }
}
