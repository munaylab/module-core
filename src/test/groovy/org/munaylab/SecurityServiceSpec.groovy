package org.munaylab

import org.munaylab.user.User
import org.munaylab.security.Token
import org.munaylab.security.TipoToken

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class SecurityServiceSpec extends Specification
        implements ServiceUnitTest<SecurityService>, DataTest {

    void setupSpec() {
        mockDomains User, Token
    }

    void 'generar token'() {
        when: 'generamos un token de confirmacion'
        def token = service.generarTokenConfirmacion(user)
        then: 'el token de confirmacion queda guardado'
        token && Token.countByTipo(TipoToken.CONFIRMACION) == 1
    }
    void 'validar token'() {
        given:
        def t = token
        when:
        t = service.validarToken(t.value, t.user.id, TipoToken.CONFIRMACION)
        then:
        t && Token.count() == 1
    }

    void 'token invalido'() {
        when: 'tratamos de validar un token'
        def t = service.validarToken('invalido', 1, TipoToken.CONFIRMACION)
        then: 'comprobamos que el token es invalido'
        !t && Token.count() == 0
    }
    void 'consultar por token'() {
        given:
        token
        expect:
        service.tokenValido(value, refId, tipo) == result
        where:
        value       | refId | tipo                   | result
        ''          | 1     | TipoToken.CONFIRMACION | false
        'TOKEN-001' | 2     | TipoToken.CONFIRMACION | false
        'TOKE'      | 1     | TipoToken.CONFIRMACION | false
        'T'         | 1     | TipoToken.CONFIRMACION | false
        'TOKEN-001' | 1     | TipoToken.CONFIRMACION | true
    }
    void 'consultar por codigo'() {
        given:
        token
        expect:
        service.tokenValido(value, refId, tipo) == result
        where:
        value       | refId | tipo                   | result
        '12345678'  | 1     | TipoToken.CONFIRMACION | false
        'SECRETOS'  | 2     | TipoToken.CONFIRMACION | false
        '1234'      | 1     | TipoToken.CONFIRMACION | false
        '5'         | 1     | TipoToken.CONFIRMACION | false
        'SECRETOS'  | 1     | TipoToken.CONFIRMACION | true
    }

    private User getUser() {
        new User(username: 'mcaligares@gmail.com', nombre: 'Augusto',
            apellido: 'Caligares', password: 'password').save(flush: true)
    }
    private Token getToken() {
        new Token(user: user, tipo: TipoToken.CONFIRMACION,
            value: 'TOKEN-001', codigo: 'SECRETOS').save(flush: true)
    }
}
