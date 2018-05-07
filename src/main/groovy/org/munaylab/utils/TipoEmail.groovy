package org.munaylab.utils

enum TipoEmail {
    ORG_REGISTRO('org.registro', '/email/org/_registro_template'),
    ORG_BIENVENIDA('org.bienvenida', '/email/org/_bienvenida_template'),
    ORG_CAPACITACION('org.capacitacion', '/email/org/_capacitacion_template')

    private id
    private plantilla

    TipoEmail(String id, String plantilla) {
        this.id = id
        this.plantilla = plantilla
    }

}
