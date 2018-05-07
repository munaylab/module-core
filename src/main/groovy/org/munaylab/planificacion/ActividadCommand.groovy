package org.munaylab.planificacion

class ActividadCommand extends PlanificacionCommand implements grails.validation.Validateable {

    Long proyectoId

    static constraints = {
        proyectoId nullable: true
    }

}
