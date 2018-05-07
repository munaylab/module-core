package org.munaylab

import org.munaylab.user.User
import org.munaylab.security.Token
import org.munaylab.security.TipoToken
import grails.gorm.transactions.Transactional

@Transactional
class SecurityService {

    Token generarTokenConfirmacion(User user) {
        String token = UUID.randomUUID().toString()
        String codigo = token.substring(0, 8).toUpperCase()
        new Token(user: user, tipo: TipoToken.CONFIRMACION,
            value: token, codigo: codigo).save()
    }

    @Transactional(readOnly = true)
    Token validarToken(String value, Long refId, TipoToken tipo) {
        if (!value) return null

        Token token = getToken(value, refId, tipo)
        if (!token) return null

        token.enabled = false
        token.save()
    }

    @Transactional(readOnly = true)
    boolean tokenValido(String value, Long refId, TipoToken tipo) {
        Token token = getToken(value, refId, tipo)
        return token ? true : false
    }

    @Transactional(readOnly = true)
    private getToken(String value, Long refId, TipoToken tipo) {
        Token.createCriteria().get {
            if (value.size() == 8) {
                eq 'codigo', value.toUpperCase()
            } else {
                eq 'value', value
            }
            eq 'tipo', tipo
            eq 'enabled', true
            user {
                eq 'id', refId
            }
        }
    }

}
