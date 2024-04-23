package cn.swust.jiur.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.swust.jiur.R;
import cn.swust.jiur.databinding.ThemeSelectItemBinding;
import cn.swust.jiur.entity.Theme;
import cn.swust.jiur.utils.SharedPreferenceUtil;

public class ThemeAdapter extends BaseAdapter<Theme,ThemeAdapter.ViewHolder>{
    public final static String key = "mSelected";
    public ThemeAdapter(Context context, List<Theme> list) {
        super(context, list);
    }

    @NonNull
    @Override
    public ThemeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.theme_select_item,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeAdapter.ViewHolder holder, int position) {
       ThemeSelectItemBinding binding = holder.binding;
       int selected = (int) SharedPreferenceUtil.readData(context, SharedPreferenceUtil.Type.INT
       ,"theme",key);
       if(position == selected){
           binding.imgIsSelect.setVisibility(View.VISIBLE);
       }
       Theme theme = list.get(position);
       binding.tvThemeName.setText(theme.getThemeName());
       binding.imgTheme.setBackgroundResource(theme.getColor());
       binding.getRoot().setOnClickListener(v->{
           clickListener.perform(position);
       });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ThemeSelectItemBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ThemeSelectItemBinding.bind(itemView);
        }
    }
}
