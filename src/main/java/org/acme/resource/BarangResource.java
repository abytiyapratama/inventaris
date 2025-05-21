package org.acme.resource;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.entity.Barang;

import java.util.*;
import java.util.stream.Collectors;

@Path("/barang")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BarangResource {

    @GET
    public List<Barang> getAll() {
        return Barang.list("isDeleted = false");
    }

    @GET
    @Path("/deleted")
    public List<Barang> getDeleted() {
        return Barang.list("isDeleted = true");
    }

    @POST
    @Transactional
    public void create(Barang barang) {
        if (barang.id == null) {
            barang.id = UUID.randomUUID();
        }
        barang.persist();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public void update(@PathParam("id") UUID id, Barang updated) {
        Barang barang = Barang.findById(id);
        if (barang == null) throw new NotFoundException("Barang tidak ditemukan");

        barang.nama = updated.nama;
        barang.kode = updated.kode;
        barang.kategori = updated.kategori;
        barang.stok = updated.stok;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void softDelete(@PathParam("id") UUID id) {
        Barang barang = Barang.findById(id);
        if (barang == null) throw new NotFoundException("Barang tidak ditemukan");
        barang.isDeleted = true;
    }

    @DELETE
    @Path("/{id}/permanent")
    @Transactional
    public void deletePermanent(@PathParam("id") UUID id) {
        boolean deleted = Barang.deleteById(id);
        if (!deleted) throw new NotFoundException("Barang tidak ditemukan");
    }

    @PATCH
    @Path("/{id}/restore")
    @Transactional
    public void restore(@PathParam("id") UUID id) {
        Barang barang = Barang.findById(id);
        if (barang == null) throw new NotFoundException("Barang tidak ditemukan");
        barang.isDeleted = false;
    }

    @GET
    @Path("/perkategori")
    public Map<String, List<Barang>> getBarangPerKategori() {
        List<Barang> semuaBarang = Barang.list("isDeleted = false");
        return semuaBarang.stream()
                .collect(Collectors.groupingBy(
                        b -> b.kategori != null && !b.kategori.isBlank() ? b.kategori : "Tidak Berkategori",
                        TreeMap::new,
                        Collectors.toList()
                ));
    }
}
