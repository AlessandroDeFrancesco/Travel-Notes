package com.capraraedefrancescosoft.progettomobidev.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.capraraedefrancescosoft.progettomobidev.models.Element;
import com.capraraedefrancescosoft.progettomobidev.models.Journal;
import com.capraraedefrancescosoft.progettomobidev.widgets.ElementFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PagerAdapterElements extends FragmentStatePagerAdapter {
    private Date currentDate;
    private Journal journal;
    private Context context;
    private List<Element> currentElements;
    private List<ElementFragment> elementFragments;

    public PagerAdapterElements(Context context, FragmentManager fm, Journal journal, Date currentDate) {
        super(fm);
        this.context = context;
        this.journal = journal;
        this.currentDate = currentDate;
        this.currentElements = journal.getElementsOfDay(currentDate);
        this.elementFragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        ElementFragment elementFragment = (ElementFragment) ElementFragment.newInstance(context, currentElements.get(position));
        elementFragments.add(elementFragment);
        return elementFragment;
    }

    public void setCurrentDate(Date currentDate){
        this.currentDate = currentDate;
        this.currentElements = journal.getElementsOfDay(currentDate);
    }

    @Override
    public int getCount() {
        return currentElements.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void refreshFragments() {
        for(ElementFragment elementFragment : elementFragments){
            elementFragment.refreshFragment();
        }
    }
}
