package devx.app.licensee.common.custumViews;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import devx.app.licensee.R;

public class BoldBlackTextView extends AppCompatTextView {

    public BoldBlackTextView(Context context) {
        super(context);
        this.setTypeface(ProFont.getInstance().getMontserrat_bold());
        this.setTextColor(getResources().getColor(R.color.black));
    }

    public BoldBlackTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            this.setTypeface(ResourcesCompat.getFont(context, R.font.montserrat_semi_bold));
        } else {
            this.setTypeface(ProFont.getInstance().getMontserrat_bold());
        }
        this.setTextColor(getResources().getColor(R.color.black));

    }

    public BoldBlackTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(ProFont.getInstance().getMontserrat_bold());
        this.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    public boolean isInEditMode() {
        return super.isInEditMode();
    }
}
