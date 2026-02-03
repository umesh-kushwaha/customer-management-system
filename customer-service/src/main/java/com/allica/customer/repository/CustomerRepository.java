package com.allica.customer.repository;

import com.allica.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Standard CRUD methods are inherited automatically
    List<Customer> findByIdGreaterThanOrderByIdAsc(Long cursor, Pageable pageable);
    List<Customer> findAllByOrderByIdAsc(Pageable pageable);
    List<Customer> findByIdLessThanOrderByIdDesc(Long beforeCursor, Pageable pageable);
    boolean existsByIdGreaterThanEqual(Long id);
}
