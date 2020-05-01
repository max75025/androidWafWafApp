package com.app2waf;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app2waf.Adapter.RVAVAdapter;
import com.app2waf.Model.AV;
import com.app2waf.util.UnixTime;

import java.util.ArrayList;
import java.util.List;

public class Tab2Fragment extends Fragment implements MainActivity.SentDataToRVAVAdapter {
    RVAVAdapter rvAVAdapter;

    //int endUnixTime = (int) (System.currentTimeMillis() / 1000L);
    //int startUnixTime = endUnixTime - 60 * 60 * 24 * 10;
    LinearLayout empty;
    RecyclerView rv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View avView = getLayoutInflater().inflate(R.layout.fragment_av, container,false);

        List<AV> av = new ArrayList<>();

        DatabaseHandler db = new DatabaseHandler(getContext());
        if (MainActivity.accountList.size()!=0){
            //Account account = MainActivity.accountList.get(0);
            av = db.getAllAVByTime(UnixTime.getUnixTimeDaysAgo(1));
        }

        rv = avView.findViewById(R.id.rvAV);
        empty = avView.findViewById(R.id.empty_av);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAVAdapter = new RVAVAdapter(getActivity(), av);
       checkEmpty();

        rv.setAdapter(rvAVAdapter);

        return avView;
    }

    private void checkEmpty(){
        if (rvAVAdapter.getItemCount() == 0) {
            empty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        }else{
            empty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setData(List<AV> data) {
        rvAVAdapter.setNewData(data);
        checkEmpty();
    }

    @Override
    public void addAV(List<AV> data) {
        rvAVAdapter.addAV(data);
        checkEmpty();
    }
}
