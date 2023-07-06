package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateAds;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.entity.AdsEntity;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.security.MyUserDetails;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.CommentService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;

import java.io.IOException;
import java.util.Collection;

import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * Сервис AdsServiceImpl
 * Сервис для добавления, удаления, редактирования и поиска объявлений в базе данных
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdsServiceImpl implements AdsService {

    private static final Logger logger = LoggerFactory.getLogger(Ads.class);

    /**
     * Поле репозитория объявлении
     */
    private final AdsRepository adsRepository;

    /**
     * Поле маппинга объявлении
     */
    private final AdsMapper adsMapper;

    /**
     * Поле репозитория пользователя
     */
    private final UserRepository userRepository;

    /**
     * Поле сервиса пользователя
     */
    private final UserService userService;
    private final ImageService imageService;
    private final CommentService commentService;
    private final MyUserDetails myUserDetails; // string security положит сюда авторизированного пользователя

    /**
     * Конструктор - создание нового объекта репозитория
     *
     * @param adsRepository  репозиторий объявления
     * @param adsMapper
     * @param userRepository репозиторий пользователя
     * @param userService    сервис пользователя
     * @param imageService
     * @param commentService
     * @param myUserDetails
     * @see AdsRepository(AdsRepository)
     */

//    public AdsServiceImpl(AdsRepository adsRepository, AdsMapper adsMapper, UserRepository userRepository, UserService userService,
//                          ImageService imageService, CommentService commentService, MyUserDetails myUserDetails) {
//        this.adsRepository = adsRepository;
//        this.adsMapper = adsMapper;
//        this.userRepository = userRepository;
//        this.userService = userService;
//        this.imageService = imageService;
//        this.commentService = commentService;
//        this.myUserDetails = myUserDetails;
//    }

    /**
     * Получение списка всех объявлений из базы данных
     *
     * @return список(коллекцию) объявлений
     */
    @Override
    public Collection<Ads> getAllAds(String title) {
        logger.info("Вызван метод получения всех объявлений");
        Collection<AdsEntity> adsEntities;
        if (!isEmpty(title)) {
            adsEntities = adsRepository.findByTitleLikeIgnoreCase(title);
        } else {
            adsEntities = adsRepository.findAll();
        }
        return adsMapper.adsEntityToCollectionDto(adsEntities);
    }

    /**
     * Добавление нового объявления и сохранение его в базе данных
     *
     * @param createAds      данные объявления
     * @param image          картинка объявления
     * @param authentication авторизованный пользователь
     * @return добавленное новое объявление
     */
    @Override
    public Ads createAds(CreateAds createAds, MultipartFile image, Authentication authentication) {
        if (createAds.getPrice() < 0) {
            throw new IllegalArgumentException("Цена должна быть больше 0!");
        }
        logger.info("Вызван метод добавления объявления");

        AdsEntity adsEntity = adsMapper.toEntity(createAds);
        UserEntity author = userRepository.findByEmailIgnoreCase(authentication.getName()).orElseThrow(RuntimeException::new); // TODO сделать свое исключение
        adsEntity.setAuthor(author);

        ImageEntity adImage;
        try {
            adImage = imageService.downloadImage(image);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке фото");
        }

        adsEntity.setImageEntity(adImage);
        adsRepository.save(adsEntity);
        logger.info("Сохранено новое объявление");

        return adsMapper.toAdsDto(adsEntity);
    }

    /**
     * Получение объявления по идентификатору (id), хранящихся в базе данных
     *
     * @param adsId идентификатор объявления, не может быть null
     * @return возвращает объявление по идентификатору (id)
     */
    @Override
    public FullAds getAds(Integer adsId) {
        logger.info("Вызван метод получения объявления по идентификатору (id)");
        return adsMapper.toFullAdsDto(adsRepository.findById(adsId).orElseThrow()); // TODO сделать свое исключение
    }

    /**
     * Удаление объявления по идентификатору (id), хранящихся в базе данных
     *
     * @param adsId идентификатор объявления, не может быть null
     */
    @Override
    public void deleteAds(Integer adsId) {
        logger.info("Вызван метод удаления объявления по идентификатору (id)");
        AdsEntity adsEntity = adsRepository.findById(adsId).orElseThrow(); // TODO сделать свое исключение
        commentService.deleteAllByAdsId(adsId);
        adsRepository.deleteById(adsId);
        imageService.deleteImage(adsEntity.getImageEntity().getId());
    }

    /**
     * Обновление объявления по идентификатору (id), хранящихся в базе данных
     *
     * @param adsId     идентификатор объявления, не может быть null
     * @param createAds данные объявления
     * @return возвращает обновленное объявление по идентификатору (id)
     */
    @Override
    public Ads updateAds(CreateAds createAds, Integer adsId) {
        if (adsId == null) {
            throw new RuntimeException("Такого объявления не существует!");
        }

        if (createAds.getPrice() < 0) {
            throw new RuntimeException("Цена должна быть больше 0!");
        }

        AdsEntity updateAd = adsRepository.findById(adsId).orElseThrow(RuntimeException::new);
        updateAd.setTitle(createAds.getTitle());
        updateAd.setPrice(createAds.getPrice());
        updateAd.setDescription(createAds.getDescription());

        adsRepository.save(updateAd);

        return adsMapper.toAdsDto(updateAd);
    }

    /**
     * Получение объявлений авторизованного пользователя, хранящихся в базе данных
     *
     * @return возвращает все объявления авторизованного пользователя
     */
    @Override
    public Collection<Ads> getAdsMe() {
        logger.info("Вызван метод получения объявлений авторизованного пользователя");
        Collection<AdsEntity> adsEntity;
        logger.info(myUserDetails.getUserId() + " " + myUserDetails.getAuthorities() + " " + myUserDetails.getUsername());
        adsEntity = adsRepository.findAllAdsByAuthorId(myUserDetails.getUserId());
        return adsMapper.adsEntityToCollectionDto(adsEntity);
    }

    /**
     * Обновление картинки объявления
     *
     * @param adsId идентификатор объявления, не может быть null
     * @param image картинка объявления
     * @return объявление с новой картинкой
     */
    @Override
    public String updateImage(Integer adsId, MultipartFile image) {
        logger.info("Вызван метод обновления картинки объявления");
        if (adsId == null) {
            throw new RuntimeException("Такого объявления не существует!");
        }

        ImageEntity adImage;
        try {
            adImage = imageService.downloadImage(image);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке фото");
        }

        int imageId = adImage.getId();
        imageService.deleteImage(imageId);

        AdsEntity ad = adsRepository.findById(adsId).orElseThrow(RuntimeException::new);
        ad.setImageEntity(adImage);
        adsRepository.save(ad);
        return adsMapper.toAdsDto(ad).getImage();
    }

    /**
     * Получение картинки объявления
     *
     * @param adsId идентификатор объявления, не может быть null
     * @return картинку объявления
     */
    @Override
    public byte[] getAdsImage(Integer adsId) {
        logger.info("Вызван метод получения картинки объявления по идентификатору (id)");
        return imageService.getImage(adsRepository.findById(adsId).orElseThrow().getImageEntity().getId()); // TODO сделать свое исключение
    }
}
