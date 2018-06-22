package com.capraraedefrancescosoft.progettomobidev.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.capraraedefrancescosoft.progettomobidev.models.Journals;
import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.models.Journal;
import com.capraraedefrancescosoft.progettomobidev.utilities.FacebookUtility;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Ianfire on 24/06/2016.
 */
public class JournalsAdapter extends BaseAdapter {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private Context context;

    public JournalsAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        List<Journal> journals = Journals.getInstance().getJournals();

        if (journals != null) {
            return journals.size();
        } else {
            return 0;
        }
    }

    @Override
    public Journal getItem(int position) {
        List<Journal> journals = Journals.getInstance().getJournals();

        if (journals.get(position) != null) {
            return journals.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Journal journal = getItem(position);

        // qui creo la view generica per i journal
        if (convertView == null) {
            // creo nuova view
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.journal_view, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            // riuso la view
            holder = (ViewHolder) convertView.getTag();
        }

        populateViewHolder(holder, journal);

        return convertView;
    }

    // popolo il view holder
    private void populateViewHolder(final ViewHolder holder, final Journal journal) {
        holder.txtName.setText(journal.getName());
        holder.txtDescription.setText( journal.getDescription());
        holder.txtDeparture.setText(dateFormat.format(journal.getDepartureDate()));
        holder.txtReturn.setText(dateFormat.format(journal.getReturnDate()));
        FacebookUtility.getInstance().getNameFromId(journal.getOwnerID(), new FacebookUtility.GetFacebookNameCallback() {
            @Override
            public void nameRetrieved(String name) {
                holder.txtOwner.setText(name);
            }
        });
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtName = (TextView) v.findViewById(R.id.journalViewJournalName);
        holder.txtOwner = (TextView)v.findViewById(R.id.journalViewJournalOwnerdId);
        holder.txtDescription = (TextView) v.findViewById(R.id.journalViewJournalDescription);
        holder.txtDeparture = (TextView) v.findViewById(R.id.journalViewJournalDeparture);
        holder.txtReturn = (TextView) v.findViewById(R.id.journalViewJournalReturn);
        holder.icon = (ImageView) v.findViewById(R.id.journalViewJournalIcon);
        return holder;
    }

    private static class ViewHolder {
        public TextView txtName, txtOwner, txtDescription, txtDeparture, txtReturn;
        public ImageView icon;
    }

}
