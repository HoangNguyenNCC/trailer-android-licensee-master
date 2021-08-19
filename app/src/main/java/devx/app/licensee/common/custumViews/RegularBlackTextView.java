package devx.app.licensee.common.custumViews;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import devx.app.licensee.R;
import devx.app.licensee.common.ResourcesUtil;

public class RegularBlackTextView extends AppCompatTextView {

    public RegularBlackTextView(Context context) {
        super(context);
        this.setTypeface(ProFont.getInstance().getMontserrat_regular());
        this.setTextColor(ResourcesUtil.getColor(R.color.black));
    }

    public RegularBlackTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(ProFont.getInstance().getMontserrat_regular());
        this.setTextColor(ResourcesUtil.getColor(R.color.black));
    }

    public RegularBlackTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(ProFont.getInstance().getMontserrat_regular());
        this.setTextColor(ResourcesUtil.getColor(R.color.black));
    }

//
}
