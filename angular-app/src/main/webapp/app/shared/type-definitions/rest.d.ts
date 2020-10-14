/* tslint:disable */
/* eslint-disable */

interface ConfigurationDTO {
    version?: string;
    activeProfiles?: string[];
}

interface InternalConfigurationDTO {
    message?: string;
}

interface AccountDTO {
    id?: string;
    username?: string;
    firstName?: string;
    lastName?: string;
    email?: string;
    authorities?: AuthorityConstant[];
}

interface FieldErrorVM {
    objectName?: string;
    field?: string;
    message?: string;
}

type AuthorityConstant = "BASISROLLE" | "BETRIEBSCENTER" | "E_AUSHANGLISTE" | "VERKAUF" | "ANONYMOUS";
