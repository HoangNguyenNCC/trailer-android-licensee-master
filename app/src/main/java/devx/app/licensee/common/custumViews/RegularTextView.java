package devx.app.licensee.common.custumViews;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class RegularTextView extends AppCompatTextView {

    public RegularTextView(Context context) {
        super(context);
        this.setTypeface(ProFont.getInstance().getMontserrat_regular());
    }

    public RegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(ProFont.getInstance().getMontserrat_regular());
    }

    public RegularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(ProFont.getInstance().getMontserrat_regular());
    }
}
