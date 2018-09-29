package org.munaylab.osc

import org.munaylab.direccion.DomicilioCommand

class OrganizacionCommand implements grails.validation.Validateable {

    Long id
    String nombre
    String objeto
    String descripcion
    Date fechaConstitucion
    TipoOrganizacion tipo
    DomicilioCommand domicilio

    static constraints = {
        id nullable: true
        nombre size: 3..200
        objeto size: 10..5000
        descripcion size: 10..1000
        fechaConstitucion nullable: true
        domicilio nullable: true
    }

}
