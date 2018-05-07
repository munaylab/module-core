package org.munaylab

import java.util.Calendar
import org.munaylab.balance.Asiento
import org.munaylab.balance.AsientoCommand
import org.munaylab.balance.Categoria
import org.munaylab.balance.CategoriaCommand
import org.munaylab.balance.TipoAsiento
import org.munaylab.balance.TipoFiltro
import org.munaylab.osc.Organizacion
import grails.gorm.transactions.Transactional
import grails.gorm.transactions.NotTransactional

@Transactional
class BalanceService {

    def obtenerCategorias(TipoAsiento tipo) {
        Categoria.createCriteria().list {
            eq 'tipo', tipo
            isNull 'categoriaPadre'
        }
    }

    def cancelarAsiento(Long id) {
        Asiento asiento = Asiento.findByIdAndEnabled(id, true)
        if (!asiento) return
        asiento.enabled = false
        asiento.save()
    }

    def actualizarAsiento(AsientoCommand command) {
        if (!command || !command.validate()) return null

        Categoria categoria = command.categoria.nuevaCategoria()
                ? actualizarCategoria(command.categoria) : Categoria.get(command.categoria.id)

        Asiento asiento = command.id ? Asiento.get(command.id) : new Asiento()
        asiento.fecha = command.fecha
        asiento.monto = command.monto
        asiento.detalle = command.detalle
        asiento.tipo = command.esIngreso ? TipoAsiento.INGRESO : TipoAsiento.EGRESO
        asiento.categoria = categoria
        if (!asiento.organizacion)
            asiento.organizacion = Organizacion.get(command.orgId)
        asiento.save()
    }

    def actualizarCategoria(CategoriaCommand command) {
        if (!command || !command.validate()) return null

        Categoria categoria = null
        if (command.nuevaCategoria()) {
            Categoria categoriaPadre = Categoria.get(command.idCategoriaPadre)
            categoria = new Categoria(command.properties)
            categoria.nombre = categoria.nombre.toLowerCase()
            if (categoriaPadre) {
                categoriaPadre.addToSubcategorias(categoria)
                categoriaPadre.save()
            } else {
                categoria.save()
            }
        } else {
            categoria = Categoria.get(command.id)
            categoria.nombre = command.nombre.toLowerCase()
            categoria.detalle = command.detalle
            categoria.save()
        }
        categoria
    }

    @Transactional(readOnly = true)
    def obtenerAsientos(Organizacion org, TipoAsiento tipo, String nombreCategoria = null,
            Date desde = null, Date hasta = null) {
        Asiento.createCriteria().list {
            eq 'organizacion', org
            eq 'enabled', true
            if (tipo != TipoAsiento.NONE) {
                eq 'tipo', tipo
            }
            if (nombreCategoria) {
                categoria {
                    eq 'tipo', tipo
                    like 'nombre', nombreCategoria.toLowerCase()
                }
            }
            if (desde) {
                between 'fecha', desde.clearTime(), hasta
            }
        }
    }
    @Transactional(readOnly = true)
    def obtenerEgresos(Organizacion org, String nombreCategoria = null,
            Date desde = null, Date hasta = null) {
        obtenerAsientos(org, TipoAsiento.EGRESO, nombreCategoria, desde, hasta)
    }
    @Transactional(readOnly = true)
    def obtenerEgresosEntre(Organizacion org, Date desde, Date hasta) {
        obtenerEgresos(org, null, desde, hasta)
    }
    @Transactional(readOnly = true)
    def obtenerEgresosDeCategoriaEntre(Organizacion org, String categoria,
            Date desde, Date hasta) {
        obtenerEgresos(org, categoria, desde, hasta)
    }

    @Transactional(readOnly = true)
    def obtenerIngresos(Organizacion org, String nombreCategoria = null,
            Date desde = null, Date hasta = null) {
        obtenerAsientos(org, TipoAsiento.INGRESO, nombreCategoria, desde, hasta)
    }
    @Transactional(readOnly = true)
    def obtenerIngresosEntre(Organizacion org, Date desde, Date hasta) {
        obtenerIngresos(org, null, desde, hasta)
    }
    @Transactional(readOnly = true)
    def obtenerIngresosDeCategoriaEntre(Organizacion org, String categoria,
            Date desde, Date hasta) {
        obtenerIngresos(org, categoria, desde, hasta)
    }
    @Transactional(readOnly = true)
    def obtenerUltimosMovimientos(Organizacion org) {
        Date desde = new Date() - 14
        obtenerAsientos(org, TipoAsiento.NONE, null, desde, new Date())
    }

    //TODO testear
    @Transactional(readOnly = true)
    def calcularBalanceTotal(Organizacion org, Date desde = null, Date hasta = null) {
        def result = Asiento.createCriteria().list {
            eq 'organizacion', org
            eq 'enabled', true
            if (desde) {
                between 'fecha', desde.clearTime(), hasta
            }
            projections {
                sum 'monto'
                groupProperty 'tipo'
            }
            order 'tipo', 'asc'
        }
        if (result.empty)
            return 0
        if (result.first()[1] != TipoAsiento.EGRESO)
            return result.first()[0]
        if (result.last()[1] != TipoAsiento.INGRESO)
            return -result.first()[0]
        int totalEgreso = result.first()[0]
        int totalIngreso = result.last()[0]
        totalIngreso - totalEgreso
    }

    //TODO testear
    @Transactional(readOnly = true)
    def obtenerBalancePorPeriodo(Organizacion org, TipoAsiento tipo, TipoFiltro filtro = TipoFiltro.ANUAL) {
        def result = Asiento.createCriteria().list {
            eq 'organizacion', org
            eq 'tipo', tipo
            eq 'enabled', true
            between 'fecha', filtro.fechaDesde, new Date()
            projections {
                rowCount()
                sum 'monto'
                if (TipoFiltro.SEMANAL == filtro)
                    groupProperty 'semana'
                if (TipoFiltro.SEMANAL == filtro || TipoFiltro.MENSUAL == filtro)
                    groupProperty 'mes'
                groupProperty 'anio'
            }
            order 'anio', 'asc'
            if (TipoFiltro.SEMANAL == filtro || TipoFiltro.MENSUAL == filtro)
                order 'mes', 'asc'
            if (TipoFiltro.SEMANAL == filtro)
                order 'semana', 'asc'
        }

        parsearResultadoALista(result, tipo, filtro)
    }

    //TODO testear
    @NotTransactional
    private List<Asiento> parsearResultadoALista(result, TipoAsiento tipo, TipoFiltro filtro) {
        def list = []
        result.each {
            Asiento asiento = new Asiento(detalle: it[0], monto: it[1], tipo: tipo)
            int posicion = 2
            if (TipoFiltro.SEMANAL == filtro)
                asiento.semana = it[posicion++]
            if (TipoFiltro.SEMANAL == filtro || TipoFiltro.MENSUAL == filtro)
                asiento.mes = it[posicion++] + 1
            asiento.anio = it[posicion]

            if (TipoFiltro.SEMANAL == filtro)
                asiento.fecha = new Date().parse('w/MM/yyyy', "${asiento.semana}/${asiento.mes}/${asiento.anio}")
            if (TipoFiltro.MENSUAL == filtro)
                asiento.fecha = new Date().parse('MM/yyyy', "${asiento.mes}/${asiento.anio}")
            if (TipoFiltro.ANUAL == filtro)
                asiento.fecha = new Date().parse('yyyy', "${asiento.anio}")
            list << asiento
        }
        list
    }

    //TODO testear
    @Transactional(readOnly = true)
    def obtenerBalanceClasificado(Organizacion org) {
        def balance = Asiento.createCriteria().list {
            createAlias 'categoria', 'cat'
            eq 'organizacion', org
            eq 'enabled', true
            isNull 'cat.categoriaPadre'
            projections {
                sum 'monto'
                property 'tipo'
                groupProperty 'cat.nombre'
            }
        }
        balance.groupBy { it[1].toString().toLowerCase() }
    }

    //TODO testear
    @Transactional(readOnly = true)
    def obtenerBalanceAnual(Organizacion org, int anio = new Date()[Calendar.YEAR]) {
        Date desde = new Date().parse('yyyy', "${anio}")
        Date hasta = new Date().parse('yyyy-MM-dd', "${anio}-12-31")

        def result = Asiento.createCriteria().list {
            eq 'organizacion', org
            eq 'enabled', true
            between 'fecha', desde, hasta
            projections {
                rowCount()
                sum 'monto'
                groupProperty 'mes'
                groupProperty 'anio'
                groupProperty 'tipo'
            }
            order 'mes', 'asc'
        }
        parsearBalanceAnual(result)
    }

    //TODO testear
    @NotTransactional
    private Map parsearBalanceAnual(result) {
        if (result.empty) return null
        def maps = [:]
        def element = result.first()
        int anio = element[3]

        (1..12).each { mes ->
            def egreso = result.find { it[2] == (mes - 1) && it[4] == TipoAsiento.EGRESO }
            def ingreso = result.find { it[2] == (mes - 1) && it[4] == TipoAsiento.INGRESO }
            if (egreso) egreso = egreso[1]
            if (ingreso) ingreso = ingreso[1]
            maps << ["${anio}-${mes}": [egreso: egreso, ingreso: ingreso]]
        }
        maps
    }

}
