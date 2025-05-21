package org.acme.dto;

import java.time.LocalDate;

public class RiwayatTransaksi {
    public LocalDate tanggal;
    public String namaBarang;
    public String tipe; // MASUK atau KELUAR
    public int jumlah;
    public String pihak; // supplier/customer
}
