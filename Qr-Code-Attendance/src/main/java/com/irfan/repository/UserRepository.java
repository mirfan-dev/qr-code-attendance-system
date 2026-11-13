package com.irfan.repository;

import com.irfan.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String>
{

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.about) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    public List<User> searchUser(String keyword);

    boolean existsByQrCodeToken(String qrCodeToken);

    Optional<User> findByQrCodeToken(String qrCodeToken);

}
