package org.munaylab.planificacion

class Actividad extends Planificacion {

    static belongsTo = [proyecto: Proyecto]

}
