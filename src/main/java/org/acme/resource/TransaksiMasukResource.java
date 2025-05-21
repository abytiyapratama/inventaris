package org.acme.resource;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.entity.Barang;
import org.acme.entity.Supplier;
import org.acme.entity.TransaksiMasuk;

import java.time.LocalDate;
import java.util.List;

@Path("/transaksimasuk")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransaksiMasukResource {

    @GET
    public List<TransaksiMasuk> getAll() {
        return TransaksiMasuk.listAll();
    }

    @POST
    @Transactional
    public void create(TransaksiMasuk input) {
        Barang barang = Barang.findById(input.barang.id);
        Supplier supplier = Supplier.findById(input.supplier.id);

        if (barang == null || supplier == null) {
            throw new NotFoundException("Barang atau Supplier tidak ditemukan");
        }

        input.barang = barang;
        input.supplier = supplier;
        input.tanggal = input.tanggal != null ? input.tanggal : LocalDate.now();
        input.persist();

        // Tambah stok barang
        barang.stok += input.jumlah;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public void update(@PathParam("id") Long id, TransaksiMasuk input) {
        TransaksiMasuk trx = TransaksiMasuk.findById(id);
        if (trx == null) {
            throw new NotFoundException("Transaksi tidak ditemukan");
        }

        Barang barangBaru = Barang.findById(input.barang.id);
        Supplier supplierBaru = Supplier.findById(input.supplier.id);

        if (barangBaru == null || supplierBaru == null) {
            throw new NotFoundException("Barang atau Supplier tidak ditemukan");
        }

        // Jika barang lama berbeda dengan yang baru
        if (!trx.barang.id.equals(barangBaru.id)) {
            // Kembalikan stok ke barang lama
            trx.barang.stok -= trx.jumlah;
            // Tambahkan stok ke barang baru
            barangBaru.stok += input.jumlah;
        } else {
            // Barang sama, hitung selisih
            int selisih = input.jumlah - trx.jumlah;
            trx.barang.stok += selisih;
        }

        trx.barang = barangBaru;
        trx.supplier = supplierBaru;
        trx.jumlah = input.jumlah;
        trx.tanggal = input.tanggal != null ? input.tanggal : LocalDate.now();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        TransaksiMasuk trx = TransaksiMasuk.findById(id);
        if (trx == null) {
            throw new NotFoundException("Transaksi tidak ditemukan");
        }

        // Kurangi stok barang jika dihapus
        if (trx.barang != null) {
            trx.barang.stok -= trx.jumlah;
        }

        trx.delete();
    }
}
