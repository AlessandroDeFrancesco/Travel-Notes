package com.capraraedefrancescosoft.progettomobidev.widgets;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.capraraedefrancescosoft.progettomobidev.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ianfire on 21/09/2016.
 */
public class DayFragment extends Fragment {

    public enum PageType{
        PRIMA_PAGINA, ULTIMA_PAGINA, PAGINA_INTERMEDIA, UNICA_PAGINA
    }

    public static Fragment newInstance(Context context, PageType pageType, Date day) {
        Bundle b = new Bundle();
        b.putSerializable("day", day);
        b.putSerializable("pagetype", pageType);
        return Fragment.instantiate(context, DayFragment.class.getName(), b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        PageType pagetype = (PageType) this.getArguments().getSerializable("pagetype");
        Date day = (Date)this.getArguments().getSerializable("day");

        ViewGroup pager = (ViewGroup) inflater.inflate(R.layout.days_fragment_pager, container, false);

        populateFragment(pager, day, pagetype);

        return pager;
    }

    private void populateFragment(ViewGroup pager, Date date, PageType pagetype) {
        LinearLayout root = (LinearLayout) pager.findViewById(R.id.root);
        TextView dayN = (TextView) root.findViewById(R.id.textDayNumber);
        TextView dayW = (TextView) root.findViewById(R.id.textDayOfWeek);
        TextView monthYear = (TextView) root.findViewById(R.id.textMonthAndYear);
        ImageButton leftArrow = (ImageButton) pager.findViewById(R.id.daysLeftArrow);
        ImageButton rightArrow = (ImageButton) pager.findViewById(R.id.daysRightArrow);

        dayN.setText(new SimpleDateFormat("dd", Locale.getDefault()).format(date));
        dayW.setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(date));
        monthYear.setText(new SimpleDateFormat("MMMM, yyyy", Locale.getDefault()).format(date));
        // mostro le frecce a seconda se c'e' o no una pagina succ o prec
        switch (pagetype){
            case PRIMA_PAGINA:
                rightArrow.setVisibility(View.VISIBLE);
                leftArrow.setVisibility(View.INVISIBLE);
                break;
            case ULTIMA_PAGINA:
                rightArrow.setVisibility(View.INVISIBLE);
                leftArrow.setVisibility(View.VISIBLE);
                break;
            case PAGINA_INTERMEDIA:
                rightArrow.setVisibility(View.VISIBLE);
                leftArrow.setVisibility(View.VISIBLE);
                break;
            case UNICA_PAGINA:
                rightArrow.setVisibility(View.INVISIBLE);
                leftArrow.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
