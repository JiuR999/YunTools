package cn.swust.jiur.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter4.BaseQuickAdapter;

import java.util.List;

import cn.swust.jiur.R;
import cn.swust.jiur.databinding.RecycleviewProfileItemBinding;
import cn.swust.jiur.entity.Profile;
import cn.swust.jiur.utils.WindowUtils;

public class PictureAdapter extends BaseQuickAdapter<Profile, PictureAdapter.ViewHolder>{
    public final static String PICASSO = "Picasso";
    @Override
    protected void onBindViewHolder(@NonNull PictureAdapter.ViewHolder viewHolder, int i, @Nullable Profile profile) {
        //不要硬盘缓存
        RequestOptions options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE);
        Log.d("加载","第"+i);
        Glide.with(getContext())
                .load(profile.getUrl())
                .placeholder(R.drawable.on_loading
                )
                .apply(options)
                .into(viewHolder.binding.imgProfileCover);
        if(profile.getCategoryName() != null){
            viewHolder.binding.tvProfileCatagoryName.setVisibility(View.VISIBLE);
            viewHolder.binding.tvProfileCatagoryName.setText(profile.getCategoryName());
        }
    }

    @NonNull
    @Override
    protected PictureAdapter.ViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycleview_profile_item,
                viewGroup,false);
        int itemViewType = getItemViewType(i);
        if(itemViewType == 2){
            view.getLayoutParams().width = (int) WindowUtils.dpToPx(context,180);
            view.getLayoutParams().height = (int) WindowUtils.dpToPx(context,300);
        }
        return new ViewHolder(view);
    }

    @Override
    protected int getItemViewType(int position, @NonNull List<? extends Profile> list) {
        return list.get(position).getType() == null ? 1 : 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RecycleviewProfileItemBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecycleviewProfileItemBinding.bind(itemView);
        }
    }
}
