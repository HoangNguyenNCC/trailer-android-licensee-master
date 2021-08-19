package devx.app.licensee.common

import androidx.appcompat.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object RxSearch {
    fun fromView(searchET: AppCompatEditText): Observable<String> {

        val subject = PublishSubject.create<String>()
        searchET.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                subject.onNext(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        return subject
    }
}
