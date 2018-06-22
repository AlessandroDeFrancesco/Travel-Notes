package com.capraraedefrancescosoft.progettomobidev.utilities;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Ianfire on 27/09/2016.
 */
public class FontUtility {

    public enum FontType{
        TITLE, EXTRA, NOTE
    }
    private Typeface title_font, extra_font, note_font;
    private static FontUtility instance;

    private FontUtility(Context context) {
        title_font = Typeface.createFromAsset(context.getAssets(), "fonts/accanthis-adf-std.bold.otf");
        extra_font = Typeface.createFromAsset(context.getAssets(), "fonts/accanthis-adf-std.regular.otf");
        note_font = Typeface.createFromAsset(context.getAssets(), "fonts/ammys_handwriting.ttf");
    }

    public synchronized static FontUtility getInstance(Context context) {
        if(instance == null)
            instance = new FontUtility(context);
        return instance;
    }

    public Typeface getFont(FontType type){
        Typeface font = null;
        switch (type){
            case TITLE:
                font = title_font;
                break;
            case EXTRA:
                font = extra_font;
                break;
            case NOTE:
                font = note_font;
                break;
        }

        return font;
    }

}
