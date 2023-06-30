package ru.skypro.homework.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Класс ImageEntity, представляет сущность изображении
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "image")
public class ImageEntity {

    /**
     * Идентификационный номер (id) изображения
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    /**
     * Тип изображения
     */
    private String mediaType;

    /**
     * Данные изображения
     */
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] data;
}
