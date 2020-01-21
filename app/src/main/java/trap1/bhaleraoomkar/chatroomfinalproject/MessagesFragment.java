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


public class MessagesFragment extends Fragment {

    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessagesAdapter mAdapter;
    private View mRootView;

    private FirebaseDatabase database;
    private DatabaseReference messagingRef;

    ValueEventListener changeMessage = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.child("lastMessage/username").getValue() != null && dataSnapshot.child("lastMessage/message").getValue() != null) {
                String user = dataSnapshot.child("lastMessage/username").getValue().toString();
                String message = dataSnapshot.child("lastMessage/message").getValue().toString();
                long id = (Long)dataSnapshot.child("lastMessage/id").getValue();
                MessagingActivity.lastMessage = id;
                messageList.add(new Message(user, message));
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    public MessagesFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_messages, container, false);

        recyclerView = mRootView.findViewById(R.id.recyclerView);

        database = FirebaseDatabase.getInstance();
        messagingRef = database.getReference("messages");

        mAdapter = new MessagesAdapter(messageList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mRootView.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        messagingRef.addValueEventListener(changeMessage);

        return mRootView;
    }



}
