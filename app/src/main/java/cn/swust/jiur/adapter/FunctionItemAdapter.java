package cn.swust.jiur.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter4.BaseQuickAdapter;

import java.util.Arrays;
import java.util.List;

import cn.swust.jiur.R;
import cn.swust.jiur.databinding.RecycleviewHomeFunctionItemBinding;
import cn.swust.jiur.entity.FunctionGroup;

/**
 * 功能布局
 */
public class FunctionItemAdapter extends BaseQuickAdapter<FunctionGroup.FunctionItem,FunctionItemAdapter.ViewHolder> {
    private List<Integer> icons;
    private List<Integer> fragments;
    @NonNull
    @Override
    protected FunctionItemAdapter.ViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.recycleview_home_function_item,viewGroup,false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @Nullable FunctionGroup.FunctionItem functionItem) {
        if(icons == null){
            icons = getIcons();
        }
        if(fragments == null){
            fragments = getFragments();
        }
        functionItem.setIcon(icons.get(functionItem.getIcon()));
        functionItem.setFragmentId(fragments.get(functionItem.getFragmentId()));
        viewHolder.binding.tvFunctionName.setText(functionItem.getName());
        viewHolder.binding.imgFunctionIcon.setImageResource(functionItem.getIcon());
    }
    private List<Integer> getFragments() {
        return Arrays.asList(R.id.navigation_today_of_history, R.id.navigation_picture, R.id.navigation_picture,
                R.id.navigation_article, R.id.navigation_form_list, R.id.navigation_article,
                R.id.navigation_analy_vedio, R.id.navigation_music,R.id.navigation_picture_category
        ,R.id.navigation_wall_paper_category,R.id.navigation_analy_picture,R.id.navigation_hotpoint);
    }

    private List<Integer> getIcons() {
        List<Integer> icons = Arrays.asList(R.drawable.twotone_access_time_24,R.drawable.twotone_newspaper_24,R.drawable.joke,R.drawable.twotone_article_24,
                R.drawable.form,R.drawable.excel,R.drawable.tiktok,R.drawable.twotone_music_note_24,
                R.drawable.twotone_person_pin_24,R.drawable.twotone_wallpaper_24,R.drawable.small_red_book,R.drawable.twotone_newspaper_24);
        return icons;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecycleviewHomeFunctionItemBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecycleviewHomeFunctionItemBinding.bind(itemView);
        }
    }
}
