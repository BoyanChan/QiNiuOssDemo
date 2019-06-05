package top.boyn.uploadfile.qiniu.storage;

import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.PutPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FileSystemStorageService implements StorageService{

    private static final Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class);
    private static String AccessKey;
    private static String SecretKey;
    private static String bucketName;
    private static Mac mac;
    private static PutPolicy putPolicy;
    private static String token;
    private static String prefix;
    private static PutExtra extra = new PutExtra();

    @Override
    public void init() throws Exception{
        Config.ACCESS_KEY=AccessKey;
        Config.SECRET_KEY=SecretKey;
        mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
        putPolicy = new PutPolicy(bucketName);
        token = putPolicy.token(mac);
    }

    @Override
    public void store(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());//将文件名中的路径去除
        try{
            if(file.isEmpty()){
                throw new StorageException("Failed to store empty file "+fileName);
            }
            if(fileName.contains("..")){
                throw new StorageException("Cannot store file with relative path outside current directory "+fileName);
            }
            try(InputStream inputStream = file.getInputStream()){
                //token是自动生成的,key指的是你要上传到服务器的文件名,七牛云OSS使用前缀/名字的方式表示文件夹
                //inputStream是文件,extra是额外的参数,此处设为空
                PutRet ret = IoApi.Put(token,prefix+file.getOriginalFilename(),inputStream,extra);
                String returnFileName = ret.getKey();//PutRet返回Hash值与Key,Key就是文件名
                logger.info("Uploaded file: "+returnFileName);
            }

        }
        catch (IOException e){
            throw new StorageException("Failed to store file "+fileName,e);
        }
    }

    @Value("${qiniu.prefix}")
    public static void setPrefix(String Prefix) {
        prefix = Prefix;
    }

    @Value("${qiniu.bucket-name}")
    public static void setBucketName(String bucketname){
        bucketName = bucketname;
    }

    @Value("${qiniu.ACCESS_KEY}")
    public void setAccessKey(String accessKey) {
        AccessKey = accessKey;
    }

    @Value("${qiniu.SECRET_KEY}")
    public void setSecretKey(String secretKey) {
        SecretKey = secretKey;
    }

}
