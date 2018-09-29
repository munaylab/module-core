package org.munaylab.ods

class Indicador {

    String posicion
    String nombre
    String descripcion

    static belongsTo = [
        meta: Meta
    ]

    static constraints = {
        posicion size: 1..3
        nombre size: 5..50
        descripcion blank: false
    }

}
