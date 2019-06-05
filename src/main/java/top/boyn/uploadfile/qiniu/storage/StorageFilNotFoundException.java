package top.boyn.uploadfile.qiniu.storage;

public class StorageFilNotFoundException extends StorageException {
    public StorageFilNotFoundException(String message) {
        super(message);
    }

    public StorageFilNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
