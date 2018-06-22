package com.capraraedefrancescosoft.progettomobidev.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.capraraedefrancescosoft.progettomobidev.models.Journal;
import com.capraraedefrancescosoft.progettomobidev.widgets.DayFragment;

import java.util.Date;
import java.util.List;

public class PagerAdapterDays extends FragmentPagerAdapter {
    private Journal journal;
    private List<Date> days;
    private Context context;

    public PagerAdapterDays(Context context, FragmentManager fm, Journal journal) {
        super(fm);
        this.context = context;
        this.journal = journal;
        this.days = journal.getAllJournalDays();
    }

    public Date getDay(int position) {
        return days.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        DayFragment.PageType pageType = DayFragment.PageType.PAGINA_INTERMEDIA;
        if (getCount() == 1)
            pageType = DayFragment.PageType.UNICA_PAGINA;
        else if (position == getCount() - 1)
            pageType = DayFragment.PageType.ULTIMA_PAGINA;
        else if (position == 0)
            pageType = DayFragment.PageType.PRIMA_PAGINA;

        return DayFragment.newInstance(context, pageType, days.get(position));
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.days = journal.getAllJournalDays();
    }

    @Override
    public int getCount() {
        return days.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
