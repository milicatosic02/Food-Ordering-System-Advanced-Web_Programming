package com.example.demo.repository;

import com.example.demo.model.ErrorMessage;
import com.example.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;


@Repository
public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, Long> {

    Page<ErrorMessage> findByOrder_CreatedBy(User user, Pageable pageable);


}
