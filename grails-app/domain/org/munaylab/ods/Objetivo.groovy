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
        descripcion blank: false, size: 0..1500
    }

    public String getNombreDeArchivo() {
        String nombreOds = nombre.toLowerCase()
        nombreOds = nombreOds.replaceAll(' ', '_').replaceAll(',', '')
                .replaceAll('á','a')
                .replaceAll('é','e')
                .replaceAll('í','i')
                .replaceAll('ó','o')
                .replaceAll('ú','u')
        return posicion + "-" + nombreOds + ".jpg"
    }

}
