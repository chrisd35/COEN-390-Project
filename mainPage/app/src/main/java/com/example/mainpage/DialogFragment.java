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
            C02amount.setText("CO2: " + main.getAverageCO2() + " ppm");
            VOCamount.setText("VOC: " + main.getAverageVOC() + " ppb");

            if ((Double.parseDouble(main.getAverageCO2()) >= 1100 && Double.parseDouble(main.getAverageCO2()) < 1500 ) || (Double.parseDouble(main.getAverageVOC()) >= 51 && Double.parseDouble(main.getAverageVOC()) < 100 )) {
                alertImage.setImageResource(R.drawable.yellowwarning);
                level.setText("MEDIOCRE");
                message.setText("Contaminated indoor air. Ventilation is recommended.");
            }

            else if ((Double.parseDouble(main.getAverageCO2()) >= 1600) || (Double.parseDouble(main.getAverageVOC()) >= 101) ){
                alertImage.setImageResource(R.drawable.redwarning);
                level.setText("BAD");
                message.setText("Heavily contaminated indoor air. Ventilation is required.");
            }
            else {
                level.setText("GOOD");
                message.setText("The air is safe to breathe. No ventilation required. ");
            }

            main.isairclicked = false;
        } else if(main.issoundclicked == true){
            C02amount.setText("Sound Level: " + main.getAverageSound() + " dB");
            VOCamount.setVisibility(View.GONE);

            // Caution Level
           if (Double.parseDouble(main.getAverageSound()) >= 70 && Double.parseDouble(main.getAverageSound()) < 80 ) {
               alertImage.setImageResource(R.drawable.yellowwarning);
                level.setText("Caution");
               message.setText("Noise above 70 dB over a prolonged period of time may start to damage your hearing.");
            }
           // Red Level 1
            else if (Double.parseDouble(main.getAverageSound()) >= 80 && Double.parseDouble(main.getAverageSound()) < 85 ) {
               alertImage.setImageResource(R.drawable.redwarning);
               level.setText("Level 1");
                message.setText("Damage to hearing possible after 2 hours of exposure");
            }
           // Red Level 2
           else if (Double.parseDouble(main.getAverageSound()) >= 85 && Double.parseDouble(main.getAverageSound()) < 95 ) {
               alertImage.setImageResource(R.drawable.redwarning);
               level.setText("Level 2");
               message.setText("Damage to hearing possible after about 50 minutes of exposure");
           }
           // Red Level 3
           else if (Double.parseDouble(main.getAverageSound()) >= 95 && Double.parseDouble(main.getAverageSound()) < 100 ) {
               alertImage.setImageResource(R.drawable.redwarning);
               level.setText("Level 3");
               message.setText("Hearing loss possible after 15 minutes");
           }
           else if (Double.parseDouble(main.getAverageSound()) >= 100 && Double.parseDouble(main.getAverageSound()) < 110 ) {
               alertImage.setImageResource(R.drawable.redwarning);
               level.setText("Level 4");
               message.setText("Hearing loss possible in less than 5 minutes");
           }
           else if (Double.parseDouble(main.getAverageSound()) >= 110 && Double.parseDouble(main.getAverageSound()) < 120 ) {
               alertImage.setImageResource(R.drawable.redwarning);
               level.setText("Level 5");
               message.setText("Hearing loss possible in less than 2 minutes");
           }
           else if (Double.parseDouble(main.getAverageSound()) >= 120) {
               alertImage.setImageResource(R.drawable.redwarning);
               level.setText("Level 6");
               message.setText("Immediate pain and injury to the ear");
           }
           else {
               message.setText("You're within the safe dB range. ");
               level.setText("Safe Level");
           }
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