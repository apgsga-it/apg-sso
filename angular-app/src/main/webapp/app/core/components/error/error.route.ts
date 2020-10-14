import {Routes} from '@angular/router';
import {ErrorComponent} from './error.component';
import {ERROR_FORBIDDEN_PATH, ERROR_NOT_FOUND_PATH, ERROR_PATH} from '../../../shared/constants/route.constants';

export const ERROR_ROUTES: Routes = [
    {
        path: ERROR_PATH,
        component: ErrorComponent,
        data: {
            authorities: [],
            pageTitle: 'Unerwarteter Fehler',
            errorTitle: 'Ups, unerwarteter Fehler',
            errorDescription: 'Etwas funktioniert nicht wie erwartet.<br>Die Techniker wurden bereits darüber informiert und werden das Problem baldmöglichst beheben.'
        }
    },
    {
        path: ERROR_FORBIDDEN_PATH,
        component: ErrorComponent,
        data: {
            authorities: [],
            pageTitle: 'Zugriff verweigert',
            errorTitle: 'Zugriff nicht erlaubt',
            errorDescription: 'Sie sind nicht berechtigt die gesuchte Seite aufzurufen.',
            showLogoutButton: true
        }
    },
    {
        path: ERROR_NOT_FOUND_PATH,
        component: ErrorComponent,
        data: {
            authorities: [],
            pageTitle: 'Seite nicht gefunden',
            errorTitle: 'Ups, hier gibt es nichts zu sehen',
            errorDescription: 'Es tut uns leid, die gesuchte Seite wurde trotz intensiver Suche nicht gefunden.<br>'
        }
    },
    {
        path: '**',
        redirectTo: ERROR_NOT_FOUND_PATH,
        pathMatch: 'full'
    }
];
