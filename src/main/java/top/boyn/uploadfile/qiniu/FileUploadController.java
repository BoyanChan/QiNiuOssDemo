package top.boyn.uploadfile.qiniu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.boyn.uploadfile.qiniu.storage.FileSystemStorageService;
import top.boyn.uploadfile.qiniu.storage.StorageFilNotFoundException;
import top.boyn.uploadfile.qiniu.storage.StorageService;

@Controller
public class FileUploadController {
    private final StorageService storageService;
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadFiles() {
        return "uploadForm";
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        if(storageService.store(file)) {
            logger.info("Success");
            redirectAttributes.addFlashAttribute("message", "Successfully uploaded " +
                    file.getOriginalFilename() + " !");
        }else {
            redirectAttributes.addFlashAttribute("message", "Failed uploaded " +
                    file.getOriginalFilename() + " !");
        }
        return "redirect:/";
    }

    /**
     * 可以提供给前端进行请求
     * @return
     */
    @GetMapping("/token")
    @ResponseBody
    @CrossOrigin(maxAge = 3600,value = "*")
    public String getToken(){
        return storageService.getToken();
    }

    @ExceptionHandler(StorageFilNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFilNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

}
