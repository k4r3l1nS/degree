package com.practice.demo.models.db_views;

import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Immutable
@Subselect("select * from CLIENT_VIEW")
public class ClientView {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "number_of_accounts")
    private int numberOfAccounts;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "email")
    private String email;

    public String getFullName() {
        return getLastName() + " " + getFirstName();
    }
}
