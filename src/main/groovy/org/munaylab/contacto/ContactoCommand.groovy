package org.munaylab.contacto

class ContactoCommand implements grails.validation.Validateable {

    Long id
    String value
    TipoContacto tipo

    static constraints = {
        id nullable: true
        value size: 5..50
    }

}
