package org.acme.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.entity.Barang;
import org.acme.entity.TransaksiMasuk;
import org.acme.entity.TransaksiKeluar;

import java.time.LocalDate;
import java.util.*;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

    @GET
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Jumlah total barang
        long totalBarang = Barang.count("isDeleted = false");

        // Total stok tersedia
        int totalStok = Barang.<Barang>list("isDeleted = false").stream()
                .mapToInt(b -> b.stok)
                .sum();

        // Total transaksi masuk dan keluar
        long totalMasuk = TransaksiMasuk.count();
        long totalKeluar = TransaksiKeluar.count();

        // Transaksi 5 terbaru (gabungan)
        List<Map<String, Object>> transaksiTerbaru = TransaksiMasuk.<TransaksiMasuk>streamAll()
                .map(tm -> Map.<String, Object>of(
                        "tanggal", tm.tanggal,
                        "namaBarang", tm.barang != null ? tm.barang.nama : "-",
                        "tipe", "MASUK",
                        "jumlah", tm.jumlah
                ))
                .toList();

        transaksiTerbaru.addAll(TransaksiKeluar.<TransaksiKeluar>streamAll()
                .map(tk -> Map.<String, Object>of(
                        "tanggal", tk.tanggal,
                        "namaBarang", tk.barang != null ? tk.barang.nama : "-",
                        "tipe", "KELUAR",
                        "jumlah", tk.jumlah
                ))
                .toList());

        transaksiTerbaru.sort(Comparator.comparing((Map<String, Object> t) -> (LocalDate) t.get("tanggal")).reversed());
        transaksiTerbaru = transaksiTerbaru.stream().limit(5).toList();

        // Masukkan ke ringkasan
        summary.put("totalBarang", totalBarang);
        summary.put("totalStok", totalStok);
        summary.put("totalTransaksiMasuk", totalMasuk);
        summary.put("totalTransaksiKeluar", totalKeluar);
        summary.put("transaksiTerbaru", transaksiTerbaru);

        return summary;
    }
}