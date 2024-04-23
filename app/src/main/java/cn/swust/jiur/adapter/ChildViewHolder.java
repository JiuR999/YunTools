package cn.swust.jiur.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.swust.jiur.R;
import cn.swust.jiur.entity.DataBean;
import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;

public class ChildViewHolder extends RecyclerView.ViewHolder {
    private TextView tv;
    private View view;
    public ChildViewHolder(@NonNull View itemView) {
        super(itemView);
        tv = itemView.findViewById(R.id.tv_child);
    }
    public void bindView(final DataBean dataBean, final Context context){
        Markwon build = Markwon.builder(context)
                .usePlugin(HtmlPlugin.create())
                .build();
        //CharSequence spanned = Html.fromHtml(dataBean.getChildTxt(), Html.FROM_HTML_MODE_LEGACY);
        build.setMarkdown(tv,dataBean.getChildTxt());
        tv.setClickable(true);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
