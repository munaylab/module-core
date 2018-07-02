package munaylab.core

import org.munaylab.ods.Indicador
import org.munaylab.ods.Meta
import org.munaylab.ods.Objetivo

class BootStrap {

    def init = { servletContext ->
        agregarLosODS()
    }
    def destroy = {}

    private void agregarLosODS() {
        agregarObjetivosDeFinDeLaPobreza()
    }

    private void agregarObjetivosDeFinDeLaPobreza() {
        def objetivo = new Objetivo().with {
            posicion = 1
            nombre = 'Fin de la pobreza'
            objetivo = 'Poner fin a la pobreza en todas sus formas en todo el mundo'
            descripcion = 'La pobreza va más allá de la falta de ingresos y recursos. Entre sus manifestaciones se incluyen el hambre y la malnutrición, el acceso limitado a la educación y a otros servicios básicos, la discriminación y la exclusión sociales y la falta de participación en la adopción de decisiones. El crecimiento económico debe ser inclusivo con el fin de crear empleos sostenibles y promover la igualdad.'
            it
        }
        objetivo.addToMetas(new Meta().with {
            posicion = "1"
            descripcion = "Erradicar para todas las personas y en todo el mundo la pobreza extrema."
            it
        })
        objetivo.addToMetas(new Meta().with {
            posicion = "2"
            descripcion = "Reducir al menos a la mitad la proporción de hombres, mujeres y niños de todas las edades que viven en la pobreza en todas sus dimensiones con arreglo a las definiciones nacionales."
            it
        })
        objetivo.addToMetas(new Meta().with {
            posicion = "3"
            descripcion = "Implementar a nivel nacional sistemas y medidas de protección social, incluidos niveles mínimos, y lograr una amplia cobertura de las personas pobres y vulnerables."
            it
        })
        objetivo.addToMetas(new Meta().with {
            posicion = "4"
            descripcion = "Garantizar que todos los hombres y mujeres, tengan los mismos derechos a los recursos económicos y acceso a los servicios básicos, la propiedad y el control de la tierra y otros bienes."
            it
        })
        objetivo.addToMetas(new Meta().with {
            posicion = "5"
            descripcion = "Fomentar la resiliencia en las personas que se encuentran en situaciones de vulnerabilidad y reducir su exposición a los fenómenos extremos relacionados con el clima y otras perturbaciones."
            it
        })
        objetivo.save(flush: true, failOnError: true)
    }
}
