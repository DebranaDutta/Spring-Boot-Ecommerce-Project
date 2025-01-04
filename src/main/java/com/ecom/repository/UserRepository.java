package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
