package munaylab.core

import org.munaylab.ods.Indicador
import org.munaylab.ods.Meta
import org.munaylab.ods.Objetivo

class BootStrap {

    def objetivosService
    def grailsApplication

    def init = { servletContext ->
        agregarLosODS()
    }
    def destroy = {}

    private void agregarLosODS() {
        grailsApplication.config.ods.each { objetivoConfig ->
            objetivosService.guardarObjetivoDesdeConfiguracion(objetivoConfig)
        }
    }

}
