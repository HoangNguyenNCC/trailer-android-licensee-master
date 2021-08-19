package devx.app.licensee.common.utils;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import devx.app.licensee.common.BaseActivity;
import devx.app.licensee.modules.editTrailer.TrailerDetailsActivity;
import devx.app.licensee.modules.home.profiletab.ProfileEditActivity;
import devx.app.seller.modules.home.hometab.HomeActivity;

public class DownloadFile extends AsyncTask<String, Void, Boolean> {

    private BaseActivity baseActivity;
    private String fileName = "";
    private String filePath = "";

    public DownloadFile(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        baseActivity.showProgressDialog();
        baseActivity.showToast("Downloading File ...");
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String fileUrl = strings[0];
        fileName = "trailer" + new Random().nextInt(1000) + ".pdf";
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "Trailer/Documents");
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File pdfFile = new File(path, "Doc.pdf");
        //filePath = pdfFile.getPath();
        try {
            pdfFile.mkdir();
           /// pdfFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return FileDownloader.downloadFile(fileUrl, pdfFile);
    }

    @Override
    protected void onPostExecute(Boolean isDone) {
        baseActivity.dismissProgressDialog();
        if (isDone) {
            if (baseActivity instanceof HomeActivity)
                ((HomeActivity) baseActivity).getProfileFragment().onFileDownloaded(fileName, filePath);
           /* else if (baseActivity instanceof TrailerDetailsActivity)
                ((TrailerDetailsActivity) baseActivity).onFileDownloaded(fileName, filePath);*/
            else if (baseActivity instanceof ProfileEditActivity)
                ((ProfileEditActivity) baseActivity).onFileDownloaded(fileName, filePath);
        } else
            HelperMethods.showToastbar(baseActivity, "Download Failed!!");
    }
}
