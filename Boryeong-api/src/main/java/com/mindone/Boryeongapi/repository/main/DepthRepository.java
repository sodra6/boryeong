package com.mindone.Boryeongapi.repository.main;

import com.mindone.Boryeongapi.domain.entity.main.Depth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface DepthRepository extends JpaRepository<Depth, Long> {
    Depth findFirstByIdOrderByColtDtDesc(String id);
    Depth findFirstByColtDtBetweenOrderByColtDtDesc(Date start, Date end);
}
