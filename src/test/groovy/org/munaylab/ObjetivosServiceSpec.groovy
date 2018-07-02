package org.munaylab

import org.munaylab.ods.Objetivo
import org.munaylab.ods.Meta
import org.munaylab.ods.Indicador
import org.munaylab.osc.Organizacion

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class ObjetivosServiceSpec extends Specification
        implements ServiceUnitTest<ObjetivosService>, DataTest, UnitTestService {

    void setupSpec() {
        mockDomains Organizacion, Objetivo, Meta, Indicador
    }

    def setup() {

    }

    void 'agregar un objetivo'() {
        given:
        def objetivo = new Objetivo(DATOS_OBJETIVO).save(flush: true)
        def org = new Organizacion(DATOS_ORG_VERIFICADA).save(flush: true)
        when:
        org = service.agregarObjetivo(org, objetivo.id)
        then:
        !org.hasErrors()
        org.objetivos.size() == 1
    }
    void 'agregar un objetivo mas de una vez'() {
        given:
        def objetivo = new Objetivo(DATOS_OBJETIVO).save(flush: true)
        def org = new Organizacion(DATOS_ORG_VERIFICADA).save(flush: true)
        when:
        5.times {
            org = service.agregarObjetivo(org, objetivo.id)
        }
        then:
        !org.hasErrors()
        org.objetivos.size() == 1
    }
    void 'eliminar unico objetivo'() {
        given:
        def objetivo = new Objetivo(DATOS_OBJETIVO).save(flush: true)
        def org = new Organizacion(DATOS_ORG_VERIFICADA).save(flush: true)
        org.addToObjetivos(objetivo).save(flush: true)
        assert org.objetivos.size() == 1
        when:
        service.eliminarObjetivo(org, objetivo.id)
        then:
        org.objetivos.size() == 0
    }
    void 'eliminar un objetivo'() {
        given:
        def org = new Organizacion(DATOS_ORG_VERIFICADA)
                .addToObjetivos(new Objetivo(DATOS_OBJETIVO).save(flush: true))
                .addToObjetivos(new Objetivo(DATOS_OBJETIVO).save(flush: true))
                .addToObjetivos(new Objetivo(DATOS_OBJETIVO).save(flush: true))
                .save(flush: true)
        assert org.objetivos.size() == 3
        when:
        service.eliminarObjetivo(org, 1)
        then:
        org.objetivos.size() == 2
    }
}
