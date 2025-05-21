package org.acme.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.acme.dto.RiwayatTransaksi;
import org.acme.entity.TransaksiMasuk;
import org.acme.entity.TransaksiKeluar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Path("/riwayat")
@Produces(MediaType.APPLICATION_JSON)
public class RiwayatResource {

    @GET
    public List<RiwayatTransaksi> getAll(@QueryParam("tanggalAwal") String tanggalAwal,
                                         @QueryParam("tanggalAkhir") String tanggalAkhir) {

        LocalDate awal = (tanggalAwal != null && !tanggalAwal.isEmpty()) ? LocalDate.parse(tanggalAwal) : LocalDate.MIN;
        LocalDate akhir = (tanggalAkhir != null && !tanggalAkhir.isEmpty()) ? LocalDate.parse(tanggalAkhir) : LocalDate.MAX;

        List<RiwayatTransaksi> riwayat = new ArrayList<>();

        List<TransaksiMasuk> masukList = TransaksiMasuk.listAll();
        for (TransaksiMasuk masuk : masukList) {
            if (!masuk.tanggal.isBefore(awal) && !masuk.tanggal.isAfter(akhir)) {
                RiwayatTransaksi r = new RiwayatTransaksi();
                r.tanggal = masuk.tanggal;
                r.namaBarang = masuk.barang != null ? masuk.barang.nama : "-";
                r.tipe = "MASUK";
                r.jumlah = masuk.jumlah;
                r.pihak = masuk.supplier != null ? masuk.supplier.nama : "-";
                riwayat.add(r);
            }
        }

        List<TransaksiKeluar> keluarList = TransaksiKeluar.listAll();
        for (TransaksiKeluar keluar : keluarList) {
            if (!keluar.tanggal.isBefore(awal) && !keluar.tanggal.isAfter(akhir)) {
                RiwayatTransaksi r = new RiwayatTransaksi();
                r.tanggal = keluar.tanggal;
                r.namaBarang = keluar.barang != null ? keluar.barang.nama : "-";
                r.tipe = "KELUAR";
                r.jumlah = keluar.jumlah;
                r.pihak = keluar.customer != null ? keluar.customer.nama : "-";
                riwayat.add(r);
            }
        }

        // Urutkan berdasarkan tanggal terbaru
        riwayat.sort(Comparator.comparing((RiwayatTransaksi r) -> r.tanggal).reversed());

        return riwayat;
    }
}
