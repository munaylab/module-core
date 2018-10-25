package org.munaylab

import org.munaylab.contacto.Contacto
import org.munaylab.contacto.ContactoCommand
import org.munaylab.contacto.TipoContacto
import org.munaylab.direccion.Domicilio
import org.munaylab.osc.Organizacion
import org.munaylab.osc.OrganizacionCommand
import org.munaylab.osc.EstadoOrganizacion
import org.munaylab.osc.RegistroCommand
import org.munaylab.osc.UserOrganizacion
import org.munaylab.osc.Voluntario
import org.munaylab.osc.VoluntarioCommand
import org.munaylab.user.User
import org.munaylab.user.UserCommand
import org.munaylab.categoria.TipoUsuario
import org.munaylab.security.ConfirmacionCommand
import org.munaylab.security.Role
import org.munaylab.security.Token
import org.munaylab.security.TipoToken
import org.munaylab.security.UserRole
import org.munaylab.utils.TipoEmail
import org.munaylab.utils.Respuesta

import grails.gorm.transactions.Transactional

@Transactional
class OrganizacionService {

    def securityService

    Organizacion registrar(RegistroCommand command) {
        if (siLosParametrosSonInvalidos(command)) return null

        User representante = obtenerRepresentanteConDatosDeContacto(command)
        Organizacion organizacion = obtenerOrganizacion(command)
        organizacion = asignarAdminDeOrganizacion(representante, organizacion)
        organizacion.save()

        if (!organizacion.hasErrors()) {
            log.info "Nueva organizacion registrada id = ${organizacion.id}"
            asignarRolDeAdminDeOrganizacion(representante).save()
        }
        return organizacion
    }

    private boolean siLosParametrosSonInvalidos(command) {
        return !command || !command.validate()
    }

    private User obtenerRepresentanteConDatosDeContacto(RegistroCommand command) {
        return new User().with {
            username = command.email
            nombre = command.nombre
            apellido = command.apellido
            password = UUID.randomUUID()
            accountLocked = true
            it
        }
        .addToContactos(new Contacto().with {
            tipo = TipoContacto.EMAIL
            value = command.email
            it
        })
        .addToContactos(new Contacto().with {
            tipo = TipoContacto.TELEFONO
            value = command.telefono
            it
        })
    }

    private Organizacion obtenerOrganizacion(RegistroCommand command) {
        return new Organizacion().with {
            nombre = command.denominacion
            objeto = command.objeto
            descripcion = command.descripcion
            nombreURL = command.nombreURL
            tipo = command.tipo
            estado = EstadoOrganizacion.PENDIENTE
            it
        }
    }

    private Organizacion asignarAdminDeOrganizacion(User userToAssign, Organizacion org) {
        TipoUsuario tipoAdministrador = TipoUsuario.findByNombre('ADMINISTRADOR')
        if (!tipoAdministrador) throw new Exception('Tipo administrador no encontrado')

        return org.addToAdmins(new UserOrganizacion().with {
            user = userToAssign
            organizacion = org
            tipo = tipoAdministrador
            it
        })
    }

    private UserRole asignarRolDeAdminDeOrganizacion(User representante) {
        Role rolDeAdministrador = Role.findByAuthority('ROLE_OSC_ADMIN')
        if (!rolDeAdministrador) throw new Exception('Rol de administrador de OSC no encontrado')

        return new UserRole(user: representante, role: rolDeAdministrador)
    }

    Organizacion confirmar(ConfirmacionCommand command, User user) {

        Organizacion org = Organizacion.createCriteria().get {
            admins {
                eq 'id', user.id
            }
            eq 'estado', EstadoOrganizacion.PENDIENTE
        }
        if (!org) {
            org = new Organizacion()
            org.errors.reject('error.org.noexiste')
            return org
        }
        org.estado = EstadoOrganizacion.REGISTRADA
        org.save()
        user.accountLocked = false
        user.password = command.password1
        user.save()

        log.info "organizacion confirmada id = ${org.id}"
        return org
    }

    @Transactional(readOnly = true)
    List<Organizacion> getOrganizacionesPendientes() {
        Organizacion.findAllByEstadoAndEnabled(EstadoOrganizacion.PENDIENTE, true)
    }

    @Transactional(readOnly = true)
    List<Organizacion> getOrganizacionesRegistradas() {
        Organizacion.findAllByEstadoAndEnabled(EstadoOrganizacion.REGISTRADA, true)
    }

    Organizacion guardar(OrganizacionCommand command) {
        if (!command || !command.validate()) return null

        Organizacion org = Organizacion.findByIdAndEstado(command.id, EstadoOrganizacion.VERIFICADA)
        if (org == null || !org.enabled) return null

        org.tipo = command.tipo
        org.nombre = command.nombre
        org.descripcion = command.descripcion
        org.objeto = command.objeto
        org.fechaConstitucion = command.fechaConstitucion
        org.save()
        if (command.domicilio) {
            if (!org.domicilio) org.domicilio = new Domicilio()
            org.domicilio.calle = command.domicilio.calle
            org.domicilio.numero = command.domicilio.numero
            org.domicilio.piso = command.domicilio.piso
            org.domicilio.departamento = command.domicilio.departamento
            org.domicilio.barrio = command.domicilio.barrio
            org.domicilio.distrito = command.domicilio.distrito
            org.domicilio.localidad = command.domicilio.localidad
            org.domicilio.provincia = command.domicilio.provincia
            org.domicilio.pais = command.domicilio.pais
            org.domicilio.latitudPos = command.domicilio.latitudPos
            org.domicilio.longitudPos = command.domicilio.longitudPos
            org.domicilio.save()
        }
        return org
    }

    Organizacion actualizarContactos(Organizacion org, ContactoCommand command) {
        if (!command || !command.validate()) return null

        Contacto contacto = command.id ? Contacto.get(command.id) : null
        if (contacto) {
            contacto.value = command.value
            contacto.tipo = command.tipo
            contacto.save()
        } else if (command.id == null) {
            org.addToContactos(new Contacto(command.properties))
            org.save()
        }
        return org
    }
    Organizacion eliminarContacto(Organizacion org, ContactoCommand command) {
        Contacto contacto = Contacto.get(command?.id)
        if (contacto) {
            org.removeFromContactos(contacto)
            contacto.delete()
            org.contactos.clear()
        }
        return org
    }

    Organizacion actualizarUsuario(Organizacion org, UserCommand command) {
        if (!command || !command.validate()) return null

        TipoUsuario administrador = TipoUsuario.findByNombre('ADMINISTRADOR')
        UserOrganizacion userOrg = UserOrganizacion.get(command.id)
        if (!userOrg) {
            User user = new User(nombre: command.nombre, apellido: command.apellido,
                    username: command.username, password: UUID.randomUUID()).save()
            userOrg = new UserOrganizacion(user: user, organizacion: org,
                    tipo: TipoUsuario.get(command.tipo))
            if (command.tipo == administrador.id) {
                org.addToAdmins(userOrg)
            } else {
                org.addToMiembros(userOrg)
            }
            //TODO send email
            org.save()
        }
        return org
    }
    Organizacion eliminarUsuario(Organizacion org, UserCommand command) {
        TipoUsuario administrador = TipoUsuario.findByNombre('ADMINISTRADOR')
        UserOrganizacion userOrg = UserOrganizacion.get(command?.id)
        if (userOrg) {
            if (userOrg.tipo == administrador) {
                org.removeFromAdmins(userOrg)
                org.admins.clear()
            } else {
                org.removeFromMiembros(userOrg)
                org.miembros.clear()
            }
            userOrg.delete()
        }
        org
    }

    @Transactional(readOnly = true)
    Organizacion getOrganizacionActualDe(User user) {
        Organizacion.createCriteria().get {
            admins {
                eq 'id', user.id
            }
            or {
                eq 'estado', EstadoOrganizacion.REGISTRADA
                eq 'estado', EstadoOrganizacion.VERIFICADA
            }
            eq 'enabled', Boolean.TRUE
        }
    }

    @Transactional(readOnly = true)
    Organizacion buscarPorNombre(String nombreURL) {
        Organizacion.findEnabledByNombreURL(nombreURL)
    }

    TipoUsuario agregarTipoUsuario(String nombre, Organizacion org) {
        new TipoUsuario(nombre: nombre, organizacion: org).save()
    }

    Respuesta actualizarVoluntario(VoluntarioCommand command, Organizacion org) {
        if (org.id != command.orgId)
            return Respuesta.conError('error.invalid.token')

        if (!command.validate())
            return Respuesta.conErrores(command, command.errors.allErrors)

        Voluntario voluntario = command.id
                ? modificarVoluntario(command) : agregarVoluntario(command, org)

        // if (command.id)
            // org.refresh() //fixed unsynchronized org

        return Respuesta.conValor(voluntario)
    }

    Voluntario modificarVoluntario(VoluntarioCommand command) {
        Voluntario voluntario = Voluntario.get(command.id)
        if (!voluntario) return null

        voluntario.email = command.email
        voluntario.nombre = command.nombre
        voluntario.apellido = command.apellido
        voluntario.nacimiento = command.nacimiento
        if (command.domicilio) {
            Domicilio domicilio = new Domicilio(command.domicilio.properties)
            domicilio.id = command.domicilio.id ?: voluntario.domicilio.id
            voluntario.domicilio = domicilio
        }
        if (command.tipoUsuarioId != voluntario.tipo?.id) {
            voluntario.tipo = TipoUsuario.get(command.tipoUsuarioId)
        } else if (command.tipoUsuarioNombre) {
            TipoUsuario tipo = agregarTipoUsuario(command.tipoUsuarioNombre, org)
            voluntario.tipo = tipo
        }
        voluntario.save()
        return voluntario
    }

    Voluntario agregarVoluntario(VoluntarioCommand command, Organizacion org) {
        Voluntario voluntario = new Voluntario(command.properties)
        voluntario.organizacion = org
        if (command.tipoUsuarioId) {
            voluntario.tipo = TipoUsuario.get(command.tipoUsuarioId)
        } else if (command.tipoUsuarioNombre) {
            TipoUsuario tipo = agregarTipoUsuario(command.tipoUsuarioNombre, org)
            voluntario.tipo = tipo
        }
        if (command.domicilio) {
            voluntario.domicilio = new Domicilio(command.domicilio.properties)
        }
        org.addToVoluntarios(voluntario)
        org.save()
        return voluntario
    }

}
