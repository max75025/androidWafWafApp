package com.wafwaf.wafwaf;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;


public class RVAttackAdapter extends RecyclerView.Adapter<RVAttackAdapter.CardViewHolder> {

    public static class CardViewHolder extends RecyclerView.ViewHolder{

        TextView country;
        TextView startAttackTime;
        TextView endAttackTime;
        TextView types;
        LinearLayout typesLinearLayout;
        ImageView imgId;
        TextView account;


        CardViewHolder(View itemView){
            super(itemView);

            country = (TextView)itemView.findViewById(R.id.attack_county);
            startAttackTime = (TextView)itemView.findViewById(R.id.attack_start);
            endAttackTime = (TextView)itemView.findViewById(R.id.attack_end);
            types = (TextView)itemView.findViewById(R.id.attack_type);
            typesLinearLayout = (LinearLayout)itemView.findViewById(R.id.types);
            imgId = (ImageView)itemView.findViewById(R.id.attack_img);
            account = (TextView)itemView.findViewById(R.id.attack_account);
        }
    }

    private List<Attack> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    RVAttackAdapter(Context context, List<Attack> data) {

        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.cardviewevent, parent, false);
        CardViewHolder cvh = new CardViewHolder(view);
        return cvh;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.country.setText(mData.get(position).country +" (" +mData.get(position).ip +")");
        /*holder.ip.setText(mData.get(position).ip);*/
        holder.startAttackTime.setText(mData.get(position).startAttackTime);
        holder.endAttackTime.setText(mData.get(position).endAttackTime);
        holder.types.setText(mData.get(position).types);
        /*holder.type2.setText(mData.get(position).type2);*/
        holder.imgId.setImageResource(mData.get(position).imgId);
        holder.account.setText("атакован: " +mData.get(position).account);




        /*Card card = mData.get(position);
        holder.myTextView.setText(card);*/
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
    Attack getItem(int id) {
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

    public void setNewData(List<Attack> data){
        //System.out.println(data);
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addAttack(List<Attack> data){
        List<Attack> tempListAttack = data;
        tempListAttack.addAll(mData);
        mData.clear();
        mData.addAll(tempListAttack);
        notifyDataSetChanged();
    }
}