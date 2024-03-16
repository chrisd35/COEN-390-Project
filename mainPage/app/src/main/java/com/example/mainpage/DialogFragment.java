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
    protected TextView level,amount,message;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_dialog_fragment, null);

        dismiss = view.findViewById(R.id.dismissButton);
        alertImage = view.findViewById(R.id.AlertImageView);
        level = view.findViewById(R.id.levelTextView);
        amount = view.findViewById(R.id.amountTextView);
        message = view.findViewById(R.id.messageTextView);


        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }

        });


        return view;
    }

}