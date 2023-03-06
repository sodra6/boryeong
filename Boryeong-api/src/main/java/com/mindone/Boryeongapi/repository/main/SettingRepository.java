package com.mindone.Boryeongapi.repository.main;

import com.mindone.Boryeongapi.domain.entity.main.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, Long> {

    public List<Setting> findByKind(String type);

    public List<Setting> findByKindAndLine(String type, String line);
}
