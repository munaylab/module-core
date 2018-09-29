package org.munaylab.security

class ConfirmacionCommand implements grails.validation.Validateable {

    Long refId
    String codigo
    String password1
    String password2

    static constraints = {
        password1 nullable: true, blank: false, matches: /((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!]).{6,20})/
        password2 nullable: true, validator: { val, obj ->
            val == obj.password1
        }
    }

}
