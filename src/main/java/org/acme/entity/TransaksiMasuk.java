package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class TransaksiMasuk extends PanacheEntity {

    public LocalDate tanggal;
    public int jumlah;

    // TransaksiMasuk.java
    @ManyToOne
    @JoinColumn(name = "barang_id")
    public Barang barang;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    public Supplier supplier;

}
