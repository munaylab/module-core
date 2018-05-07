package org.munaylab.factory

import org.munaylab.direccion.*

class DomicilioBuilder {
    Domicilio domicilio = new Domicilio()

    DomicilioCommandBuilder getCommand() {
        new DomicilioCommandBuilder()
    }

    DomicilioBuilder conCalle(value) {
        domicilio.calle = value
        this
    }
    DomicilioBuilder conNumero(value) {
        domicilio.numero = value
        this
    }
    DomicilioBuilder conPiso(value) {
        domicilio.piso = value
        this
    }
    DomicilioBuilder conDepartamento(value) {
        domicilio.departamento = value
        this
    }
    DomicilioBuilder conDistrito(value) {
        domicilio.distrito = value
        this
    }
    DomicilioBuilder conLocalidad(value) {
        domicilio.localidad = value
        this
    }
    DomicilioBuilder conProvincia(value) {
        domicilio.provincia = value
        this
    }
    DomicilioBuilder conPais(value) {
        domicilio.pais = value
        this
    }
    DomicilioBuilder conLatitudPos(value) {
        domicilio.latitudPos = value
        this
    }
    DomicilioBuilder conLongitudPos(value) {
        domicilio.longitudPos = value
        this
    }
    DomicilioBuilder conDatos(properties) {
        domicilio.properties = properties
        this
    }
    Domicilio crear() {
        domicilio
    }
}

class DomicilioCommandBuilder {
    DomicilioCommand command = new DomicilioCommand()

    DomicilioCommandBuilder conId(Long value) {
        command.id = value
        this
    }
    DomicilioCommandBuilder conCalle(String value) {
        command.calle = value
        this
    }
    DomicilioCommandBuilder conNumero(String value) {
        command.numero = value
        this
    }
    DomicilioCommandBuilder conBarrio(String value) {
        command.barrio = value
        this
    }
    DomicilioCommandBuilder conLocalidad(String value) {
        command.localidad = value
        this
    }
    DomicilioCommandBuilder conProvincia(String value) {
        command.provincia = value
        this
    }
    DomicilioCommandBuilder conPais(String value) {
        command.pais = value
        this
    }
    DomicilioCommandBuilder conDatos(properties) {
        command = new DomicilioCommand(properties)
        this
    }
    DomicilioCommand crear() {
        command
    }
}
