package org.munaylab.factory

import org.munaylab.contacto.*

class ContactoBuilder {
    Contacto contacto = new Contacto()

    ContactoCommandBuilder getCommand() {
        new ContactoCommandBuilder()
    }

    ContactoBuilder conValue(value) {
        contacto.value = value
        this
    }
    ContactoBuilder deTipo(value) {
        contacto.tipo = value
        this
    }
    ContactoBuilder conWeb(value) {
        contacto.value = value
        contacto.tipo = TipoContacto.WEB
        this
    }
    ContactoBuilder conEmail(value) {
        contacto.value = value
        contacto.tipo = TipoContacto.EMAIL
        this
    }
    ContactoBuilder conTelefono(value) {
        contacto.value = value
        contacto.tipo = TipoContacto.TELEFONO
        this
    }
    ContactoBuilder conCelular(value) {
        contacto.value = value
        contacto.tipo = TipoContacto.CELULAR
        this
    }
    ContactoBuilder conDatos(properties) {
        contacto.properties = properties
        this
    }
    Contacto getCrear() {
        contacto
    }
}

class ContactoCommandBuilder {
    ContactoCommand command = new ContactoCommand()

    ContactoCommandBuilder conId(value) {
        command.id = value
        this
    }
    ContactoCommandBuilder conValue(value) {
        command.value = value
        this
    }
    ContactoCommandBuilder deTipo(value) {
        command.tipo = value
        this
    }
    ContactoCommandBuilder conEmail(value) {
        command.value = value
        command.tipo = TipoContacto.EMAIL
        this
    }
    ContactoCommandBuilder conDatos(properties) {
        command = new ContactoCommand(properties)
        this
    }
    ContactoCommand getCrear() {
        command
    }
}
