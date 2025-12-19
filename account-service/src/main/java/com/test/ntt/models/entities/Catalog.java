package com.test.ntt.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "catalogs")
public class Catalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String mnemonic;

    private String description;

    private String name;

    private String value;

    @ManyToOne
    @JoinColumn(name = "catalog_parent")
    private Catalog catalogParent;
}
