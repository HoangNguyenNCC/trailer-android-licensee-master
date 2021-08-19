package devx.app.licensee.common.custumViews;

import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

import devx.app.licensee.R;
import devx.app.licensee.common.TrailerApplication;

public class ProFont {
    private static final ProFont ourInstance = new ProFont();
    private Typeface monBold = null;
    private Typeface monReg = null;

    private ProFont() {
    }

    public static ProFont getInstance() {
        return ourInstance;
    }

    public Typeface getMontserrat_regular() {
        if (monReg == null) {
            monReg = ResourcesCompat.getFont(TrailerApplication.getInstance().getApplicationContext(), R.font.montserrat_regular);
        }
        return monReg;
    }

    public Typeface getMontserrat_bold() {
        if (monBold == null) {
            monBold = ResourcesCompat.getFont(TrailerApplication.getInstance().getApplicationContext(), R.font.montserrat_semi_bold);
        }
        return monBold;
    }
}
