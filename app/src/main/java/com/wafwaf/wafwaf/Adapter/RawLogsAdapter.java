package com.wafwaf.wafwaf.Adapter;

import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.wafwaf.wafwaf.Model.AttackRawLogs;
import com.wafwaf.wafwaf.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RawLogsAdapter extends RecyclerView.Adapter<RawLogsAdapter.RawLogsViewHolder> {
    public class RawLogsViewHolder extends RecyclerView.ViewHolder {

        private TextView time;
        private TextView attackDirection;
        private TextView attackPackage;



        public RawLogsViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.attack_time);
            attackDirection = itemView.findViewById(R.id.attack_direction);
            attackPackage = itemView.findViewById(R.id.attack_package);


            attackPackage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //holder.title.setMaxLines(100);
                    cycleTextViewExpansion(attackPackage);
                }
            });
        }

        private void cycleTextViewExpansion(TextView tv){
            int collapsedMaxLines = 4;
            ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines",
                    tv.getMaxLines() == collapsedMaxLines? tv.getLineCount() : collapsedMaxLines);
            animation.setDuration(200).start();
        }



        public void bind(AttackRawLogs rawLogs) {
            String DateFormatted = rawLogs.getAttackTime();
            time.setText(DateFormatted);
            attackDirection.setText(rawLogs.getAttackDirection());
           attackPackage.setText(rawLogs.getAttackPackage());


        }


    }


    private List<AttackRawLogs> rawLogsList = new ArrayList<>();
   // private static final String TWITTER_RESPONSE_FORMAT="EEE MMM dd HH:mm:ss ZZZZZ yyyy"; // Thu Oct 26 07:31:08 +0000 2017
    //private static final String MONTH_DAY_FORMAT = "MMM d"; // Oct 26
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
    public RawLogsAdapter.RawLogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_raw_logs, parent, false);
        return new RawLogsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RawLogsAdapter.RawLogsViewHolder holder, int position) {
        holder.bind(rawLogsList.get(position));
    }

    @Override
    public int getItemCount() {
        return rawLogsList.size();
    }



}
