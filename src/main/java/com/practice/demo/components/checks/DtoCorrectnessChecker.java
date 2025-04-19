package com.practice.demo.components.checks;

import com.practice.demo.components.units.CurrencyUnit;
import com.practice.demo.custom_annotations.DtoCorrectnessCheck;
import com.practice.demo.dto.entity_dto.AccountDto;
import com.practice.demo.dto.entity_dto.ClientDto;
import com.practice.demo.dto.entity_dto.OperationDto;
import com.practice.demo.exceptions.models.AccountNameAlreadyTakenException;
import com.practice.demo.exceptions.models.ClientAlreadyExistsException;
import com.practice.demo.repos.entity_repos.AccountRepository;
import com.practice.demo.repos.entity_repos.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Aspect
@RequiredArgsConstructor
public class DtoCorrectnessChecker {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final CurrencyUnit currencyUnit;

    @Pointcut("@annotation(com.practice.demo.custom_annotations.DtoCorrectnessCheck) " +
            "&& args(clientDto, ..)")
    public void clientCorrectnessPointcut(ClientDto clientDto) { }

    @Pointcut("@annotation(com.practice.demo.custom_annotations.DtoCorrectnessCheck) " +
            "&& args(accountDto, ..)")
    public void accountCorrectnessPointcut(AccountDto accountDto) { }

    @Pointcut("@annotation(com.practice.demo.custom_annotations.DtoCorrectnessCheck) " +
            "&& args(operationDto, ..)")
    public void operationCorrectnessPointcut(OperationDto operationDto) { }

    @Before("clientCorrectnessPointcut(clientDto)")
    public void check(JoinPoint joinPoint, ClientDto clientDto) {

        var annotation = getDtoCorrectnessCheckAnnotation((ProceedingJoinPoint) joinPoint);
        if (annotation != null && annotation.filled()) {

            clientDto.throwIfNotFilled();
        }

        throwIfClientAlreadyExists(clientDto.getFirstName(),
                clientDto.getLastName(), clientDto.getBirthDate());
    }

    @Before("accountCorrectnessPointcut(accountDto)")
    public void check(JoinPoint joinPoint, AccountDto accountDto) {

        var annotation = getDtoCorrectnessCheckAnnotation((ProceedingJoinPoint) joinPoint);
        if (annotation != null && annotation.filled()) {
            accountDto.throwIfNotFilled();
        }

        currencyUnit.throwIfNotSupported(accountDto.getCurrency());

        throwIfAccountNameAlreadyTaken(accountDto.getAccountName());

    }

    @Before("operationCorrectnessPointcut(operationDto)")
    public void check(JoinPoint joinPoint, OperationDto operationDto) {

        var annotation = getDtoCorrectnessCheckAnnotation((ProceedingJoinPoint) joinPoint);
        if (annotation != null && annotation.filled()) {

            operationDto.throwIfNotFilled();
        }

        operationDto.throwIfInvalid();
    }

    private DtoCorrectnessCheck getDtoCorrectnessCheckAnnotation(ProceedingJoinPoint joinPoint) {

        var methodSignature = (MethodSignature) joinPoint.getSignature();
        var method = methodSignature.getMethod();

        return method.isAnnotationPresent(DtoCorrectnessCheck.class) ?
                method.getAnnotation(DtoCorrectnessCheck.class) : null;
    }


    private void throwIfClientAlreadyExists(String firstName, String lastName, LocalDate birthDate) {

        if (clientRepository.existsByFirstNameAndLastNameAndBirthDate(firstName, lastName, birthDate)) {

            throw new ClientAlreadyExistsException("Client with these data already exists");
        }
    }

    private void throwIfAccountNameAlreadyTaken(String accountName) {

        if (accountRepository.existsByName(accountName)
                && accountRepository.findByName(accountName).isActive()) {

            throw new AccountNameAlreadyTakenException("This account name is already taken");
        }
    }
}
