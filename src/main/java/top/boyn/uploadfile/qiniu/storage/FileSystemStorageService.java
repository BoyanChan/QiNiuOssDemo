package top.boyn.uploadfile.qiniu.storage;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class FileSystemStorageService implements StorageService{

    private static final Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class);
    private static String AccessKey;//官网上的访问秘钥
    private static String SecretKey;//官网上的秘钥
    private static String bucketName;//对象存储空间名字
    private static Auth auth;//认证对象
    private static String prefix;//前缀
    private static Configuration cfg;
    private static UploadManager uploadManager;
    private static String token;//token是动态变化的,所以每次上传都需要请求一次


    @Override
    public void init(){

        cfg = new Configuration(Zone.zone0());
        uploadManager = new UploadManager(cfg);

        /* 6.* version
        Config.ACCESS_KEY=AccessKey;
        Config.SECRET_KEY=SecretKey;
        mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
        putPolicy = new PutPolicy(bucketName);
        token = putPolicy.token(mac);*/
    }

    @Override
    public boolean store(MultipartFile file) {
        auth = Auth.create(AccessKey,SecretKey);
        token = auth.uploadToken(bucketName);
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());//将文件名中的路径去除
        try{
            if(file.isEmpty()){
                throw new StorageException("Failed to store empty file "+fileName);
            }
            if(fileName.contains("..")){
                throw new StorageException("Cannot store file with relative path outside current directory "+fileName);
            }
            try(InputStream inputStream = file.getInputStream()){
                Response response = uploadManager.put(inputStream,fileName,token,null,null);
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                logger.info(putRet.key);//显示上传的文件名
                return true;
            }
        }
        catch (QiniuException ex){
            System.out.println(ex.error());
            System.out.println(ex.code());
            Response r = ex.response;
            if(r!=null) {
                System.out.println(r);
                try {
                    System.out.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception e){
            logger.info("Fail");
            throw new StorageException("Failed to store file "+fileName,e);
        }
        return false;
        /*  6.* version
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
        }*/

    }

    @Override
    public String getToken() {
        auth = Auth.create(AccessKey,SecretKey);
        token = auth.uploadToken(bucketName);
        return token;
    }

    @Value("${qiniu.prefix}")
    public void setPrefix(String Prefix) {
        prefix = Prefix;

    }

    @Value("${qiniu.bucket-name}")
    public void setBucketName(String bucketname){
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
