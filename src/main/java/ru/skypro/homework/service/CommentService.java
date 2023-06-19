package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateComment;
import ru.skypro.homework.dto.ResponseWrapperComment;
import ru.skypro.homework.entity.AdsEntity;


public interface CommentService {
    ResponseWrapperComment getComments(Integer adsId);
    Comment addComment(AdsEntity ad, CreateComment createComment, Authentication authentication);

    void deleteComment(Integer adsId, Integer commentId, Authentication authentication);

    Comment updateComment(Integer adsId, Integer commentId, Comment comment, Authentication authentication);
}
