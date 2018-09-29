package org.munaylab

import org.munaylab.contacto.Contacto
import org.munaylab.direccion.Domicilio
import org.munaylab.direccion.DomicilioCommand
import org.munaylab.contacto.Contacto
import org.munaylab.contacto.ContactoCommand
import org.munaylab.contacto.TipoContacto
import org.munaylab.categoria.TipoUsuario
import org.munaylab.osc.Organizacion
import org.munaylab.osc.OrganizacionCommand
import org.munaylab.osc.UserOrganizacion
import org.munaylab.osc.RegistroCommand
import org.munaylab.osc.Voluntario
import org.munaylab.osc.EstadoOrganizacion
import org.munaylab.osc.TipoOrganizacion
import org.munaylab.osc.Voluntario
import org.munaylab.osc.VoluntarioCommand
import org.munaylab.user.User
import org.munaylab.user.UserCommand
import org.munaylab.security.Token
import org.munaylab.security.UserRole
import org.munaylab.security.ConfirmacionCommand

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class OrganizacionServiceSpec extends Specification
        implements ServiceUnitTest<OrganizacionService>, DataTest, UnitTestService {

    def registroCommandValido, registroCommandInvalido

    void setupSpec() {
        mockDomains Organizacion, User, Token, UserRole
    }

    def setup() {
        service.securityService = Mock(SecurityService)
        service.securityService.generarTokenConfirmacion(_) >> { [value: ''] }

        registroCommandValido = new RegistroCommand(DATOS_REGISTRO_VALIDOS)
        registroCommandInvalido = new RegistroCommand(DATOS_REGISTRO_INVALIDOS)

    }

    void 'registro incompleto'() {
        expect:
        service.registrar(registroCommandInvalido) == null
        registroCommandInvalido.validate() == false
        Organizacion.count() == 0
    }
    void 'registro completo'() {
        expect:
        service.registrar(registroCommandValido) != null
        registroCommandValido.validate() == true
        Organizacion.count() == 1 && User.all.size() == 1
        Organizacion.get(1).admins.size() == 1
    }
    void 'registrar una organizacion ya existente'() {
        given:
        registrarUnaOrganizacionConDatos(registroCommandValido)
        when:
        def org = service.registrar(registroCommandValido)
        then:
        registroCommandValido.validate() == true
        org != null && org.hasErrors() && Organizacion.count() == 1
    }
    private void registrarUnaOrganizacionConDatos(command) {
        def org = service.registrar(command)
        assert org != null && !org.hasErrors()
    }
    void 'confirmar un registro'() {
        given:
        registrarUnaOrganizacionConDatos(registroCommandValido)
        and:
        def confirmacionCommand = new ConfirmacionCommand().with {
            codigo      = 'codigo'
            password1   = 'asdQWE123'
            password2   = 'asdQWE123'
            it
        }
        when:
        service.confirmar(confirmacionCommand, User.get(1))
        then:
        Organizacion.countByEstado(EstadoOrganizacion.REGISTRADA) == 1
    }
    void 'confirmar un registro invalido'() {
        given:
        registrarUnaOrganizacionConDatos(registroCommandValido)
        and:
        def confirmacionCommand = new ConfirmacionCommand().with {
            codigo      = 'codigo'
            password1   = 'asdQWE123'
            password2   = 'asdQWE123'
            it
        }
        when:
        service.confirmar(confirmacionCommand, new User())
        then:
        Organizacion.countByEstado(EstadoOrganizacion.PENDIENTE) == 1
    }
    void 'listar organizaciones pendientes'() {
        given:
        registrarUnaOrganizacionConDatos(registroCommandValido)
        expect:
        service.organizacionesPendientes.size() == 1
    }
    void 'listar organizaciones registradas'() {
        given:
        new Organizacion(DATOS_ORG_REGISTRADA).save(flush: true)
        expect:
        service.organizacionesRegistradas.size() == 1
    }
    void 'guardar datos'() {
        given:
        new Organizacion(DATOS_ORG_VERIFICADA).save(flush: true)
        and:
        def command = new OrganizacionCommand().with {
            id                  = 1
            nombre              = 'MunayLab'
            objeto              = 'Hacer de este mundo un mundo mejor.'
            descripcion         = 'Hacer de este mundo un mundo mejor.'
            tipo                = TipoOrganizacion.ASOCIACION_CIVIL
            fechaConstitucion   = new Date() -100
            it
        }
        when:
        def orgActualizada = service.guardar(command)
        then:
        comprobarDatosActualizados(orgActualizada, DATOS_ORG_VERIFICADA)
    }
    void 'guardar direccion'() {
        given:
        def org = new Organizacion(DATOS_ORG_VERIFICADA)
        org.domicilio = new Domicilio(DATOS_DOMICILIO_VALIDOS)
        org.save(flush: true)
        and:
        def domicilioCommand = new DomicilioCommand().with {
            id          = 1
            calle       = 'Reconquista'
            numero      = '1125'
            barrio      = 'Centro'
            localidad   = 'CABA'
            provincia   = 'Buenos Aires'
            it
        }
        def command = new OrganizacionCommand().with {
            id                  = 1
            nombre              = 'MunayLab'
            objeto              = 'Hacer de este mundo un mundo mejor'
            descripcion         = 'Hacer de este mundo un mundo mejor'
            tipo                = TipoOrganizacion.ASOCIACION_CIVIL
            fechaConstitucion   = new Date() -100
            domicilio           = domicilioCommand
            it
        }
        when:
        def orgActualizada = service.guardar(command)
        then:
        comprobarDatosActualizados(orgActualizada, DATOS_ORG_VERIFICADA)
        comprobarDomicilioActualizado(orgActualizada.domicilio, DATOS_DOMICILIO_VALIDOS)
    }

    void comprobarDatosActualizados(org, datos) {
        assert org.tipo != datos.tipo
        assert org.nombre != datos.nombre
        assert org.objeto != datos.objeto
        assert org.fechaConstitucion != datos.fechaConstitucion
    }
    void comprobarDomicilioActualizado(domicilio, datos) {
        assert domicilio.calle != datos.calle
        assert domicilio.numero != datos.numero
        assert domicilio.barrio != datos.barrio
        assert domicilio.localidad != datos.localidad
        assert domicilio.provincia != datos.provincia
    }

    void 'agregar contacto'() {
        given:
        def org = new Organizacion(DATOS_ORG_VERIFICADA).save(flush: true)
        def command = new ContactoCommand(value: 'mcaligares@gmail.com', tipo: TipoContacto.EMAIL)
        when:
        org = service.actualizarContactos(org, command)
        then:
        org.contactos.size() == 1 && Contacto.count() == 1
    }
    void 'eliminar contacto'() {
        given:
        def contacto = new Contacto(value: 'mcaligares@gmail.com', tipo: TipoContacto.EMAIL)
        def org = new Organizacion(DATOS_ORG_VERIFICADA)
                .addToContactos(contacto)
                .save(flush: true)
        when:
        org = service.eliminarContacto(org, new ContactoCommand().with { id = 1; it })
        then:
        Contacto.count() == 0 && org.contactos.size() == 0
        Organizacion.get(1).contactos.size() == 0
    }
    void 'agregar administrador'() {
        given:
        def admin = new TipoUsuario(nombre: 'ADMINISTRADOR').save(flush:true)
        def org = new Organizacion(DATOS_ORG_VERIFICADA).save(flush: true)
        def command = new UserCommand().with {
            nombre      = 'Augusto'
            apellido    = 'Caligares'
            username    = 'mcaligares@gmail.com'
            tipo        = admin.id
            it
        }
        when:
        org = service.actualizarUsuario(org, command)
        then:
        User.count() == 1 && org.admins.size() == 1
        Organizacion.get(1).admins.size() == 1
    }
    void 'eliminar administrador'() {
        given:
        def user = new User(DATOS_USER)
        def admin = new TipoUsuario(nombre: 'ADMINISTRADOR').save(flush:true)
        def org = new Organizacion(DATOS_ORG_VERIFICADA)
        org.addToAdmins(
            new UserOrganizacion(user: user, organizacion: org, tipo: admin)
        ).save(flush: true)
        when:
        org = service.eliminarUsuario(org, new UserCommand().with { id = 1; it })
        then:
        User.count() == 1 && org.admins.size() == 0
        Organizacion.get(1).admins.size() == 0
    }
    void 'agregar miembro'() {
        given:
        new TipoUsuario(nombre: 'ADMINISTRADOR').save(flush:true)
        def miembro = new TipoUsuario(nombre: 'MIEMBRO').save(flush:true)
        def org = new Organizacion(DATOS_ORG_VERIFICADA).save(flush: true)
        def command = new UserCommand().with {
            nombre      = 'Augusto'
            apellido    = 'Caligares'
            username    = 'mcaligares@gmail.com'
            tipo        = miembro.id
            cargo       = 'Director Ejecutivo'
            it
        }
        when:
        org = service.actualizarUsuario(org, command)
        then:
        User.count() == 1 && org.miembros.size() == 1
        Organizacion.get(1).miembros.size() == 1
    }
    void 'eliminar miembro'() {
        given:
        def miembro = new TipoUsuario(nombre: 'MIEMBRO').save(flush:true)
        def user = new User(DATOS_USER).save(flush:true)
        def org = new Organizacion(DATOS_ORG_VERIFICADA).save(flush: true)
        org.addToMiembros(
            new UserOrganizacion(user: user, organizacion: org, tipo: miembro)
        ).save(flush: true)
        when:
        org = service.eliminarUsuario(org, new UserCommand().with { id = 1; it })
        then:
        User.count() == 1 && org.miembros.size() == 0
        Organizacion.get(1).miembros.size() == 0
    }
    void 'agregar voluntario'() {
        given:
        def command = new VoluntarioCommand(DATOS_VOLUNTARIO_VALIDOS)
        def org = new Organizacion(DATOS_ORG_VERIFICADA).save(flush: true)
        when:
        def result = service.actualizarVoluntario(command, org)
        then:
        result.valor.id == 1 && Voluntario.count() == 1
        org.voluntarios.size() == 1 && Organizacion.get(1).voluntarios.size() == 1
    }
    void 'agregar voluntario con errores'() {
        given:
        def command = new VoluntarioCommand(DATOS_VOLUNTARIO_INVALIDOS)
        def org = new Organizacion(DATOS_ORG_VERIFICADA).save(flush: true)
        expect:
        service.actualizarVoluntario(command, org).errores != null
    }
    void 'agregar voluntario con domicilio'() {
        given:
        def org = new Organizacion(DATOS_ORG_VERIFICADA).save(flush: true)
        def domicilioCommand = new DomicilioCommand().with {
            calle       = 'Reconquista'
            numero      = '1125'
            barrio      = 'Centro'
            localidad   = 'CABA'
            provincia   = 'Buenos Aires'
            it
        }
        def command = new VoluntarioCommand(DATOS_VOLUNTARIO_VALIDOS).with {
            domicilio = domicilioCommand
            it
        }
        expect:
        service.actualizarVoluntario(command, org).valor.id == 1
    }
    void 'modificar voluntario'() {
        given:
        def command = new VoluntarioCommand(DATOS_VOLUNTARIO_VALIDOS)
        def org = new Organizacion(DATOS_ORG_VERIFICADA).save(flush: true)
        service.actualizarVoluntario(command, org)
        and:
        def commandUpdated = new VoluntarioCommand(DATOS_VOLUNTARIO_VALIDOS).with {
            id      = 1
            nombre  = 'Augusto'
            it
        }
        when:
        def result = service.actualizarVoluntario(commandUpdated, org)
        then:
        result.valor.id == 1
        Voluntario.get(1).nombre == 'Augusto'
    }
}
