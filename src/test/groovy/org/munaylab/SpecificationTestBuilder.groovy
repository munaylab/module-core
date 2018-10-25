package org.munaylab

// import org.munaylab.contacto.Contacto
// import org.munaylab.direccion.Domicilio
// import org.munaylab.direccion.DomicilioCommand
// import org.munaylab.contacto.Contacto
// import org.munaylab.contacto.ContactoCommand
// import org.munaylab.contacto.TipoContacto
import org.munaylab.categoria.TipoUsuario
import org.munaylab.osc.Organizacion
// import org.munaylab.osc.OrganizacionCommand
// import org.munaylab.osc.UserOrganizacion
import org.munaylab.osc.RegistroCommand
// import org.munaylab.osc.Voluntario
import org.munaylab.osc.EstadoOrganizacion
// import org.munaylab.osc.TipoOrganizacion
// import org.munaylab.osc.Voluntario
// import org.munaylab.osc.VoluntarioCommand
import org.munaylab.user.User
// import org.munaylab.user.UserCommand
// import org.munaylab.security.Token
import org.munaylab.security.Role
import org.munaylab.security.UserRole
// import org.munaylab.security.ConfirmacionCommand

import spock.lang.Specification

class SpecificationTestBuilder extends Specification implements TestDataSample {

    protected RegistroCommand getCommandDeRegistroInvalido() {
        return new RegistroCommand(DATOS_DE_REGISTRO_COMMAND_INVALIDOS)
    }

    protected RegistroCommand getCommandDeRegistroValido() {
        return new RegistroCommand(DATOS_DE_REGISTRO_COMMAND_VALIDOS)
    }

    protected void comprobarQueLosDatosDelUsuarioSonLosMismosQueDelCommand(User user, RegistroCommand command) {
        assert user.username == command.email
        assert user.nombre == command.nombre
        assert user.apellido == command.apellido
        assert user.password && user.password.size() == 36
        assert user.accountLocked
        assert user.contactos.size() == 2
        assert user.contactos.first().value == command.email
        assert user.contactos.last().value == command.telefono
    }

    protected void comprobarQueLosDatosDeLaOrganizacionSonLosMismosQueDelCommand(Organizacion org, RegistroCommand command) {
        assert org.nombre == command.denominacion
        assert org.objeto == command.objeto
        assert org.descripcion == command.descripcion
        assert org.nombreURL == command.nombreURL
        assert org.tipo == command.tipo
        assert org.estado == EstadoOrganizacion.PENDIENTE
    }

    protected User getUsuarioValido() {
        return new User(DATOS_DE_USUARIO_VALIDOS)
    }

    protected Organizacion getOrganizacionValida() {
        return new Organizacion(DATOS_DE_ORGANIZACION_VALIDOS)
    }

    protected TipoUsuario getTipoDeUsuarioAdministrador() {
        return new TipoUsuario(nombre: 'ADMINISTRADOR')
    }

    protected void comprobarQueLaOrganizacionTieneAsignadoUnAdministrador(Organizacion org, User user) {
        assert org.admins.size() == 1
        assert org.admins.first().user == user
        assert org.admins.first().organizacion == org
        assert org.admins.first().tipo.nombre == tipoDeUsuarioAdministrador.nombre
    }

    protected Role getRolDeAdministradorDeOSC() {
        return new Role(authority: 'ROLE_OSC_ADMIN')
    }

    protected void comprobarQueLosDatosDelRolSonCorrectos(UserRole userRol, User user) {
        assert userRol.user == user
        assert userRol.role.authority == 'ROLE_OSC_ADMIN'
    }

    protected void comprobarQueElRegistroSeRealizoCorrectamente(Organizacion org, RegistroCommand command) {
        User user = org.admins.first().user
        comprobarQueLosDatosDelUsuarioSonLosMismosQueDelCommand(user, command)
        comprobarQueLosDatosDeLaOrganizacionSonLosMismosQueDelCommand(org, command)
        comprobarQueLaOrganizacionTieneAsignadoUnAdministrador(org, user)

        assert org != null && org.id != null
        assert User.count() == 1
        assert UserRole.count() == 1
        assert Organizacion.count() == 1
    }

    protected void comprobarQueElRegistroNoSeRealizo(Organizacion org) {
        assert org != null
        assert org.hasErrors()

        assert User.count() == 0
        assert UserRole.count() == 0
        assert Organizacion.count() == 0
    }
}
