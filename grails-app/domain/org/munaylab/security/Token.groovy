package org.munaylab.security

import org.munaylab.user.User

class Token {

    User user
    String value
    String codigo
    TipoToken tipo
    Date dateCreated
    Boolean enabled = Boolean.TRUE

    static constraints = {
        codigo nullable: true, minSize: 8, maxSize: 8
    }
}
