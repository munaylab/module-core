package org.munaylab.user

import org.munaylab.contacto.Contacto
import org.munaylab.contacto.TipoContacto
import org.munaylab.direccion.Domicilio
import org.munaylab.security.Role
import org.munaylab.security.UserRole
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class User implements Serializable {

    private static final long serialVersionUID = 1

    String username
    String password
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    boolean enabled = true

    String nombre
    String apellido
    Date nacimiento
    Domicilio domicilio

    static constraints = {
        password nullable: false, blank: false, password: true
        username nullable: false, blank: false, unique: true
        nombre nullable: false, size: 3..50
        apellido nullable: false, size: 3..30
        nacimiento nullable: true,
            range: new Date().parse('yyyy/MM/dd', "${Calendar.instance[Calendar.YEAR] -99}/1/1")..new Date().parse('yyyy/MM/dd', "${Calendar.instance[Calendar.YEAR] -16}/1/1")
        domicilio nullable: true
    }

    static hasMany = [
        contactos: Contacto
    ]

    static mapping = {
	    password column: '`password`'
    }

    Set<Role> getAuthorities() {
        (UserRole.findAllByUser(this) as List<UserRole>)*.role as Set<Role>
    }

    Contacto getEmail() {
        contactos.find { it.tipo == TipoContacto.EMAIL }
    }

}
