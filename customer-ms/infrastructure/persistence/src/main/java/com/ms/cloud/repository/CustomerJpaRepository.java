package com.ms.cloud.repository;

import com.ms.cloud.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, String> {

    Optional<CustomerEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
