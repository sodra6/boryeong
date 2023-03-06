package com.mindone.Boryeongapi.controller;

import com.mindone.Boryeongapi.service.DashBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

@Controller
public class DashBoardController {

    @Autowired
    DashBoardService dashBoardService;

    @RequestMapping(value="/dashBoard/main")
    public @ResponseBody Map<String, Object> getDashBoard() {
        Map<String, Object> result = new HashMap<String, Object>();
        result = dashBoardService.getDashBoard();
        return result;
    }

    @RequestMapping(value="/dashboard/popup")
    public @ResponseBody
    Map<String, Object> getPopupData(HttpServletRequest httpServletRequest) throws ParseException {
        Map<String, Object> result = new HashMap<String, Object>();

        String kind = httpServletRequest.getParameter("kind").toUpperCase();
        String direction = kind.equals("DEPTH") ? "" : httpServletRequest.getParameter("direction").toUpperCase();
        String fromDate = httpServletRequest.getParameter("fromDate");
        String toDate = httpServletRequest.getParameter("toDate");

        result = dashBoardService.getPopupData(kind,direction, fromDate, toDate);
        return result;
    }
}
