package com.ikonicit.invoice.repository;

import com.ikonicit.invoice.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
