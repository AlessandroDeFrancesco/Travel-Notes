package com.capraraedefrancescosoft.progettomobidev.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.models.FacebookFriend;

import java.util.ArrayList;

/**
 * Created by Gianpaolo Caprara on 9/13/2016.
 */
public class FacebookFriendsAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<FacebookFriend> friends;

    public FacebookFriendsAdapter(Context context, ArrayList<FacebookFriend> friends){
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
            convertView = vi.inflate(R.layout.facebook_friend_element, null);
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
        holder.checkbox.setText(friendName);
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItem(position).setChecked(holder.checkbox.isChecked());
            }
        });
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.checkbox = (CheckBox) v.findViewById(R.id.checkBoxFriend);
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
        public CheckBox checkbox;
    }
}
