package org.munaylab.osc

import org.munaylab.direccion.DomicilioCommand

class VoluntarioCommand implements grails.validation.Validateable {

    Long id
    Long orgId
    String email
    String nombre
    String apellido
    Date nacimiento
    DomicilioCommand domicilio
    Long tipoUsuarioId
    String tipoUsuarioNombre

    static constraints = {
        id nullable: true
        email nullable: false, email: true
        nombre nullable: false, size: 3..50
        apellido nullable: false, size: 3..30
        nacimiento nullable: true,
            range: new Date().parse('yyyy/MM/dd', "${Calendar.instance[Calendar.YEAR] -99}/1/1")..new Date().parse('yyyy/MM/dd', "${Calendar.instance[Calendar.YEAR] -16}/1/1")
        domicilio nullable: true
        tipoUsuarioId nullable: true
        tipoUsuarioNombre nullable: true
    }

}
