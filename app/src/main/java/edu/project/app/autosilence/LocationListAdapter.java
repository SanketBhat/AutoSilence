package edu.project.app.autosilence;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Sanket on 19-02-2018.
 * The RecyclerView Adapter. It shows the list of geofencing area in the MainActivity.
 */

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.LocationListViewHolder> {

    private ArrayList<AutoSilenceLocation> locations;

    private RecyclerViewClickCallbacks recyclerViewClickCallbacks = null;

    LocationListAdapter(ArrayList<AutoSilenceLocation> locations) {
        this.locations = locations;
    }

    void setLocations(ArrayList<AutoSilenceLocation> locations) {
        this.locations = locations;
        this.notifyDataSetChanged();
    }

    /*void addLocation(AutoSilenceLocation location) {
        locations.add(location);
        this.notifyItemInserted(locations.indexOf(location));
    }*/

    void removeLocation(int position) {
        locations.remove(position);
        this.notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public LocationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_item, parent, false);
        return new LocationListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationListViewHolder holder, int position) {
        holder.listName.setText(locations.get(position).getName());
        holder.listLat.setText(String.format(Locale.ENGLISH, "%.4f", locations.get(position).getLat()));
        holder.listLng.setText(String.format(Locale.ENGLISH, "%.4f", locations.get(position).getLng()));
        holder.listRad.setText(String.valueOf(locations.get(position).getRadius()));
        holder.listAdd.setText(locations.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        if (locations == null) return 0;
        return locations.size();
    }

    void setRecyclerViewClickCallbacks(RecyclerViewClickCallbacks recyclerViewClickCallbacks) {
        this.recyclerViewClickCallbacks = recyclerViewClickCallbacks;
    }

    AutoSilenceLocation getItem(int position) {
        return locations.get(position);
    }

    ItemSwipeHelper getSwipeHelper() {
        return new ItemSwipeHelper();
    }

    interface RecyclerViewClickCallbacks {
        void onItemClick(View v, int position);

        boolean onItemLongClick(View v, int position);

        void onItemSwipe(int position);
    }

    class LocationListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView listName, listRad, listLat, listLng, listAdd;

        LocationListViewHolder(View itemView) {
            super(itemView);
            listName = itemView.findViewById(R.id.listItemName);
            listLat = itemView.findViewById(R.id.listItemLat);
            listLng = itemView.findViewById(R.id.listItemLng);
            listRad = itemView.findViewById(R.id.listItemRadius);
            listAdd = itemView.findViewById(R.id.listItemAddress);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (recyclerViewClickCallbacks != null)
                recyclerViewClickCallbacks.onItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return recyclerViewClickCallbacks != null && recyclerViewClickCallbacks.onItemLongClick(v, getAdapterPosition());
        }
    }

    class ItemSwipeHelper extends ItemTouchHelper.SimpleCallback {

        ItemSwipeHelper() {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //Ignore
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            recyclerViewClickCallbacks.onItemSwipe(viewHolder.getAdapterPosition());
        }
    }

}
