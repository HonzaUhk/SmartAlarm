package cz.uhk.knejpja1.smartalarm.model;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private static final String androidns = "http://schemas.android.com/apk/res/android";

    private SeekBar seekBar;
    private TextView valueText;
    private Context context;

    private String dialogMessage;

    private int defaultValue = 0;
    private int maxValue = 0;
    private int currentValue = 0;

    public SeekBarPreference(Context context, AttributeSet attrs) {

        super(context, attrs);
        this.context = context;

        dialogMessage = attrs.getAttributeValue(androidns, "dialogMessage");
        defaultValue = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
        maxValue = attrs.getAttributeIntValue(androidns, "max", 100);
    }

    @Override
    protected View onCreateDialogView() {
        LinearLayout.LayoutParams params;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6, 6, 6, 6);

        valueText = new TextView(context);
        valueText.setGravity(Gravity.CENTER_HORIZONTAL);
        valueText.setTextSize(30);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(valueText, params);

        seekBar = new SeekBar(context);
        seekBar.setOnSeekBarChangeListener(this);
        layout.addView(seekBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        if (shouldPersist())
            currentValue = getPersistedInt(defaultValue);

        seekBar.setMax(maxValue);
        seekBar.setProgress(currentValue);

        return layout;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        seekBar.setMax(maxValue);
        seekBar.setProgress(currentValue);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        super.onSetInitialValue(restore, defaultValue);
        if (restore)
            currentValue = shouldPersist() ? getPersistedInt(this.defaultValue) : 0;
        else
            currentValue = (Integer) defaultValue;
    }

    @Override
    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
        valueText.setText(Integer.toString(value));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seek) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seek) {
    }

    public void setMax(int max) {
        maxValue = max;
    }

    public int getMax() {
        return maxValue;
    }

    public void setProgress(int progress) {
        currentValue = progress;
        if (seekBar != null)
            seekBar.setProgress(progress);
    }

    public int getProgress() {
        return currentValue;
    }

    @Override
    public void showDialog(Bundle state) {

        super.showDialog(state);

        Button positiveButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (shouldPersist()) {

            currentValue = seekBar.getProgress();
            persistInt(seekBar.getProgress());
            callChangeListener(Integer.valueOf(seekBar.getProgress()));
        }

        ((AlertDialog) getDialog()).dismiss();
    }
}