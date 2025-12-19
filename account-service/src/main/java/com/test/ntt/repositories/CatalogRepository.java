package com.test.ntt.repositories;

import com.test.ntt.models.entities.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {

    Catalog findByMnemonic (String mnemonic);
}
