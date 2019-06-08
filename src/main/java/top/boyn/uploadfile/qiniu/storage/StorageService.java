package top.boyn.uploadfile.qiniu.storage;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface StorageService {

    void init() throws Exception;

    String getToken();

    boolean store(MultipartFile file);
}
