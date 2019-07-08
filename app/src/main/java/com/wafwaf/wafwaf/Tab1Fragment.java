package com.wafwaf.wafwaf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wafwaf.wafwaf.Adapter.RVAttackAdapter;
import com.wafwaf.wafwaf.Model.Attack;

import java.util.ArrayList;
import java.util.List;



public class Tab1Fragment extends Fragment implements MainActivity.SentDataToRVAttackAdapter {
    RVAttackAdapter rvAttackAdapter;
    TextView empty;
    RecyclerView rv;

    //int endUnixTime = (int) (System.currentTimeMillis() / 1000L);
    //int startUnixTime = endUnixTime - 60 * 60 * 24 * 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View attackView = getLayoutInflater().inflate(R.layout.fragment_attack,container,false);


         List<Attack> attacks = new ArrayList<>();



       DatabaseHandler db = new DatabaseHandler(getContext());
       if (MainActivity.accountList.size()!=0){
           attacks = db.getAllAttackByTime(MainActivity.getUnixTimeDaysAgo(1));
       }




        rv = attackView.findViewById(R.id.rvAttack);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
         rvAttackAdapter = new RVAttackAdapter(getActivity(), attacks);



        rv.setAdapter(rvAttackAdapter);

        empty = attackView.findViewById(R.id.empty_attack);
        checkEmpty();


        //return rv;
        return attackView;
    }


    private void checkEmpty(){
        if (rvAttackAdapter.getItemCount() == 0) {
            empty.bringToFront();
            empty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        }else{
            empty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }
    }




    @Override
    public void setData(List<Attack> data) {
        rvAttackAdapter.setNewData(data);
        checkEmpty();
    }

    @Override
    public void addAttacks(List<Attack> data) {
        rvAttackAdapter.addAttack(data);
        checkEmpty();
    }
}
