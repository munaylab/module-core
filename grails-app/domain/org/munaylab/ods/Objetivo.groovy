package org.munaylab.ods

import org.munaylab.osc.Organizacion

class Objetivo {

    Integer posicion
    String nombre
    String descripcion

    static hasMany = [
        metas: Meta
    ]

    static constraints = {
        nombre size: 5..50
        descripcion blank: false
    }

}
