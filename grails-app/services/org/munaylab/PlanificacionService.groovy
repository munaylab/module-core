package org.munaylab

import org.munaylab.components.PanelEventos
import org.munaylab.components.PanelProgramas
import org.munaylab.components.PanelProyectos
import org.munaylab.components.PanelActividades
import org.munaylab.direccion.Domicilio
import org.munaylab.osc.Organizacion
import org.munaylab.planificacion.Actividad
import org.munaylab.planificacion.ActividadCommand
import org.munaylab.planificacion.Evento
import org.munaylab.planificacion.EventoCommand
import org.munaylab.planificacion.Programa
import org.munaylab.planificacion.ProgramaCommand
import org.munaylab.planificacion.Proyecto
import org.munaylab.planificacion.ProyectoCommand
import org.munaylab.planificacion.PlanificacionCommand
import org.munaylab.utils.Respuesta

import grails.gorm.transactions.Transactional

@Transactional
class PlanificacionService {

    Programa getPrograma(Long id, Organizacion org) {
        Programa programa = Programa.get(id)
        if (!programa) return null

        if (programa.organizacion != org) return null

        programa
    }

    def actualizarPlanificacion(PlanificacionCommand command, Organizacion org) {
        if (command && command.validate() && org) {
            if (command in ProgramaCommand) return actualizarPrograma(command, org)
            if (command in ProyectoCommand) return actualizarProyecto(command, org)
            if (command in ActividadCommand) return actualizarActividad(command, org)
        }
        return null
    }

    Respuesta actualizarPrograma(ProgramaCommand command, Organizacion org) {
        if (command.orgId != org.id)
            return Respuesta.conError('error.invalid.token')

        if (!command.validate())
            return Respuesta.conErrores(command, command.errors.allErrors)

        Programa programa = command.id ? Programa.get(command.id) : null
        if (programa) {
            programa.imagen = command.imagen
            programa.nombre = command.nombre
            programa.descripcion = command.descripcion
            programa.save()
            org.refresh()
        } else {
            programa = new Programa(command.properties)
            org.addToProgramas(programa)
            org.save()
        }
        return Respuesta.conValor(programa)
    }

    void eliminarPrograma(Long id) {
        eliminarPrograma(Programa.get(id))
    }

    void eliminarPrograma(Programa programa) {
        if (!programa || !programa.organizacion) return

        Organizacion org = programa.organizacion
        org.removeFromProgramas(programa)
        programa.delete()
        org.programas.clear()
    }

    Proyecto getProyecto(Long id, Organizacion org) {
        Proyecto proyecto = Proyecto.get(id)
        if (!proyecto) return null

        if (proyecto.programa.organizacion != org) return null

        proyecto
    }

    Respuesta actualizarProyecto(ProyectoCommand command, Organizacion org) {
        if (command.orgId != org.id)
            return Respuesta.conError('error.invalid.token')

        if (!command.validate())
            return Respuesta.conErrores(command, command.errors.allErrors)

        Proyecto proyecto = command.id ? Proyecto.get(command.id) : null
        if (proyecto) {
            proyecto.imagen = command.imagen
            proyecto.nombre = command.nombre
            proyecto.descripcion = command.descripcion
            proyecto.save()
        } else {
            proyecto = new Proyecto(command.properties)
            Programa programa = org.programas.find { it.id == command.programaId }
            programa.addToProyectos(proyecto)
            org.save()
        }
        return Respuesta.conValor(proyecto)
    }

    void eliminarProyecto(Long id) {
        eliminarProyecto(Proyecto.get(id))
    }

    void eliminarProyecto(Proyecto proyecto) {
        if (!proyecto || !proyecto.programa) return

        Programa programa = proyecto.programa
        programa.removeFromProyectos(proyecto)
        proyecto.delete()
        programa.proyectos.clear()
    }


    Actividad getActividad(Long id, Organizacion org) {
        Actividad actividad = Actividad.get(id)
        if (!actividad) return null

        if (actividad.proyecto.programa.organizacion != org) return null

        actividad
    }

    Respuesta actualizarActividad(ActividadCommand command, Organizacion org) {
        if (command.orgId != org.id)
            return Respuesta.conError('error.invalid.token')

        if (!command.validate())
            return Respuesta.conErrores(command, command.errors.allErrors)

        Actividad actividad = command.id ? Actividad.get(command.id) : null
        if (actividad) {
            actividad.imagen = command.imagen
            actividad.nombre = command.nombre
            actividad.descripcion = command.descripcion
            actividad.save()
        } else {
            Proyecto proyecto = Proyecto.get(command.proyectoId)
            actividad = new Actividad(command.properties)
            proyecto.addToActividades(actividad)
            proyecto.save()
        }
        return Respuesta.conValor(actividad)
    }

    void eliminarActividad(Long id) {
        eliminarActividad(Actividad.get(id))
    }

    void eliminarActividad(Actividad actividad) {
        if (!actividad || !actividad.proyecto) return

        Proyecto proyecto = actividad.proyecto
        proyecto.removeFromActividades(actividad)
        actividad.delete()
        proyecto.actividades.clear()
    }

    Evento getEvento(Long id, Organizacion org) {
        Evento evento = Evento.get(id)
        if (!evento) return null

        if (evento.organizacion != org) return null

        evento
    }

    Respuesta actualizarEvento(EventoCommand command, Organizacion org) {
        if (command.orgId != org.id)
            return Respuesta.conError('error.invalid.token')

        if (!command.validate())
            return Respuesta.conErrores(command, command.errors.allErrors)

        Evento evento = command.id ? Evento.get(command.id) : null
        command.fechaIni = command.componerFechaInicial()
        command.fechaFin = command.componerFechaFinal()
        command.fechaDifusion = command.componerFechaDifusion()
        if (evento) {
            evento.imagen = command.imagen
            evento.nombre = command.nombre
            evento.descripcion = command.descripcion
            evento.fechaIni = command.fechaIni
            evento.fechaFin = command.fechaFin
            evento.fechaDifusion = command.fechaDifusion
            if (command.direccion) {
                if (!evento.direccion) evento.direccion = new Domicilio()
                evento.direccion.calle = command.direccion.calle
                evento.direccion.numero = command.direccion.numero
                evento.direccion.barrio = command.direccion.barrio
                evento.direccion.distrito = command.direccion.distrito
                evento.direccion.localidad = command.direccion.localidad
                evento.direccion.provincia = command.direccion.provincia
            }
            evento.save()
            org.refresh()
        } else {
            evento = new Evento(nombre: command.nombre, descripcion: command.descripcion, imagen: command.imagen)
            evento.organizacion = org
            evento.fechaIni = command.fechaIni
            evento.fechaFin = command.fechaFin
            evento.fechaDifusion = command.fechaDifusion
            evento.direccion = new Domicilio(command.direccion.properties)
            evento.save()
        }
        return Respuesta.conValor(evento)
    }

    void eliminarEvento(Long id) {
        eliminarEvento(Evento.get(id))
    }

    void eliminarEvento(Evento evento) {
        if (!evento) return
        evento.delete()
    }

    void cancelarEvento(Evento evento) {
        if (!evento || !evento.organizacion) return

        Organizacion org = evento.organizacion
        org.removeFromEventos(evento)
        evento.delete()
        org.eventos.clear()
    }

    def getProgramas(Organizacion org) {
        Programa.findAllByOrganizacion(org)
    }

    def getProyectos(Organizacion org) {
        def programas = getProgramas(org)
        programas*.proyectos.flatten()
    }

    def getProximosEventos(Organizacion org) {
        Evento.findAllByOrganizacionAndFechaIniGreaterThanEquals(org, new Date())
    }

    def getPlanificaciones(Organizacion org) {
        def eventos = getProximosEventos(org)
        def programas = getProgramas(org)
        def proyectos = programas*.proyectos.flatten()
        def actividades = proyectos*.actividades.flatten()
        [programas: programas, proyectos: proyectos, actividades: actividades, eventos: eventos]
    }

    def getResumen(Organizacion org) {
        def panels = []
        int totalEventos = getTotalEventos(org)
        int totalProgramas = getTotalProgramas(org)
        int totalProyectos = getTotalProyectos(org)
        int totalActividades = getTotalActividades(org)

        panels << new PanelProgramas(name: 'Programas', value: totalProgramas)
        panels << new PanelProyectos(name: 'Proyectos', value: totalProyectos)
        panels << new PanelActividades(name: 'Actividades', value: totalActividades)
        panels << new PanelEventos(name: 'Eventos', value: totalEventos)

        return panels
    }
    private int getTotalProgramas(Organizacion org) {
        Programa.createCriteria().get {
            eq 'publicado', true
            eq 'organizacion', org
            projections {
                rowCount()
            }
        }
    }
    private int getTotalProyectos(Organizacion org) {
        Proyecto.createCriteria().get {
            eq 'publicado', true
            programa {
                eq 'organizacion', org
            }
            projections {
                rowCount()
            }
        }
    }
    private int getTotalActividades(Organizacion org) {
        Actividad.createCriteria().get {
            eq 'publicado', true
            proyecto {
                programa {
                    eq 'organizacion', org
                }
            }
            projections {
                rowCount()
            }
        }
    }
    private int getTotalEventos(Organizacion org) {
        Evento.createCriteria().get {
            eq 'publicado', true
            eq 'organizacion', org
            projections {
                rowCount()
            }
        }
    }
}
