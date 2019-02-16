package com.wafwaf.wafwaf.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.wafwaf.wafwaf.Attack;
import com.wafwaf.wafwaf.DatabaseHandler;
import com.wafwaf.wafwaf.MainActivity;
import com.wafwaf.wafwaf.Manager.IpInfoManager;
import com.wafwaf.wafwaf.R;
import com.wafwaf.wafwaf.RawLogsActivity;

import java.util.List;


public class RVAttackAdapter extends RecyclerView.Adapter<RVAttackAdapter.CardViewHolder> {


    DatabaseHandler db;







    public static class CardViewHolder extends RecyclerView.ViewHolder{

        TextView country;
        TextView startAttackTime;
        TextView endAttackTime;
        TextView types;
        LinearLayout typesLinearLayout;
        ImageView imgId;
        TextView account;
        TextView buttonViewOption;


        CardViewHolder(View itemView){
            super(itemView);

            country = (TextView)itemView.findViewById(R.id.attack_county);
            startAttackTime = (TextView)itemView.findViewById(R.id.attack_start);
            endAttackTime = (TextView)itemView.findViewById(R.id.attack_end);
            types = (TextView)itemView.findViewById(R.id.attack_type);
            typesLinearLayout = (LinearLayout)itemView.findViewById(R.id.types);
            imgId = (ImageView)itemView.findViewById(R.id.attack_img);
            account = (TextView)itemView.findViewById(R.id.attack_account);
            buttonViewOption = (TextView)itemView.findViewById(R.id.attack_option);
        }
    }

    private List<Attack> mData;
    private LayoutInflater mInflater;
    private Context mContext;
   // private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RVAttackAdapter(Context context, List<Attack> data) {
        this.mContext = context;
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
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        holder.country.setText(mData.get(position).getCountry() +" (" +mData.get(position).getIp() +")");
        /*holder.ip.setText(mData.get(position).ip);*/
        holder.startAttackTime.setText(mData.get(position).getStartAttackTime());
        holder.endAttackTime.setText(mData.get(position).getEndAttackTime());
        holder.types.setText(mData.get(position).getTypes());
        /*holder.type2.setText(mData.get(position).type2);*/
        holder.imgId.setImageResource(mData.get(position).getImgId());
        holder.account.setText("атакован: " +mData.get(position).getAccount());


        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.attack_card_context_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_raw_logs:
                                Intent intent = new Intent(mContext, RawLogsActivity.class);
                                mContext.startActivity(intent);
                                return true;
                            case R.id.menu_ip_info:
                                new IpInfoManager().showIpInfoAlert(mContext, mData.get(holder.getAdapterPosition()).getApiKey()  , mData.get(holder.getAdapterPosition()).getIp());

                                return true;
                            case R.id.menu_share:
                                Toast.makeText(mContext, "you clicked" + mData.get(holder.getAdapterPosition()).getIp() , Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });


        /*Card card = mData.get(position);
        holder.myTextView.setText(card);*/
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.title);
            //itemView.setOnClickListener(this);
        }

        /*@Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }*/
    }

    // convenience method for getting data at click position
    public Attack getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    /*void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }*/

    // parent activity will implement this method to respond to click events
    /*public interface ItemClickListener {
        void onItemClick(View view, int position);
    }*/

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