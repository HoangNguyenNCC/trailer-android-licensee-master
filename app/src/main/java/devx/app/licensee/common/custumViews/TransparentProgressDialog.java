package devx.app.licensee.common.custumViews;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import devx.app.licensee.R;


/**
 * Created by WinDOWS on 22-12-2015.
 */
public class TransparentProgressDialog extends Dialog{

    private ImageView movingImage;


    public TransparentProgressDialog(Context context, int resourceIdOfImage)
    {
        super(context, R.style.TransparentProgressDialog);

        setingWindow(context,resourceIdOfImage);

    }

    private void setingWindow(Context context, int resourceIdOfImage) {
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        movingImage = new ImageView(context);
        movingImage.setImageResource(resourceIdOfImage);
        layout.addView(movingImage, params);
        addContentView(layout, params);

    }

    @Override
    public void show()
    {
        super.show();
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    RotateAnimation anim = new RotateAnimation(0.0f, 360.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
                    anim.setInterpolator(new LinearInterpolator());
                    anim.setRepeatCount(Animation.INFINITE);
                    anim.setDuration(2000);
                    movingImage.setAnimation(anim);
                    movingImage.startAnimation(anim);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
