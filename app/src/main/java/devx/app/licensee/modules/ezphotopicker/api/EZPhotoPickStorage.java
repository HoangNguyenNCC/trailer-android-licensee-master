package devx.app.licensee.modules.ezphotopicker.api;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.IOException;

import devx.app.licensee.modules.ezphotopicker.models.PhotoIntentConstants;
import devx.app.licensee.modules.ezphotopicker.storage.PhotoIntentHelperStorage;

public class EZPhotoPickStorage {

    private PhotoIntentHelperStorage photoIntentHelperStorage;

    public EZPhotoPickStorage(Context context) {
        photoIntentHelperStorage = PhotoIntentHelperStorage.getInstance(context);
    }

    public Bitmap loadLatestStoredPhotoBitmap() throws IOException {
        return loadLatestStoredPhotoBitmap(0);
    }

    public Bitmap loadLatestStoredPhotoBitmapThumbnail() throws IOException {
        String storedPhotoDir = photoIntentHelperStorage.loadLatestStoredPhotoDir();
        String storedPhotoName = photoIntentHelperStorage.loadLatestStoredPhotoName() + PhotoIntentConstants.THUMB_NAME_SUFFIX;
        return loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName, 0);
    }

    public Bitmap loadLatestStoredPhotoBitmap(int maxScaleSize) throws IOException {
        String storedPhotoDir = photoIntentHelperStorage.loadLatestStoredPhotoDir();
        String storedPhotoName = photoIntentHelperStorage.loadLatestStoredPhotoName();
        return loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName, maxScaleSize);
    }

    public Bitmap loadStoredPhotoBitmap(String storedPhotoDir, String storedPhotoName) throws IOException {
        return loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName, 0);
    }

    public Bitmap loadStoredPhotoBitmapThumbnail(String storedPhotoDir, String storedPhotoName) throws IOException {
        String storedPhotoThumbnailName = storedPhotoName + PhotoIntentConstants.THUMB_NAME_SUFFIX;
        return loadStoredPhotoBitmap(storedPhotoDir, storedPhotoThumbnailName, 0);
    }

    public Bitmap loadStoredPhotoBitmap(String storedPhotoDir, String storedPhotoName, int maxScaleSize) throws IOException {
        return photoIntentHelperStorage.loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName, maxScaleSize);
    }

    public boolean removePhoto(String storedPhotoDir, String storedPhotoName) {
        return photoIntentHelperStorage.removePhoto(storedPhotoDir, storedPhotoName);
    }

    public String getAbsolutePathOfStoredPhoto(String storedPhotoDir, String storedPhotoName) {
        return photoIntentHelperStorage.getAbsolutePathOfStoredPhoto(storedPhotoDir, storedPhotoName);
    }

}
