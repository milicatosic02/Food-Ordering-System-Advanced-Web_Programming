package com.example.demo.service;

import com.example.demo.model.ErrorMessage;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.repository.ErrorMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;


@Service
public class ErrorMessageService implements IServicee<ErrorMessage, Long> {

    ErrorMessageRepository errorMessageRepository;

    @Autowired
    public ErrorMessageService(ErrorMessageRepository errorMessageRepository) {
        this.errorMessageRepository = errorMessageRepository;
    }

    @Override
    public <S extends ErrorMessage> S save(S errorMessage) {
        return errorMessageRepository.save(errorMessage);
    }

    @Override
    public Optional<ErrorMessage> findById(Long id) {
        return errorMessageRepository.findById(id);
    }

    @Override
    public List<ErrorMessage> findAll() {
        return errorMessageRepository.findAll();
    }

    @Override
    public void deleteById(Long var1) {

    }

    public Page<ErrorMessage> paginateForAllUsers(Integer page, Integer size){
        return errorMessageRepository.findAll(PageRequest.of(page, size, Sort.by("timestamp").descending()));
    }

    public Page<ErrorMessage> paginateForLoggedUser(Integer page, Integer size, User user){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return errorMessageRepository.findByOrder_CreatedBy(user, pageable);
    }

}
