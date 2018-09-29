package org.munaylab.ods

class Meta {

    String posicion
    String descripcion

    static belongsTo = [
        objetivo: Objetivo
    ]

    static hasMany = [
        indicadores: Indicador
    ]

    static constraints = {
        posicion size: 1..3
        descripcion blank: false
    }

}
