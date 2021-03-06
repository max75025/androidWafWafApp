package com.app2waf;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app2waf.Adapter.RVAttackAdapter;
import com.app2waf.Model.Attack;
import com.app2waf.util.UnixTime;

import java.util.ArrayList;
import java.util.List;



public class Tab1Fragment extends Fragment implements MainActivity.SentDataToRVAttackAdapter {
    RVAttackAdapter rvAttackAdapter;
    LinearLayout empty;
    RecyclerView rv;
    TextView emptyText;

    //int endUnixTime = (int) (System.currentTimeMillis() / 1000L);
    //int startUnixTime = endUnixTime - 60 * 60 * 24 * 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View attackView = getLayoutInflater().inflate(R.layout.fragment_attack,container,false);
        emptyText = attackView.findViewById(R.id.empty_attack_text);


         List<Attack> attacks = new ArrayList<>();



       DatabaseHandler db = new DatabaseHandler(getContext());
       if (MainActivity.accountList.size()!=0){
           attacks = db.getAllAttackByTime(UnixTime.getUnixTimeDaysAgo(1));
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

    public void setEmptyText(int str){
        emptyText.setText(str);

    }
}
