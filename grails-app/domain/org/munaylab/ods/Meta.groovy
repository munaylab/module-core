package org.munaylab.ods

class Meta {

    String posicion
    String nombre
    String descripcion

    static belongsTo = [
        objetivo: Objetivo
    ]

    static hasMany = [
        indicadores: Indicador
    ]

    static constraints = {
        posicion size: 1..3
        nombre size: 5..50
        descripcion blank: false
    }

}
