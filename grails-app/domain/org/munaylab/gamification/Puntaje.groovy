package org.munaylab.gamification

import org.munaylab.osc.Organizacion

class Puntaje {

    Integer total
    Date dateCreated
    Date lastUpdated
    Organizacion organizacion

    static hasMany = [puntos: HistorialPuntaje]

    static constraints = {
        total nullable: false, min: 0, max: 101
    }

}
