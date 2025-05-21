package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Barang extends PanacheEntityBase {

    @Id
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    public UUID id;

    public String nama;
    public String kode;
    public String kategori;
    public int stok;
    public boolean isDeleted = false;

    @PrePersist
    public void generateId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
