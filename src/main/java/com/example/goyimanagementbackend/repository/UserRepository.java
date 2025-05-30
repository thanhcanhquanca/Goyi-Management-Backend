package com.example.goyimanagementbackend.repository;

import com.example.goyimanagementbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserCode(String userCode);
    Optional<User> findByProfilePicture(String profilePicture);
    Optional<User> findByCoverPhoto(String coverPhoto);
}