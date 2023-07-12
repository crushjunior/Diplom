package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateComment;
import ru.skypro.homework.dto.ResponseWrapperComment;


public interface CommentService {
    ResponseWrapperComment getComments(Integer adsId);
    Comment addComment(Integer adsId, CreateComment createComment, Authentication authentication);

    void deleteComment(Integer adsId, Integer commentId);

    Comment updateComment(Integer adsId, Integer commentId, Comment comment);
}
