package org.zerock.ziczone.service.storage;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String uploadFile(MultipartFile file, String folderName, String objectName, String bucketName);

    void deleteFile(String bucketName, String folderName, String fileUUID);
}
