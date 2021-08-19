package devx.app.licensee.modules

import android.os.Bundle
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.utils.IntentParams
import devx.app.seller.common.CommonTopbarView
import kotlinx.android.synthetic.main.activity_pdf_viewer.*
import java.io.File

class PdfViewerActivity : BaseActivity(), OnErrorListener, OnPageErrorListener {

    private var filePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)
        CommonTopbarView().setup(this, safeText(intent.getStringExtra(IntentParams.FILE_NAME), ""))

        if (intent.hasExtra(IntentParams.FILE_PATH))
            filePath = intent.getStringExtra(IntentParams.FILE_PATH)!!

        pdfView.fitToWidth()
        loadPDFFile()
    }

    private fun loadPDFFile() {
        pdfView.fromFile(File(filePath))
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .defaultPage(0)
            .swipeHorizontal(false)
            .onError(this)
            .onPageError(this)
            .enableAnnotationRendering(false)
            .scrollHandle(null)
            .enableAntialiasing(true)
            .spacing(10)
            .load()
    }

    override fun onError(t: Throwable?) {
        if (t != null)
            showToast(t.message)
    }

    override fun onPageError(page: Int, t: Throwable?) {
        if (t != null)
            showToast(t.message)
    }
}
