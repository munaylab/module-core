package org.munaylab.osc

import org.munaylab.contacto.Contacto
import org.munaylab.contacto.TipoContacto
import org.munaylab.direccion.Domicilio
import org.munaylab.planificacion.Evento
import org.munaylab.planificacion.Programa
import org.munaylab.user.User

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class Organizacion {

    String nombre
    String objeto
    String nombreURL
    Date fechaConstitucion
    TipoOrganizacion tipo
    EstadoOrganizacion estado
    Domicilio domicilio

    Date dateCreated
    Date lastUpdated
    Boolean enabled = Boolean.TRUE

    static hasMany = [
        admins: UserOrganizacion,
        miembros: UserOrganizacion,
        voluntarios: Voluntario,
        contactos: Contacto,
        programas: Programa,
        eventos: Evento
    ]

    static constraints = {
        nombre size: 3..200, unique: true
        objeto size: 10..500
        fechaConstitucion nullable: true
        domicilio nullable: true
        nombreURL size: 3..250, unique: true
    }

}
