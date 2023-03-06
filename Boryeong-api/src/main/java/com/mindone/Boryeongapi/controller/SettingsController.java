package com.mindone.Boryeongapi.controller;

import com.mindone.Boryeongapi.domain.entity.main.Setting;
import com.mindone.Boryeongapi.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SettingsController {
    @Autowired
    SettingsService settingsService;

    @RequestMapping(value="/settingAll")
    public @ResponseBody List<Setting> getSettingAll() {
        return settingsService.getSettingAll();
    }

    @RequestMapping(value="/settings")
    public @ResponseBody List<Setting> getSettings(HttpServletRequest httpServletRequest) {
        String type = httpServletRequest.getParameter("type").toUpperCase();
        return settingsService.getSettings(type);
    }

    @RequestMapping(value="/findSetting")
    public @ResponseBody ModelAndView findSetting(HttpServletRequest httpServletRequest) {
        ModelAndView mv = new ModelAndView("jsonView");
        String type = httpServletRequest.getParameter("type").toUpperCase();
        String direction = httpServletRequest.getParameter("direction").toUpperCase();
        System.out.println(type);
        if (type.equals("DEPTH")){
            List<Setting> levelList = settingsService.findSetting(type, direction);
            Map<String, Object> resultMap = new HashMap<>();
            for (Setting setting : levelList) {
                resultMap.put("depth" + setting.getId().split("_")[1], setting);
            }
            mv.addObject("result", resultMap);
        } else {
            mv.addObject("result", settingsService.findSetting(type, direction));
        }
        return mv;
    }

    @RequestMapping(value="/saveSetting" , method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView saveSetting(@RequestBody Setting setting) {
        ModelAndView mv = new ModelAndView("jsonView");
        settingsService.saveSetting(setting);
        mv.addObject("msg","저장");
        return mv;
    }
}
