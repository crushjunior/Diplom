package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateComment;
import ru.skypro.homework.dto.ResponseWrapperComment;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.AdsEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.security.MyUserDetails;
import ru.skypro.homework.service.CommentService;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;


/**
 * Сервис для методов работы с комментариями
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(Comment.class);

    /**
     * Поле репозитория комментариев
     */
    private final CommentRepository commentRepository;

    /**
     * Поле репозитория объявлений
     */
    private final AdsRepository adsRepository;

    /**
     * Поле репозитория пользователя
     */
    private final UserRepository userRepository;

    /**
     * Поле маппинга комментариев
     */
    private final CommentMapper commentMapper;

    private final MyUserDetails userDetails;

    /**
     * Конструктор - создание нового объекта репозитория
     *
     * @param commentRepository репозиторий комментариев
     * @param adsRepository     репозиторий объявлений
     * @param userRepository    репозиторий пользователя
     * @param commentMapper
     * @see CommentRepository (CommentRepository)
     */
//    public CommentServiceImpl(CommentRepository commentRepository, AdsRepository adsRepository, UserRepository userRepository, CommentMapper commentMapper) {
//        this.commentRepository = commentRepository;
//        this.adsRepository = adsRepository;
//        this.userRepository = userRepository;
//        this.commentMapper = commentMapper;
//    }

    /**
     * Позволяет получить все комментарии к определенному объявлению
     * <br> Использован метод репозитория {@link ru.skypro.homework.repository.CommentRepository#getByAdsId(Integer)}
     *
     * @param adsId идентификатор объявления, не может быть null
     * @return возвращает все комментарии к определенному объявлению
     */
    @Override
    public ResponseWrapperComment getComments(Integer adsId) {
        logger.info("Вызван метод получения всех комментариев к определенному объявлению");
        Collection<CommentEntity> comments = commentRepository.getCommentEntitiesByAd_Id(adsId);
        ResponseWrapperComment responseWrapperComment = new ResponseWrapperComment();
        responseWrapperComment.setResults(commentMapper.commentsEntityToCommentsDtoCollection(comments));
        return responseWrapperComment;
    }

    /**
     * Позволяет добавить комментарий к определенному объявлению
     * <br> Использован метод репозитория {@link ru.skypro.homework.repository.CommentRepository#save(Object)}
     *
     * @param adsId идентификатор объявления, не может быть null
     * @param createComment  создание текста комментария
     * @param authentication авторизованный пользователь
     * @return возвращает добавленный комментарий
     */
    @Override
    public Comment addComment(@NotNull Integer adsId, CreateComment createComment, Authentication authentication) {
        logger.info("Вызван метод добавления комментария");
        CommentEntity commentEntity = commentMapper.toEntity(createComment);
        AdsEntity adsEntity = adsRepository.findById(adsId).orElseThrow(RuntimeException::new);
        UserEntity author = userRepository.getUserEntitiesByEmail(authentication.getName());
        commentEntity.setAd(adsEntity);
        commentEntity.setAuthor(author);
        commentEntity.setCreatedAt(LocalDateTime.now());
        commentRepository.save(commentEntity);

        return commentMapper.toDto(commentEntity);
    }

    /**
     * Позволяет удалить комментарий
     * <br> Использован метод репозитория {@link ru.skypro.homework.repository.CommentRepository#deleteByIdAndAdsId(Integer, Integer)}
     *
     * @param commentId идентификатор комментария, не может быть null
     * @param adsId     идентификатор объявления, не может быть null
     */
    @Override
    public void deleteComment(Integer adsId, Integer commentId) {
        logger.info("Вызван метод удаления комментария по идентификатору (id)");
        if (isOwner(adsId, commentId, userDetails)) {
            commentRepository.deleteCommentEntitiesByAd_IdAndId(adsId, commentId);
        }
    }

    /**
     * Позволяет изменить комментарий
     * <br> Использован метод репозитория {@link ru.skypro.homework.repository.CommentRepository#getByIdAndAdsId(Integer, Integer)}
     * <br> Использован метод репозитория {@link ru.skypro.homework.repository.CommentRepository#save(Object)}
     *
     * @param commentId      идентификатор комментария, не может быть null
     * @param comment        измененный комментарий
     * @return возвращает измененный комментарий
     */
    @Override
    public Comment updateComment(Integer adsId, @NotNull Integer commentId, Comment comment) {
        logger.info("Вызван метод обновления комментария по идентификатору (id)");
        CommentEntity updateCommentEntity = commentRepository.getCommentEntityByAd_IdAndId(adsId, commentId);

        if (isOwner(adsId, commentId, userDetails)) {
            updateCommentEntity.setText(comment.getText());
            commentRepository.save(updateCommentEntity);
            return commentMapper.toDto(updateCommentEntity);
        }

        throw new RuntimeException("Вы не можете редактировать чужой комментарий");
    }

    private boolean isOwner(Integer adsId, Integer commentId, MyUserDetails details) {
        if (commentRepository.getCommentEntityByAd_IdAndId(adsId, commentId).getAuthor().getEmail().equals(details.getUserSecurity().getEmail()) || details.getUserSecurity().getRole() == Role.ADMIN) {
            return true;
        } else {
            return false;
        }
    }
}
