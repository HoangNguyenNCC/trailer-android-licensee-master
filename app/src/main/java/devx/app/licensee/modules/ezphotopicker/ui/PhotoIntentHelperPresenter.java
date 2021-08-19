package devx.app.licensee.modules.ezphotopicker.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import devx.app.licensee.modules.ezphotopicker.api.models.EZPhotoPickConfig;
import devx.app.licensee.modules.ezphotopicker.api.models.PhotoSource;
import devx.app.licensee.modules.ezphotopicker.models.PhotoIntentConstants;
import devx.app.licensee.modules.ezphotopicker.models.PhotoIntentException;
import devx.app.licensee.modules.ezphotopicker.storage.PhotoGenerator;
import devx.app.licensee.modules.ezphotopicker.storage.PhotoIntentContentProvider;
import devx.app.licensee.modules.ezphotopicker.storage.PhotoIntentHelperStorage;
import devx.app.licensee.modules.ezphotopicker.storage.PhotoUriHelper;

class PhotoIntentHelperPresenter implements PhotoIntentHelperContract.Presenter {

    private static final int STORE_SUCCESS_MSG = 0;
    private static final int STORE_FAIL_MSG = 1;

    private static boolean isStoringPhoto = false;
    private static boolean isOpenedPhotoPick = false;
    private static List<Uri> photoUriList = new ArrayList<>();
    private static List<String> storedPhotoNames = new ArrayList<>();
    private PhotoIntentHelperContract.View view;
    private PhotoIntentHelperStorage photoIntentHelperStorage;
    private PhotoUriHelper photoUriHelper;
    private PhotoGenerator photoGenerator;
    private EZPhotoPickConfig eZPhotoPickConfig;
    private String storingPhotoName;
    private boolean isAllowMultipleSelect;
    private Handler photoPickHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case STORE_SUCCESS_MSG:
                    finishPickPhotoWithSuccessResult();
                    photoUriList.clear();
                    storedPhotoNames.clear();
                    break;
                case STORE_FAIL_MSG:
                    view.showPickPhotoFromGalleryError(eZPhotoPickConfig.unexpectedErrorStringResource);
                    view.finishWithNoResult();
                    break;
            }
            isStoringPhoto = false;
            return false;
        }
    });

    PhotoIntentHelperPresenter(PhotoIntentHelperContract.View view, PhotoUriHelper photoUriHelper, PhotoGenerator photoGenerator, PhotoIntentHelperStorage photoIntentHelperStorage, EZPhotoPickConfig eZPhotoPickConfig) {
        this.view = view;
        this.photoUriHelper = photoUriHelper;
        this.photoGenerator = photoGenerator;
        this.photoIntentHelperStorage = photoIntentHelperStorage;
        this.eZPhotoPickConfig = eZPhotoPickConfig;
        isAllowMultipleSelect = eZPhotoPickConfig.isAllowMultipleSelect && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) throws PhotoIntentException {
        if (eZPhotoPickConfig == null) {
            throw PhotoIntentException.getNullPhotoPickConfigException();
        }

        if (isStoringPhoto) {
            view.showLoading();
            return;
        }

        if (isOpenedPhotoPick) {
            return;
        }

        if (eZPhotoPickConfig.photoSource == PhotoSource.CAMERA) {
            view.requestCameraAndExternalStoragePermission(eZPhotoPickConfig.needToAddToGallery);
        } else {
            onPickPhotoWithGallery();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPhotoPickedFromGallery(final Intent data) {
        photoUriList.clear();
        if (isAllowMultipleSelect && data.getClipData() != null) {
            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                photoUriList.add(data.getClipData().getItemAt(i).getUri());
            }
        } else {
            photoUriList.add(data.getData());
        }
        for (Uri photoUri : photoUriList) {
            boolean isPhotoUriPointToExternalStorage = isUriPointToExternalStorage(photoUri);
            if (!isPhotoUriPointToExternalStorage) {
                view.requestReadExternalStoragePermission();
                return;
            }
        }

        onPhotoPicked();
    }

    @Override
    public void onRequestReadExternalPermissionGranted() {
        onPhotoPicked();
    }

    private void onPhotoPicked() {
        isOpenedPhotoPick = false;
        isStoringPhoto = true;
        view.showLoading();
        processPickedUrisInBackground();
    }

    private boolean isUriPointToExternalStorage(Uri photoUri) {
        return photoUri.toString().contains(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    private void processPickedUrisInBackground() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Uri photoUri : photoUriList) {
                    processPickedUri(photoUri);
                }
                if (photoPickHandler != null)
                    photoPickHandler.sendEmptyMessage(STORE_SUCCESS_MSG);
            }
        }).start();
    }

    private void processPickedUri(Uri photoUri) {
        generateStoringPhotoName();
        try {
            Bitmap pickingPhoto = photoGenerator.generatePhotoWithValue(photoUri, eZPhotoPickConfig);

            Bitmap.CompressFormat bitmapConfig = photoUriHelper.getUriPhotoBitmapFormat(photoUri);

            photoIntentHelperStorage.storePhotoBitmap(pickingPhoto, bitmapConfig, eZPhotoPickConfig.storageDir, storingPhotoName);

            Bitmap thumbnail;
            if (eZPhotoPickConfig.needToExportThumbnail) {
                thumbnail = photoGenerator.scalePhotoByMaxSize(eZPhotoPickConfig.exportingThumbSize, pickingPhoto);
                photoIntentHelperStorage.storePhotoBitmapThumbnail(thumbnail, bitmapConfig, eZPhotoPickConfig.storageDir, storingPhotoName);
            }

            if (needToAddToGallery()) {
                addLastestCapturedPhotoToGallery(pickingPhoto);
            }
            storedPhotoNames.add(storingPhotoName);
            photoIntentHelperStorage.storeLatestStoredPhotoName(storingPhotoName);
            photoIntentHelperStorage.storeLatestStoredPhotoDir(eZPhotoPickConfig.storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            photoPickHandler.sendEmptyMessage(STORE_FAIL_MSG);
        }
    }

    private boolean needToAddToGallery() {
        return eZPhotoPickConfig.needToAddToGallery && eZPhotoPickConfig.photoSource == PhotoSource.CAMERA;
    }

    private void generateStoringPhotoName() {

        if (eZPhotoPickConfig.isGenerateUniqueName || isPickingMultiplePhotoFromGallery()) {
            storingPhotoName = UUID.randomUUID().toString();
            return;
        }

        if (!TextUtils.isEmpty(eZPhotoPickConfig.exportedPhotoName)) {
            storingPhotoName = eZPhotoPickConfig.exportedPhotoName;
            return;
        }

        storingPhotoName = PhotoIntentConstants.TEMP_STORING_PHOTO_NAME;

    }

    private boolean isPickingMultiplePhotoFromGallery() {
        return isAllowMultipleSelect && eZPhotoPickConfig.photoSource == PhotoSource.GALLERY;
    }

    @Override
    public void onPhotoPickedFromCamera(File internalDir) {
//        view.notifyGalleryDataChanged(exportedPhotoUri);
        File photoFile = new File(internalDir, PhotoIntentContentProvider.TEMP_PHOTO_NAME);
        photoUriList.clear();
        photoUriList.add(Uri.fromFile(photoFile));
        onPhotoPicked();
    }

    private void addLastestCapturedPhotoToGallery(Bitmap thumb) {
        try {
            Calendar calendar = Calendar.getInstance();
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
            File myDir = new File(root);
            myDir.mkdirs();
            String fname = calendar.getTime().getTime() + ".jpg";
            File file = new File(myDir, fname);
            if (file.exists()) file.delete();
            FileOutputStream out = new FileOutputStream(file);
            thumb.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            view.sendBroadcastToScanFileInGallery(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestCameraPermissionGranted() {
        onPickPhotoWithCamera();
    }

    @Override
    public void onRequestPermissionDenied() {
        view.showToastMessagePermissionDenied(eZPhotoPickConfig.permisionDeniedErrorStringResource);
        view.finishWithNoResult();
    }

    @Override
    public void onDestroy() {
        isStoringPhoto = false;
        isOpenedPhotoPick = false;
        if (photoPickHandler != null) {
            photoPickHandler = null;
        }
    }

    private void finishPickPhotoWithSuccessResult() {
        if (storedPhotoNames.size() > 0) {
            String pickedPhotoName = storedPhotoNames.get(storedPhotoNames.size() - 1);
            ArrayList<String> pickedPhotoNames = new ArrayList<>(storedPhotoNames);
            view.finishPickPhotoWithSuccessResult(pickedPhotoName, pickedPhotoNames);
        }
    }

    private void onPickPhotoWithCamera() {
        isOpenedPhotoPick = true;
        view.openCamera();
    }

    private void onPickPhotoWithGallery() {
        isOpenedPhotoPick = true;
        view.openGallery(isAllowMultipleSelect);
    }
}
