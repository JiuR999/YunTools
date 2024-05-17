package cn.swust.jiur.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.swust.jiur.R;

import cn.swust.jiur.databinding.ItemHotpointBinding;
import cn.swust.jiur.entity.HotPoint;

public class HotPointAdapter extends RecyclerView.Adapter<HotPointAdapter.ViewHolder> {
    private List<HotPoint.DataBean> hotPoints;
    private Context context;

    public HotPointAdapter(List<HotPoint.DataBean> hotPoints, Context context) {
        this.hotPoints = hotPoints;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hotpoint,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.tvHotpointTitle.setText(hotPoints.get(position).getTitle());
        holder.binding.tvIndex.setText("" + (position+1));
        holder.binding.tvHotpointHot.setText(hotPoints.get(position).getHot());
    }

    @Override
    public int getItemCount() {
        return hotPoints.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemHotpointBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemHotpointBinding.bind(itemView);
        }
    }
}
