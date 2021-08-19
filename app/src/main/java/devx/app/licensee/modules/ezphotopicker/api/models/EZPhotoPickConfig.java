package devx.app.licensee.modules.ezphotopicker.api.models;

import java.io.Serializable;

import devx.app.licensee.modules.ezphotopicker.models.PhotoIntentConstants;

public class EZPhotoPickConfig implements Serializable {

    /**
     * Source: {@link PhotoSource}
     * CAMERA | GALLERY
     */
    public PhotoSource photoSource;
    /**
     * storage folder
     * for eg: "abc/def"
     */
    public String storageDir;


    /**
     * rotate the photo to right direction by exif value
     * it happen many times in Samsung phone
     * default True
     */
    public boolean needToRotateByExif = true;

    /**
     * Add to gallery after capturing photo
     */
    public boolean needToAddToGallery = false;

    public boolean needToExportThumbnail = false;
    public int exportingThumbSize = 200;

    /**
     * generate file name base on the current time
     * or keep it hard name as
     * {@link PhotoIntentConstants#TEMP_STORING_PHOTO_NAME}
     */
    public boolean isGenerateUniqueName = false;
    public boolean isAllowMultipleSelect = false;
    public String exportedPhotoName = null;

    /**
     * exporting photo size to internal storage,
     * default is 0, mean original size
     * for eg:
     * exportingSize = 1000;
     * your photo : W x H : 2000 x 500;
     * then after picking photo: W x H: 1000 x 250.
     */
    public int exportingSize = 0;

    /**
     * Error message resource id
     * default is hard string by english
     * use it when u want to support multiple language
     */
    public int permisionDeniedErrorStringResource;
    public int unexpectedErrorStringResource;
}
