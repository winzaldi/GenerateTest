package apps.ftumj.ac.id.androidgeneratetest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import apps.ftumj.ac.id.androidgeneratetest.model.Rumah;

/**
 * Created by winzaldi on 12/19/17.
 */

public class RumahAdapter extends RecyclerView.Adapter<RumahAdapter.RumahViewHolder> implements Filterable {

    private List<Rumah> rumahList;
    private List<Rumah> rumahListFilter;
    private RumahAdapterListener listener;
    private Context mContext;

    public RumahAdapter(Context context, List<Rumah> rumahList, RumahAdapterListener listener) {
        this.rumahList = rumahList;
        mContext = context;
        this.listener = listener;
        this.rumahListFilter = rumahList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    rumahListFilter = rumahList;
                } else {
                    List<Rumah> filteredList = new ArrayList<>();
                    for (Rumah row : rumahList) {
                        if (row.getNama().toLowerCase().contains(charString.toLowerCase()) || row.getAlamat().contains(charString)) {
                            filteredList.add(row);
                        }
                    }
                    rumahListFilter = filteredList;

                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = rumahListFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                rumahListFilter = (ArrayList<Rumah>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public RumahViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_rumah, parent, false);

        return new RumahViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RumahViewHolder holder, int position) {
        final Rumah r = rumahListFilter.get(position);
        holder.tvTitle.setText(r.getNama());
        holder.tvAddress.setText(r.getAlamat());
        holder.imgContent.setBackgroundResource(R.drawable.ic_place_24dp);


    }

    @Override
    public int getItemCount() {
        return rumahListFilter.size();
    }

    public class RumahViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle,tvAddress;
        private ImageView imgContent;

        public RumahViewHolder(View itemView) {
            super(itemView);
            imgContent = itemView.findViewById(R.id.img_content);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAddress = itemView.findViewById(R.id.tv_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRumahSelected(rumahListFilter.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface RumahAdapterListener {
        void onRumahSelected(Rumah rumah);
    }
}
