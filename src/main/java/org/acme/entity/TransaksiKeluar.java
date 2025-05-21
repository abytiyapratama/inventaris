package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class TransaksiKeluar extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public LocalDate tanggal;

    public int jumlah;

    @ManyToOne
    @JoinColumn(name = "barang_id", referencedColumnName = "id")
    public Barang barang;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    public Customer customer;
}
