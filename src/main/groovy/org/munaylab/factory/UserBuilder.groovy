package org.munaylab.factory

import org.munaylab.categoria.TipoUsuario
import org.munaylab.user.*

class UserBuilder {
    User user = new User()

    UserCommandBuilder getCommand() {
        new UserCommandBuilder()
    }

    TipoUsuario getAdministrador() {
        new TipoUsuario(nombre: 'ADMINISTRADOR')
    }
    TipoUsuario getMiembro() {
        new TipoUsuario(nombre: 'MIEMBRO')
    }

    UserBuilder conNombre(value) {
        user.nombre = value
        this
    }
    UserBuilder conApellido(value) {
        user.apellido = value
        this
    }
    UserBuilder conUsername(value) {
        user.username = value
        this
    }
    UserBuilder conPassword(value) {
        user.password = value
        this
    }
    UserBuilder conDatos(properties) {
        user.properties = properties
        this
    }
    User getCrear() {
        user
    }
}

class UserCommandBuilder {
    UserCommand command = new UserCommand()

    UserCommandBuilder conId(value) {
        command.id = value
        this
    }
    UserCommandBuilder conNombre(value) {
        command.nombre = value
        this
    }
    UserCommandBuilder conApellido(value) {
        command.apellido = value
        this
    }
    UserCommandBuilder conUsername(value) {
        command.username = value
        this
    }
    UserCommandBuilder deTipo(TipoUsuario value) {
        command.tipo = value.id
        this
    }
    UserCommandBuilder conCargo(value) {
        command.cargo = value
        this
    }
    UserCommandBuilder conDatos(properties) {
        command = new UserCommand(properties)
        this
    }
    UserCommand getCrear() {
        command
    }
}
