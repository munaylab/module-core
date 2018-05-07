package org.munaylab.factory

import org.munaylab.osc.*

class VoluntarioBuilder {
    Voluntario voluntario = new Voluntario()

    VoluntarioCommandBuilder getCommand() {
        new VoluntarioCommandBuilder()
    }
}

class VoluntarioCommandBuilder {
    VoluntarioCommand command = new VoluntarioCommand()

    VoluntarioCommandBuilder conId(value) {
        command.id = value
        this
    }
    VoluntarioCommandBuilder conOrgId(value) {
        command.orgId = value
        this
    }
    VoluntarioCommandBuilder conEmail(value) {
        command.email = value
        this
    }
    VoluntarioCommandBuilder conNombre(value) {
        command.nombre = value
        this
    }
    VoluntarioCommandBuilder conApellido(value) {
        command.apellido = value
        this
    }
    VoluntarioCommandBuilder conNacimiento(value) {
        command.nacimiento = value
        this
    }
    VoluntarioCommandBuilder conDomicilio(value) {
        command.domicilio = value
        this
    }
    VoluntarioCommandBuilder conTipoUsuario(value) {
        command.tipoUsuarioId = value.id
        this
    }
    VoluntarioCommandBuilder conTipoUsuarioNombre(value) {
        command.tipoUsuarioNombre = value
        this
    }
    VoluntarioCommandBuilder conDatos(properties) {
        command = new VoluntarioCommand(properties)
        this
    }
    VoluntarioCommand getCrear() {
        command
    }
}
