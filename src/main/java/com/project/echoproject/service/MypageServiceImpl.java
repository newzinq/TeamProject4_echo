package com.project.echoproject.service;

import com.project.echoproject.entity.Image;
import com.project.echoproject.entity.SiteUser;
import com.project.echoproject.repository.SiteUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class MypageServiceImpl implements MypageService {

    private final SiteUserRepository userRepository;
    private final ImageService imageService;

    @Autowired
    public MypageServiceImpl(SiteUserRepository userRepository, ImageService imageService) {
        this.userRepository = userRepository;
        this.imageService = imageService;
    }

    @Override
    public SiteUser getUserById(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    @Override
    public void updateUser(String userId, SiteUser updatedUser, MultipartFile file) throws IOException {
        SiteUser updateInfo = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        updateInfo.setUserName(updatedUser.getUserName());
        updateInfo.setEmail(updatedUser.getEmail());
        updateInfo.setPhoneNum(updatedUser.getPhoneNum());

        if (file != null && !file.isEmpty()) {
            Image image = imageService.saveImage(file);
            updateInfo.setImgUrl(image.getFilePath());
        }

        userRepository.save(updateInfo);
    }

    @Transactional
    @Override
    public void deleteUser(String userId) {
        SiteUser user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        userRepository.delete(user);
    }


    @Override
    public String encodeImageToBase64(String filePath) throws IOException {
        return imageService.encodeImageToBase64(filePath);
    }
}






