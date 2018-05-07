package org.munaylab.planificacion

import org.munaylab.osc.Organizacion

class Programa extends Planificacion {

    static belongsTo = [organizacion: Organizacion]

    static hasMany = [proyectos: Proyecto]

}
