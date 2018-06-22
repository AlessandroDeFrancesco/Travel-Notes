package com.capraraedefrancescosoft.progettomobidev.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.capraraedefrancescosoft.progettomobidev.R;

/**
 * Created by Ianfire on 27/09/2016.
 * attributo src per cambiare l'icona
 * attributo hint per cambiare il testo
 * attributo editable per attivare o disattivare l'editabilita' del testo
 */
public class TextWithIcon extends RelativeLayout{

    private View root;
    private ImageView icon;
    private EditText text;

    public TextWithIcon(Context context){
        super(context);
        init(context, null);
    }

    public TextWithIcon(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context, attrs);
    }

    public TextWithIcon(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        root = inflate(context, R.layout.edit_text_with_icon_view, this);
        text = (EditText) root.findViewById(R.id.textWithIconViewText);
        icon = (ImageView) root.findViewById(R.id.textWithIconViewImage);

        if(attrs != null){
            int hintResource = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "hint", R.string.defaultText);
            text.setHint(context.getString(hintResource));
            int iconResource = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "src", R.drawable.journal_type);
            icon.setImageDrawable(context.getDrawable(iconResource));
            boolean enabled = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/android", "editable", true);
            text.setClickable(enabled);
            text.setFocusable(enabled);
        }
    }

    public String getText() {
        return text.getText().toString();
    }

    public void setError(String error) {
        text.setError(error);
    }

    public void setText(String text) {
        this.text.setText(text);
    }
}
