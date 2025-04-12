package com.practice.demo.controllers;

import com.practice.demo.dto.entity_dto.ClientDto;
import com.practice.demo.dto.paging_and_sotring_dto.models.ClientAbstractPagingAndSortingDto;
import com.practice.demo.dto.specification_dto.models.ClientSpecificationDto;
import com.practice.demo.models.db_views.ClientView;
import com.practice.demo.service.ClientService;
import com.practice.demo.uri_handler.UriHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/clients")
    public String clients(@ModelAttribute ClientAbstractPagingAndSortingDto clientPagingAndSortingDto,
                          @ModelAttribute ClientSpecificationDto clientSpecificationDto,
                          Model model, HttpServletRequest httpServletRequest) {

        model.addAttribute("uriPairList", UriHandler.parse(httpServletRequest.getRequestURI()));

        clientPagingAndSortingDto.fillEmptyFields();
        clientSpecificationDto.fillEmptyFields();

        model.addAttribute("PASdto", clientPagingAndSortingDto);
        model.addAttribute("CSdto", clientSpecificationDto);

        Page<ClientView> clients = clientService.fetchNextPage(clientPagingAndSortingDto, clientSpecificationDto);

        model.addAttribute("clients", clients);

        return "clients";
    }

    @GetMapping("/clients/add")
    public String addClient(Model model) {

        model.addAttribute("clientDto", ClientDto.builder().build());

        return "new-client";
    }

    @PostMapping("/clients/add")
    public String addClient(@ModelAttribute ClientDto clientDto) {

        clientService.addClient(clientDto);

        return "redirect:/clients";
    }

    @GetMapping("/clients/{id}/edit")
    public String editClient(@PathVariable(value = "id") Long id, Model model) {

        ClientView client = clientService.findClientById(id);
        model.addAttribute("client", client);

        model.addAttribute("clientDto", ClientDto.builder().build());

        return "edit-client";
    }

    @PutMapping("/clients/{id}/edit")
    public String updateClient(@PathVariable(value = "id") Long id, @ModelAttribute ClientDto clientDto) {

        clientService.updateClient(clientDto, id);

        return "redirect:/clients";
    }

    @GetMapping("/clients/{id}/activate")
    public String activateClientById(@PathVariable(value = "id") Long id) {

        clientService.activateClientById(id);

        return "redirect:/clients";
    }

    @GetMapping("/clients/{id}/deactivate")
    public String deactivateClientById(@PathVariable(value = "id") Long id) {

        clientService.deactivateClientById(id);

        return "redirect:/clients";
    }
}
