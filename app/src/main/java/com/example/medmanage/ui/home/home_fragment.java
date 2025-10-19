package com.example.medmanage.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.medmanage.R;
import com.example.medmanage.activities.ViewMedicationActivity;

public class home_fragment extends Fragment {

    public home_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CardView viewMedicationCard = view.findViewById(R.id.view_medication);
        viewMedicationCard.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), ViewMedicationActivity.class);
            if (getActivity() != null && getActivity().getIntent() != null) {
                String userType = getActivity().getIntent().getStringExtra("USER_TYPE");

                // Pass it along to the next activity
                intent.putExtra("USER_TYPE", userType);
            }

            startActivity(intent);
        });
    }

}