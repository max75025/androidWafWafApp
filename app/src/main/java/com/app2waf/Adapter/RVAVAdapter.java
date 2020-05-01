package com.app2waf.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app2waf.Model.AV;
import com.app2waf.R;

import java.util.List;

public class RVAVAdapter extends RecyclerView.Adapter<RVAVAdapter.CardViewHolder> {

    public static class CardViewHolder extends RecyclerView.ViewHolder{
        TextView file;
        TextView timeAndType;
        TextView description;
        ImageView imgId;
        TextView account;


        CardViewHolder(View itemView){
            super(itemView);

            file = (TextView)itemView.findViewById(R.id.av_file);
            timeAndType = (TextView)itemView.findViewById(R.id.av_time);
            description = (TextView)itemView.findViewById(R.id.av_description);
            imgId = (ImageView)itemView.findViewById(R.id.av_img);
            account = (TextView)itemView.findViewById(R.id.av_account);
        }
    }

    private List<AV> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RVAVAdapter(Context context, List<AV> data) {

        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.cardviewav, parent, false);
        CardViewHolder cvh = new CardViewHolder(view);
        return cvh;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.file.setText(mData.get(position).getFileName());
        holder.timeAndType.setText(mData.get(position).getEventType() +" ("+mData.get(position).getEventTime()+")");
        holder.description.setText(mData.get(position).getDescription());
        holder.imgId.setImageResource(mData.get(position).getImgId());
        holder.account.setText(mData.get(position).getAccount());

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    AV getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setNewData(List<AV> data){
        //System.out.println(data);
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();

    }

    public void addAV(List<AV> data){
        List<AV> temp = data;
        temp.addAll(mData);
        mData.clear();
        mData.addAll(temp);
        notifyDataSetChanged();
    }
}
