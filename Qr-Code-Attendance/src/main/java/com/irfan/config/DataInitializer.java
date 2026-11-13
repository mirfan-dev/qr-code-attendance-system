package com.irfan.config;

import com.irfan.entity.Role;
import com.irfan.repository.RoleRepository;
import com.irfan.util.AppConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // Create ADMIN role if not exists
        if (!roleRepository.existsByRoleName(AppConstant.ADMIN_ROLE)) {

            Role adminRole = new Role();
            adminRole.setRoleId(UUID.randomUUID().toString());
            adminRole.setRoleName(AppConstant.ADMIN_ROLE);

            roleRepository.save(adminRole);

            log.info("ADMIN role created successfully");
        }

        // Create GUEST role if not exists
        if (!roleRepository.existsByRoleName(AppConstant.GUEST_ROLE)) {

            Role guestRole = new Role();
            guestRole.setRoleId(UUID.randomUUID().toString());
            guestRole.setRoleName(AppConstant.GUEST_ROLE);

            roleRepository.save(guestRole);

            log.info("GUEST role created successfully");
        }

        log.info("Data initialization completed successfully");
    }
}
