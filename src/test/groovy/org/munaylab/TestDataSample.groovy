package org.munaylab

import org.munaylab.osc.EstadoOrganizacion
import org.munaylab.osc.TipoOrganizacion

interface TestDataSample {

    static final DATOS_DE_REGISTRO_COMMAND_INVALIDOS = [denominacion: 'Fundación MunayLab',
        tipo: TipoOrganizacion.FUNDACION, nombre: 'Miguel', apellido: 'Caligares',
        objeto: 'brindar soluciones a las organizaciones sociales']

    static final DATOS_DE_REGISTRO_COMMAND_VALIDOS = [denominacion: 'Fundación MunayLab',
        tipo: TipoOrganizacion.FUNDACION, nombre: 'Augusto', apellido: 'Caligares',
        email: 'mcaligares@gmail.com', telefono: '1234567',
        objeto: 'brindar soluciones a las organizaciones sociales',
        descripcion: 'brindar soluciones a las organizaciones sociales']

    static final DATOS_DE_ORGANIZACION_VALIDOS = [nombre: 'Fundación MunayLab',
        tipo: TipoOrganizacion.FUNDACION, nombreURL: 'fundacion_munaylab',
        estado: EstadoOrganizacion.PENDIENTE,
        objeto: 'brindar soluciones a las organizaciones sociales',
        descripcion: 'brindar soluciones a las organizaciones sociales']

    static final DATOS_DE_USUARIO_VALIDOS = [nombre: 'Augusto', apellido: 'Caligares',
        username: 'mcaligares@gmail.com', password: 'password']
}
