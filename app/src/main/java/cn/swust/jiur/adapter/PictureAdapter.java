package cn.swust.jiur.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter4.BaseQuickAdapter;

import java.util.List;

import cn.swust.jiur.R;
import cn.swust.jiur.databinding.ItemProfileBinding;
import cn.swust.jiur.entity.Profile;
import cn.swust.jiur.utils.WindowUtils;

public class PictureAdapter extends BaseQuickAdapter<Profile, PictureAdapter.ViewHolder>{
    public final static String PICASSO = "Picasso";
    public static final int WALL_PAPER = 2;
    public static final int AVATAR = 1;

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
            // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
            viewHolder.binding.tvProfileCatagoryName.setVisibility(View.VISIBLE);
            viewHolder.binding.tvProfileCatagoryName.setText(profile.getCategoryName());
        }
    }

    @NonNull
    @Override
    protected PictureAdapter.ViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile,
                viewGroup,false);
        int itemViewType = getItemViewType(i);

        if(itemViewType == WALL_PAPER){
            view.getLayoutParams().width = (int) WindowUtils.dpToPx(context,180);
            view.getLayoutParams().height = (int) WindowUtils.dpToPx(context,300);
        }
        return new ViewHolder(view);
    }

    @Override
    protected int getItemViewType(int position, @NonNull List<? extends Profile> list) {
        return list.get(position).getType() == null ? AVATAR : WALL_PAPER;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemProfileBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemProfileBinding.bind(itemView);

        }
    }
}
