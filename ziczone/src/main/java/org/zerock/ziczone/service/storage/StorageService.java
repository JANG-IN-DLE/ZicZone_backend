package org.zerock.ziczone.service.storage;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface StorageService {

    Map<String, String> uploadFile(MultipartFile file, String folderName, String bucketName);

    void deleteFile(String bucketName, String folderName, String fileUUID);

    Map<String, Object> getFile(String bucketName, String folderName, String fileUUID);
}
