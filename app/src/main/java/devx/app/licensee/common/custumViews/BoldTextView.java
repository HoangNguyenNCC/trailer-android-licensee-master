package devx.app.licensee.common.custumViews;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import devx.app.licensee.R;

public class BoldTextView extends AppCompatTextView {
    public BoldTextView(Context context) {
        super(context);
        this.setTypeface(ProFont.getInstance().getMontserrat_bold());
    }

    public BoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            this.setTypeface(ResourcesCompat.getFont(context, R.font.montserrat_semi_bold));
        } else {
            this.setTypeface(ProFont.getInstance().getMontserrat_bold());
        }
    }

    public BoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(ProFont.getInstance().getMontserrat_bold());
    }

    @Override
    public boolean isInEditMode() {
        return super.isInEditMode();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.setTypeface(ProFont.getInstance().getMontserrat_bold());
        super.onLayout(changed, left, top, right, bottom);
    }
}
