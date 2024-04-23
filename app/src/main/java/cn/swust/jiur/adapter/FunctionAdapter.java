package cn.swust.jiur.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter4.BaseMultiItemAdapter;
import com.chad.library.adapter4.BaseQuickAdapter;

import java.util.Arrays;
import java.util.List;

import cn.swust.jiur.R;
import cn.swust.jiur.databinding.RecycleviewHomeItemBinding;
import cn.swust.jiur.entity.FunctionGroup;
import cn.swust.jiur.impl.FunctionItemClickListener;

public class FunctionAdapter extends BaseQuickAdapter<FunctionGroup,FunctionAdapter.ViewHolder> {
    public static final int FUNCTION_CATEGORY = 0;
    public static final int FUNCTION_ITEM = 1;
    public static final int SPAN_COUNT = 4;
    private final String TAG = "FunctionAdapter";

    private FunctionItemClickListener itemClickListener;
    @Override
    protected void onBindViewHolder(@NonNull FunctionAdapter.ViewHolder viewHolder, int i, @Nullable FunctionGroup functionGroup) {

        //设置分类名称
        viewHolder.binding.tvHomeItemTitle.setText(functionGroup.getGroupName());
        //设置item
        RecyclerView recyclerView = viewHolder.binding.recycleHomeFunctions;
        FunctionItemAdapter adapter = new FunctionItemAdapter();
        List<FunctionGroup.FunctionItem> functionItems = functionGroup.getFunctionItems();
        adapter.submitList(functionItems);
        adapter.setOnItemClickListener(new OnItemClickListener<FunctionGroup.FunctionItem>() {
            @Override
            public void onClick(@NonNull BaseQuickAdapter<FunctionGroup.FunctionItem, ?> baseQuickAdapter, @NonNull View view, int i) {
                assert itemClickListener != null;
                FunctionGroup.FunctionItem functionItem = functionItems.get(i);
                itemClickListener.click(functionItem.getFragmentId(), functionItem.getName());
                Log.d(TAG,viewHolder.binding.toString() + "-->" + functionItem.getFragmentId());
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));
        recyclerView.setAdapter(adapter);
    }

    @NonNull
    @Override
    protected FunctionAdapter.ViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycleview_home_item,viewGroup,false));
    }

    public void setItemClickListener(FunctionItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecycleviewHomeItemBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecycleviewHomeItemBinding.bind(itemView);
        }
    }
}
