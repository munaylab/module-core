package org.munaylab.factory

import org.munaylab.balance.*

class AsientoBuilder {
    Asiento asiento = new Asiento()

    AsientoCommandBuilder getCommand() { new AsientoCommandBuilder() }
    CategoriaCommandBuilder getCategoriaCommand() { new CategoriaCommandBuilder() }

    AsientoBuilder conMonto(value) {
        asiento.monto = value
        this
    }
    AsientoBuilder conDetalle(value) {
        asiento.detalle = value
        this
    }
    AsientoBuilder conFecha(value) {
        asiento.fecha = value
        this
    }
    AsientoBuilder conCategoria(value) {
        asiento.categoria = value
        this
    }
    AsientoBuilder deTipo(value) {
        asiento.tipo = value
        this
    }
    AsientoBuilder esUnIngreso() {
        asiento.tipo = TipoAsiento.INGRESO
        this
    }
    AsientoBuilder esUnEgreso() {
        asiento.tipo = TipoAsiento.EGRESO
        this
    }
    AsientoBuilder conOrganizacion(value) {
        asiento.organizacion = value
        this
    }
    AsientoBuilder enabled() {
        asiento.enabled = Boolean.TRUE
        this
    }
    AsientoBuilder conDatos(properties) {
        asiento.properties = properties
        this
    }
    Asiento getCrear() {
        asiento
    }
    Categoria crearCategoriaIngreso(nombre) {
        new Categoria(nombre: nombre, tipo: TipoAsiento.INGRESO)
    }
    Categoria crearCategoriaEgreso(nombre) {
        new Categoria(nombre: nombre, tipo: TipoAsiento.EGRESO)
    }
}

class AsientoCommandBuilder {
    AsientoCommand command = new AsientoCommand()

    AsientoCommandBuilder conId(value) {
        command.id = value
        this
    }
    AsientoCommandBuilder conMonto(value) {
        command.monto = value
        this
    }
    AsientoCommandBuilder conFecha(value) {
        command.fecha = value
        this
    }
    AsientoCommandBuilder conDetalle(value) {
        command.detalle = value
        this
    }
    AsientoCommandBuilder esUnIngreso(value) {
        command.esIngreso = value
        this
    }
    AsientoCommandBuilder conOrgId(value) {
        command.orgId = value
        this
    }
    AsientoCommandBuilder conCategoria(value) {
        command.categoria = value
        this
    }
    AsientoCommandBuilder conDatos(properties) {
        command = new AsientoCommand(properties)
        this
    }
    AsientoCommand getCrear() {
        command
    }
}

class CategoriaCommandBuilder {
    CategoriaCommand crearCategoriaEgresoConId(id) {
        new CategoriaCommand(id: id, tipo: TipoAsiento.EGRESO)
    }
    CategoriaCommand crearCategoriaEgreso(nombre, detalle) {
        new CategoriaCommand(nombre: nombre, detalle: detalle, tipo: TipoAsiento.EGRESO)
    }
    CategoriaCommand crearCategoriaIngresoConId(id) {
        new CategoriaCommand(id: id, tipo: TipoAsiento.INGRESO)
    }
    CategoriaCommand crearCategoriaIngreso(nombre, detalle) {
        new CategoriaCommand(nombre: nombre, detalle: detalle, tipo: TipoAsiento.INGRESO)
    }
}
