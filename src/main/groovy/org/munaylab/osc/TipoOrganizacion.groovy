package org.munaylab.osc

enum TipoOrganizacion {
    FUNDACION('FUNDACION'),
    ASOCIACION_CIVIL('ASOCIACION_CIVIL'),
    ASOCIACION_SIMPLE('ASOCIACION_SIMPLE')

    String value

    TipoOrganizacion(String value) {
        this.value = value
    }

}
