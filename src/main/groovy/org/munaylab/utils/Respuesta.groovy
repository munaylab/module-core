package org.munaylab.utils

class Respuesta {
    def valor
    def errores

    String error
    String mensaje

    static Respuesta conValor(valor) {
        new Respuesta(valor: valor)
    }

    static Respuesta conValor(valor, String mensaje) {
        new Respuesta(valor: valor, mensaje: mensaje)
    }

    static Respuesta conErrores(errores) {
        new Respuesta(errores: errores)
    }

    static Respuesta conError(String error) {
        new Respuesta(error: error)
    }

    static Respuesta conErrores(valor, errores) {
        new Respuesta(valor: valor, errores: errores)
    }

    Map getModelo() {
        Map map = [:]
        if (valor) map << [valor: valor]
        if (error) map << [error: error]
        if (errores) map << [errores: errores]
        if (mensaje) map << [mensaje: mensaje]
        map
    }
}
