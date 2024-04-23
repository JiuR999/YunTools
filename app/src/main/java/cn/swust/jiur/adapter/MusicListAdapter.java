package cn.swust.jiur.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.swust.jiur.R;
import cn.swust.jiur.entity.Music;
import cn.swust.jiur.impl.AdapterClickListener;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {
    private Context context;
    private List<Music> musicList;
    private AdapterClickListener adapterClickListener;
    public MusicListAdapter(Context context, List<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
        notifyDataSetChanged();
    }

    public void setMusicClickListener(AdapterClickListener adapterClickListener) {
        this.adapterClickListener = adapterClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(context, R.layout.recycleview_music_item,null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music music = musicList.get(position);

        holder.textViewSongName.setText(music.getName());
            Glide.with(context)
                    .load(music.getCover())
                    .placeholder(R.drawable.twotone_music_note_24)
                    .into(holder.cover);
        holder.play.setOnClickListener(v->{
            adapterClickListener.perform(position);
        });
        holder.save.setOnClickListener(v->{
            adapterClickListener.downMusic(position);
        });
    }
    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewSongName;
        ImageView cover,play,save;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.img_item_cover);
            textViewSongName = itemView.findViewById(R.id.tv_item_song_name);
            play = itemView.findViewById(R.id.img_item_play);
            save = itemView.findViewById(R.id.img_item_save);
        }
    }
}
