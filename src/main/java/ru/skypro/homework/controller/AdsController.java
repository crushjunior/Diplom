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
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.AdsService;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * Контроллер AdsController
 * Контроллер для обработки REST-запросов, в данном случае добавления, удаления, редактирования и поиска объявлений
 *
 * @see
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ads")
public class AdsController {

    /**
     * Поле сервиса объявлений
     */
    private final AdsService adsService;

    /**
     * Функция получения всех объявлений, хранящихся в базе данных
     *
     * @param title заголовок объявления
     * @return возвращает все объявления
     */
    @Operation(
            summary = "Получение списка всех объявлений",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "ОК",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseWrapperAds.class)
                            )
                    )
            }

    )
    @GetMapping  //GET http://localhost:8080/ads
    public ResponseEntity<ResponseWrapperAds<Ads>> getAllAds(@RequestParam(required = false) String title) {
        ResponseWrapperAds<Ads> ads = new ResponseWrapperAds<>(adsService.getAllAds(title));
        return ResponseEntity.ok(ads);
    }

    /**
     * Функция добавление объявления
     *
     * @param createAds      данные объявления
     * @param image          картинка объявления
     * @param authentication авторизованный пользователь
     * @return возвращает объект, содержащий данные созданного объявления
     */
    @Operation(
            summary = "Функция добавления объявления в базу данных",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Созданное объявление",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неавторизованный пользователь",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class))
                    )
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //POST http://localhost:8080/abs
    public ResponseEntity<Ads> createAds(@RequestPart("properties") @NotNull CreateAds createAds,
                                         @RequestPart MultipartFile image,
                                         @NonNull Authentication authentication) {
        return ResponseEntity.ok(adsService.createAds(createAds, image, authentication));
    }

    /**
     * Функция получения объявления по идентификатору (id), хранящихся в базе данных
     *
     * @param id идентификатор объявления, не может быть null
     * @return возвращает объявление по идентификатору (id)
     */
    @Operation(
            summary = "Функция получения объявления по идентификатору из базы данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Объявление найдено",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неавторизованный пользователь",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<FullAds> getAds(@PathVariable Integer id) {
        return ResponseEntity.ok(adsService.getAds(id));
    }

    /**
     * Функция удаления объявления по идентификатору (id), хранящихся в базе данных
     *
     * @param id идентификатор объявления, не может быть null
     */
    @Operation(
            summary = "Удаление  объявления из базы данных по идентификатору (id)",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Без содержания",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неавторизованный пользователь",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Запрещенный пользователь",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = NewPassword.class)
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAds(@PathVariable Integer id) {
        adsService.deleteAds(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Функция обновления объявления по идентификатору (id), хранящихся в базе данных
     *
     * @param id        идентификатор объявления, не может быть null
     * @param createAds данные объявления
     * @return возвращает обновленное объявление по идентификатору (id)
     */
    @Operation(
            summary = "Функция обновления объявления по идентификатору из базы данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "ОК",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неавторизованный пользователь",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Запрещенный пользователь",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = NewPassword.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class)
                            )
                    )
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Ads> updateAds(@PathVariable int id,
                                         @RequestBody CreateAds createAds) {
        return ResponseEntity.ok(adsService.updateAds(createAds, id));
    }

    /**
     * Функция получения объявления авторизованного пользователя, хранящихся в базе данных
     *
     * @param authentication авторизованный пользователь
     * @return возвращает объявление авторизованного пользователя
     */
    @Operation(
            summary = "Функция получения объявления авторизованного пользователя из базы данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "ОК",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неавторизованный пользователь",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class))
                    )
            }
    )
    @GetMapping("/me") //GET http://localhost:8080/abs/me
    public ResponseEntity<ResponseWrapperAds<Ads>> getAdsMe(Authentication authentication) {
        ResponseWrapperAds<Ads> ads = new ResponseWrapperAds<>(adsService.getAdsMe(authentication));
        return ResponseEntity.ok(ads);
    }

    /**
     * Функция обновления картинки объявления
     *
     * @param id    идентификатор объявления, не может быть null
     * @param image картинка объявления
     * @return обновленная картинка объявления
     */
    @Operation(
            summary = "Обновление картинки объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "ОК",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неавторизованный пользователь",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Запрещенный пользователь",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = NewPassword.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class))
                    )
            }
    )
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //PATCH http://localhost:8080/abs/{id}/image
    public ResponseEntity<String> updateImage(@PathVariable int id,
                                              @RequestPart MultipartFile image) throws IOException {
        return ResponseEntity.ok(adsService.updateImage(id, image));
    }

    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable Integer id){
        return ResponseEntity.ok(adsService.getAdImage(id));
    }
}
