package cn.swust.jiur.myView;

import android.graphics.Paint;
import android.text.style.LineHeightSpan;

public class VerticalLineHeightSpan implements LineHeightSpan {

        private int verticalSpacing;
        private int ascent;
        private int descent;

        public VerticalLineHeightSpan(int verticalSpacing, int ascent, int descent) {
            this.verticalSpacing = verticalSpacing;
            this.ascent = ascent;
            this.descent = descent;
        }


    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm) {
        if (fm.bottom - fm.top < ascent + descent) {
            fm.top = fm.bottom - descent - ascent + ascent - verticalSpacing;
        } else {
            fm.top -= verticalSpacing;
        }
    }
}