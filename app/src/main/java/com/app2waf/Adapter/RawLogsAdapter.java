package com.app2waf.Adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.app2waf.Model.AttackRawLogs;
import com.app2waf.R;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RawLogsAdapter extends RecyclerView.Adapter<RawLogsAdapter.RawLogsViewHolder> {
    public class RawLogsViewHolder extends RecyclerView.ViewHolder {

        private TextView time;
        private TextView attackDirection;
        private TextView attackPackage;
        private ImageButton shareButton;


        public RawLogsViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.attack_time);
            attackDirection = itemView.findViewById(R.id.attack_direction);
            attackPackage = itemView.findViewById(R.id.attack_package);
            shareButton = itemView.findViewById(R.id.button_share_log);

            /*zip/unzip logs on tap*/
            /*attackPackage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //holder.title.setMaxLines(100);
                    cycleTextViewExpansion(attackPackage);
                }
            });*/
        }

        private void cycleTextViewExpansion(TextView tv) {
            int collapsedMaxLines = 4;
            ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines",
                    tv.getMaxLines() == collapsedMaxLines ? tv.getLineCount() : collapsedMaxLines);
            animation.setDuration(200).start();
        }


        public void bind(AttackRawLogs rawLogs) {
            String DateFormatted = rawLogs.getAttackTime();
            time.setText(DateFormatted);
            attackDirection.setText(rawLogs.getAttackDirection());
            attackPackage.setText(rawLogs.getAttackPackage());


        }


    }

    private Context mContext;
    private List<AttackRawLogs> rawLogsList = new ArrayList<>();

    public void setItems(Collection<AttackRawLogs> logs) {
        rawLogsList.addAll(logs);
        notifyDataSetChanged();
    }

    public void clearItems() {
        rawLogsList.clear();
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RawLogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_raw_logs, parent, false);

        mContext = parent.getContext();

        return new RawLogsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RawLogsViewHolder holder, int position) {
        holder.bind(rawLogsList.get(position));
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String sendData = rawLogsList.get(holder.getAdapterPosition()).getAttackTime() + "\n" +
                                  rawLogsList.get(holder.getAdapterPosition()).getAttackDirection() + "\n" +
                                  rawLogsList.get(holder.getAdapterPosition()).getAttackPackage();

                Log.d("SEND_INTENT_IN_RAW_LOG:", "size:" + sendData.getBytes(Charset.forName("UTF-8")).length);
                //sendIntent.putExtra("extra", new byte[1024 *256]);

                if (sendData.getBytes(Charset.forName("UTF-8")).length > 200000){
                    Toast ToastCourt = Toast.makeText(mContext, R.string.toast_raw_logs_share_limit, Toast.LENGTH_LONG);
                    ToastCourt.show();
                    return;
                }
               sendIntent.putExtra(Intent.EXTRA_TEXT, sendData);
                sendIntent.setType("text/plain");
                mContext.startActivity(sendIntent);
                /*Toast ToastCourt = Toast.makeText(getContext(), DataOfListView.get(classPosition).getPseudoUser(), Toast.LENGTH_SHORT);
                ToastCourt.show();
                Log.i("position", ""+positionForListener);*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return rawLogsList.size();
    }


}
