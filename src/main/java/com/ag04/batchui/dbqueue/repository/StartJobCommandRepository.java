package com.ag04.batchui.dbqueue.repository;

import com.ag04.batchui.dbqueue.domain.StartJobCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StartJobCommandRepository extends JpaRepository<StartJobCommand, Long> {
}
