package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.CommentEntity;

import java.util.Collection;

/**
 * Интерфейс CommentRepository
 * для работы с БД (для комментариев)
 */
@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    // Collection<CommentEntity> getByAdsId(Integer adsId);
    Collection<CommentEntity> getCommentEntitiesByAd_Id(Integer adsId);

    // CommentEntity getByIdAndAdsId(Integer adsId, Integer commentId);

    CommentEntity getCommentEntityByAd_IdAndId(Integer adsId, Integer commentId);

    //void deleteByIdAndAdsId(Integer adsId, Integer commentId);

    void deleteCommentEntitiesByAd_IdAndId(Integer adsId, Integer commentId);

}
