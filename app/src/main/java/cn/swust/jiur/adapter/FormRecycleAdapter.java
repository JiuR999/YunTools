package cn.swust.jiur.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.chip.Chip;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.swust.jiur.R;
import cn.swust.jiur.entity.Formation;
import cn.swust.jiur.fragment.HomeFragment;
import cn.swust.jiur.utils.ImageUtils;

public class FormRecycleAdapter extends RecyclerView.Adapter<FormRecycleAdapter.ViewHolder> {
    private List<Formation> formationList;
    private Context context;

    private ImageUtils imageUtils;

    public FormRecycleAdapter(List<Formation> formationList, Context context) {
        this.formationList = formationList;
        this.context = context;
        imageUtils = new ImageUtils(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.coc_formation_item, null);
        return new ViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestOptions options = RequestOptions
                .bitmapTransform(new RoundedCorners(16))
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        //清理缓存
//        .skipMemoryCache(true)

        Glide.with(context)
                .load(formationList.get(position).getImg())
                .apply(options)
                .placeholder(R.drawable.load_32)
                .into(holder.img);
        holder.img.setOnClickListener(view -> {
            Dialog dialog = new Dialog(context);
            View view1 = LayoutInflater.from(context).inflate(R.layout.pic_dialog,
                    null, false);
            ImageView img1 = view1.findViewById(R.id.img_formation_dialog);
            img1.setImageDrawable(holder.img.getDrawable());
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(view1);
            dialog.show();
        });
        holder.copy.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // 设置ComponentName参数1:packagename 参数2:MainActivity路径
            ComponentName cn = new ComponentName(HomeFragment.PACGAGENAME_COC, HomeFragment.PACGAGE_CLASS);
            intent.setComponent(cn);
            Pattern pattern = Pattern.compile("action.*tencent");
            Matcher matcher = pattern.matcher(formationList.get(position)
                    .getLink());
            while (matcher.find()) {
                Log.d("匹配", matcher.group(0));
                intent.setData(Uri.parse("clashofclans://" + matcher.group(0)));
            }
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return formationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        Chip chip;
        Button copy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_formation);
            copy = itemView.findViewById(R.id.btn_form_copy);
        }
    }

    public void setFormationList(List<Formation> formationList) {
        this.formationList = formationList;
        notifyDataSetChanged();
    }

}
