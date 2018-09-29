package org.munaylab.components

class Panel {
    String icon
    String style
    String name
    String value
    String link
}

class PanelProgramas extends Panel {
    PanelProgramas() {
        icon = 'fa-archive'
        style = 'panel-primary'
        link = 'planificacion'
    }
}

class PanelProyectos extends Panel {
    PanelProyectos() {
        icon = 'fa-folder'
        style = 'panel-primary'
        link = 'planificacion'
    }
}

class PanelActividades extends Panel {
    PanelActividades() {
        icon = 'fa-clone'
        style = 'panel-primary'
        link = 'planificacion'
    }
}

class PanelEventos extends Panel {
    PanelEventos() {
        icon = 'fa-calendar'
        style = 'panel-primary'
        link = 'planificacion'
    }
}

class PanelOrganizaciones extends Panel {
    PanelOrganizaciones() {
        icon = 'fa-users'
        style = 'panel-primary'
    }
}

class PanelVoluntarios extends Panel {
    PanelVoluntarios() {
        icon = 'fa-users'
        style = 'panel-primary'
    }
}

class EventsPanel extends Panel {
    EventsPanel() {
        icon = 'fa-calendar'
        style = 'panel-green'
    }
}

class MessagesPanel extends Panel {
    MessagesPanel() {
        icon = 'fa-envelope'
        style = 'panel-yellow'
    }
}

class StatePanel extends Panel {
    StatePanel() {
        icon = 'fa-line-chart'
        style = 'panel-red'
    }
}
