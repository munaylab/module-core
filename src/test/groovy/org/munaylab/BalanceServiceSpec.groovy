package org.munaylab

import org.munaylab.balance.Categoria
import org.munaylab.balance.CategoriaCommand
import org.munaylab.balance.Asiento
import org.munaylab.balance.TipoAsiento

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class BalanceServiceSpec extends Specification
        implements ServiceUnitTest<BalanceService>, DataTest {

    void setupSpec() {
        mockDomains Asiento, Categoria
    }

    void 'agregar egreso'() {
        given:
        Builder.crearOrganizacionConDatos().save(flush: true)
        and:
        def command = Builder.egresoCommand
        command.categoria = Builder.categoriaEgresoCommand
        when:
        def egreso = service.actualizarAsiento(command)
        then:
        egreso != null && Asiento.countByEnabled(true) == 1
        Categoria.count() == 1
    }
    void 'modificar egreso'() {
        given:
        def egreso = Builder.crearEgreso()
        egreso.organizacion = Builder.crearOrganizacionConDatos().save(flush: true)
        egreso.save(flush: true)
        and:
        def command = Builder.egresoCommand
        command.id = 1
        command.categoria = new CategoriaCommand(id: 1, tipo: TipoAsiento.EGRESO)
        when:
        egreso = service.actualizarAsiento(command)
        then:
        egreso != null && Asiento.countByEnabled(true) == 1
        egreso.monto == command.monto && Asiento.get(1).monto == command.monto
        egreso.detalle == command.detalle && Asiento.get(1).detalle == command.detalle
        Categoria.count() == 1
    }
    void 'cancelar egreso'() {
        given:
        def egreso = Builder.crearEgreso()
        egreso.organizacion = Builder.crearOrganizacionConDatos().save(flush: true)
        egreso.save(flush: true)
        when:
        service.cancelarAsiento(egreso.id)
        then:
        Asiento.countByEnabled(true) == 0
        Asiento.countByEnabled(false) == 1
        Categoria.count() == 1
    }
    void 'agregar ingreso'() {
        given:
        Builder.crearOrganizacionConDatos().save(flush: true)
        and:
        def command = Builder.ingresoCommand
        command.categoria = Builder.categoriaIngresoCommand
        when:
        def ingreso = service.actualizarAsiento(command)
        then:
        ingreso != null && Asiento.countByEnabled(true) == 1
        Categoria.count() == 1
    }
    void 'modificar ingreso'() {
        given:
        def ingreso = Builder.crearIngreso()
        ingreso.organizacion = Builder.crearOrganizacionConDatos().save(flush: true)
        ingreso.save(flush: true)
        and:
        def command = Builder.ingresoCommand
        command.id = 1
        command.categoria = new CategoriaCommand(id: 1, tipo: TipoAsiento.INGRESO)
        when:
        ingreso = service.actualizarAsiento(command)
        then:
        ingreso != null && Asiento.countByEnabled(true) == 1
        ingreso.monto == command.monto && Asiento.get(1).monto == command.monto
        ingreso.detalle == command.detalle && Asiento.get(1).detalle == command.detalle
        Categoria.count() == 1
    }
    void 'cancelar ingreso'() {
        given:
        def ingreso = Builder.crearIngreso()
        ingreso.organizacion = Builder.crearOrganizacionConDatos().save(flush: true)
        ingreso.save(flush: true)
        when:
        service.cancelarAsiento(ingreso.id)
        then:
        Asiento.countByEnabled(true) == 0
        Asiento.countByEnabled(false) == 1
        Categoria.count() == 1
    }
    void 'crear categoria'() {
        when:
        service.actualizarCategoria(Builder.categoriaIngresoCommand)
        then:
        Categoria.count() == 1
    }
    void 'crear subcategoria'() {
        given:
        Builder.crearCategoria().save(flush: true)
        def command = Builder.categoriaIngresoCommand
        command.idCategoriaPadre = 1
        when:
        def categoria = service.actualizarCategoria(command)
        then:
        categoria != null && Categoria.count() == 2
        Categoria.get(1).subcategorias.size() == 1
    }
    void 'modificar categoria'() {
        given:
        Builder.crearCategoria().save(flush: true)
        def command = Builder.categoriaIngresoCommand
        command.id = 1
        when:
        def categoria = service.actualizarCategoria(command)
        then:
        categoria != null && Categoria.count() == 1
        categoria.nombre == command.nombre
        categoria.detalle == command.detalle
        Categoria.get(1).nombre == command.nombre
        Categoria.get(1).detalle == command.detalle
    }
    void 'obtener categorias de egresos'() {
        given:
        def categoria = Builder.crearCategoria('egreso', TipoAsiento.EGRESO)
        5.times {
            categoria.addToSubcategorias(Builder.crearCategoria("subcategoria $it", TipoAsiento.EGRESO))
        }
        categoria.save(flush: true)
        when:
        def categorias = service.obtenerCategorias(TipoAsiento.EGRESO)
        then:
        categorias.size() == 1
        categorias.first().subcategorias.size() == 5
    }
    void 'obtener categorias de ingresos'() {
        given:
        def categoria = Builder.crearCategoria('ingreso')
        5.times {
            categoria.addToSubcategorias(Builder.crearCategoria("subcategoria $it"))
        }
        categoria.save(flush: true)
        when:
        def categorias = service.obtenerCategorias(TipoAsiento.INGRESO)
        then:
        categorias.size() == 1
        categorias.first().subcategorias.size() == 5
    }

    /* Metodo groupProperty no funciona en unit test
    void 'calcular balance sin fechas'() {
        given:
        def org = Builder.crearOrganizacionConDatos().save(flush: true)
        def categoriaEgresos = Builder.crearCategoriaEgreso().save(flush: true)
        def categoriaIngresos = Builder.crearCategoriaIngreso().save(flush: true)
        crearAsientos(org, categoriaEgresos, TipoAsiento.EGRESO, [egreso1, egreso2, egreso3])
        crearAsientos(org, categoriaIngresos, TipoAsiento.INGRESO, [ingreso1, ingreso2, ingreso3])
        expect:
        service.calcularBalanceTotal(org) == total
        where:
        egreso1 | egreso2 | egreso3 | ingreso1 | ingreso2 | ingreso3 | total
        10.0    | 10.0    | 10.0    | 20.0     | 20.0     | 20.0     | 30.0
        20.0    | 10.0    | 10.0    | 20.0     | 20.0     | 20.0     | 20.0
        20.0    | 20.0    | 10.0    | 10.0     | 10.0     | 10.0     | -20.0
    }
    */
    void crearAsientos(org, categoria, tipo, values) {
        values.each {
            new Asiento(fecha: new Date(), monto: it, detalle: 'asiento',
                categoria: categoria, organizacion: org, tipo: tipo).save(flush: true, failOnError: true)
        }
    }
    /* Metodo groupProperty no funciona en unit test
    void 'calcular balance con fechas'() {
        given:
        def org = Builder.crearOrganizacionConDatos().save(flush: true)
        def categoriaEgresos = Builder.crearCategoriaEgreso().save(flush: true)
        def categoriaIngresos = Builder.crearCategoriaIngreso().save(flush: true)
        crearAsientosConFechas(org, categoriaEgresos, TipoAsiento.EGRESO, egreso)
        crearAsientosConFechas(org, categoriaIngresos, TipoAsiento.INGRESO, ingreso)
        expect:
        service.calcularBalanceTotal(org, desde, hasta) == total
        where:
        egreso                | ingreso                | total | desde         | hasta
        [40.0, new Date() -2] | [100.0, new Date() -3] | 60.0  | new Date() -3 | new Date() -1
        [90.0, new Date() -5] | [100.0, new Date() -3] | 100.0 | new Date() -3 | new Date() -1
        [90.0, new Date() -5] | [100.0, new Date() -5] | 0.0   | new Date() -1 | new Date() -1
        [90.0, new Date() -1] | [50.0, new Date() -1]  | -40.0 | new Date() -2 | new Date() -1
    }
    */
    void crearAsientosConFechas(org, categoria, tipo, value) {
        new Asiento(fecha: value[1], monto: value[0], detalle: 'asiento',
            categoria: categoria, organizacion: org, tipo: tipo).save(flush: true, failOnError: true)
    }

    void 'obtener egresos'() {
        given:
        def org = Builder.crearOrganizacionConDatos().save(flush: true)
        def categoria = Builder.crearCategoriaEgreso().save(flush: true)
        crearAsientos(org, categoria, TipoAsiento.EGRESO, [10.0, 20.0, 30.0, 40.0])
        when:
        def list = service.obtenerEgresos(org, 'nueva categoria', new Date(), new Date() + 1)
        then:
        list.size() == 4
    }
    void 'obtener egresos de una categoria'() {
        given:
        def org = Builder.crearOrganizacionConDatos().save(flush: true)
        def categoria = Builder.crearCategoria('categoria', TipoAsiento.EGRESO).save(flush: true)
        def otraCategoria = Builder.crearCategoriaEgreso().save(flush: true)
        crearAsientos(org, categoria, TipoAsiento.EGRESO, [10.0, 20.0, 30.0, 40.0])
        crearAsientos(org, otraCategoria, TipoAsiento.EGRESO, [10.0, 20.0, 30.0, 40.0])
        when:
        def list = service.obtenerEgresos(org, 'categoria')
        then:
        list.size() == 4
    }
    void 'obtener egresos entre fechas'() {
        given:
        def org = Builder.crearOrganizacionConDatos().save(flush: true)
        def categoria = Builder.crearCategoriaEgreso().save(flush: true)
        crearAsientosConFechas(org, categoria, TipoAsiento.EGRESO, egreso)
        crearAsientosConFechas(org, categoria, TipoAsiento.EGRESO, egreso)
        crearAsientosConFechas(org, categoria, TipoAsiento.EGRESO, otroEgreso)
        when:
        def list = service.obtenerEgresosEntre(org, new Date() -1, new Date() +1)
        then:
        list.size() == 2
        where:
        egreso                | otroEgreso
        [40.0, new Date() -1] | [30.0, new Date() -3]
    }
    void 'obtener egresos de categoria entre fechas'() {
        given:
        def org = Builder.crearOrganizacionConDatos().save(flush: true)
        def categoria = Builder.crearCategoria('categoria', TipoAsiento.EGRESO).save(flush: true)
        def otraCategoria = Builder.crearCategoriaEgreso().save(flush: true)
        crearAsientosConFechas(org, categoria, TipoAsiento.EGRESO, egreso)
        crearAsientosConFechas(org, otraCategoria, TipoAsiento.EGRESO, egreso)
        crearAsientosConFechas(org, categoria, TipoAsiento.EGRESO, otroEgreso)
        when:
        def list = service.obtenerEgresosDeCategoriaEntre(org, 'categoria', new Date() -1, new Date() +1)
        then:
        list.size() == 1
        where:
        egreso                | otroEgreso
        [40.0, new Date() -1] | [30.0, new Date() -3]
    }
    void 'obtener ingresos'() {
        given:
        def org = Builder.crearOrganizacionConDatos().save(flush: true)
        def categoria = Builder.crearCategoriaIngreso().save(flush: true)
        crearAsientos(org, categoria, TipoAsiento.INGRESO, [10.0, 20.0, 30.0, 40.0])
        when:
        def list = service.obtenerIngresos(org, 'nueva categoria', new Date(), new Date() + 1)
        then:
        list.size() == 4
    }
    void 'obtener ingresos de una categoria'() {
        given:
        def org = Builder.crearOrganizacionConDatos().save(flush: true)
        def categoria = Builder.crearCategoria('categoria', TipoAsiento.INGRESO).save(flush: true)
        def otraCategoria = Builder.crearCategoriaIngreso().save(flush: true)
        crearAsientos(org, categoria, TipoAsiento.INGRESO, [10.0, 20.0, 30.0, 40.0])
        crearAsientos(org, otraCategoria, TipoAsiento.INGRESO, [10.0, 20.0, 30.0, 40.0])
        when:
        def list = service.obtenerIngresos(org, 'categoria')
        then:
        list.size() == 4
    }
    void 'obtener ingresos entre fechas'() {
        given:
        def org = Builder.crearOrganizacionConDatos().save(flush: true)
        def categoria = Builder.crearCategoriaIngreso().save(flush: true)
        crearAsientosConFechas(org, categoria, TipoAsiento.INGRESO, ingreso)
        crearAsientosConFechas(org, categoria, TipoAsiento.INGRESO, ingreso)
        crearAsientosConFechas(org, categoria, TipoAsiento.INGRESO, otroIngreso)
        when:
        def list = service.obtenerIngresosEntre(org, new Date() -1, new Date() +1)
        then:
        list.size() == 2
        where:
        ingreso               | otroIngreso
        [40.0, new Date() -1] | [30.0, new Date() -3]
    }
    void 'obtener ingresos de categoria entre fechas'() {
        given:
        def org = Builder.crearOrganizacionConDatos().save(flush: true)
        def categoria = Builder.crearCategoria('categoria', TipoAsiento.INGRESO).save(flush: true)
        def otraCategoria = Builder.crearCategoriaEgreso().save(flush: true)
        crearAsientosConFechas(org, categoria, TipoAsiento.INGRESO, ingreso)
        crearAsientosConFechas(org, otraCategoria, TipoAsiento.INGRESO, ingreso)
        crearAsientosConFechas(org, categoria, TipoAsiento.INGRESO, otroIngreso)
        when:
        def list = service.obtenerIngresosDeCategoriaEntre(org, 'categoria', new Date() -1, new Date() +1)
        then:
        list.size() == 1
        where:
        ingreso               | otroIngreso
        [40.0, new Date() -1] | [30.0, new Date() -3]
    }
    /*void 'obtener reporte de ingresos mensual'() {
        given:
        def org = Builder.crearOrganizacionConDatos().save(flush: true)
        def cat = Builder.crearCategoria('categoria', TipoAsiento.INGRESO).save(flush: true)
        5.times {
            new Asiento(monto: 100.0, detalle: 'ingreso', fecha: new Date().parse('dd/MM/yyyy', "01/01/2017"), mes: 1,
                categoria: cat, tipo: TipoAsiento.INGRESO, organizacion: org).save(failOnError: true)
        }
        5.times {
            new Asiento(monto: 100.0, detalle: 'ingreso', fecha: new Date().parse('dd/MM/yyyy', "01/05/2017"), mes: 5,
                categoria: cat, tipo: TipoAsiento.INGRESO, organizacion: org).save(failOnError: true)
        }
        assert Asiento.count() == 10
        when:
        def list = service.obtenerBalancePorPeriodo(org, TipoAsiento.INGRESO, 'mes')
        then:
        list.size() == 2
    }

    private Date date(dia, mes, anio = 2017) {
        new Date().parse('dd/MM/yyyy', "$dia/$mes/$anio")
    }*/
}
