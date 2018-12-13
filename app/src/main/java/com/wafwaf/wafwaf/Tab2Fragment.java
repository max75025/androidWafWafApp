package com.wafwaf.wafwaf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class Tab2Fragment extends Fragment implements MainActivity.SentDataToRVAVAdapter {
    RVAVAdapter rvAVAdapter;

    //int endUnixTime = (int) (System.currentTimeMillis() / 1000L);
    //int startUnixTime = endUnixTime - 60 * 60 * 24 * 10;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View avView = getLayoutInflater().inflate(R.layout.fragment_av, container,false);


        List<AV> av = new ArrayList<>();



        DatabaseHandler db = new DatabaseHandler(getContext());
        if (MainActivity.accountList.size()!=0){
            Account account = MainActivity.accountList.get(0);
            av = db.getAllAVByTime(MainActivity.getUnixTimeDaysAgo(1));
        }
        //av = db.getAllAV("test");
        RecyclerView rv = avView.findViewById(R.id.rvAV);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAVAdapter = new RVAVAdapter(getActivity(), av);
        //rvAttackAdapter.setClickListener(this);

        rv.setAdapter(rvAVAdapter);

        return rv;
    }

    @Override
    public void setData(List<AV> data) {
        rvAVAdapter.setNewData(data);
    }

    @Override
    public void addAV(List<AV> data) {
        rvAVAdapter.addAV(data);
    }
}
