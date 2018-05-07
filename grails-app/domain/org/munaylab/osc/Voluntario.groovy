package org.munaylab.osc

import org.munaylab.user.User
import org.munaylab.direccion.Domicilio
import org.munaylab.categoria.TipoUsuario

class Voluntario {

    String email
    String nombre
    String apellido
    Date nacimiento
    Domicilio domicilio
    TipoUsuario tipo
    Organizacion organizacion

    static constraints = {
        email nullable: false, email: true
        nombre nullable: false, size: 3..50
        apellido nullable: false, size: 3..30
        nacimiento nullable: true,
            range: new Date().parse('yyyy/MM/dd', "${Calendar.instance[Calendar.YEAR] -99}/1/1")..new Date().parse('yyyy/MM/dd', "${Calendar.instance[Calendar.YEAR] -16}/1/1")
        domicilio nullable: true
    }

}
