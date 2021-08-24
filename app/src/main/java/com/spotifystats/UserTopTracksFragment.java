package com.spotifystats;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class UserTopTracksFragment extends Fragment implements
        AdapterView.OnItemSelectedListener{


    private String type = "tracks";
    private DisplayDataActivity activity;
    boolean hasVisit = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_top_tracks, container, false);
        activity = ((DisplayDataActivity)getActivity());

        ImageView forwardArrow = v.findViewById(R.id.backwardArrow2);
        forwardArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager viewPager = activity.viewPager;
                viewPager.setCurrentItem(viewPager.getCurrentItem()-1,true);
            }
        });

        EditText editText = v.findViewById(R.id.top_value);
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if(TextUtils.isEmpty(editText.getText().toString()) ||Integer.parseInt(editText.getText().toString()) < 5 || Integer.parseInt(editText.getText().toString()) > 50 ){
                        activity.makeToast("Please enter number between 5-50");
                    }else{
                        activity.setLimit(Integer.parseInt(editText.getText().toString()));
                        activity.generateRequest(type);
                    }


                    return true;
                }
                return false;
            }
        });

        // Create ArrayAdapter using the string array and default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),R.array.time_range_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.

        //Create Spinner
        Spinner spinner = v.findViewById(R.id.time_range_spinner);
        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
        }
        if (spinner != null) {
            spinner.setAdapter(adapter);
        }



        return v;
    }




    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!hasVisit) {
            if (isVisibleToUser) {
                hasVisit = true;
                activity.generateRequest(type);
            } else {
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activity.setIndex(position);
        activity.setIndex(position);
        if(hasVisit) {
            activity.generateRequest(type);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}