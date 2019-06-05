package top.boyn.uploadfile;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import top.boyn.uploadfile.qiniu.storage.StorageService;

@SpringBootApplication
public class UploadfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(UploadfileApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args -> {
            storageService.init();
        });
    }
}
