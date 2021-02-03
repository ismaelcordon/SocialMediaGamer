package com.icdominguez.socialmediagamer.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.icdominguez.socialmediagamer.R;
import com.icdominguez.socialmediagamer.ui.activities.FilterActivity;

public class FilterFragment extends Fragment {

    View mView;
    CardView mCardViewPs4, mCardViewXbox, mCardViewNintendo, mCardViewPc;

    public FilterFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_filter, container, false);
        findViews();
        events();
        return mView;
    }

    private void findViews() {
        mCardViewPs4 = mView.findViewById(R.id.cardViewPs4);
        mCardViewXbox = mView.findViewById(R.id.cardViewXbox);
        mCardViewNintendo = mView.findViewById(R.id.cardViewNintendo);
        mCardViewPc = mView.findViewById(R.id.cardViewPc);
    }

    private void events() {
        mCardViewPs4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("PS4");
            }
        });
        mCardViewXbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("XBOX");
            }
        });
        mCardViewNintendo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("NINTENDO");
            }
        });
        mCardViewPc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("PC");
            }
        });


    }

    private void goToFilterActivity(String category) {
        Intent i = new Intent(getContext(), FilterActivity.class);
        i.putExtra("category", category);
        startActivity(i);
    }
}