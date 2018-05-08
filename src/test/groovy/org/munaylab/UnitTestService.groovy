package org.munaylab

import org.munaylab.contacto.Contacto
import org.munaylab.factory.Builder
import org.munaylab.osc.Organizacion
import org.munaylab.osc.Voluntario
import org.munaylab.osc.EstadoOrganizacion
import org.munaylab.osc.TipoOrganizacion
import org.munaylab.user.User
import org.munaylab.security.Token
import org.munaylab.security.UserRole

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

interface UnitTestService {

    static final DATOS_REGISTRO_VALIDOS = [denominacion: 'Fundación MunayLab',
        tipo: TipoOrganizacion.FUNDACION, nombre: 'Augusto', apellido: 'Caligares',
        email: 'mcaligares@gmail.com', telefono: '1234567',
        objeto: 'brindar soluciones a las organizaciones sociales']

    static final DATOS_REGISTRO_INVALIDOS = [denominacion: 'Fundación MunayLab',
        tipo: TipoOrganizacion.FUNDACION, nombre: 'Miguel', apellido: 'Caligares',
        objeto: 'brindar soluciones a las organizaciones sociales']

    static final DATOS_ORG_VALIDOS = [nombre: 'Fundación MunayLab', tipo: TipoOrganizacion.FUNDACION,
        nombreURL: 'fundacion_munaylab', estado: EstadoOrganizacion.PENDIENTE,
        objeto: 'brindar soluciones a las organizaciones sociales']

    static final DATOS_ORG_VERIFICADA = [nombre: 'Fundación MunayLab', tipo: TipoOrganizacion.FUNDACION,
        nombreURL: 'fundacion_munaylab', estado: EstadoOrganizacion.VERIFICADA,
        objeto: 'brindar soluciones a las organizaciones sociales']

    static final DATOS_DOMICILIO_VALIDOS = [calle: 'Peat 32', numero: '570', barrio: 'San Pedrito',
        localidad: 'San Salvador de Jujuy', provincia: 'Jujuy']

    static final DATOS_USER = [nombre: 'Augusto', apellido: 'Caligares',
        username: 'mcaligares@gmail.com', password: 'password']

    static final DATOS_VOLUNTARIO_VALIDOS = [orgId: 1, email: 'voluntario@munaylab.org', tipoUsuarioId: 1,
        nombre: 'miguel', apellido: 'caligares', nacimiento: new Date().parse('yyyy/MM/dd', "1990/1/1")]

    static final DATOS_VOLUNTARIO_INVALIDOS = [orgId: 1, tipoUsuarioId: 1,
        nombre: 'miguel', apellido: 'caligares', nacimiento: new Date()]

    static final DATOS_EGRESO = [monto: 100.0, fecha: new Date(), detalle: 'detalle de asiento',
        esIngreso: false, orgId: 1]

    static final DATOS_INGRESO = [monto: 10.0, fecha: new Date(), detalle: 'detalle de asiento',
        esIngreso: true, orgId: 1]
}
