package com.mindone.Boryeongapi.repository.main;

import com.mindone.Boryeongapi.domain.entity.main.Valve;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValveRepository extends JpaRepository<Valve, Long> {
    Valve findFirstByIdOrderByColtDtDesc(String id);
}
