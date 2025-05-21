package org.acme.resource;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.entity.Barang;
import org.acme.entity.Customer;
import org.acme.entity.TransaksiKeluar;

import java.time.LocalDate;
import java.util.List;

@Path("/transaksikeluar")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransaksiKeluarResource {

    @GET
    public List<TransaksiKeluar> getAll() {
        return TransaksiKeluar.listAll();
    }

    @POST
    @Transactional
    public void create(TransaksiKeluar input) {
        System.out.println(">>> CREATE TRANSAKSI KELUAR");

        Barang barang = Barang.findById(input.barang.id);
        Customer customer = Customer.findById(input.customer.id);

        if (barang == null || customer == null) {
            throw new NotFoundException("Barang atau Customer tidak ditemukan");
        }

        if (barang.stok < input.jumlah) {
            throw new BadRequestException("Stok tidak mencukupi");
        }

        input.barang = barang;
        input.customer = customer;
        input.tanggal = input.tanggal != null ? input.tanggal : LocalDate.now();
        input.persist();

        // Kurangi stok
        barang.stok -= input.jumlah;
        barang.persist();

        System.out.println(">>> TRANSAKSI KELUAR BERHASIL");
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public void update(@PathParam("id") Long id, TransaksiKeluar input) {
        System.out.println(">>> UPDATE TRANSAKSI KELUAR ID: " + id);

        TransaksiKeluar trx = TransaksiKeluar.findById(id);
        if (trx == null) {
            throw new NotFoundException("Transaksi tidak ditemukan");
        }

        Barang barangBaru = Barang.findById(input.barang.id);
        Customer customerBaru = Customer.findById(input.customer.id);

        if (barangBaru == null || customerBaru == null) {
            throw new NotFoundException("Barang atau Customer tidak ditemukan");
        }

        // Kembalikan stok lama
        trx.barang.stok += trx.jumlah;
        trx.barang.persist();

        // Cek apakah stok baru cukup
        if (barangBaru.stok < input.jumlah) {
            throw new BadRequestException("Stok tidak mencukupi untuk update");
        }

        // Update isi transaksi
        trx.barang = barangBaru;
        trx.customer = customerBaru;
        trx.jumlah = input.jumlah;
        trx.tanggal = input.tanggal != null ? input.tanggal : trx.tanggal;

        // Kurangi stok barang baru
        barangBaru.stok -= input.jumlah;
        barangBaru.persist();

        trx.persist();

        System.out.println(">>> UPDATE BERHASIL");
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        System.out.println(">>> DELETE TRANSAKSI KELUAR ID: " + id);

        TransaksiKeluar trx = TransaksiKeluar.findById(id);
        if (trx == null) {
            throw new NotFoundException("Transaksi tidak ditemukan");
        }

        // Kembalikan stok
        trx.barang.stok += trx.jumlah;
        trx.barang.persist();

        trx.delete();
        System.out.println(">>> DELETE TRANSAKSI KELUAR BERHASIL");
    }
}
