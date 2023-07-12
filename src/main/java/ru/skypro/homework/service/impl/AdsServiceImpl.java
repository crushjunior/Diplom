package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateAds;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.AdsEntity;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.AccessException;
import ru.skypro.homework.exception.ObjectAbsenceException;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.security.MyUserDetails;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;

import java.io.IOException;
import java.util.Collection;


/**
 * Сервис AdsServiceImpl
 * Сервис для добавления, удаления, редактирования и поиска объявлений в базе данных
 */

@Service
@RequiredArgsConstructor
@Transactional
public class AdsServiceImpl implements AdsService {

    private static final Logger logger = LoggerFactory.getLogger(AdsServiceImpl.class);

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

    private final MyUserDetails userDetails;

    private final CommentRepository commentRepository;

    /**
     * Получение списка всех объявлений из базы данных
     *
     * @return список(коллекцию) объявлений
     */
    @Override
    public Collection<Ads> getAllAds(String title) {
        logger.info("Вызван метод получения всех объявлений");
        if (title == null) {

            return adsMapper.adsEntityToCollectionDto(adsRepository.findAll());
        }
        return adsMapper.adsEntityToCollectionDto(adsRepository.findByTitleLikeIgnoreCase(title));
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
        UserEntity author = userRepository.findByEmailIgnoreCase(authentication.getName()).orElseThrow(RuntimeException::new);
        adsEntity.setAuthor(author);

        ImageEntity adImage;
        try {
            adImage = imageService.downloadImage(image);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке фото");
        }

        adsEntity.setImageEntity(adImage);
        adsRepository.save(adsEntity);

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
        return adsMapper.toFullAdsDto(adsRepository.findById(adsId).orElseThrow());
    }

    /**
     * Удаление объявления по идентификатору (id), хранящихся в базе данных
     *
     * @param adsId          идентификатор объявления, не может быть null
     */
    @Override
    public void deleteAds(Integer adsId) {
        logger.info("Вызван метод удаления объявления по идентификатору (id)");
        AdsEntity deleteAd = adsRepository.findById(adsId).orElseThrow(RuntimeException::new);
        if (isOwner(deleteAd, userDetails)) {
            commentRepository.deleteCommentEntitiesByAd_Id(adsId);
            imageService.deleteImage(deleteAd.getImageEntity().getId());
            adsRepository.deleteById(adsId);
        } else {
            try {
                throw new AccessException("Вы не можете удалять чужие объявления");
            } catch (AccessException e) {
                e.getMessage();
            }
        }
    }

    /**
     * Обновление объявления по идентификатору (id), хранящихся в базе данных
     *
     * @param adsId          идентификатор объявления, не может быть null
     * @param createAds      данные объявления
     * @return возвращает обновленное объявление по идентификатору (id)
     */
    @Override
    public Ads updateAds(CreateAds createAds, Integer adsId) {
        if (adsId == null) {
            try {
                throw new ObjectAbsenceException("Такого объявления не существует!");
            } catch (ObjectAbsenceException e) {
                e.getMessage();
            }
        }

        if (createAds.getPrice() < 0) {
            try {
                throw new RuntimeException("Цена должна быть больше 0!");
            } catch (RuntimeException e) {
                e.getMessage();
            }

        }

        AdsEntity updateAd = adsRepository.findById(adsId).orElseThrow(RuntimeException::new);
        if (isOwner(updateAd, userDetails)) {
            updateAd.setTitle(createAds.getTitle());
            updateAd.setPrice(createAds.getPrice());
            updateAd.setDescription(createAds.getDescription());

            adsRepository.save(updateAd);

            return adsMapper.toAdsDto(updateAd);
        } else {
            try {
                throw new AccessException("Вы не можете изменять чужие объявления");
            } catch (AccessException e) {
                e.getMessage();
            }
            return null;
        }
    }

    /**
     * Получение объявлений авторизованного пользователя, хранящихся в базе данных
     *
     * @param authentication авторизованный пользователь
     * @return возвращает все объявления авторизованного пользователя
     */
    @Override
    public Collection<Ads> getAdsMe(Authentication authentication) {
        logger.info("Вызван метод получения объявлений авторизованного пользователя");
        Collection<AdsEntity> AdsEntity = adsRepository.findByAuthorId(userService.getUser(authentication).getId());
        return adsMapper.adsEntityToCollectionDto(AdsEntity);
    }

    /**
     * Обновление картинки объявления
     *
     * @param adsId          идентификатор объявления, не может быть null
     * @param image          картинка объявления
     * @return объявление с новой картинкой
     */
    @Override
    public String updateImage(Integer adsId, MultipartFile image) throws IOException{
        logger.info("Вызван метод обновления картинки объявления");
        AdsEntity updateAd = adsRepository.findById(adsId).orElseThrow(RuntimeException::new);
        if (isOwner(updateAd, userDetails)) {
            Integer idDeleteImage = updateAd.getImageEntity().getId();
            updateAd.setImageEntity(imageService.downloadImage(image));
            imageService.deleteImage(idDeleteImage);
            adsRepository.save(updateAd);
            return adsMapper.toAdsDto(updateAd).getImage();
        } else {
            try {
                throw new AccessException("Вы не можете изменить автар этого профиля");
            } catch (AccessException e) {
                e.getMessage();
            }
            return null;
        }
    }

    @Override
    public byte[] getAdImage(Integer adsId) {
        logger.info("Get image of an AD with a ID: " + adsId);
        return imageService.getImage(adsRepository.findById(adsId).orElseThrow(RuntimeException::new).getImageEntity().getId());
    }

    private boolean isOwner(AdsEntity ad, MyUserDetails details) {
        boolean isOwner = false;
        if (ad.getAuthor().getEmail().equals(details.getUserSecurity().getEmail()) || details.getUserSecurity().getRole() == Role.ADMIN) {
            isOwner = true;
        }
        return isOwner;
    }
}
