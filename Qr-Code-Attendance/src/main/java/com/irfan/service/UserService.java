package com.irfan.service;

import com.irfan.dto.UserDto;
import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

public interface UserService {

    //create
    UserDto createUser(UserDto userDto);

    //update
    UserDto updateUser(UserDto userDto, String userId);

    //delete
    void deleteUser(String userId);


    //get all users
    Page<UserDto> getAllUser(int pageNumber, int pageSize, String sortBy, String sortDir);



    //get single user by id
    UserDto getUserById(String userId);

    //get  single user by email
    UserDto getUserByEmail(String email);

    //search user
    List<UserDto> searchUser(String keyword);


    public ByteArrayInputStream exportDataInExcel();

}
