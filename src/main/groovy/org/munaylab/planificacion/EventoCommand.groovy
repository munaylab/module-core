package org.munaylab.planificacion

import org.munaylab.direccion.DomicilioCommand

class EventoCommand implements grails.validation.Validateable {

    Long id
    Long orgId
    String nombre
    String descripcion
    String imagen
    Date fechaIni
    String horaIni
    Date fechaFin
    String horaFin
    Date fechaDifusion
    DomicilioCommand direccion

    static constraints = {
        id nullable: true
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

    Date componerFechaInicial() {
        String date = fechaIni.format('dd/MM/yyyy') + " $horaIni"
        return new Date().parse('dd/MM/yyyy HH:mm', date)
    }

    Date componerFechaFinal() {
        String date = fechaFin.format('dd/MM/yyyy') + " $horaFin"
        return new Date().parse('dd/MM/yyyy HH:mm', date)
    }

    Date componerFechaDifusion() {
        String date = fechaDifusion.format('dd/MM/yyyy') + ' 08:00'
        return new Date().parse('dd/MM/yyyy HH:mm', date)
    }
}
