package org.munaylab.user

class UserCommand implements grails.validation.Validateable {

    Long id
    String nombre
    String apellido
    String username
    Long tipo

    String cargo

    static constraints = {
        id nullable: true
        username nullable: false, blank: false
        nombre nullable: false, size: 3..50
        apellido nullable: false, size: 3..30
        cargo nullable: true
    }

}
