package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateComment;
import ru.skypro.homework.dto.ResponseWrapperComment;
import ru.skypro.homework.service.CommentService;


/**
 * CommentController
 * Контроллер для обработки REST-запросов, добавление, удаление, редактирование и поиска комментарии
 *
 * @see
 */

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("ads")
public class CommentController {

    private final CommentService commentService;

    /**
     * Получить комментарии объявления
     *
     * @param id идентификатор объявления, не может быть null
     * @return комментарии
     */
    @Operation(
            summary = "Получить комментарии объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "ОК",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Integer.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Integer.class))
                    )
            }
    )
    @GetMapping("{id}/comments")
    public ResponseEntity<ResponseWrapperComment> getComments(@PathVariable int id) {
        return ResponseEntity.ok(commentService.getComments(id));
    }

    /**
     * Добавить комментарий к объявлению
     *
     * @param id           Id объявление, не может быть null
     * @param createComment  данные комментария
     * @param authentication авторизованный пользователь
     * @return возвращает объект, содержащий данные созданного комментария
     */
    @Operation(
            summary = "Добавить комментарий к объявлению",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "ОК",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Integer.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Integer.class))
                    )
            }
    )
    @PostMapping("{id}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable int id,
                                              @RequestBody CreateComment createComment,
                                              @NonNull Authentication authentication) {
        return ResponseEntity.ok(commentService.addComment(id, createComment, authentication));
    }

    /**
     * Обновить комментарий
     *
     * @param commentId      идентификатор комментария, не может быть null
     * @param comment        обновленный комментарий
     * @return обновленный комментарий
     */
    @Operation(
            summary = "Обновить комментарий",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "ОК",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Integer.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Integer.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Integer.class))
                    )
            }
    )
    @PatchMapping("{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable int adId, @PathVariable int commentId,
                                                 @RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.updateComment(adId, commentId, comment));
    }

    /**
     * Удалить комментарий
     *
     * @param commentId      идентификатор комментария, не может быть null
     * @param adId          идентификатор объявления, не может быть null
     */
    @Operation(
            summary = "Удалить комментарий",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "ОК",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Integer.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Integer.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Integer.class))
                    )
            }
    )
    @DeleteMapping("{adId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable int adId, @PathVariable int commentId) {
        commentService.deleteComment(adId, commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
