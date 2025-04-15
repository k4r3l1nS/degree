package com.practice.demo.controllers;

import com.practice.demo.uri_handler.UriHandler;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutUsController {


    @GetMapping("/about-us")
    public String aboutUs(HttpServletRequest httpServletRequest, Model model) {

        model.addAttribute("uriPairList", UriHandler.parse(httpServletRequest.getRequestURI()));

        return "about-us";
    }
}
