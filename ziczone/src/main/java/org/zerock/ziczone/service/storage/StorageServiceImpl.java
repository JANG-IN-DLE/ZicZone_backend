package org.zerock.ziczone.service.storage;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class StorageServiceImpl implements StorageService {

    private final AmazonS3 amazonS3;

    @Override
    public String uploadFile(MultipartFile file, String folderName, String objectName, String bucketName) {
        if (file == null) {
            return null;
        }
        if (folderName == null || folderName.isEmpty()) {
            return "Folder name is null or empty";
        }
        if (objectName == null || objectName.isEmpty()) {
            return "Object name is null or empty";
        }
        if (bucketName == null || bucketName.isEmpty()) {
            return "Bucket name is null or empty";
        }
        String fileUrl = null;
        try {
            if(!folderName.endsWith("/")){
                folderName += "/";
            }
            String fullObjectName = folderName + objectName;
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

        } catch (IOException ioException) {
            return ioException.getMessage();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        if (fileUrl == null) {
            return "File URL is null";
        }


        return fileUrl;
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
            System.out.format("Object %s has been deleted.\n", fileKey);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete object: " + fileUUID, e);
        } catch (SdkClientException e) {
            e.printStackTrace();
            throw new RuntimeException("SDK client error while deleting object: " + fileUUID, e);
        }
    }
}
