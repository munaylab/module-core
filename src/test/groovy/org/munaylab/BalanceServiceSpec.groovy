package org.munaylab

import org.munaylab.balance.Categoria
import org.munaylab.balance.CategoriaCommand
import org.munaylab.balance.Asiento
import org.munaylab.balance.TipoAsiento
import org.munaylab.factory.Builder

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class BalanceServiceSpec extends Specification
        implements ServiceUnitTest<BalanceService>, DataTest, UnitTestService {

    void setupSpec() {
        mockDomains Asiento, Categoria
    }

    void 'agregar egreso'() {
        given:
        Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear.save(flush: true)
        and:
        def command = Builder.asiento.command
                .conDatos(DATOS_EGRESO)
                .conCategoria(
                    Builder.asiento.categoriaCommand
                        .crearCategoriaEgreso('nueva categoria', 'detalle')
                    )
                .crear
        when:
        def egreso = service.actualizarAsiento(command)
        then:
        egreso != null && Asiento.countByEnabled(true) == 1
        Categoria.count() == 1
    }
    void 'modificar egreso'() {
        given:
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear.save(flush: true)
        def egreso = crearEgreso(org).save(flush: true)
        and:
        def command = Builder.asiento.command
                .conId(1)
                .conOrgId(1)
                .conMonto(20.0)
                .conDetalle('modificado')
                .conFecha(new Date())
                .esUnIngreso(false)
                .conCategoria(Builder.asiento.categoriaCommand.crearCategoriaEgresoConId(1))
                .crear
        when:
        egreso = service.actualizarAsiento(command)
        then:
        egreso != null && Asiento.countByEnabled(true) == 1
        egreso.monto == command.monto && Asiento.get(1).monto == command.monto
        egreso.detalle == command.detalle && Asiento.get(1).detalle == command.detalle
        Categoria.count() == 1
    }
    private crearEgreso(org) {
        Builder.asiento
                .conMonto(10.0)
                .conDetalle('egreso')
                .conFecha(new Date())
                .esUnEgreso()
                .conCategoria(Builder.asiento.crearCategoriaEgreso('categoria'))
                .conOrganizacion(org)
                .crear
    }
    void 'cancelar egreso'() {
        given:
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear.save(flush: true)
        def egreso = crearEgreso(org).save(flush: true)
        when:
        service.cancelarAsiento(egreso.id)
        then:
        Asiento.countByEnabled(true) == 0
        Asiento.countByEnabled(false) == 1
        Categoria.count() == 1
    }
    void 'agregar ingreso'() {
        given:
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear.save(flush: true)
        and:
        def command = Builder.asiento.command
                .conDatos(DATOS_INGRESO)
                .conCategoria(
                    Builder.asiento.categoriaCommand
                        .crearCategoriaIngreso('nueva categoria', 'detalle')
                    )
                .crear
        when:
        def ingreso = service.actualizarAsiento(command)
        then:
        ingreso != null && Asiento.countByEnabled(true) == 1
        Categoria.count() == 1
    }
    void 'modificar ingreso'() {
        given:
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear.save(flush: true)
        def ingreso = crearIngreso(org).save(flush: true)
        and:
        def command = Builder.asiento.command
                .conId(1)
                .conOrgId(1)
                .conMonto(20.0)
                .conDetalle('modificado')
                .conFecha(new Date())
                .esUnIngreso(true)
                .conCategoria(Builder.asiento.categoriaCommand.crearCategoriaIngresoConId(1))
                .crear
        when:
        ingreso = service.actualizarAsiento(command)
        then:
        assert ingreso.enabled == true
        ingreso != null && Asiento.countByEnabled(true) == 1
        ingreso.monto == command.monto && Asiento.get(1).monto == command.monto
        ingreso.detalle == command.detalle && Asiento.get(1).detalle == command.detalle
        Categoria.count() == 1
    }
    private crearIngreso(org) {
        Builder.asiento
                .conMonto(10.0)
                .conDetalle('ingreso')
                .conFecha(new Date())
                .esUnIngreso()
                .enabled()
                .conCategoria(Builder.asiento.crearCategoriaIngreso('categoria'))
                .conOrganizacion(org)
                .crear
    }
    void 'cancelar ingreso'() {
        given:
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear.save(flush: true)
        def ingreso = crearIngreso(org).save(flush: true)
        when:
        service.cancelarAsiento(ingreso.id)
        then:
        Asiento.countByEnabled(true) == 0
        Asiento.countByEnabled(false) == 1
        Categoria.count() == 1
    }
    void 'crear categoria'() {
        given:
        def command = Builder.asiento.categoriaCommand.crearCategoriaIngreso('categoria', 'detalle')
        when:
        service.actualizarCategoria(command)
        then:
        Categoria.count() == 1
    }
    void 'crear subcategoria'() {
        given:
        Builder.asiento.crearCategoriaIngreso('categoria').save(flush: true)
        def command = Builder.asiento.categoriaCommand.crearCategoriaIngreso('subcategoria', 'detalle')
        command.idCategoriaPadre = 1
        when:
        def categoria = service.actualizarCategoria(command)
        then:
        categoria != null && Categoria.count() == 2
        Categoria.get(1).subcategorias.size() == 1
    }
    void 'modificar categoria'() {
        given:
        Builder.asiento.crearCategoriaIngreso('categoria').save(flush: true)
        def command = Builder.asiento.categoriaCommand.crearCategoriaIngreso('modificado', 'detalle...')
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
        def categoria = Builder.asiento.crearCategoriaEgreso('egreso')
        5.times {
            categoria.addToSubcategorias(Builder.asiento.crearCategoriaEgreso("subcategoria $it"))
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
        def categoria = Builder.asiento.crearCategoriaIngreso('ingreso')
        5.times {
            categoria.addToSubcategorias(Builder.asiento.crearCategoriaIngreso("subcategoria $it"))
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
    void crearAsientos(org, categoria, tipo, values) {
        values.each {
            Builder.asiento.conFecha(new Date()).conMonto(it).conDetalle('asiento')
                .conCategoria(categoria).conOrganizacion(org).deTipo(tipo)
                .crear.save(flush: true, failOnError: true)
        }
    }
    void crearAsientosConFechas(org, categoria, tipo, value) {
        Builder.asiento.conFecha(value[1]).conMonto(value[0]).conDetalle('asiento')
            .conCategoria(categoria).conOrganizacion(org).deTipo(tipo)
            .crear.save(flush: true, failOnError: true)
    }

    void 'obtener egresos'() {
        given:
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear.save(flush: true, failOnError: true)
        def categoria = Builder.asiento.crearCategoriaEgreso('nueva categoria').save(flush: true, failOnError: true)
        crearAsientos(org, categoria, TipoAsiento.EGRESO, [10.0, 20.0, 30.0, 40.0])
        when:
        def list = service.obtenerEgresos(org, 'nueva categoria', new Date() -1, new Date() + 1)
        then:
        list.size() == 4
    }
    void 'obtener egresos de una categoria'() {
        given:
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear.save(flush: true)
        def categoria = Builder.asiento.crearCategoriaEgreso('categoria').save(flush: true)
        def otraCategoria = Builder.asiento.crearCategoriaEgreso('otro egreso').save(flush: true)
        crearAsientos(org, categoria, TipoAsiento.EGRESO, [10.0, 20.0, 30.0, 40.0])
        crearAsientos(org, otraCategoria, TipoAsiento.EGRESO, [10.0, 20.0, 30.0, 40.0])
        when:
        def list = service.obtenerEgresos(org, 'categoria')
        then:
        list.size() == 4
    }
    void 'obtener egresos entre fechas'() {
        given:
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear.save(flush: true)
        def categoria = Builder.asiento.crearCategoriaEgreso('categoria').save(flush: true)
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
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear.save(flush: true)
        def categoria = Builder.asiento.crearCategoriaEgreso('categoria').save(flush: true)
        def otraCategoria = Builder.asiento.crearCategoriaEgreso('otra categoria').save(flush: true)
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
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear.save(flush: true)
        def categoria = Builder.asiento.crearCategoriaIngreso('categoria').save(flush: true)
        crearAsientos(org, categoria, TipoAsiento.INGRESO, [10.0, 20.0, 30.0, 40.0])
        when:
        def list = service.obtenerIngresos(org, 'categoria', new Date(), new Date() + 1)
        then:
        list.size() == 4
    }
    void 'obtener ingresos de una categoria'() {
        given:
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear.save(flush: true)
        def categoria = Builder.asiento.crearCategoriaIngreso('categoria').save(flush: true)
        def otraCategoria = Builder.asiento.crearCategoriaIngreso('otra categoria').save(flush: true)
        crearAsientos(org, categoria, TipoAsiento.INGRESO, [10.0, 20.0, 30.0, 40.0])
        crearAsientos(org, otraCategoria, TipoAsiento.INGRESO, [10.0, 20.0, 30.0, 40.0])
        when:
        def list = service.obtenerIngresos(org, 'categoria')
        then:
        list.size() == 4
    }
    void 'obtener ingresos entre fechas'() {
        given:
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear.save(flush: true)
        def categoria = Builder.asiento.crearCategoriaIngreso('categoria').save(flush: true)
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
        def org = Builder.organizacion.conDatos(DATOS_ORG_VERIFICADA).crear.save(flush: true)
        def categoria = Builder.asiento.crearCategoriaIngreso('categoria').save(flush: true)
        def otraCategoria = Builder.asiento.crearCategoriaIngreso('otra categoria').save(flush: true)
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
