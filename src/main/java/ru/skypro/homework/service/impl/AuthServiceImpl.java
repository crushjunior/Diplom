package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.security.MyUserDetailsService;
import ru.skypro.homework.service.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final MyUserDetailsService manager;

  private final PasswordEncoder encoder;

  private final UserRepository userRepository;

  private final UserMapper userMapper;


  @Override
  public boolean login(String userName, String password) {
    UserDetails userDetails = manager.loadUserByUsername(userName);
    return encoder.matches(password, userDetails.getPassword());
  }

  @Override
  public boolean register(RegisterReq registerReq, Role role) {
    if (userRepository.findByEmailIgnoreCase(registerReq.getUsername()).isPresent()) {
      return false;
    }
    UserEntity userEntity = userMapper.toEntityFromReq(registerReq);
    userEntity.setRole(role);
    userEntity.setPassword(encoder.encode(userEntity.getPassword()));
    userRepository.save(userEntity);
    return true;
  }
}
