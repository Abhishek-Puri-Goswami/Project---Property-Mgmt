package com.propertyhub.property.repository;

import com.propertyhub.property.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findByUserId(Long userId);

    @Query("SELECT v FROM Visit v WHERE v.property.agentId = :agentId")
    List<Visit> findByPropertyAgentId(@Param("agentId") Long agentId);

}
