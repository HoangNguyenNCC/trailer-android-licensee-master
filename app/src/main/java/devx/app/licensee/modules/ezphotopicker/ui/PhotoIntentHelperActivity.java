package devx.app.licensee.modules.ezphotopicker.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.ArrayList;

import devx.app.licensee.R;
import devx.app.licensee.modules.ezphotopicker.api.models.EZPhotoPickConfig;
import devx.app.licensee.modules.ezphotopicker.models.PhotoIntentConstants;
import devx.app.licensee.modules.ezphotopicker.models.PhotoIntentException;
import devx.app.licensee.modules.ezphotopicker.storage.PhotoGenerator;
import devx.app.licensee.modules.ezphotopicker.storage.PhotoIntentContentProvider;
import devx.app.licensee.modules.ezphotopicker.storage.PhotoIntentHelperStorage;
import devx.app.licensee.modules.ezphotopicker.storage.PhotoUriHelper;

import static devx.app.licensee.modules.ezphotopicker.api.EZPhotoPick.PICKED_PHOTO_NAMES_KEY;
import static devx.app.licensee.modules.ezphotopicker.api.EZPhotoPick.PICKED_PHOTO_NAME_KEY;

public class PhotoIntentHelperActivity extends AppCompatActivity implements PhotoIntentHelperContract.View {

    private static final int REQUEST_CAMERA_PERMISSION = 2001;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 2002;
    private static final int PICK_PHOTO_FROM_GALLERY = 1001;
    private static final int PICK_PHOTO_FROM_CAMERA = 1002;

    private PhotoIntentHelperContract.Presenter presenter;
    private PhotoIntentHelperStorage helperStorage;
    private View loadingView;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int screenOrientation = getIntent().getIntExtra(PhotoIntentConstants.SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        setRequestedOrientation(screenOrientation);

        setContentView(R.layout.photo_pick_activity);
        loadingView = findViewById(R.id.loading_view);
        EZPhotoPickConfig EZPhotoPickConfig = (EZPhotoPickConfig) getIntent().getSerializableExtra(PhotoIntentConstants.PHOTO_PICK_CONFIG_KEY);
        helperStorage = PhotoIntentHelperStorage.getInstance(this);
        presenter = new PhotoIntentHelperPresenter(this,
                new PhotoUriHelper(this),
                new PhotoGenerator(this),
                helperStorage, EZPhotoPickConfig);
        try {
            presenter.onCreate(savedInstanceState);
        } catch (PhotoIntentException e) {
            e.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void openGallery(boolean isAllowMultipleSelect) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (isAllowMultipleSelect) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FROM_GALLERY);
    }

    @Override
    public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishPickPhotoWithSuccessResult(String pickedPhotoName, ArrayList<String> pickedPhotoNames) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(PICKED_PHOTO_NAME_KEY, pickedPhotoName);
        resultIntent.putStringArrayListExtra(PICKED_PHOTO_NAMES_KEY, pickedPhotoNames);
        setResult(RESULT_OK, resultIntent);
        finishedWithoutAnimation();
    }

    private void finishedWithoutAnimation() {
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void showPickPhotoFromGalleryError(int unexpectedErrorStringResource) {
        String message = "Unexpected error, please try again";
        if (unexpectedErrorStringResource != 0) {
            message = getString(unexpectedErrorStringResource);
        }
        Toast.makeText(PhotoIntentHelperActivity.this, message
                , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishWithNoResult() {
        finishedWithoutAnimation();
    }

    /**
     * checkPermissions: Used as a wrapper over ActivityCompat.checkSelfPermission to validate if a
     * number of Permissions have been granted
     *
     * @param permissions: The requested Permissions.
     * @return True is all permissions have been graned, otherwise false.
     */
    private boolean checkPermissions(String[] permissions) {

        for (String permission : permissions) {

            if (ActivityCompat.checkSelfPermission(PhotoIntentHelperActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

                return false;

            }

        }

        return true;

    }

    @Override
    public void requestCameraAndExternalStoragePermission(boolean needToAddToGallery) {
        String[] permissions;
        if (needToAddToGallery) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        } else {
            permissions = new String[]{Manifest.permission.CAMERA};
        }

        if (checkPermissions(permissions)) {
            presenter.onRequestCameraPermissionGranted();
        } else {
            ActivityCompat.requestPermissions(PhotoIntentHelperActivity.this,
                    permissions,
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, PhotoIntentContentProvider.getContentUri(this));
        startActivityForResult(takePictureIntent, PICK_PHOTO_FROM_CAMERA);
    }

    @Override
    public void requestReadExternalStoragePermission() {
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        if (checkPermissions(permissions)) {
            presenter.onRequestReadExternalPermissionGranted();
        } else {
            ActivityCompat.requestPermissions(PhotoIntentHelperActivity.this,
                    permissions,
                    REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    @Override
    public void showToastMessagePermissionDenied(int permisionDeniedErrorStringResource) {
        String message = "Permission denied, cannot complete the action";
        if (permisionDeniedErrorStringResource != 0) {
            message = getString(permisionDeniedErrorStringResource);
        }
        Toast.makeText(PhotoIntentHelperActivity.this, message
                , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void sendBroadcastToScanFileInGallery(File file) {
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (grantResults.length == 0) {
            presenter.onRequestPermissionDenied();
            return;
        }

        boolean isGrantedPermission = true;
        for (Integer grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                isGrantedPermission = false;
                break;
            }
        }

        if (!isGrantedPermission) {
            presenter.onRequestPermissionDenied();
            return;
        }

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                presenter.onRequestCameraPermissionGranted();
                break;
            }
            case REQUEST_READ_EXTERNAL_STORAGE_PERMISSION: {
                presenter.onRequestReadExternalPermissionGranted();
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            finishedWithoutAnimation();
            return;
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_PHOTO_FROM_GALLERY) {
                presenter.onPhotoPickedFromGallery(data);
            } else if (requestCode == PICK_PHOTO_FROM_CAMERA) {
                presenter.onPhotoPickedFromCamera(getFilesDir());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        if (helperStorage != null)
            helperStorage = null;
    }
}
