package devx.app.licensee.modules.ezphotopicker.storage;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.IOException;

import static devx.app.licensee.modules.ezphotopicker.models.PhotoIntentConstants.THUMB_NAME_SUFFIX;

public class PhotoIntentHelperStorage {

    private static PhotoIntentHelperStorage photoIntentHelperStorage;
    private PathStorage pathStorage;
    private PhotoInternalStorage photoInternalStorage;


    public PhotoIntentHelperStorage(Context context) {
        pathStorage = new PathStorage(context);
        photoInternalStorage = new PhotoInternalStorage(context);
    }


    public static PhotoIntentHelperStorage getInstance(Context context) {
        if (photoIntentHelperStorage == null) {
            photoIntentHelperStorage = new PhotoIntentHelperStorage(context);
        }
        return photoIntentHelperStorage;
    }

    public void storePhotoBitmapThumbnail(Bitmap pickingPhoto, Bitmap.CompressFormat bitmapConfig, String storageDir, String storingPhotoName) {
        storingPhotoName += THUMB_NAME_SUFFIX;
        storePhotoBitmap(pickingPhoto, bitmapConfig, storageDir, storingPhotoName);
    }

    public Bitmap storePhotoBitmap(Bitmap pickingPhoto, Bitmap.CompressFormat compressFormat, String internalStorageDir, String randomPhotoName) {
        return photoInternalStorage.storePhotoBitmap(pickingPhoto, compressFormat, internalStorageDir, randomPhotoName);
    }

    public void storeLatestStoredPhotoName(String randomPhotoName) {
        pathStorage.storeLatestStoredPhotoName(randomPhotoName);
    }

    public String loadLatestStoredPhotoName() {
        return pathStorage.loadLastetStoredPhotoName();
    }

    public void storeLatestStoredPhotoDir(String photoDir) {
        pathStorage.storeLastetStoredPhotoDir(photoDir);
    }

    public String loadLatestStoredPhotoDir() {
        return pathStorage.loadLastetStoredPhotoDir();
    }

    public Bitmap loadLatestStoredPhotoBitmap() throws IOException {
        return loadLatestStoredPhotoBitmap(0);
    }

    public Bitmap loadLatestStoredPhotoBitmap(int maxScaleSize) throws IOException {
        String storedPhotoDir = loadLatestStoredPhotoDir();
        String storedPhotoName = loadLatestStoredPhotoName();
        return loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName, maxScaleSize);
    }

    public Bitmap loadStoredPhotoBitmap(String storedPhotoDir, String storedPhotoName) throws IOException {
        return loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName, 0);
    }

    public Bitmap loadStoredPhotoBitmap(String storedPhotoDir, String storedPhotoName, int maxScaleSize) throws IOException {
        return photoInternalStorage.loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName, maxScaleSize);
    }


    public boolean removePhoto(String storedPhotoDir, String storedPhotoName) {
        return photoInternalStorage.removePhoto(storedPhotoDir, storedPhotoName);
    }

    public String getAbsolutePathOfStoredPhoto(String storedPhotoDir, String storedPhotoName) {
        return photoInternalStorage.getAbsolutePathOfStoredPhoto(storedPhotoDir, storedPhotoName);
    }
}
