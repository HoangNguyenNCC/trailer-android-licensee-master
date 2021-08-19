package devx.app.licensee.common.custumViews;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import devx.app.licensee.R;

import static androidx.core.content.ContextCompat.getColor;
import static androidx.core.content.ContextCompat.getDrawable;

public class YellowBoldBtn extends BoldTextView {
    public YellowBoldBtn(Context context) {
        super(context);
        setup();
    }

    public YellowBoldBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public YellowBoldBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        this.setBackgroundResource(R.drawable.complete_yellow_round_background);
        this.setGravity(Gravity.CENTER);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TypedValue outValue = new TypedValue();
                this.getContext().getTheme().resolveAttribute(
                        android.R.attr.selectableItemBackgroundBorderless, outValue, true);
                this.setForeground(getDrawable(this.getContext(), outValue.resourceId));
            }
        } catch (Exception e) {
        }
        this.setTextColor(getColor(this.getContext(), R.color.light_black));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            setup();
        }
    }
}
