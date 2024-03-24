package com.example.mainpage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogFragment extends androidx.fragment.app.DialogFragment {

    protected Button dismiss;
    protected ImageView alertImage;
    protected TextView level,C02amount,VOCamount, message;

    MainPageActivity main = new MainPageActivity();
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_dialog_fragment, null);

        dismiss = view.findViewById(R.id.dismissButton);
        alertImage = view.findViewById(R.id.AlertImageView);
        level = view.findViewById(R.id.levelTextView);
        C02amount = view.findViewById(R.id.CO2TextView);
        VOCamount = view.findViewById(R.id.VOCTextView);
        message = view.findViewById(R.id.messageTextView);


        if(main.isairclicked == true){
            C02amount.setText("CO2: " + main.getAverageCO2());
            VOCamount.setText("VOC: " + main.getAverageVOC());
            main.isairclicked = false;
        } else if(main.issoundclicked == true){
            C02amount.setText("Sound DB " + main.getAverageSound());
            VOCamount.setVisibility(View.GONE);
            main.issoundclicked = false;
        }


        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }

        });


        return view;
    }

}