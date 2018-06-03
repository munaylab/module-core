package org.munaylab.factory

class Builder {

    static OrganizacionBuilder getOrganizacion() { new OrganizacionBuilder() }

    static DomicilioBuilder getDomicilio() { new DomicilioBuilder() }

    static ContactoBuilder getContacto() { new ContactoBuilder() }

    static UserBuilder getUser() { new UserBuilder() }

    static VoluntarioBuilder getVoluntario() { new VoluntarioBuilder() }

}
