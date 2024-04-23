package cn.swust.jiur.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter4.BaseQuickAdapter;

import java.util.Random;

import cn.swust.jiur.R;
import cn.swust.jiur.databinding.RecycleviewTodayOfHistoryItemBinding;
import cn.swust.jiur.entity.TodayOfHistory;

public class TodayOfHistoryAdapter extends BaseQuickAdapter<TodayOfHistory, TodayOfHistoryAdapter.ViewHolder> {
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @Nullable TodayOfHistory todayOfHistory) {
        viewHolder.bind(todayOfHistory);

        assert todayOfHistory != null;
        Glide.with(getContext())
                .load(todayOfHistory.getCover())
                .into( viewHolder.binding.todayCover);
    }

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .into(imageView);
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {

        View root = DataBindingUtil.bind(LayoutInflater.from(context).inflate(R.layout.recycleview_today_of_history_item, viewGroup, false)).getRoot();
        int height = new Random(2).nextInt()+1;
        root.getLayoutParams().height = height * 160;

        return new ViewHolder(DataBindingUtil.bind(root));
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RecycleviewTodayOfHistoryItemBinding binding;

        public ViewHolder(RecycleviewTodayOfHistoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(TodayOfHistory todayOfHistory) {
            binding.setItem(todayOfHistory);
            binding.executePendingBindings();
        }
    }
}
