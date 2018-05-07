package org.munaylab

import org.munaylab.osc.Organizacion
import org.munaylab.direccion.Domicilio
import org.munaylab.planificacion.Actividad
import org.munaylab.planificacion.ActividadCommand
import org.munaylab.planificacion.Evento
import org.munaylab.planificacion.EventoCommand
import org.munaylab.planificacion.Programa
import org.munaylab.planificacion.ProgramaCommand
import org.munaylab.planificacion.Proyecto
import org.munaylab.planificacion.ProyectoCommand
import org.munaylab.user.User

// import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class PlanificacionServiceSpec extends Specification
        implements ServiceUnitTest<PlanificacionService>, DataTest {

    void setupSpec() {
        mockDomains Organizacion
    }

    void "agregar programa"() {
        given:
        def org = Builder.crearOrganizacionConDatos().save(flush: true)
        when:
        def respuesta = service.actualizarPrograma(Builder.programaCommand, org)
        then:
        comprobarProgramaGuardado(org, respuesta.valor)
    }
    void "modificar programa"() {
        given:
        def programa = Builder.crearPrograma()
        def org = Builder.crearOrganizacionConDatos().addToProgramas(programa).save(flush: true)
        and:
        def command = Builder.programaCommand
        command.id = programa.id
        when:
        def respuesta = service.actualizarPrograma(command, org)
        then:
        comprobarProgramaGuardado(org, respuesta.valor)
        comprobarDatosProgramaActualizados(respuesta.valor, command)
    }
    void "eliminar programa"() {
        given:
        def programa = Builder.crearPrograma()
        def org = Builder.crearOrganizacionConDatos().addToProgramas(programa).save(flush: true)
        when:
        service.eliminarPrograma(programa)
        then:
        Programa.all.isEmpty() && org.programas.isEmpty() && Organizacion.get(1).programas.isEmpty()
    }
    void comprobarProgramaGuardado(Organizacion org, Programa programa) {
        assert programa != null && Programa.count() == 1 && Programa.get(programa.id) != null
        assert org.programas.size() == 1 && Organizacion.get(1).programas.size() == 1
    }
    void comprobarDatosProgramaActualizados(Programa programa, ProgramaCommand command) {
        assert (programa.imagen == command.imagen && programa.nombre == command.nombre
                && programa.descripcion == command.descripcion)
        assert Programa.get(programa.id).imagen == command.imagen
        assert Programa.get(programa.id).nombre == command.nombre
        assert Programa.get(programa.id).descripcion == command.descripcion
    }
    void "agregar proyecto"() {
        given:
        def programa = Builder.crearPrograma()
        def org = Builder.crearOrganizacionConDatos().addToProgramas(programa).save(flush: true)
        when:
        def respuesta = service.actualizarProyecto(Builder.proyectoCommand, org)
        then:
        comprobarProyectoGuardado(org, programa, respuesta.valor)
    }
    void "modificar proyecto"() {
        given:
        def proyecto = Builder.crearProyecto()
        def programa = Builder.crearPrograma().addToProyectos(proyecto)
        def org = Builder.crearOrganizacionConDatos().addToProgramas(programa).save(flush: true)
        and:
        def command = Builder.proyectoCommand
        command.id = proyecto.id
        command.programaId = programa.id
        when:
        def respuesta = service.actualizarProyecto(command, org)
        then:
        comprobarProyectoGuardado(org, programa, respuesta.valor)
        comprobarDatosProyectoActualizados(respuesta.valor, command)
    }
    void "eliminar proyecto"() {
        given:
        def proyecto = Builder.crearProyecto()
        def programa = Builder.crearPrograma().addToProyectos(proyecto)
        def org = Builder.crearOrganizacionConDatos().addToProgramas(programa).save(flush: true)
        when:
        service.eliminarProyecto(proyecto)
        then:
        Proyecto.all.isEmpty() && programa.proyectos.isEmpty()
        comprobarProgramaGuardado(org, programa)
    }
    void comprobarProyectoGuardado(Organizacion org, Programa programa, Proyecto proyecto) {
        comprobarProgramaGuardado(org, programa)
        assert proyecto != null && Proyecto.count() == 1 && Proyecto.get(proyecto.id) != null
        assert programa.proyectos.size() == 1 && Programa.get(programa.id).proyectos.size() == 1
    }
    void comprobarDatosProyectoActualizados(Proyecto proyecto, ProyectoCommand command) {
        assert (proyecto.imagen == command.imagen && proyecto.nombre == command.nombre
                && proyecto.descripcion == command.descripcion)
        assert Proyecto.get(proyecto.id).imagen == command.imagen
        assert Proyecto.get(proyecto.id).nombre == command.nombre
        assert Proyecto.get(proyecto.id).descripcion == command.descripcion
    }
    void "agregar actividad"() {
        given:
        def proyecto = Builder.crearProyecto()
        def programa = Builder.crearPrograma().addToProyectos(proyecto)
        def org = Builder.crearOrganizacionConDatos().addToProgramas(programa).save(flush: true)
        when:
        def respuesta = service.actualizarActividad(Builder.getActividadCommand(proyecto.id), org)
        then:
        comprobarActividadGuardada(org, programa, proyecto, respuesta.valor)
    }
    void "modificar actividad"() {
        given:
        def actividad = Builder.crearActividad()
        def proyecto = Builder.crearProyecto().addToActividades(actividad)
        def programa = Builder.crearPrograma().addToProyectos(proyecto)
        def org = Builder.crearOrganizacionConDatos().addToProgramas(programa).save(flush: true)
        and:
        def command = Builder.getActividadCommand(proyecto.id)
        command.id = actividad.id
        when:
        def respuesta = service.actualizarActividad(command, org)
        then:
        comprobarActividadGuardada(org, programa, proyecto, respuesta.valor)
        comprobarDatosActividadActualizados(respuesta.valor, command)
    }
    void "eliminar actividad"() {
        given:
        def actividad = Builder.crearActividad()
        def proyecto = Builder.crearProyecto().addToActividades(actividad)
        def programa = Builder.crearPrograma().addToProyectos(proyecto)
        def org = Builder.crearOrganizacionConDatos().addToProgramas(programa).save(flush: true)
        when:
        service.eliminarActividad(actividad)
        then:
        Actividad.all.isEmpty() && proyecto.actividades.isEmpty()
        comprobarProyectoGuardado(org, programa, proyecto)
    }
    void comprobarActividadGuardada(org, programa, proyecto, actividad) {
        comprobarProyectoGuardado(org, programa, proyecto)
        assert actividad != null && Actividad.count() == 1
        assert Proyecto.get(proyecto.id).actividades.size() == 1
    }
    void comprobarDatosActividadActualizados(Actividad actividad, ActividadCommand command) {
        assert (actividad.imagen == command.imagen && actividad.nombre == command.nombre
                && actividad.descripcion == command.descripcion)
        assert Actividad.get(actividad.id).imagen == command.imagen
        assert Actividad.get(actividad.id).nombre == command.nombre
        assert Actividad.get(actividad.id).descripcion == command.descripcion
    }
    void "agregar evento"() {
        given:
        def org = Builder.crearOrganizacionConDatos().save(flush: true)
        when:
        def respuesta = service.actualizarEvento(Builder.eventoCommand, org)
        then:
        comprobarEventoGuardado(org, respuesta.valor)
    }
    void "modificar evento"() {
        given:
        def evento = Builder.crearEvento()
        def org = Builder.crearOrganizacionConDatos().addToEventos(evento).save(flush: true)
        and:
        def command = Builder.eventoCommand
        command.id = evento.id
        when:
        def respuesta = service.actualizarEvento(command, org)
        then:
        comprobarEventoGuardado(org, respuesta.valor)
        comprobarDatosEventoActualizados(respuesta.valor, command)
    }
    void "cancelar evento"() {
        given:
        def evento = Builder.crearEvento()
        def org = Builder.crearOrganizacionConDatos().addToEventos(evento).save(flush: true)
        when:
        service.cancelarEvento(evento)
        then:
        Programa.all.isEmpty() && org.eventos.isEmpty() && Organizacion.get(1).eventos.isEmpty()
    }
    void comprobarEventoGuardado(Organizacion org, Evento evento) {
        assert evento != null && Evento.count() == 1
        assert evento.direccion != null && Domicilio.count() == 1
        assert org.eventos.size() == 1 && Organizacion.get(1).eventos.size() == 1
    }
    void comprobarDatosEventoActualizados(Evento evento, EventoCommand command) {
        assert (evento.imagen == command.imagen && evento.nombre == command.nombre
                && evento.descripcion == command.descripcion)
        assert (Evento.get(1).imagen == command.imagen && Evento.get(1).nombre == command.nombre
                && Evento.get(1).descripcion == command.descripcion)
    }

    void "obtener resumen"() {
        given:
        def org = Builder.crearOrganizacionConDatos().save(flush: true)
        Actividad actividad = new Actividad(nombre: 'Presentacion Innovacion', imagen: 'proyecto/actividad/innovacion',
            descripcion: 'Presentacion de innovaciones realizadas en el taller', publicado: true)
        Proyecto proyecto = new Proyecto(nombre: 'Taller de Innovaciones', imagen: 'proyecto/programa/innovacion',
            descripcion: 'Taller donde se exponen innovaciones para la sociedad', publicado: true)
        Programa programa = new Programa(nombre: 'Innovaciones Sociales', imagen: 'programa/innovacion',
            descripcion: 'Brindar innovaciones a las osc.', publicado: true)

        proyecto.addToActividades(actividad)
        programa.addToProyectos(proyecto)
        org.addToProgramas(programa)
        org.save(flush: true)
        when:
        def result = service.getResumen(org)
        then:
        result[0].value == "1"
    }
}
