package com.accenture.repository;

import com.accenture.model.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {

    @Query("SELECT p.* FROM products p " +
            "JOIN branches b ON p.branch_id = b.id " +
            "WHERE b.franchise_id = :franchiseId " +
            "AND p.stock = (SELECT MAX(p2.stock) FROM products p2 WHERE p2.branch_id = p.branch_id)")
    Flux<Product> findTopProductsByFranchise(@Param("franchiseId") Long franchiseId);
}
