package top.boyn.uploadfile.qiniu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.boyn.uploadfile.qiniu.storage.StorageFilNotFoundException;
import top.boyn.uploadfile.qiniu.storage.StorageService;

@Controller
public class FileUploadController {
    private final StorageService storageService;

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
        storageService.store(file);
        redirectAttributes.addFlashAttribute("message", "Successfully uploaded " +
                file.getOriginalFilename() + " !");
        return "redirect:/";
    }

    @ExceptionHandler(StorageFilNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFilNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

}
