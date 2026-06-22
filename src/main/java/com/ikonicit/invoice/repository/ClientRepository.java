package com.ikonicit.invoice.repository;

import com.ikonicit.invoice.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByIsActiveTrueOrderByCreatedAtDesc();

}
