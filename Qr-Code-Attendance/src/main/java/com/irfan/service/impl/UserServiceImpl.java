package com.irfan.service.impl;


import com.irfan.dto.UserDto;
import com.irfan.entity.Role;
import com.irfan.entity.User;
import com.irfan.exception.BadApiRequestException;
import com.irfan.exception.ResourceAlreadyExistException;
import com.irfan.exception.ResourceNotFoundException;
import com.irfan.repository.RoleRepository;
import com.irfan.repository.UserRepository;
import com.irfan.service.AuthService;
import com.irfan.service.UserService;
import com.irfan.util.UserHelper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;


    private final ModelMapper mapper;

    @Value("${restaurant.file.path}")
    private String imagePath;


    private final PasswordEncoder passwordEncoder;


    private final RoleRepository roleRepository;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    private final AuthService authService;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 24;


    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        // Check if user already exists
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ResourceAlreadyExistException("User already exists with email: " + userDto.getEmail());
        }

        User user =mapper.map(userDto,User.class);
        user.setUserId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setQrCodeToken(generateUniqueToken());

        // Assign default ROLE_USER if no role is provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {

            Role defaultRole = roleRepository
                    .findByRoleName("ROLE_USER")
                    .orElseThrow(() ->
                            new RuntimeException("ROLE_USER not found in database")
                    );

            user.setRoles(new HashSet<>());
            user.getRoles().add(defaultRole);
        }




        User savedUser=userRepository.save(user);

        try {
            authService.sendOtp(savedUser.getEmail());
        } catch (Exception e) {
            logger.warn("[CustomerService] User created successfully, but OTP email dispatch skipped/failed: {}", e.getMessage());
        }



        return mapper.map(savedUser,UserDto.class);
    }



    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id !!"));
        user.setName(userDto.getName());


        if (!userDto.getPassword().equalsIgnoreCase(user.getPassword()))
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        user.setImageName(userDto.getImageName());
        user.setAbout(userDto.getAbout());
        user.setGender(userDto.getGender());
        //save data
        User updatedUser = userRepository.save(user);

        UserDto updatedDto = mapper.map(updatedUser,UserDto.class);
        return updatedDto;
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with given id !!"));

        // Delete user profile image - ONLY if image name exists
        if (user.getImageName() != null && !user.getImageName().trim().isEmpty()) {
            String fullPath = imagePath + user.getImageName();
            try {
                Path path = Paths.get(fullPath);
                if (Files.exists(path)) {
                    Files.delete(path);
                    logger.info("Deleted user image: {}", fullPath);
                } else {
                    logger.info("User image not found: {}", fullPath);
                }
            } catch (IOException e) {
                logger.error("Error deleting user image: {}", e.getMessage());
            }
        } else {
            logger.info("User has no profile image to delete");
        }

        // ALWAYS delete user from database
        userRepository.delete(user);
        logger.info("User deleted successfully: {}", userId);
    }



    @Override
    public Page<UserDto> getAllUser(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("desc")) ?
                (Sort.by(sortBy).descending()) :
                (Sort.by(sortBy).ascending());

        // pageNumber default starts from 0
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<User> page = userRepository.findAll(pageable);

        return page.map(user -> mapper.map(user, UserDto.class));
    }





    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found with given id !!"));
        return mapper.map(user,UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with given email id !!"));
        return mapper.map(user,UserDto.class);
    }

    @Override
    public List<UserDto> searchUser(String keyword) {
        List<User> users = userRepository.searchUser(keyword);
        return users.stream()
                .map(user -> mapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }


    @Override
    public ByteArrayInputStream exportDataInExcel() {
        List<User> all = userRepository.findAll();
        ByteArrayInputStream byteArrayInputStream = UserHelper.dataToExcel(all);

        return byteArrayInputStream;
    }

    private String generateUniqueToken() {
        for (int attempt = 0; attempt < 5; attempt++) {
            byte[] bytes = new byte[TOKEN_BYTES];
            RANDOM.nextBytes(bytes);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            if (!userRepository.existsByQrCodeToken(token)) {
                return token;
            }
        }
        throw new BadApiRequestException("Could not generate a unique QR token after 5 attempts");
    }


}
