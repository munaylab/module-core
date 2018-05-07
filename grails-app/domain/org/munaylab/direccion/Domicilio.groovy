package org.munaylab.direccion

class Domicilio {

    String calle
    String numero
    Integer piso
    String departamento
    String barrio
    String distrito
    String localidad
    String provincia
    String pais
    String latitudPos
    String longitudPos

    static constraints = {
        calle size: 3..50
        numero size: 1..5
        piso nullable: true
        departamento nullable: true
        barrio size: 3..50
        distrito nullable: true, size: 3..50
        localidad size: 3..50
        provincia size: 3..50
        pais nullable: true, size: 3..50
        latitudPos nullable: true
        longitudPos nullable: true
    }

}
