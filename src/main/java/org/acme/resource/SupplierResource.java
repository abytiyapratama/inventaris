package org.acme.resource;

import java.util.UUID;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.entity.Supplier;

import java.util.List;

@Path("/supplier")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SupplierResource {

    @GET
    public List<Supplier> getAll() {
        List<Supplier> list = Supplier.listAll();
        System.out.println("DATA SUPPLIER (from DB): " + list);
        return list;
    }



    @POST
    @Transactional
    public Supplier create(Supplier supplier) {
        System.out.println(">>> CREATE SUPPLIER: " + supplier.nama + " - " + supplier.alamat + " - " + supplier.kontak);
        supplier.persist();
        return supplier; // return untuk melihat hasil di frontend jika perlu
    }


    @PUT
    @Path("/{id}")
    @Transactional
    public void update(@PathParam("id") UUID id, Supplier updated) {
        Supplier supplier = Supplier.findById(id);
        if (supplier == null) throw new NotFoundException();
        supplier.nama = updated.nama;
        supplier.alamat = updated.alamat;
        supplier.kontak = updated.kontak;
    }

@DELETE
@Path("/{id}")
@Transactional
public void delete(@PathParam("id") UUID id) {
    System.out.println(">>> Menerima DELETE untuk ID: " + id);
    boolean success = Supplier.deleteById(id);
    System.out.println(">>> DELETE sukses? " + success);
    if (!success) {
        throw new NotFoundException("Supplier dengan ID " + id + " tidak ditemukan");
    }
}

}

