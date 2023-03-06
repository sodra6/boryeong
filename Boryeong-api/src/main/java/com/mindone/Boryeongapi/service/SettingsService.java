package com.mindone.Boryeongapi.service;

import com.mindone.Boryeongapi.domain.entity.main.Setting;
import com.mindone.Boryeongapi.repository.main.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingsService {

    @Autowired
    private SettingRepository settingsRepository;

    public List<Setting> getSettingAll() {
        return settingsRepository.findAll();
    }

    public List<Setting> getSettings(String kind) { return settingsRepository.findByKind(kind); }

    public List<Setting> findSetting(String kind, String direction) {
        List<Setting> result;
        if (kind.equals("DEPTH")) {
            result = settingsRepository.findByKind(kind);
        } else {
            result = settingsRepository.findByKindAndLine(kind, direction);
        }
        return result;
    }

    public void saveSetting(Setting setting) { settingsRepository.save(setting); }
}
