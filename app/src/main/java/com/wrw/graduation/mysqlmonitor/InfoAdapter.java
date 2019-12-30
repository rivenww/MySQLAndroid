package com.wrw.graduation.mysqlmonitor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder>{

    private static final String TAG = "InfoAdapter";

    private Context mContext;

    private List<Dbinfo> mInfoList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView infoIP;
        TextView infoStaus;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            infoIP = (TextView) view.findViewById(R.id.info_IP);
            infoStaus = (TextView) view.findViewById(R.id.info_status);
        }
    }

    public InfoAdapter(List<Dbinfo> infoList) {
        mInfoList = infoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.info_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Dbinfo dbinfo = mInfoList.get(position);
                Intent intent = new Intent(mContext,ConsoleActivity.class);
                intent.putExtra(ConsoleActivity.INFO_IP,dbinfo.getIP());
                mContext.startActivity(intent);

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dbinfo dbinfo = mInfoList.get(position);
        holder.infoIP.setText(dbinfo.getIP());
        holder.infoStaus.setText(dbinfo.getStatus());
    }

    @Override
    public int getItemCount() {
        return mInfoList.size();
    }

}
