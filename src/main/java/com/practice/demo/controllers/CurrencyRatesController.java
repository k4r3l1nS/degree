package com.practice.demo.controllers;

import com.practice.demo.dto.entity_dto.CurrencyRatesDto;
import com.practice.demo.models.db_views.CurrencyRatesView;
import com.practice.demo.service.CurrencyRatesService;
import com.practice.demo.uri_handler.UriHandler;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CurrencyRatesController {

    private final CurrencyRatesService currencyRatesService;

    @GetMapping("/currency-rates-2")
    public String currencyRates(@ModelAttribute CurrencyRatesDto currencyRatesDto,
                                HttpServletRequest httpServletRequest, Model model) {

        model.addAttribute("uriPairList", UriHandler.parse(httpServletRequest.getRequestURI()));

        model.addAttribute("CRSdto", currencyRatesDto);

        model.addAttribute("rate", currencyRatesService.findRate(currencyRatesDto));

        List<CurrencyRatesView> currencyRatesViewList = currencyRatesService.findAllViews();

        model.addAttribute("currencyRatesViewList", currencyRatesViewList);

        return "currency-rates";
    }
}
