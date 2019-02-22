package com.wafwaf.wafwaf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wafwaf.wafwaf.Adapter.RVAttackAdapter;
import com.wafwaf.wafwaf.Model.Attack;

import java.util.ArrayList;
import java.util.List;



public class Tab1Fragment extends Fragment implements MainActivity.SentDataToRVAttackAdapter {
    RVAttackAdapter rvAttackAdapter;

    //int endUnixTime = (int) (System.currentTimeMillis() / 1000L);
    //int startUnixTime = endUnixTime - 60 * 60 * 24 * 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View attackView = getLayoutInflater().inflate(R.layout.fragment_attack,container,false);


         List<Attack> attacks = new ArrayList<>();



       DatabaseHandler db = new DatabaseHandler(getContext());
       if (MainActivity.accountList.size()!=0){
           //Account account = MainActivity.accountList.get(0);
           attacks = db.getAllAttackByTime(MainActivity.getUnixTimeDaysAgo(1));
       }
       //attacks = db.getAllAttack("test");
        RecyclerView rv = attackView.findViewById(R.id.rvAttack);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
         rvAttackAdapter = new RVAttackAdapter(getActivity(), attacks);

        //rvAttackAdapter.setClickListener(this);

        rv.setAdapter(rvAttackAdapter);

        //not work animation
       /* LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);
        rv.setLayoutAnimation(animation);
        rv.getAdapter().notifyDataSetChanged();
        rv.scheduleLayoutAnimation();*/


        return rv;
    }



    @Override
    public void setData(List<Attack> data) {
        rvAttackAdapter.setNewData(data);
    }

    @Override
    public void addAttacks(List<Attack> data) {
        rvAttackAdapter.addAttack(data);
    }
}
