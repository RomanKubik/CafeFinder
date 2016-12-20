package com.example.kubik.cafefinder.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.kubik.cafefinder.R;

/**
 * Dialog to pick source of getting profile picture
 * Created by Kubik on 12/17/16.
 */

public class ChoosePictureDialog extends DialogFragment {

    public interface ChoosePictureDialogListener {
        void onMethodChosen(int code);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.picture_title)
                .setItems(R.array.picture_choose, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ChoosePictureDialogListener listener = (ChoosePictureDialogListener) getActivity();
                        listener.onMethodChosen(i);
                    }
                });
        return builder.create();
    }


}
