package com.magicbullet.bt;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

public class ProgressDialog extends AppCompatDialog {
    //private Context context;
    private TextView textViewTitle, textViewMessage;

    public ProgressDialog(Context context) {
        super(context);
        //this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_dialog);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewMessage = findViewById(R.id.textViewMessage);
        //setTitle("Please wait...");
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setCancelable(false);
    }

    public void showProgress(String title, String message) {
        super.show();
        textViewTitle.setText(title);
        textViewMessage.setText(message);
    }

    public void dismissProgress() {
        super.dismiss();
    }
}
