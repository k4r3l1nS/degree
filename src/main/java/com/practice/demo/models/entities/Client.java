package com.practice.demo.models.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "client")
public class Client implements UserDetails {

    /**
     * Unique personal client id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
    @SequenceGenerator(name = "client_seq", sequenceName = "client_seq")
    private Long id;

    /**
    Client's first name
     */
    @Column(name = "first_name", length = 50)
    private String firstName;

    /**
     * Client's last name
     */
    @Column(name = "last_name", length = 50)
    private String lastName;

    /**
     * Client's birthdate
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * Client's registration date & time
     */
    @CreationTimestamp
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    /**
     * Client's email
     */
    @Column(name = "email")
    private String email;

    /**
     * Whether client is active or not
     */
    @Column(name = "is_active")
    private boolean isActive = true;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        ADMIN, USER, SU
    }

    /**
     * List of account entities which belong to client
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "client")
    private List<Account> accounts = new ArrayList<>();

    /**
     * Links account entity to client
     *
     * @param account account entity
     */
    public void addAccount(Account account) {

        Objects.requireNonNull(account);
        this.accounts.add(account);
        account.setClient(this);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
