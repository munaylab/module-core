package org.munaylab.planificacion

import org.munaylab.direccion.Domicilio
import org.munaylab.osc.Organizacion

class Evento {

    String nombre
    String descripcion
    String imagen
    Date fechaIni
    Date fechaFin
    Date fechaDifusion
    Domicilio direccion
    Boolean publicado = Boolean.FALSE

    Date dateCreated
    Date lastUpdated

    static belongsTo = [organizacion: Organizacion]

    static constraints = {
        nombre size: 5..500
        descripcion size: 5..1000
        imagen nullable: true
        fechaIni validator: { val, obj ->
            if (val < new Date()) return 'menorFechaActual'
        }
        fechaFin nullable: true, validator: { val, obj ->
            if (val) {
                if (!obj.fechaIni) return 'faltaFechaInicio'
                if (obj.fechaIni && obj.fechaIni > val) return 'fechaIniDespuesDeFechaFin'
            }
        }
        fechaDifusion nullable: true, validator: { val, obj ->
            if (val) {
                if (val < new Date()) return 'menorFechaActual'
                if (!obj.direccion) return 'faltaDireccion'
                if (!obj.fechaIni) return 'faltaFechaInicio'
                if (obj.fechaIni && obj.fechaIni < val) return 'fechaIniAntesDeFechaDifusion'
            }
        }
        direccion nullable: true
    }

}
