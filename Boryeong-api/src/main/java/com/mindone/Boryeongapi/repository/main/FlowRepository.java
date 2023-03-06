package com.mindone.Boryeongapi.repository.main;

import com.mindone.Boryeongapi.domain.entity.main.Flow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface FlowRepository extends JpaRepository<Flow, Long> {

    Flow findFirstByIdOrderByColtDtDesc(String id);
    Flow findFirstByIdAndColtDtBetweenOrderByColtDtDesc(String id, Date start, Date end);
}
