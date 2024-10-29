package com.example.screenwiper.controller;

import com.example.screenwiper.service.RcmdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/recommend")
public class RcmdController {

    private final RcmdService rcmdService;

    @Autowired
    public RcmdController(RcmdService rcmdService) {
        this.rcmdService = rcmdService;
    }

    @GetMapping("/random")
    public Map<String, Object> getRandomDataByCategory() {
        return rcmdService.getRandomDataByCategory();
    }
}
