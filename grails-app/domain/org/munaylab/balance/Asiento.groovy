package org.munaylab.balance

import org.munaylab.osc.Organizacion

class Asiento {

    Date fecha
    Double monto
    String detalle
    Date dateCreated
    Boolean enabled = Boolean.TRUE
    TipoAsiento tipo
    Categoria categoria
    Programable programable
    Organizacion organizacion
    Integer semana = 0
    Integer mes = 0
    Integer anio = 0

    static constraints = {
        monto min: 0d, max: 999999d
        detalle size: 5..500
        programable nullable: true
    }

    void actualizarFechaDeFiltro() {
        this.semana = fecha[Calendar.WEEK_OF_YEAR]
        this.mes = fecha[Calendar.MONTH]
        this.anio = fecha[Calendar.YEAR]
    }
    def beforeInsert() {
        actualizarFechaDeFiltro()
    }
    def beforeUpdate() {
        actualizarFechaDeFiltro()
    }

    String getBalanceData(String formatDate = 'yyyy') {
        "{tiempo:'${fecha.format(formatDate)}', monto:$monto},"
    }

}
