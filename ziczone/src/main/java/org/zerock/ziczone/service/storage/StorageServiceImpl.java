package org.zerock.ziczone.service.storage;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final AmazonS3 amazonS3;

    @Override
    public Map<String,String> uploadFile(MultipartFile file, String folderName, String bucketName) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String fileUUID = UUID.randomUUID().toString();
        String fileUrl = null;
        try {
            String fullObjectName = folderName + "/" + fileUUID;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(new PutObjectRequest(bucketName, fullObjectName, file.getInputStream(), metadata));
            // 업로드된 파일의 접근 제어 리스트 가져오기
            AccessControlList accessControlList = amazonS3.getObjectAcl(bucketName, fullObjectName);
            //  모든 사용자에게 읽기 권한 부여
            accessControlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);
            amazonS3.setObjectAcl(bucketName, fullObjectName, accessControlList);
            // 업로드된 파일의 URL 가져오기
            fileUrl = amazonS3.getUrl(bucketName, fullObjectName).toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file",e);
        }

        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new NullPointerException("fileUrl is null");
        }

        Map<String,String> result = new HashMap<>();
        result.put("fileUrl",fileUrl);
        result.put("fileUUID",fileUUID);
        result.put("fileOriginalFileName",file.getOriginalFilename());

        return result;
    }

    @Override
    public void deleteFile(String bucketName, String folderName, String fileUUID) {
        if (bucketName == null || bucketName.isEmpty()) {
            throw new IllegalArgumentException("Bucket name is null or empty");
        }
        if (folderName == null || folderName.isEmpty()) {
            throw new IllegalArgumentException("Folder name is null or empty");
        }
        if (fileUUID == null || fileUUID.isEmpty()) {
            throw new IllegalArgumentException("File UUID is null or empty");
        }

        try {
            String fileKey = folderName + "/" + fileUUID;  // 폴더 이름과 파일 UUID를 결합하여 파일 키 생성
            amazonS3.deleteObject(bucketName, fileKey);
            log.info("Object %s has been deleted.{}", fileKey);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete object: " + fileUUID, e);
        } catch (SdkClientException e) {
            e.printStackTrace();
            throw new RuntimeException("SDK client error while deleting object: " + fileUUID, e);
        }
    }
}
