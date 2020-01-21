package trap1.bhaleraoomkar.chatroomfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OnlineUserActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference loginRef;
    private TextView welcome;
    private String username;

    public static long lastMessage = 0;

    ValueEventListener changeOnline = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(((String)(dataSnapshot.child("online").getValue())).equals("false")) {
                Map<String, Object> updates = new HashMap<String, Object>();
                updates.put("online","true");
                loginRef.updateChildren(updates);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_user);
        welcome = (TextView)findViewById(R.id.welcome);

        Intent intent = getIntent();

        username = intent.getStringExtra(getString(R.string.username_key));

        welcome.setText(getString(R.string.welcome, username));

        database = FirebaseDatabase.getInstance();

        loginRef = database.getReference("users/" + username);

        loginRef.addValueEventListener(changeOnline);


    }
    @Override
    protected void onStop(){
        loginRef.removeEventListener(changeOnline);
        super.onStop();
    }

    /*@Override
    protected void onStart(){
        loginRef.addValueEventListener(changeOnline);
        super.onStart();
    }*/







}
