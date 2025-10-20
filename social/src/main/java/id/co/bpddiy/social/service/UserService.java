package id.co.bpddiy.social.service;

import java.util.List;

import id.co.bpddiy.social.dto.UserDto;
import id.co.bpddiy.social.model.User.Role;

public interface UserService {
    List<UserDto> getAll();

    UserDto createUser(String username,String email,String password,Role role);

    UserDto updateUser(Long id, String username,String email,String password,Role role);

    UserDto deleteUser(Long id);
}