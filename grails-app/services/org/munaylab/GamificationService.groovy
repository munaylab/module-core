package org.munaylab

// // import org.munaylab.contenido.Articulo
import org.munaylab.gamification.HistorialPuntaje
import org.munaylab.gamification.Puntaje
import org.munaylab.osc.Organizacion
// import org.munaylab.planificacion.Actividad
// import org.munaylab.planificacion.Evento
// import org.munaylab.planificacion.Programa
// import org.munaylab.planificacion.Proyecto

import grails.gorm.transactions.Transactional

@Transactional
class GamificationService {

    def grailsApplication

    def sumarSiNoTienePuntos(def config, Organizacion org, Long referencia) {
        if (HistorialPuntaje.countByEvento(config.nombre) > 0) return null
        HistorialPuntaje historial = new HistorialPuntaje(referencia: referencia, evento: config.nombre, puntos: config.puntos)

        Puntaje puntaje = Puntaje.findOrCreateByOrganizacion(org)
        if (!puntaje.total) puntaje.total = 0
        puntaje.total += config.puntos
        puntaje.addToPuntos(historial)
        puntaje.save()
    }

    def restarSiTienePuntos(def config, Long referencia) {
        HistorialPuntaje historial = HistorialPuntaje.findByEventoAndReferencia(config.nombre, referencia)
        if (!historial) return

        historial.enabled = false
        historial.save()

        historial.puntaje.total -= historial.puntos
        historial.puntaje.save()
    }

    def operarPuntosPerfil(Organizacion org) {
        def perfilConfig = grailsApplication.config.gamification.eventos.perfil
        org = comprobarDatosPerfil(org)
        if (org.hasErrors()) {
            restarSiTienePuntos(perfilConfig, org.id)
        } else {
            sumarSiNoTienePuntos(perfilConfig, org, org.id)
        }
    }
    @Transactional(readOnly = true)
    def comprobarDatosPerfil(Organizacion org) {
        if (!org.tipo) org.errors.rejectValue('tipo', 'org.tipo.null')
        if (!org.nombre) org.errors.rejectValue('nombre', 'org.nombre.null')
        if (!org.objeto) org.errors.rejectValue('objeto', 'org.objeto.null')
        if (!org.domicilio) org.errors.rejectValue('domicilio', 'org.domicilio.null')
        if (!org.fechaConstitucion) org.errors.rejectValue('fechaConstitucion', 'org.fechaConstitucion.null')
        if (org.contactos == null || org.contactos.isEmpty())
            org.errors.rejectValue('contactos', 'org.contactos.empty')
        return org
    }

    /*def operarPuntosPrograma(Programa programa) {
        def programaConfig = grailsApplication.config.gamification.eventos.programa
        programa = comprobarDatosPrograma(programa)
        if (programa.hasErrors()) {
            restarSiTienePuntos(programaConfig, programa.id)
        } else {
            sumarSiNoTienePuntos(programaConfig, programa.organizacion, programa.id)
        }
    }
    @Transactional(readOnly = true)
    def comprobarDatosPrograma(Programa programa) {
        if (!programa.nombre) programa.errors.rejectValue('nombre', 'programa.nombre.null')
        if (!programa.descripcion) programa.errors.rejectValue('descripcion', 'programa.descripcion.null')
        if (!programa.publicado) programa.errors.rejectValue('publicado', 'programa.publicado.null')
        return programa
    }

    def operarPuntosProyecto(Proyecto proyecto) {
        def proyectoConfig = grailsApplication.config.gamification.eventos.proyecto
        proyecto = comprobarDatosProyecto(proyecto)
        if (proyecto.hasErrors()) {
            restarSiTienePuntos(proyectoConfig, proyecto.id)
        } else {
            sumarSiNoTienePuntos(proyectoConfig, proyecto.programa.organizacion, proyecto.id)
        }
    }
    @Transactional(readOnly = true)
    def comprobarDatosProyecto(Proyecto proyecto) {
        if (!proyecto.nombre) proyecto.errors.rejectValue('nombre', 'proyecto.nombre.null')
        if (!proyecto.descripcion) proyecto.errors.rejectValue('descripcion', 'proyecto.descripcion.null')
        if (!proyecto.publicado) proyecto.errors.rejectValue('publicado', 'proyecto.publicado.null')
        return proyecto
    }

    def operarPuntosActividad(Actividad actividad) {
        def actividadConfig = grailsApplication.config.gamification.eventos.actividad
        actividad = comprobarDatosActividad(actividad)
        if (actividad.hasErrors()) {
            restarSiTienePuntos(actividadConfig, actividad.id)
        } else {
            Organizacion org = actividad.proyecto.programa.organizacion
            sumarSiNoTienePuntos(actividadConfig, org, actividad.id)
        }
    }
    @Transactional(readOnly = true)
    def comprobarDatosActividad(Actividad actividad) {
        if (!actividad.nombre) actividad.errors.rejectValue('nombre', 'actividad.nombre.null')
        if (!actividad.descripcion) actividad.errors.rejectValue('descripcion', 'actividad.descripcion.null')
        if (!actividad.publicado) actividad.errors.rejectValue('publicado', 'actividad.publicado.null')
        return actividad
    }

    def operarPuntosEvento(Evento evento) {
        def eventoConfig = grailsApplication.config.gamification.eventos.evento
        evento = comprobarDatosEvento(evento)
        if (evento.hasErrors()) {
            restarSiTienePuntos(eventoConfig, evento.id)
        } else {
            sumarSiNoTienePuntos(eventoConfig, evento.organizacion, evento.id)
        }
    }
    @Transactional(readOnly = true)
    def comprobarDatosEvento(Evento evento) {
        if (!evento.nombre) evento.errors.rejectValue('nombre', 'evento.nombre.null')
        if (!evento.descripcion) evento.errors.rejectValue('descripcion', 'evento.descripcion.null')
        if (!evento.fechaIni) evento.errors.rejectValue('fechaIni', 'evento.fechaIni.null')
        if (!evento.fechaFin) evento.errors.rejectValue('fechaFin', 'evento.fechaFin.null')
        if (!evento.fechaDifusion) evento.errors.rejectValue('fechaDifusion', 'evento.fechaDifusion.null')
        if (!evento.direccion) evento.errors.rejectValue('direccion', 'evento.direccion.null')
        if (!evento.publicado) evento.errors.rejectValue('publicado', 'evento.publicado.null')
        return evento
    }*/

    // def operarPuntosArticulo(Articulo articulo) {
    //     def articuloConfig = grailsApplication.config.gamification.eventos.articulo
    //     articulo = comprobarDatosArticulo(articulo)
    //     if (articulo.hasErrors()) {
    //         restarSiTienePuntos(articuloConfig, articulo.id)
    //     } else {
    //         Organizacion org = articulo.organizacion
    //         sumarSiNoTienePuntos(articuloConfig, org, articulo.id)
    //     }
    // }
    // @Transactional(readOnly = true)
    // def comprobarDatosArticulo(Articulo articulo) {
    //     if (!articulo.autor) articulo.errors.rejectValue('autor', 'articulo.autor.null')
    //     if (!articulo.titulo) articulo.errors.rejectValue('titulo', 'articulo.titulo.null')
    //     if (!articulo.contenido) articulo.errors.rejectValue('contenido', 'articulo.contenido.null')
    //     if (!articulo.tipo) articulo.errors.rejectValue('tipo', 'articulo.tipo.null')
    //     if (!articulo.publicado) articulo.errors.rejectValue('publicado', 'articulo.publicado.null')
    //     return articulo
    // }
}
