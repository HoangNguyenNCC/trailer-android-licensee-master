package devx.app.licensee.modules.ezphotopicker.models;

public class PhotoIntentException extends Exception {

    public PhotoIntentException(String message) {
        super(message);
    }

    public static PhotoIntentException getNullPhotoPickConfigException() {
        return new PhotoIntentException("You are missing the config for photo pick action");
    }
}
