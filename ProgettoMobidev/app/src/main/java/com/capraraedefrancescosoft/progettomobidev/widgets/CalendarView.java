package com.capraraedefrancescosoft.progettomobidev.widgets;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.utilities.ListenerUtility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Ianfire on 27/09/2016.
 * attributo editable per attivare o disattivare l'editabilita' della data
 * attributo hint per cambiare il testo
 */
public class CalendarView extends LinearLayout implements View.OnClickListener, View.OnFocusChangeListener {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private View root;
    private TextView label;
    private TextView dateText;
    private ImageButton imageButton;
    private Date date;

    public CalendarView (Context context){
        super(context);
        init(context, null);
    }

    public CalendarView (Context context, AttributeSet attrs){
        super(context, attrs);
        init(context, attrs);
    }

    public CalendarView (Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.root = inflate(context, R.layout.calendar_view, this);
        this.dateText = (TextView) root.findViewById(R.id.calendarViewText);
        this.imageButton = (ImageButton) root.findViewById(R.id.calendarViewImage);
        this.label = (TextView) root.findViewById(R.id.calendarViewLabel);

        this.dateText.setOnClickListener(this);
        this.dateText.setOnFocusChangeListener(this);
        this.imageButton.setOnClickListener(this);
        this.label.setOnClickListener(this);

        // setto gli attributi
        if(attrs != null){
            int hintResource = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "hint", R.string.defaultText);
            this.label.setText(context.getString(hintResource));
            setDate(new Date());
            boolean editable = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/android", "editable", true);
            this.dateText.setClickable(editable);
            this.dateText.setFocusable(editable);
            this.imageButton.setClickable(editable);
        }
    }

    public Date getDate(){
        return date;
    }

    private void setDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        this.date = cal.getTime();
        this.dateText.setText(dateFormat.format(date));
    }

    @Override
    public void onClick(View v) {
        ListenerUtility.getDatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                setDate(year, monthOfYear, dayOfMonth);
            }
        }).show();
    }

    public void onFocusChange(View v, boolean hasFocus) {
        ListenerUtility.getDatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                setDate(year, monthOfYear, dayOfMonth);
            }
        }).show();
    }

    public void setDate(Date date) {
        this.date = date;
        this.dateText.setText(dateFormat.format(date));
    }
}
