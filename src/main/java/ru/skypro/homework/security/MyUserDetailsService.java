package ru.skypro.homework.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.UserSecurity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;

/**
 * MyUserDetailsService
 * Сервис для данных пользователя
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    /**
     * Поле репозитория пользователя
     */
    private final UserRepository userRepository;

    /**
     * Поле маппинга пользователя
     */
    private UserMapper userMapper;

    /**
     * Поле данных пользователя
     */
    private final MyUserDetails myUserDetails;

    /**
     * Конструктор - создание нового объекта
     *
     * @param userRepository
     * @param userMapper
     * @param myUserDetails
     * @see UserRepository (UserRepository)
     * @see UserMapper (UserMapper)
     * @see MyUserDetails (MyUserDetails)
     */
    public MyUserDetailsService(UserRepository userRepository, UserMapper userMapper, MyUserDetails myUserDetails) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.myUserDetails = myUserDetails;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserSecurity userSecurity = userRepository.findByEmailIgnoreCase(username)
                .map(userMapper::toSecurityDTO)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        myUserDetails.setUserSecurity(userSecurity);
        return myUserDetails;
    }
}
