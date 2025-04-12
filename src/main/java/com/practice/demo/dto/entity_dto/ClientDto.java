package com.practice.demo.dto.entity_dto;

import com.practice.demo.exceptions.models.EmptyFieldException;
import com.practice.demo.models.entities.Client;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    private String email;

    public Client toEntity() {

        Client client = new Client();

        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setBirthDate(birthDate);
        client.setEmail(email);

        return client;
    }

    public void mapTo(Client entity) {

        if (this.firstName != null && !this.firstName.isEmpty()) {
            entity.setFirstName(this.firstName);
        }

        if (this.lastName != null && !this.lastName.isEmpty()) {
            entity.setLastName(this.lastName);
        }

        if (this.birthDate != null) {
            entity.setBirthDate(this.birthDate);
        }

        if (this.email != null && !this.email.isEmpty()) {
            entity.setEmail(this.email);
        }
    }

    private boolean hasEmptyFields() {

        return firstName == null || firstName.isEmpty() || lastName == null ||
                lastName.isEmpty() || email == null || email.isEmpty() ||
                birthDate == null;
    }

    public void throwIfNotFilled() {

        if (hasEmptyFields()) {

            throw new EmptyFieldException("All fields must be filled in");
        }
    }
}
