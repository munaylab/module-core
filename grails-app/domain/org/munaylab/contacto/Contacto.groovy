package org.munaylab.contacto

class Contacto {

    String value
    TipoContacto tipo

    static constraints = {
        value size: 5..50
    }
}
