package edu.ivytech.newtipcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import static android.widget.TextView.*;

public class TipCalculatorActivity extends AppCompatActivity {

    private EditText billAmountEditText;
    private EditText tipEditText;
    private EditText totalEditText;
    private EditText percentEditText;
    private float tipPercent = .15f;
    private SharedPreferences savedValues;
    private String billAmountString;
    private Spinner splitSpinner;
    private int split = 1;
    private SeekBar tipSeek;
    private RadioGroup roundingRadioGroup;
    private int round = 0;

    private final int ROUND_NONE = 0;
    private final int ROUND_TIP = 1;
    private final int ROUND_TOTAL = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipcalculator);

        billAmountEditText = findViewById(R.id.billAmountEditText);
        tipEditText = findViewById(R.id.tipEditText);
        totalEditText = findViewById(R.id.totalEditText);
        percentEditText = findViewById(R.id.percentEditText);

        billAmountEditText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_UNSPECIFIED
                || i == EditorInfo.IME_ACTION_NEXT) {
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(billAmountEditText.getWindowToken(),0);
                    calculateAndDisplay();
                }
                return false;
            }
        });

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
        splitSpinner = findViewById(R.id.splitSpinner);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,R.array.split_array,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        splitSpinner.setAdapter(adapter);
        splitSpinner.setSelection(0);

        splitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                split = position + 1;
                calculateAndDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        tipSeek = findViewById(R.id.tipSeek);
        tipSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                percentEditText.setText(String.format("%d", i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                tipPercent = (float)progress/100.0f;
                calculateAndDisplay();
            }
        });

        roundingRadioGroup = findViewById(R.id.roundingRadioGroup);
        roundingRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d("Tip Calculator","Entered onCheckedChanged");
                switch(i) {
                    case R.id.noRoundingRadioButton:
                        round = ROUND_NONE;
                        Log.d("Tip Calculator", "no round");
                        break;
                    case R.id.roundTipRadioButton:
                        round = ROUND_TIP;
                        Log.d("Tip Calculator", "round tip");
                        break;
                    case R.id.roundTotalRadioButton:
                        round = ROUND_TOTAL;
                        Log.d("Tip Calculator", "round total");
                        break;
                }
                calculateAndDisplay();

            }
        });
    }

    @Override
    protected void onPause() {
        Editor editor = savedValues.edit();
        editor.putString("billAmountString",billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.commit();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        billAmountString = savedValues.getString("billAmountString","");
        tipPercent = savedValues.getFloat("tipPercent",0.15f);

        billAmountEditText.setText(billAmountString);
        int progress = (int)(tipPercent * 100);
        tipSeek.setProgress(progress);

        calculateAndDisplay();
    }

    public void calculateAndDisplay() {
        billAmountString = billAmountEditText.getText().toString();
        float billAmount;
        if (billAmountString.equals("")) {
            billAmount = 0;
        }
        else {
            billAmount = Float.parseFloat(billAmountString);
        }


        float tipAmount = billAmount * tipPercent;
        float totalAmount = billAmount + tipAmount;


        switch(round) {
            case ROUND_NONE:
                break;
            case ROUND_TIP:
                tipAmount = (float)Math.ceil(tipAmount);
                break;
            case ROUND_TOTAL:
               totalAmount = (float)Math.ceil(totalAmount);
                break;
        }
        totalAmount = totalAmount / split;
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        NumberFormat percent = NumberFormat.getPercentInstance();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setPercent('\0');
        symbols.setCurrencySymbol("");
        ((DecimalFormat) percent).setDecimalFormatSymbols(symbols);
        ((DecimalFormat) currency).setDecimalFormatSymbols(symbols);

        percentEditText.setText(percent.format(tipPercent));
        tipEditText.setText(currency.format(tipAmount));
        totalEditText.setText(currency.format(totalAmount));

    }

    /*public void changePercent(View v) {
        switch (v.getId()) {
            case R.id.percentDownButton:
                tipPercent = tipPercent - 0.01f;
                calculateAndDisplay();
                break;
            case R.id.percentUpButton:
                tipPercent = tipPercent + 0.01f;
                calculateAndDisplay();
                break;
        }
    }*/


}
