package org.munaylab.planificacion

class Proyecto extends Planificacion {

    static belongsTo = [programa: Programa]

    static hasMany = [actividades: Actividad]

}
