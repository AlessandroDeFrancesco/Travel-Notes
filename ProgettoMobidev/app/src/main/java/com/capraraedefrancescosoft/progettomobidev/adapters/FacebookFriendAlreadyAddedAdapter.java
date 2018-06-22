package com.capraraedefrancescosoft.progettomobidev.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.models.FacebookFriend;

import java.util.ArrayList;

/**
 * Created by Gianpaolo Caprara on 9/22/2016.
 */
public class FacebookFriendAlreadyAddedAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<FacebookFriend> friends;

    public FacebookFriendAlreadyAddedAdapter(Context context, ArrayList<FacebookFriend> friends){
        this.context = context;
        this.friends = friends;
    }

    @Override
    public int getCount() {
        if (friends != null) {
            return friends.size();
        } else {
            return 0;
        }
    }

    @Override
    public FacebookFriend getItem(int position) {
        if (friends.get(position) != null) {
            return friends.get(position);
        } else {
            return null;
        }
    }

    public void addFriend(FacebookFriend friend){
        friends.add(friend);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // qui creo la view generica per gli elementi e
        // aggiungo la view interna giusta
        if (convertView == null) {
            // creo nuova view
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.facebook_friend_already_added, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            // riuso la view
            holder = (ViewHolder) convertView.getTag();
        }

        String friendName = getItem(position).getName();
        populateViewHolder(holder, friendName, position);

        return convertView;
    }

    // popolo il view holder
    private void populateViewHolder(final ViewHolder holder, String friendName, final int position) {
        holder.textView.setText(friendName);
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.textView = (TextView) v.findViewById(R.id.textViewFriendAdded);
        return holder;
    }

    public ArrayList<Long> getListaSceltiID(){
        ArrayList<Long> lista = new ArrayList<>();
        for(int i=0; i<getCount(); i++)
            if(getItem(i).isChecked())
                lista.add(Long.valueOf(getItem(i).getId()));

        return lista;
    }

    public ArrayList<String> getListaSceltiNomi(){
        ArrayList<String> lista = new ArrayList<>();
        for(int i=0; i<getCount(); i++)
            if(getItem(i).isChecked())
                lista.add(getItem(i).getName());

        return lista;
    }

    private static class ViewHolder {
        public TextView textView;
    }
}
