package com.gowtham.library.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.gowtham.library.R;


@SuppressWarnings("ConstantConditions")
public class CustomProgressView extends Dialog {

    @SuppressLint("InflateParams")
    public CustomProgressView(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.setCancelable(false);
        this.setContentView(view);
    }
}
