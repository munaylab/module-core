package org.munaylab

import org.munaylab.contacto.Contacto
import org.munaylab.factory.Builder
import org.munaylab.osc.Organizacion
import org.munaylab.osc.Voluntario
import org.munaylab.osc.EstadoOrganizacion
import org.munaylab.osc.TipoOrganizacion
import org.munaylab.user.User
import org.munaylab.security.Token
import org.munaylab.security.UserRole

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

        registroCommandValido = Builder.organizacion.registroCommand
                .conDatos(DATOS_REGISTRO_VALIDOS).crear()
        registroCommandInvalido = Builder.organizacion.registroCommand
                .conDatos(DATOS_REGISTRO_INVALIDOS).crear()

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
        def confirmacionCommand = Builder.organizacion.confirmacionCommand
                .conCodigo('codigo').conAmbasPassword('asdQWE123').crear()
        when:
        service.confirmar(confirmacionCommand, User.get(1))
        then:
        Organizacion.countByEstado(EstadoOrganizacion.REGISTRADA) == 1
    }
    void 'confirmar un registro invalido'() {
        given:
        registrarUnaOrganizacionConDatos(registroCommandValido)
        and:
        def confirmacionCommand = Builder.organizacion.confirmacionCommand
                .conCodigo('codigo').conAmbasPassword('asdQWE123').crear()
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
        Builder.organizacion.conDatos(DATOS_ORG_VALIDOS)
                .conEstado(EstadoOrganizacion.REGISTRADA)
                .crear().save(flush: true)
        expect:
        service.organizacionesRegistradas.size() == 1
    }
    void 'guardar datos'() {
        given:
        Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear().save(flush: true)
        and:
        def command = Builder.organizacion.command.conId(1)
                .conNombre('MunayLab')
                .conObjeto('Hacer de este mundo un mundo mejor.')
                .deTipo(TipoOrganizacion.ASOCIACION_CIVIL)
                .conFechaConstitucion(new Date() -100)
                .crear()
        when:
        def orgActualizada = service.guardar(command)
        then:
        comprobarDatosActualizados(orgActualizada, DATOS_ORG_VERIFICADA)
    }
    void 'guardar direccion'() {
        given:
        Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA)
                .conDomicilio(Builder.domicilio.conDatos(DATOS_DOMICILIO_VALIDOS).crear())
                .crear().save(flush: true)
        and:
        def domicilioCommand = Builder.domicilio.command
                .conId(1)
                .conCalle('Reconquista')
                .conNumero('1125')
                .conBarrio('Centro')
                .conLocalidad('CABA')
                .conProvincia('Buenos Aires')
                .crear()
        def command = Builder.organizacion.command.conId(1)
                .conNombre('MunayLab')
                .conObjeto('Hacer de este mundo un mundo mejor.')
                .deTipo(TipoOrganizacion.ASOCIACION_CIVIL)
                .conFechaConstitucion(new Date() -100)
                .conDomicilio(domicilioCommand)
                .crear()
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
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear().save(flush: true)
        def command = Builder.contacto.command.conEmail('mcaligares@gmail.com').crear()
        when:
        org = service.actualizarContactos(org, command)
        then:
        org.contactos.size() == 1 && Contacto.count() == 1
    }
    void 'eliminar contacto'() {
        given:
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear()
                .addToContactos(Builder.contacto.conEmail('mcaligares@gmail.com').crear())
                .save(flush: true)
        when:
        org = service.eliminarContacto(org, Builder.contacto.command.conId(1).crear())
        then:
        Contacto.count() == 0 && org.contactos.size() == 0
        Organizacion.get(1).contactos.size() == 0
    }
    void 'agregar administrador'() {
        given:
        def admin = Builder.user.administrador.save(flush:true)
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear().save(flush: true)
        def command = Builder.user.command
                .conNombre('Augusto')
                .conApellido('Caligares')
                .conUsername('mcaligares@gmail.com')
                .deTipo(admin)
                .crear
        when:
        org = service.actualizarUsuario(org, command)
        then:
        User.count() == 1 && org.admins.size() == 1
        Organizacion.get(1).admins.size() == 1
    }
    void 'eliminar administrador'() {
        given:
        def user = Builder.user.conDatos(DATOS_USER).crear
        def admin = Builder.user.administrador.save(flush:true)
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear()
        org.addToAdmins(
                Builder.organizacion.userOrganizacion
                        .conUser(user)
                        .conOrganizacion(org)
                        .deTipo(admin)
                        .crear
                )
            .save(flush: true)
        when:
        org = service.eliminarUsuario(org, Builder.user.command.conId(1).crear)
        then:
        User.count() == 1 && org.admins.size() == 0
        Organizacion.get(1).admins.size() == 0
    }
    void 'agregar miembro'() {
        given:
        Builder.user.administrador.save(flush:true)
        def miembro = Builder.user.miembro.save(flush:true)
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear().save(flush: true)
        def command = Builder.user.command
                .conNombre('Augusto')
                .conApellido('Caligares')
                .conUsername('mcaligares@gmail.com')
                .deTipo(miembro)
                .conCargo('Director Ejecutivo')
                .crear
        when:
        org = service.actualizarUsuario(org, command)
        then:
        User.count() == 1 && org.miembros.size() == 1
        Organizacion.get(1).miembros.size() == 1
    }
    void 'eliminar miembro'() {
        given:
        def miembro = Builder.user.miembro.save(flush:true)
        def user = Builder.user.conDatos(DATOS_USER).crear.save(flush:true)
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear().save(flush: true)
        org.addToMiembros(
                Builder.organizacion.userOrganizacion
                        .conUser(user)
                        .conOrganizacion(org)
                        .deTipo(miembro)
                        .crear
                )
            .save(flush: true)
        when:
        org = service.eliminarUsuario(org, Builder.user.command.conId(1).crear)
        then:
        User.count() == 1 && org.miembros.size() == 0
        Organizacion.get(1).miembros.size() == 0
    }
    void 'agregar voluntario'() {
        given:
        def command = Builder.voluntario.command.conDatos(DATOS_VOLUNTARIO_VALIDOS).crear
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear().save(flush: true)
        when:
        def result = service.actualizarVoluntario(command, org)
        then:
        result.valor.id == 1 && Voluntario.count() == 1
        org.voluntarios.size() == 1 && Organizacion.get(1).voluntarios.size() == 1
    }
    void 'agregar voluntario con errores'() {
        given:
        def command = Builder.voluntario.command.conDatos(DATOS_VOLUNTARIO_INVALIDOS).crear
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear().save(flush: true)
        expect:
        service.actualizarVoluntario(command, org).errores != null
    }
    void 'agregar voluntario con domicilio'() {
        given:
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear().save(flush: true)
        def command = Builder.voluntario.command
                .conDatos(DATOS_VOLUNTARIO_VALIDOS)
                .conDomicilio(
                    Builder.domicilio.command
                        .conCalle('Reconquista')
                        .conNumero('1125')
                        .conBarrio('Centro')
                        .conLocalidad('CABA')
                        .conProvincia('Buenos Aires')
                        .crear()
                    )
                .crear
        expect:
        service.actualizarVoluntario(command, org).valor.id == 1
    }
    void 'modificar voluntario'() {
        given:
        def command = Builder.voluntario.command.conDatos(DATOS_VOLUNTARIO_VALIDOS).crear
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear().save(flush: true)
        service.actualizarVoluntario(command, org)
        and:
        def commandUpdated = Builder.voluntario.command
                .conDatos(DATOS_VOLUNTARIO_VALIDOS)
                .conId(1)
                .conNombre('Augusto')
                .crear
        when:
        def result = service.actualizarVoluntario(commandUpdated, org)
        then:
        result.valor.id == 1
        Voluntario.get(1).nombre == 'Augusto'
    }
}
