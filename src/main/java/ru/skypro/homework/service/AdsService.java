package ru.skypro.homework.service;


import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateAds;
import ru.skypro.homework.dto.FullAds;

import java.io.IOException;
import java.util.Collection;

public interface AdsService {
    Collection<Ads> getAllAds(String title);
    Ads createAds(CreateAds createAds, MultipartFile image, Authentication authentication);
    FullAds getAds(Integer adsId);
    void deleteAds(Integer adsId);
    Ads updateAds(CreateAds createAds, Integer adsId);
    Collection<Ads> getAdsMe(Authentication authentication);
    String updateImage(Integer adsId, MultipartFile image) throws IOException;
    byte[] getAdImage(Integer adsId);
}
