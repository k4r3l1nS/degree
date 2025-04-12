package com.practice.demo.service;

import com.practice.demo.custom_annotations.DtoCorrectnessCheck;
import com.practice.demo.dto.entity_dto.ClientDto;
import com.practice.demo.dto.specification_dto.models.ClientSpecificationDto;
import com.practice.demo.dto.paging_and_sotring_dto.models.ClientAbstractPagingAndSortingDto;
import com.practice.demo.exceptions.models.ResourceNotFoundException;
import com.practice.demo.models.entities.Client;
import com.practice.demo.models.db_views.ClientView;
import com.practice.demo.models.specification.SpecificationBuilder;
import com.practice.demo.repos.entity_repos.ClientRepository;
import com.practice.demo.repos.db_view_repos.ClientViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientViewRepository clientViewRepository;

    @Transactional(readOnly = true)
    public Client findById(Long clientId) {

        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client with id = " + clientId + " not found"));
    }


    public ClientView findClientById(Long clientId) {

        return clientRepository.findClientById(clientId);
    }

    public Page<ClientView> fetchNextPage(ClientAbstractPagingAndSortingDto pagingAndSortingDto) {

        var pageRequest = pagingAndSortingDto.toPageRequest();

        return clientViewRepository.findAll(pageRequest);
    }

    public Page<ClientView> fetchNextPage(ClientAbstractPagingAndSortingDto pagingAndSortingDto,
                                          ClientSpecificationDto clientSpecificationDto) {

        var conditions = clientSpecificationDto.toConditions();

        var specification = new SpecificationBuilder<ClientView>().with(conditions).build();
        var pageRequest = pagingAndSortingDto.toPageRequest();

        return clientViewRepository.findAll(specification, pageRequest);
    }

    @DtoCorrectnessCheck(filled = true)
    public void addClient(ClientDto clientDto) {

        clientRepository.save(clientDto.toEntity());
    }

    @DtoCorrectnessCheck
    public void updateClient(ClientDto clientDto, Long clientId) {

        var clientEntity = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client with id = " + clientId + " not found"));

        clientDto.mapTo(clientEntity);
    }

    public void activateClientById(Long clientId) {

        var clientEntity = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client with id = " + clientId + " not found"));

        clientEntity.setActive(true);
    }

    public void deactivateClientById(Long clientId) {

        var clientEntity = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client with id = " + clientId + " not found"));

        clientEntity.setActive(false);
    }
}
