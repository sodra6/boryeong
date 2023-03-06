package com.mindone.Boryeongapi.controller;

import com.mindone.Boryeongapi.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@Controller
public class ReportController {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private ReportService reportService;

    @RequestMapping(value="/report")
    public @ResponseBody
    ModelAndView getReport(HttpServletRequest httpServletRequest) {
        ModelAndView mv = new ModelAndView("jsonView");
        try {
            String date = httpServletRequest.getParameter("date");
            mv.addObject("result", reportService.getReport(date));
        } catch (Exception e) {
            logger.warning(e.toString());
            mv.addObject("fail", e);
        }
        return mv;
    }
}
