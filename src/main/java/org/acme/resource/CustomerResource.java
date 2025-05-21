package org.acme.resource;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.entity.Customer;

import java.util.List;

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @GET
    public List<Customer> getAll() {
        return Customer.listAll();
    }

    @POST
    @Transactional
    public void create(Customer customer) {
        customer.persist();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public void update(@PathParam("id") Long id, Customer updated) {
        Customer customer = Customer.findById(id);
        if (customer == null) throw new NotFoundException();
        customer.nama = updated.nama;
        customer.alamat = updated.alamat;
        customer.kontak = updated.kontak;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        Customer.deleteById(id);
    }
}
