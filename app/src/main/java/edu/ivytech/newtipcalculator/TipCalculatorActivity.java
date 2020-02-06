package edu.ivytech.newtipcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
