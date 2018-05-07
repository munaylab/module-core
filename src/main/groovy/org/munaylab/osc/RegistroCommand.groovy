package org.munaylab.osc

import org.munaylab.user.User

class RegistroCommand implements grails.validation.Validateable {

    String denominacion
    String objeto
    TipoOrganizacion tipo
    //datos del representante
    String nombre
    String apellido
    String email
    String telefono

    static constraints = {
        denominacion size: 3..200
        objeto size: 10..500
        nombre size: 3..50
        apellido size: 3..30
        email email: true
        telefono size: 7..15
    }

    User getRepresentante() {
        new User(username: email, nombre: nombre, apellido: apellido,
            password: UUID.randomUUID(), accountLocked: true)
    }

    Organizacion getOrganizacion() {
        new Organizacion(nombre: denominacion, objeto: objeto, nombreURL: nombreURL,
            tipo: tipo, estado: EstadoOrganizacion.PENDIENTE)
    }

    String getNombreURL() {
        String nombreURL = ''
        denominacion.toLowerCase().each {
            char caracter = it as char
            if (Character.isLetterOrDigit(caracter)) {
                nombreURL += it
            } else if (Character.isSpaceChar(caracter)) {
                nombreURL += '_'
            }
        }
        return nombreURL
    }

}
