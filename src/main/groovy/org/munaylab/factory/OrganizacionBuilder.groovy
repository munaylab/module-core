package org.munaylab.factory

import org.munaylab.osc.*
import org.munaylab.direccion.Domicilio
import org.munaylab.security.ConfirmacionCommand

class OrganizacionBuilder {
    Organizacion org = new Organizacion()

    UserOrganizacionBuilder getUserOrganizacion() {
        new UserOrganizacionBuilder()
    }
    OrganizacionCommandBuilder getCommand() {
        new OrganizacionCommandBuilder()
    }
    RegistroCommandBuilder getRegistroCommand() {
        new RegistroCommandBuilder()
    }
    ConfirmacionCommandBuilder getConfirmacionCommand() {
        new ConfirmacionCommandBuilder()
    }

    OrganizacionBuilder conNombre(String value) {
        org.nombre = value
        this
    }
    OrganizacionBuilder conObjeto(String value) {
        org.objeto = value
        this
    }
    OrganizacionBuilder conNombreUrl(String value) {
        org.nombreURL = value
        this
    }
    OrganizacionBuilder conFechaConstitucion(String value) {
        org.fechaConstitucion = value
        this
    }
    OrganizacionBuilder deTipo(TipoOrganizacion value) {
        org.tipo = value
        this
    }
    OrganizacionBuilder conEstado(EstadoOrganizacion value) {
        org.estado = value
        this
    }
    OrganizacionBuilder conDomicilio(Domicilio value) {
        org.domicilio = value
        this
    }
    OrganizacionBuilder conDatos(properties) {
        org.properties = properties
        this
    }
    Organizacion getCrear() {
        org
    }

}

class UserOrganizacionBuilder {
    UserOrganizacion userOrg = new UserOrganizacion()

    UserOrganizacionBuilder conUser(value) {
        userOrg.user = value
        this
    }
    UserOrganizacionBuilder conOrganizacion(value) {
        userOrg.organizacion = value
        this
    }
    UserOrganizacionBuilder conCargo(value) {
        userOrg.cargo = value
        this
    }
    UserOrganizacionBuilder deTipo(value) {
        userOrg.tipo = value
        this
    }
    UserOrganizacion getCrear() {
        userOrg
    }
}

class OrganizacionCommandBuilder {
    OrganizacionCommand command = new OrganizacionCommand()

    OrganizacionCommandBuilder conId(Long value) {
        command.id = value
        this
    }
    OrganizacionCommandBuilder conNombre(String value) {
        command.nombre = value
        this
    }
    OrganizacionCommandBuilder conObjeto(String value) {
        command.objeto = value
        this
    }
    OrganizacionCommandBuilder deTipo(TipoOrganizacion value) {
        command.tipo = value
        this
    }
    OrganizacionCommandBuilder conFechaConstitucion(Date value) {
        command.fechaConstitucion = value
        this
    }
    OrganizacionCommandBuilder conDomicilio(value) {
        command.domicilio = value
        this
    }
    OrganizacionCommand getCrear() {
        command
    }
}

class RegistroCommandBuilder {
    RegistroCommand command = new RegistroCommand()

    RegistroCommandBuilder conDenominacion(String value) {
        command.denominacion = value
        this
    }
    RegistroCommandBuilder conObjeto(String value) {
        command.objeto = value
        this
    }
    RegistroCommandBuilder deTipo(TipoOrganizacion value) {
        command.tipo = value
        this
    }
    RegistroCommandBuilder conNombre(String value) {
        command.nombre = value
        this
    }
    RegistroCommandBuilder conApellido(String value) {
        command.apellido = value
        this
    }
    RegistroCommandBuilder conEmail(String value) {
        command.email = value
        this
    }
    RegistroCommandBuilder conTelefono(String value) {
        command.telefono = value
        this
    }
    RegistroCommandBuilder conRepresentante(String nombre, String apellido,
            String email, String telefono) {
        command.nombre = nombre
        command.apellido = apellido
        command.email = email
        command.telefono = telefono
        this
    }
    RegistroCommandBuilder conDatos(properties) {
        command = new RegistroCommand(properties)
        this
    }
    RegistroCommand getCrear() {
        command
    }
}

class ConfirmacionCommandBuilder {
    ConfirmacionCommand command =  new ConfirmacionCommand()

    ConfirmacionCommandBuilder conCodigo(String value) {
        command.codigo = value
        this
    }
    ConfirmacionCommandBuilder conPassword(String value) {
        command.password1 = value
        this
    }
    ConfirmacionCommandBuilder conPasswordConfirmada(String value) {
        command.password2 = value
        this
    }
    ConfirmacionCommandBuilder conAmbasPassword(String password) {
        command.password1 = password
        command.password2 = password
        this
    }
    ConfirmacionCommandBuilder conDatos(properties) {
        command = new ConfirmacionCommand(properties)
        this
    }
    ConfirmacionCommand getCrear() {
        command
    }
}
