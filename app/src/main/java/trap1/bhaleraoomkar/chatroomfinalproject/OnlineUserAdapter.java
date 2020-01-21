package trap1.bhaleraoomkar.chatroomfinalproject;

import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.TreeSet;

public class OnlineUserAdapter extends RecyclerView.Adapter<OnlineUserAdapter.MyViewHolder> {

    private List<User> usersList;
    private Geocoder geocoder;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView user;
        public TextView location;
        public TextView distance;

        public MyViewHolder(View view) {
            super(view);
            user = (TextView) view.findViewById(R.id.name2);
            location = (TextView) view.findViewById(R.id.location);
            distance = (TextView) view.findViewById(R.id.distance);

        }
    }


    public OnlineUserAdapter(List<User> usersList) {
        this.usersList = usersList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User u = usersList.get(position);
        double lat = u.getLatitude();
        double longi = u.getLongitude();
        if(lat > 200 || longi > 200){
            holder.user.setText(u.getUsername());
            holder.location.setText("Last seen in an unknown city");
            holder.distance.setText("Unknown distance away");
        } else if(MessagingActivity.lat > 200 || MessagingActivity.longi > 200){
            holder.user.setText(u.getUsername());
            holder.location.setText("Last seen in " + u.getCity() + ", " + u.getCountry());
            holder.distance.setText("Unknown distance away");
        }else {
            double distanceKm = getDistance(lat, longi, MessagingActivity.lat, MessagingActivity.longi);
            double distanceMi = distanceKm / 1.60934;
            holder.user.setText(u.getUsername());
            holder.location.setText("Last seen in " + u.getCity() + ", " + u.getCountry());
            holder.distance.setText("" + Math.round(distanceMi) + " miles away");
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public double getDistance(double lat1, double long1, double lat2, double long2){
        lat1 = Math.toRadians(lat1);
        long1 = Math.toRadians(long1);
        lat2 = Math.toRadians(lat2);
        long2 = Math.toRadians(long2);

        double radius = 6371.0;
        double dlon = long2 - long1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double ans = 2.0 * Math.asin(Math.sqrt(a));
        return radius*ans;
    }
}