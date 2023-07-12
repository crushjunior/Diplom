package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public ImageEntity downloadImage(MultipartFile image) throws IOException {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setMediaType(image.getContentType());
        imageEntity.setData(image.getBytes());
        return imageRepository.save(imageEntity);
    }

    @Override
    public void deleteImage(Integer id) {
        imageRepository.deleteById(id);
    }

    @Override
    public byte[] getImage(Integer id) {
        Optional<ImageEntity> imageEntity = imageRepository.findById(id);
        return imageEntity.orElseThrow().getData();
    }
}
