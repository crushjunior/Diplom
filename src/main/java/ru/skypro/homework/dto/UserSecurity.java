package ru.skypro.homework.dto;

import lombok.Data;

/**
 * Класс UserSecurity, представляет побочный класс пользователя
 */
@Data
public class UserSecurity {

    /**
     * id пользователя
     */
    private Integer id;

    /**
     * Логин пользователя
     */
    private String email;

    /**
     * Пароль пользователя
     */
    private String password;

    /**
     * Роль пользователя
     */
    private Role role;
}
