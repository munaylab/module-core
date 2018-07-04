package org.munaylab.ods

import org.munaylab.osc.Organizacion

class Objetivo {

    Integer posicion
    String nombre
    String objetivo
    String descripcion

    static hasMany = [
        metas: Meta
    ]

    static constraints = {
        posicion unique: true
        nombre size: 5..50
        objetivo blank: false
        descripcion blank: false
    }

}
