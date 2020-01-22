package com.mrcoder.webapi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> implements Filterable {

    // private ArrayList<City> names;

    private List<City> contactList;
    private List<City> contactListFiltered;
    private ContactsAdapterListener listener;
    private Context context;


    public CustomAdapter(Context context, List<City> contactList, ContactsAdapterListener listener) {
        this.context = context;
        //this.listener = listener;
        this.listener =listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.textViewName.setText(contactListFiltered.get(position).getRestoName());
        //viewHolder.textViewName.setText(contactListFiltered.get(position));
        viewHolder.textViewCityName.setText(contactListFiltered.get(position).getCity());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                City city = contactListFiltered.get(position);
                Intent intent = new Intent(v.getContext(), RestoDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("city", city);
                intent.putExtra("city", city);
                (v.getContext()).startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {
                    List<City> filteredList = new ArrayList<>();
                    for (City row : contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match

                        if (row.getRestoName().toLowerCase().contains(charString.toLowerCase()) || row.getCity().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }
                    contactListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contactListFiltered = (ArrayList<City>) results.values;
                notifyDataSetChanged();
            }
        };

    }
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewCityName;

        ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewCityName = (TextView) itemView.findViewById(R.id.textViewCity);
        }
    }

    public interface ContactsAdapterListener {
        void onContactSelected(City contact);
    }


}
