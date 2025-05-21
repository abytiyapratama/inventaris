package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

import java.util.UUID;
@Entity
public class Supplier extends PanacheEntityBase {
    @Id
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    public UUID id;

    public String nama;
    public String alamat;
    public String kontak;

    @PrePersist
    public void generateId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
