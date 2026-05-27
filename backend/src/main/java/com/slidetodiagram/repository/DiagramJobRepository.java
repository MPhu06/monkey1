package com.slidetodiagram.repository;

import com.slidetodiagram.model.DiagramJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiagramJobRepository extends JpaRepository<DiagramJob, UUID> {

    List<DiagramJob> findAllByOrderByCreatedAtDesc();
}
