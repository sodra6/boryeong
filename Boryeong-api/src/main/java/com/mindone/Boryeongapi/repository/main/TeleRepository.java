package com.mindone.Boryeongapi.repository.main;

import com.mindone.Boryeongapi.domain.entity.main.Tele;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TeleRepository extends JpaRepository<Tele, Long> {
    Tele findFirstByIdOrderByColtDtDesc(String id);
    Tele findFirstByIdAndValNotInOrderByColtDtDesc(String id, List<String> val);
    List<Tele> findByIdInAndColtDtBetween(List<String> id, Date start, Date end);
}
