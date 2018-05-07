package org.munaylab

import org.munaylab.gamification.HistorialPuntaje
import org.munaylab.gamification.Puntaje
import org.munaylab.osc.Organizacion
import org.munaylab.osc.TipoOrganizacion
import org.munaylab.user.User

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class GamificationServiceSpec extends Specification
        implements ServiceUnitTest<GamificationService>, DataTest {

    void setupSpec() {
        mockDomains Organizacion, User, Puntaje
    }

    void setup() {
        Builder.crearOrganizacionCompleta().save(flush: true)
    }

    void 'sumar puntos por completar perfil'() {
        given: 'org con todos los datos de perfil'
        def org = Organizacion.get(1)
        when:
        service.operarPuntosPerfil(org)
        then: 'se crea un puntaje y un historial de puntos de la org'
        Puntaje.count() == 1 && HistorialPuntaje.count() == 1
        Puntaje.findByOrganizacion(org).total == 10
    }
    void 'tratar de sumar puntos por completar perfil otra vez'() {
        given: 'org con todos los datos de perfil'
        def org = Organizacion.get(1)
        and: 'se suma puntos una primera vez'
        service.operarPuntosPerfil(org)
        when: 'se trata de volver a sumar puntos'
        service.operarPuntosPerfil(org)
        then: 'se crea solamente un puntaje y un historial de puntos de la org'
        Puntaje.count() == 1 && HistorialPuntaje.count() == 1
        Puntaje.findByOrganizacion(org).total == 10
    }
    void "tratar de sumar puntos con perfil incompleto"() {
        given: 'org con todos los datos de perfil'
        def org = Organizacion.get(1)
        and: 'cambiamos los datos para dejar un perfil incompleto'
        cambiarDatosPerfil(org, tipo, nombre, objeto, domicilio, fechaConstitucion, contactos)
        when:
        service.operarPuntosPerfil(org)
        then: 'no se crea puntaje ni historial'
        Puntaje.count() == 0 && HistorialPuntaje.count() == 0
        Puntaje.findByOrganizacion(org) == null
        where: 'datos de perfil para cambiar (al menos uno debe ser nulo)'
        tipo  | nombre | objeto | domicilio | fechaConstitucion | contactos
        false | true   | true   | true      | true              | true
        true  | false  | true   | true      | true              | true
        true  | true   | false  | true      | true              | true
        true  | true   | true   | false     | true              | true
        true  | true   | true   | true      | false             | true
        true  | true   | true   | true      | true              | false
    }
    void 'restar puntos por modificar datos de perfil'() {
        given: 'org con todos los datos de perfil'
        def org = Organizacion.get(1)
        and: 'se suma puntos por completar perfil'
        service.operarPuntosPerfil(org)
        and: 'cambiamos los datos para dejar un perfil incompleto'
        cambiarDatosPerfil(org, tipo, nombre, objeto, domicilio, fechaConstitucion, contactos)
        when: 'se trata de volver a sumar puntos'
        service.operarPuntosPerfil(org)
        then: 'el puntaje debe ser de 0 y el historial deshabilitado'
        Puntaje.count() == 1 && HistorialPuntaje.count() == 1
        Puntaje.findByOrganizacion(org).total == 0
        HistorialPuntaje.countByEnabled(false) == 1
        where: 'datos de perfil para cambiar'
        tipo  | nombre | objeto | domicilio | fechaConstitucion | contactos
        true  | true   | true   | true      | false             | true
    }
    def cambiarDatosPerfil(org, tipo, nombre, objeto, domicilio, fechaConstitucion, contactos) {
        if (!tipo) org.tipo = null
        if (!nombre) org.nombre = null
        if (!objeto) org.objeto = null
        if (!domicilio) org.domicilio = null
        if (!fechaConstitucion) org.fechaConstitucion = null
        if (!contactos) {
            def contacto = org.contactos.first()
            org.removeFromContactos(contacto)
            contacto.delete()
            org.contactos.clear()
        }
        org.save(flush: true)
    }
    void 'sumar puntos por publicar programa'() {
        given: 'org y programa con todos los datos'
        def org = Organizacion.get(1)
        def programa = org.programas.first()
        // 1 * service.sumarSiNoTienePuntos(_,_,_)
        when:
        service.operarPuntosPrograma(programa)
        then: 'se crea un puntaje y un historial de puntos de la org'
        Puntaje.count() == 1 && HistorialPuntaje.count() == 1
        Puntaje.findByOrganizacion(org).total == 3
    }
    void 'tratar de sumar puntos por publicar programa otra vez'() {
        given: 'org y programa con todos los datos'
        def org = Organizacion.get(1)
        def programa = org.programas.first()
        and: 'se suma puntos una primera vez'
        service.operarPuntosPrograma(programa)
        when: 'se trata de volver a sumar puntos'
        service.operarPuntosPrograma(programa)
        then: 'se crea solamente un puntaje y un historial de puntos de la org'
        Puntaje.count() == 1 && HistorialPuntaje.count() == 1
        Puntaje.findByOrganizacion(org).total == 3
    }
    void "tratar de sumar puntos con programa incompleto"() {
        given: 'org y programa con todos los datos'
        def org = Organizacion.get(1)
        def programa = org.programas.first()
        and: 'cambiamos los datos para dejar un programa incompleto'
        cambiarDatosPrograma(programa, nombre, descripcion, publicado)
        when:
        service.operarPuntosPrograma(programa)
        then: 'no se crea puntaje ni historial'
        Puntaje.count() == 0 && HistorialPuntaje.count() == 0
        Puntaje.findByOrganizacion(org) == null
        where: 'datos de programa para cambiar (al menos uno debe ser nulo)'
        nombre | descripcion | publicado
        false  | true        | true
        true   | false       | true
        true   | true        | false
    }
    void 'restar puntos por modificar datos de programa'() {
        given: 'org y programa con todos los datos'
        def org = Organizacion.get(1)
        def programa = org.programas.first()
        and: 'se suma puntos por completar programa'
        service.operarPuntosPrograma(programa)
        and: 'cambiamos los datos para dejar un programa incompleto'
        cambiarDatosPrograma(programa, nombre, descripcion, publicado)
        when: 'se trata de volver a sumar puntos'
        service.operarPuntosPrograma(programa)
        then: 'el puntaje debe ser de 0 y el historial deshabilitado'
        Puntaje.count() == 1 && HistorialPuntaje.count() == 1
        Puntaje.findByOrganizacion(org).total == 0
        HistorialPuntaje.countByEnabled(false) == 1
        where: 'datos de programa para cambiar'
        nombre | descripcion | publicado
        false  | true        | true
    }
    def cambiarDatosPrograma(programa, nombre, descripcion, publicado) {
        if (!nombre) programa.nombre = null
        if (!descripcion) programa.descripcion = null
        if (!publicado) programa.publicado = false
        programa.save(flush: true)
    }
    void 'sumar puntos por publicar proyecto'() {
        given: 'org y proyecto con todos los datos'
        def org = Organizacion.get(1)
        def proyecto = org.programas.first().proyectos.first()
        // 1 * service.sumarSiNoTienePuntos(_,_,_)
        when:
        service.operarPuntosProyecto(proyecto)
        then: 'se crea un puntaje y un historial de puntos de la org'
        Puntaje.count() == 1 && HistorialPuntaje.count() == 1
        Puntaje.findByOrganizacion(org).total == 2
    }
    void 'tratar de sumar puntos por publicar proyecto otra vez'() {
        given: 'org y proyecto con todos los datos'
        def org = Organizacion.get(1)
        def proyecto = org.programas.first().proyectos.first()
        and: 'se suma puntos una primera vez'
        service.operarPuntosProyecto(proyecto)
        when: 'se trata de volver a sumar puntos'
        service.operarPuntosProyecto(proyecto)
        then: 'se crea solamente un puntaje y un historial de puntos de la org'
        Puntaje.count() == 1 && HistorialPuntaje.count() == 1
        Puntaje.findByOrganizacion(org).total == 2
    }
    void "tratar de sumar puntos con proyecto incompleto"() {
        given: 'org y proyecto con todos los datos'
        def org = Organizacion.get(1)
        def proyecto = org.programas.first().proyectos.first()
        and: 'cambiamos los datos para dejar un proyecto incompleto'
        cambiarDatosProyecto(proyecto, nombre, descripcion, publicado)
        when:
        service.operarPuntosProyecto(proyecto)
        then: 'no se crea puntaje ni historial'
        Puntaje.count() == 0 && HistorialPuntaje.count() == 0
        Puntaje.findByOrganizacion(org) == null
        where: 'datos de proyecto para cambiar (al menos uno debe ser nulo)'
        nombre | descripcion | publicado
        false  | true        | true
        true   | false       | true
        true   | true        | false
    }
    void 'restar puntos por modificar datos de proyecto'() {
        given: 'org y proyecto con todos los datos'
        def org = Organizacion.get(1)
        def proyecto = org.programas.first().proyectos.first()
        and: 'se suma puntos por completar proyecto'
        service.operarPuntosProyecto(proyecto)
        and: 'cambiamos los datos para dejar un proyecto incompleto'
        cambiarDatosProyecto(proyecto, nombre, descripcion, publicado)
        when: 'se trata de volver a sumar puntos'
        service.operarPuntosProyecto(proyecto)
        then: 'el puntaje debe ser de 0 y el historial deshabilitado'
        Puntaje.count() == 1 && HistorialPuntaje.count() == 1
        Puntaje.findByOrganizacion(org).total == 0
        HistorialPuntaje.countByEnabled(false) == 1
        where: 'datos de proyecto para cambiar'
        nombre | descripcion | publicado
        false  | true        | true
    }
    def cambiarDatosProyecto(proyecto, nombre, descripcion, publicado) {
        if (!nombre) proyecto.nombre = null
        if (!descripcion) proyecto.descripcion = null
        if (!publicado) proyecto.publicado = false
        proyecto.save(flush: true)
    }

    void 'sumar puntos por publicar actividad'() {
        given: 'org y actividad con todos los datos'
        def org = Organizacion.get(1)
        def actividad = org.programas.first().proyectos.first().actividades.first()
        // 1 * service.sumarSiNoTienePuntos(_,_,_)
        when:
        service.operarPuntosActividad(actividad)
        then: 'se crea un puntaje y un historial de puntos de la org'
        Puntaje.count() == 1 && HistorialPuntaje.count() == 1
        Puntaje.findByOrganizacion(org).total == 1
    }
    void 'tratar de sumar puntos por publicar actividad otra vez'() {
        given: 'org y actividad con todos los datos'
        def org = Organizacion.get(1)
        def actividad = org.programas.first().proyectos.first().actividades.first()
        and: 'se suma puntos una primera vez'
        service.operarPuntosActividad(actividad)
        when: 'se trata de volver a sumar puntos'
        service.operarPuntosActividad(actividad)
        then: 'se crea solamente un puntaje y un historial de puntos de la org'
        Puntaje.count() == 1 && HistorialPuntaje.count() == 1
        Puntaje.findByOrganizacion(org).total == 1
    }
    void "tratar de sumar puntos con actividad incompleta"() {
        given: 'org y actividad con todos los datos'
        def org = Organizacion.get(1)
        def actividad = org.programas.first().proyectos.first().actividades.first()
        and: 'cambiamos los datos para dejar una actividad incompleta'
        cambiarDatosActividad(actividad, nombre, descripcion, publicado)
        when:
        service.operarPuntosActividad(actividad)
        then: 'no se crea puntaje ni historial'
        Puntaje.count() == 0 && HistorialPuntaje.count() == 0
        Puntaje.findByOrganizacion(org) == null
        where: 'datos de actividad para cambiar (al menos uno debe ser nulo)'
        nombre | descripcion | publicado
        false  | true        | true
        true   | false       | true
        true   | true        | false
    }
    void 'restar puntos por modificar datos de actividad'() {
        given: 'org y actividad con todos los datos'
        def org = Organizacion.get(1)
        def actividad = org.programas.first().proyectos.first().actividades.first()
        and: 'se suma puntos por completar actividad'
        service.operarPuntosActividad(actividad)
        and: 'cambiamos los datos para dejar una actividad incompleta'
        cambiarDatosActividad(actividad, nombre, descripcion, publicado)
        when: 'se trata de volver a sumar puntos'
        service.operarPuntosActividad(actividad)
        then: 'el puntaje debe ser de 0 y el historial deshabilitado'
        Puntaje.count() == 1 && HistorialPuntaje.count() == 1
        Puntaje.findByOrganizacion(org).total == 0
        HistorialPuntaje.countByEnabled(false) == 1
        where: 'datos de actividad para cambiar'
        nombre | descripcion | publicado
        false  | true        | true
    }
    def cambiarDatosActividad(actividad, nombre, descripcion, publicado) {
        if (!nombre) actividad.nombre = null
        if (!descripcion) actividad.descripcion = null
        if (!publicado) actividad.publicado = false
        actividad.save(flush: true)
    }

    void 'sumar puntos por publicar evento'() {
        given: 'org y evento con todos los datos'
        def org = Organizacion.get(1)
        def evento = org.eventos.first()
        // 1 * service.sumarSiNoTienePuntos(_,_,_)
        when:
        service.operarPuntosEvento(evento)
        then: 'se crea un puntaje y un historial de puntos de la org'
        Puntaje.count() == 1 && HistorialPuntaje.count() == 1
        Puntaje.findByOrganizacion(org).total == 2
    }
    void 'tratar de sumar puntos por publicar evento otra vez'() {
        given: 'org y evento con todos los datos'
        def org = Organizacion.get(1)
        def evento = org.eventos.first()
        and: 'se suma puntos una primera vez'
        service.operarPuntosEvento(evento)
        when: 'se trata de volver a sumar puntos'
        service.operarPuntosEvento(evento)
        then: 'se crea solamente un puntaje y un historial de puntos de la org'
        Puntaje.count() == 1 && HistorialPuntaje.count() == 1
        Puntaje.findByOrganizacion(org).total == 2
    }
    void "tratar de sumar puntos con evento incompleta"() {
        given: 'org y evento con todos los datos'
        def org = Organizacion.get(1)
        def evento = org.eventos.first()
        and: 'cambiamos los datos para dejar un evento incompleto'
        cambiarDatosEvento(evento, nombre, descripcion, fechaIni, fechaFin, fechaDifusion, direccion, publicado)
        when:
        service.operarPuntosEvento(evento)
        then: 'no se crea puntaje ni historial'
        Puntaje.count() == 0 && HistorialPuntaje.count() == 0
        Puntaje.findByOrganizacion(org) == null
        where: 'datos de evento para cambiar (al menos uno debe ser nulo)'
        nombre | descripcion | fechaIni | fechaFin | fechaDifusion | direccion | publicado
        false  | true        | true     | true     | true          | true      | true
        true   | false       | true     | true     | true          | true      | true
        true   | true        | false    | true     | true          | true      | true
        true   | true        | true     | false    | true          | true      | true
        true   | true        | true     | true     | false         | true      | true
        true   | true        | true     | true     | true          | false     | true
        true   | true        | true     | true     | true          | true      | false

    }
    void 'restar puntos por modificar datos de evento'() {
        given: 'org y evento con todos los datos'
        def org = Organizacion.get(1)
        def evento = org.eventos.first()
        and: 'se suma puntos por completar evento'
        service.operarPuntosEvento(evento)
        and: 'cambiamos los datos para dejar un evento incompleto'
        cambiarDatosEvento(evento, nombre, descripcion, fechaIni, fechaFin, fechaDifusion, direccion, publicado)
        when: 'se trata de volver a sumar puntos'
        service.operarPuntosEvento(evento)
        then: 'el puntaje debe ser de 0 y el historial deshabilitado'
        Puntaje.count() == 1 && HistorialPuntaje.count() == 1
        Puntaje.findByOrganizacion(org).total == 0
        HistorialPuntaje.countByEnabled(false) == 1
        where: 'datos de evento para cambiar'
        nombre | descripcion | fechaIni | fechaFin | fechaDifusion | direccion | publicado
        false  | true        | true     | true     | true          | true      | true
    }
    def cambiarDatosEvento(evento, nombre, descripcion, fechaIni, fechaFin, fechaDifusion, direccion, publicado) {
        if (!nombre) evento.nombre = null
        if (!descripcion) evento.descripcion = null
        if (!fechaIni) evento.fechaIni = null
        if (!fechaFin) evento.fechaFin = null
        if (!fechaDifusion) evento.fechaDifusion = null
        if (!direccion) evento.direccion = null
        if (!publicado) evento.publicado = false
        evento.save(flush: true)
    }

    // void 'sumar puntos por publicar articulo'() {
    //     given: 'org y articulo con todos los datos'
    //     def org = Organizacion.get(1)
    //     def articulo = org.articulos.first()
    //     // 1 * service.sumarSiNoTienePuntos(_,_,_)
    //     when:
    //     service.operarPuntosArticulo(articulo)
    //     then: 'se crea un puntaje y un historial de puntos de la org'
    //     Puntaje.count() == 1 && HistorialPuntaje.count() == 1
    //     Puntaje.findByOrganizacion(org).total == 1
    // }
    // void 'tratar de sumar puntos por publicar un articulo otra vez'() {
    //     given: 'org y un articulo con todos los datos'
    //     def org = Organizacion.get(1)
    //     def articulo = org.articulos.first()
    //     and: 'se suma puntos una primera vez'
    //     service.operarPuntosArticulo(articulo)
    //     when: 'se trata de volver a sumar puntos'
    //     service.operarPuntosArticulo(articulo)
    //     then: 'se crea solamente un puntaje y un historial de puntos de la org'
    //     Puntaje.count() == 1 && HistorialPuntaje.count() == 1
    //     Puntaje.findByOrganizacion(org).total == 1
    // }
    // void "tratar de sumar puntos con un articulo incompleto"() {
    //     given: 'org y un articulo con todos los datos'
    //     def org = Organizacion.get(1)
    //     def articulo = org.articulos.first()
    //     and: 'cambiamos los datos para dejar un articulo incompleto'
    //     cambiarDatosArticulo(articulo, autor, titulo, contenido, tipo, publicado)
    //     when:
    //     service.operarPuntosArticulo(articulo)
    //     then: 'no se crea puntaje ni historial'
    //     Puntaje.count() == 0 && HistorialPuntaje.count() == 0
    //     Puntaje.findByOrganizacion(org) == null
    //     where: 'datos de articulo para cambiar (al menos uno debe ser nulo)'
    //     autor | titulo | contenido | tipo  | publicado
    //     false | true   | true      | true  | true
    //     true  | false  | true      | true  | true
    //     true  | true   | false     | true  | true
    //     true  | true   | true      | false | true
    //     true  | true   | true      | true  | false
    // }
    // void 'restar puntos por modificar datos de un articulo'() {
    //     given: 'org y articulo con todos los datos'
    //     def org = Organizacion.get(1)
    //     def articulo = org.articulos.first()
    //     and: 'se suma puntos por completar un articulo'
    //     service.operarPuntosArticulo(articulo)
    //     and: 'cambiamos los datos para dejar un articulo incompleto'
    //     cambiarDatosArticulo(articulo, autor, titulo, contenido, tipo, publicado)
    //     when: 'se trata de volver a sumar puntos'
    //     service.operarPuntosArticulo(articulo)
    //     then: 'el puntaje debe ser de 0 y el historial deshabilitado'
    //     Puntaje.count() == 1 && HistorialPuntaje.count() == 1
    //     Puntaje.findByOrganizacion(org).total == 0
    //     HistorialPuntaje.countByEnabled(false) == 1
    //     where: 'datos de articulo para cambiar'
    //     autor | titulo | contenido | tipo  | publicado
    //     false | true   | true      | true  | true
    // }
    // def cambiarDatosArticulo(articulo, autor, titulo, contenido, tipo, publicado) {
    //     if (!autor) articulo.autor = null
    //     if (!titulo) articulo.titulo = null
    //     if (!contenido) articulo.contenido = null
    //     if (!tipo) articulo.tipo = null
    //     if (!publicado) articulo.publicado = false
    //     articulo.save(flush: true)
    // }
}
