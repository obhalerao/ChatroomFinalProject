package trap1.bhaleraoomkar.chatroomfinalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class OnlineUserFragment extends Fragment {

    private List<User> usersList = new ArrayList<User>();
    private RecyclerView recyclerView;
    private OnlineUserAdapter ouAdapter;
    private View mRootView;

    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private DatabaseReference loginRef;

    ValueEventListener changeUser = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            ArrayList<User> tempUser = new ArrayList<User>();
            for(DataSnapshot ds: dataSnapshot.getChildren()) {
                if (((String) ds.child("online").getValue()).equals("true")) {
                    tempUser.add(new User(((String) ds.getKey()), Double.parseDouble("" + ds.child("latitude").getValue()), Double.parseDouble("" + ds.child("longitude").getValue()), (String) ds.child("city").getValue(), (String) ds.child("country").getValue()));
                }
            }
            usersList = tempUser;
            ouAdapter = new OnlineUserAdapter(usersList);
            recyclerView.swapAdapter(ouAdapter, false);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    public OnlineUserFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_online_user, container, false);

        recyclerView = mRootView.findViewById(R.id.recyclerView2);

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        loginRef = database.getReference("users");

        loginRef.addListenerForSingleValueEvent(changeUser);

        getOnlineUsers();

        ouAdapter = new OnlineUserAdapter(usersList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mRootView.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(ouAdapter);

        usersRef.addValueEventListener(changeUser);

        return mRootView;
    }

    private void getOnlineUsers(){
        loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.child("online").getValue().toString().equals("true")){
                        usersList.add(new User(((String)ds.getKey()), Double.parseDouble(""+ds.child("latitude").getValue()), Double.parseDouble(""+ds.child("longitude").getValue()), (String)ds.child("city").getValue(), (String)ds.child("country").getValue()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
