package org.acme.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.dto.LaporanStok;
import org.acme.entity.Barang;
import org.acme.entity.TransaksiKeluar;
import org.acme.entity.TransaksiMasuk;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/laporan")
public class LaporanResource {

    // Endpoint untuk frontend: Tampilkan laporan stok (opsional filter kategori)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<LaporanStok> getLaporan(@QueryParam("kategori") String kategori) {
        List<Barang> barangList = (kategori != null && !kategori.isEmpty())
                ? Barang.list("kategori = ?1 and isDeleted = false", kategori)
                : Barang.list("isDeleted = false");

        List<LaporanStok> hasil = new ArrayList<>();

        for (Barang barang : barangList) {
            LaporanStok stok = new LaporanStok();
            stok.idBarang = barang.id;
            stok.namaBarang = barang.nama;
            stok.kategori = barang.kategori;
            stok.stok = barang.stok;

            stok.totalMasuk = TransaksiMasuk.find("barang.id = ?1", barang.id)
                    .stream().mapToInt(t -> ((TransaksiMasuk) t).jumlah).sum();
            stok.totalKeluar = TransaksiKeluar.find("barang.id = ?1", barang.id)
                    .stream().mapToInt(t -> ((TransaksiKeluar) t).jumlah).sum();

            hasil.add(stok);
        }

        return hasil;
    }

    // Endpoint untuk export Excel
    @GET
    @Path("/export-excel")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response exportExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Laporan Stok");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID Barang");
            header.createCell(1).setCellValue("Nama Barang");
            header.createCell(2).setCellValue("Kategori");
            header.createCell(3).setCellValue("Stok Saat Ini");
            header.createCell(4).setCellValue("Total Masuk");
            header.createCell(5).setCellValue("Total Keluar");

            List<Barang> barangList = Barang.list("isDeleted = false");
            int rowNum = 1;

            for (Barang barang : barangList) {
                int totalMasuk = TransaksiMasuk.find("barang.id = ?1", barang.id)
                        .stream().mapToInt(t -> ((TransaksiMasuk) t).jumlah).sum();
                int totalKeluar = TransaksiKeluar.find("barang.id = ?1", barang.id)
                        .stream().mapToInt(t -> ((TransaksiKeluar) t).jumlah).sum();

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(barang.id.toString());
                row.createCell(1).setCellValue(barang.nama);
                row.createCell(2).setCellValue(barang.kategori);
                row.createCell(3).setCellValue(barang.stok);
                row.createCell(4).setCellValue(totalMasuk);
                row.createCell(5).setCellValue(totalKeluar);
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return Response.ok(out.toByteArray())
                    .header("Content-Disposition", "attachment; filename=laporan_stok.xlsx")
                    .build();

        } catch (IOException e) {
            return Response.serverError().entity("Gagal membuat file Excel: " + e.getMessage()).build();
        }
    }
}
