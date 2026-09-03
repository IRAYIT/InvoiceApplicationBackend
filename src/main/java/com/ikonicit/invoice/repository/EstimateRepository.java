package com.ikonicit.invoice.repository;

import com.ikonicit.invoice.entity.Estimate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface EstimateRepository extends JpaRepository<Estimate, Long> {

    // Get all active estimates
    @Query("SELECT e FROM Estimate e WHERE e.active = true ORDER BY e.createdAt DESC")
    List<Estimate> findAllActiveEstimates();

    // Auto-generate estimate number
    @Query("SELECT COUNT(e) FROM Estimate e")
    Long countAllEstimates();
}