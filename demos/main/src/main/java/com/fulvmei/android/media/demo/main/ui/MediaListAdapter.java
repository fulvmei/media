package com.fulvmei.android.media.demo.main.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fulvmei.android.media.demo.main.PlayerActivity;
import com.fulvmei.android.media.demo.main.R;
import com.fulvmei.android.media.demo.main.bean.Media;

import java.util.ArrayList;
import java.util.List;

public class MediaListAdapter extends RecyclerView.Adapter<MediaListAdapter.ViewHolder> {

    List<Media> dataList;

    public void submitList(List<Media> list) {
        dataList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Media media = dataList.get(position);
        holder.title.setText(media.getName());
        holder.subTitle.setText(media.getPath());
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

     class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView subTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            subTitle = itemView.findViewById(R.id.subTitle);

            itemView.setOnClickListener(view -> {
                Intent intent=new Intent(view.getContext(), PlayerActivity.class);
                ArrayList<Media> mediaArrayList=new ArrayList<>();
                mediaArrayList.add(dataList.get(getAdapterPosition()));
                intent.putParcelableArrayListExtra("list",mediaArrayList);
                view.getContext().startActivity(intent);
            });
        }
    }
}
