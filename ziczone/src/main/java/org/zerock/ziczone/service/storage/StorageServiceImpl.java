package org.zerock.ziczone.service.storage;

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
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(new PutObjectRequest(bucketName, objectName, file.getInputStream(), metadata));
            // 업로드된 파일의 접근 제어 리스트 가져오기
            AccessControlList accessControlList = amazonS3.getObjectAcl(bucketName, objectName);
            //  모든 사용자에게 읽기 권한 부여
            accessControlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);

            amazonS3.setObjectAcl(bucketName, objectName, accessControlList);

            // 업로드된 파일의 URL 가져오기
            fileUrl = amazonS3.getUrl(bucketName, objectName).toString();

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
    public void deleteFile(String fileUrl) {
        if (fileUrl != null && !fileUrl.isEmpty()) {
            amazonS3.deleteObject("ziczone-bucket-jangindle-optimizer", fileUrl);
        }
    }
}
