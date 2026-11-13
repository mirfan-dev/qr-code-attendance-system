package com.irfan.repository;

import com.irfan.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,String> {

    Optional<Role> findByRoleName(String roleName);
    boolean existsByRoleName(String name);
}
