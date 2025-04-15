package com.practice.demo.controllers;

import com.practice.demo.dto.entity_dto.OperationDto;
import com.practice.demo.dto.entity_dto.OperationListDto;
import com.practice.demo.dto.paging_and_sotring_dto.models.OperationAbstractPagingAndSortingDto;
import com.practice.demo.dto.specification_dto.models.OperationSpecificationDto;
import com.practice.demo.models.entities.Operation;
import com.practice.demo.models.db_views.OperationView;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.service.OperationService;
import com.practice.demo.uri_handler.UriHandler;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
public class OperationController {

    private final OperationService operationService;

    @GetMapping("/clients/{client_id}/account-{account_id}")
    public String accountById(@PathVariable(value = "client_id") Long clientId,
                              @PathVariable(value = "account_id") Long accountId,
                              @ModelAttribute OperationAbstractPagingAndSortingDto pagingAndSortingDto,
                              @ModelAttribute OperationSpecificationDto operationSpecificationDto,
                              Model model, HttpServletRequest httpServletRequest) {

        model.addAttribute("uriPairList", UriHandler.parse(httpServletRequest.getRequestURI()));

        pagingAndSortingDto.fillEmptyFields();
        operationSpecificationDto.fillEmptyFields();

        model.addAttribute("PASdto", pagingAndSortingDto);
        model.addAttribute("OSdto", operationSpecificationDto);

        OperationListDto operationListDto = OperationListDto.valueFrom(
                operationService.fetchNextPageByAccountId(pagingAndSortingDto, operationSpecificationDto, accountId),
                operationService.findOneOperationView(accountId));

        model.addAttribute("operationListDto", operationListDto);

        model.addAttribute("operationKinds", Operation.OperationKind.values());
        model.addAttribute("currencies", Currency.values());

        return "operations";
    }

    @GetMapping("/clients/{client_id}/account-{account_id}/operation-{operation_id}")
    public String operationById(@PathVariable(value = "client_id") Long clientId,
                                @PathVariable(value = "account_id") Long accountId,
                                @PathVariable(value = "operation_id") Long operationId, Model model) {

        OperationView operation = operationService.findViewById(operationId);

        model.addAttribute("operation", operation);

        return "operation-by-id";
    }

    @GetMapping("/clients/{client_id}/account-{account_id}/new-operation")
    public String performOperation(@PathVariable(value = "client_id") Long clientId,
                                   @PathVariable(value = "account_id") Long accountId, Model model) {

        model.addAttribute("client_id", clientId);
        model.addAttribute("account_id", accountId);
        model.addAttribute("currencies", Currency.values());
        model.addAttribute("operationKinds", Operation.OperationKind.values());
        model.addAttribute("operationDto", OperationDto.builder().build());

        return "new-operation";
    }

    @PostMapping("/clients/{client_id}/account-{account_id}/new-operation")
    public String addOperation(@PathVariable(value = "client_id") Long clientId,
                               @PathVariable(value = "account_id") Long accountId, @ModelAttribute OperationDto operationDto) {

        operationService.addOperation(operationDto, accountId);

        return "redirect:/clients/{client_id}/account-{account_id}";
    }
}
