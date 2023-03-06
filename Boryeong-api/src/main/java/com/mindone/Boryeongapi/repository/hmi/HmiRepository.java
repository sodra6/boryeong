package com.mindone.Boryeongapi.repository.hmi;

import com.mindone.Boryeongapi.domain.entity.hmi.Hmi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface HmiRepository extends JpaRepository<Hmi, Long> {

    public List<Hmi> findByTagIdAndLogTimeBetweenOrderByLogTime(String tagId, Date start, Date end);
    public List<Hmi> findByTagIdInAndLogTimeBetweenOrderByLogTime(List<String> tagId, Date start, Date end);
}
