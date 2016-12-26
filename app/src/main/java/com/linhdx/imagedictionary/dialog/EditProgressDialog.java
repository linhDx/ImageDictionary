package com.linhdx.imagedictionary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.linhdx.imagedictionary.R;

/**
 * Created by shine on 26/12/2016.
 */

public class EditProgressDialog extends Dialog {
    public EditProgressDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(
                R.drawable.bg_black_transparent);
        setContentView(R.layout.layout_progress_dialog);
    }
}