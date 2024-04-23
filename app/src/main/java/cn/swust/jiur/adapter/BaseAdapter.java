package cn.swust.jiur.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.List;

import cn.swust.jiur.impl.AdapterClickListener;

abstract class BaseAdapter<T,VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
     Context context;
     List<T> list;
     AdapterClickListener clickListener;
    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public void setList(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setClickListener(AdapterClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public BaseAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
    }
}
