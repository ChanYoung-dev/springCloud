package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.jpa.UserEntity;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();


}
