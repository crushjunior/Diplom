package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateComment;
import ru.skypro.homework.entity.CommentEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "createdAt", expression = "java(time(commentEntity))")
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    @Mapping(target = "authorImage", expression = "java(image(commentEntity))")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    Comment toDto(CommentEntity commentEntity);

    CommentEntity toEntity(CreateComment createComment);

    List<Comment> commentsEntityToCommentsDtoCollection(Collection<CommentEntity> commentEntityCollection);


    default Long time(CommentEntity commentEntity) {
        LocalDateTime time = commentEntity.getCreatedAt();
        if (time == null) {
            return 0L;
        }
        return time.toInstant(ZoneOffset.ofHours(3)).toEpochMilli();
    }

    default String image(CommentEntity commentEntity) {
        int id = commentEntity.getAuthor().getId();
        return "/users/" + id + "/image";
    }


}
