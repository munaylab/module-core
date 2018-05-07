package org.munaylab.gamification

class HistorialPuntaje {

    Long referencia
    String evento
    Integer puntos
    Date dateCreated
    Date lastUpdated
    Boolean enabled = Boolean.TRUE

    static belongsTo = [puntaje: Puntaje]

    static constraints = {
        evento blank: false, size: 3..50
        puntos nullable: false, min: 0, max: 101
    }

}
