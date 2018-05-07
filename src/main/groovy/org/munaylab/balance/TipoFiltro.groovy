package org.munaylab.balance

enum TipoFiltro {
    SEMANAL,
    MENSUAL,
    ANUAL

    Date getFechaDesde() {
        use (groovy.time.TimeCategory) {
            switch (this) {
                case SEMANAL: return 6.week.ago
                case MENSUAL: return 1.year.ago
                case ANUAL: return 6.year.ago
                default: return new Date()
            }
        }
    }

}
