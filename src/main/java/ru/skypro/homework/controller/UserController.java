package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.service.UserService;

import java.io.IOException;

/**
 * Контроллер UserController
 * Контроллер для обработки REST-запросов, добавление, удаление, редактирование и поиска пользователей
 *
 * @see
 */

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    /**
     * Поле сервиса пользователя
     */
    private final UserService userService;

    /**
     * Обновление пароля
     *
     * @param newPassword    новый пароль
     * @param authentication авторизованный пользователь
     * @return новый пароль для авторизованного пользователя
     */
    @Operation(
            summary = "Обновление пароля",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "ОК",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = NewPassword.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = NewPassword.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = NewPassword.class))
                    )
            }
    )
    @PostMapping("/set_password")
    public ResponseEntity<?> setPassword(@RequestBody NewPassword newPassword, Authentication authentication) {
        userService.setNewPassword(newPassword, authentication);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение информации об авторизованном пользователе
     *
     * @param authentication авторизованный пользователь
     * @return информацию об авторизованном пользователе
     */
    @Operation(
            summary = "Получить информацию об авторизованном пользователе пароля",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "ОК",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Authentication.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Authentication.class))
                    )
            }
    )
    @GetMapping("/me")
    public ResponseEntity<User> getUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getUser(authentication));
    }

    /**
     * Обновить информацию об авторизованном пользователе
     *
     * @param user           пользователь
     * @param authentication авторизованный пользователь
     * @return обновленную информацию об авторизованном пользователе
     */
    @Operation(
            summary = "Обновить информацию об авторизованном пользователе",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "ОК",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = User.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = User.class))
                    )
            }
    )
    @PatchMapping("/me")
    public ResponseEntity<User> updateUser(@RequestBody User user, Authentication authentication) {
        return ResponseEntity.ok(userService.updateUser(user, authentication));
    }

    /**
     * Обновить аватар авторизованного пользователя
     *
     * @param image аватар авторизованного пользователя
     * @return обновленный аватар авторизованного пользователя
     */
    @Operation(
            summary = "Обновить аватар авторизованного пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "ОК",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MultipartFile.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MultipartFile.class))
                    )
            }
    )
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserImage(@RequestPart MultipartFile image, Authentication authentication) throws IOException {
        userService.updateUserImage(image, authentication);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable Integer id) throws IOException {
        log.info("Get user image with id" + id);
        return ResponseEntity.ok(userService.getUserImage(id));
    }
}
