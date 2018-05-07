package org.munaylab.balance

class Categoria {

    String nombre
    String detalle
    TipoAsiento tipo

    static belongsTo = [categoriaPadre: Categoria]
    static hasMany = [subcategorias: Categoria]

    static constraints = {
        nombre size: 5..500
        detalle nullable: true
    }
}
