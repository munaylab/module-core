package org.munaylab.osc

import org.munaylab.contacto.Contacto
import org.munaylab.contacto.TipoContacto
import org.munaylab.direccion.Domicilio
import org.munaylab.ods.Objetivo
import org.munaylab.user.User

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class Organizacion {

    String nombre
    String objeto
    String descripcion
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
        objetivos: Objetivo
    ]

    static constraints = {
        nombre size: 3..200, unique: true
        objeto size: 10..5000
        descripcion size: 10..1000
        nombreURL size: 3..250, unique: true
        fechaConstitucion nullable: true
        domicilio nullable: true
    }

}
