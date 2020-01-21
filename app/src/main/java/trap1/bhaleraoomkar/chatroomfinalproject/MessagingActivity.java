package trap1.bhaleraoomkar.chatroomfinalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.location.Location;
import android.location.Geocoder;
import android.location.Address;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class MessagingActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference loginRef;
    private DatabaseReference messageRef;
    private TextView welcome;
    private TextView onlineUsers;
    private String username;
    private Button logout;
    private Button onlineUserActivity;
    private EditText messageBox;
    private ImageButton sendButton;
    public TreeSet<String> users;
    private Location loc;
    private Geocoder geocoder;
    public static double lat;
    public static double longi;
    private FusedLocationProviderClient fusedLocationClient;
    public static long lastMessage = 0;

    View.OnClickListener sendMessage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = messageBox.getText().toString();
            if(text.length() > 0){
                Map<String, Object> updates = new HashMap<String, Object>();
                DatabaseReference newRef = messageRef.child("lastMessage");
                updates.put("username", username);
                updates.put("message",messageBox.getText().toString());
                updates.put("id", System.currentTimeMillis());
                newRef.updateChildren(updates);
                messageBox.setText("");
            }
        }
    };

    View.OnClickListener online = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), OnlineUserActivity.class);
            intent.putExtra(getString(R.string.username_key), username);
            startActivity(intent);
        }
    };

    ValueEventListener changeOnline = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot ds: dataSnapshot.getChildren()){
                if(users.contains(ds.getKey()) && ds.child("online").getValue().toString().equals("false")){
                    users.remove(ds.getKey());
                }else if(!users.contains(ds.getKey()) && ds.child("online").getValue().toString().equals("true")){
                    users.add(ds.getKey());
                }
            }
            //onlineUsers.setText(getString(R.string.online, users.toString().substring(1, users.toString().length()-1)));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void updateLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Map<String, Object> updates = new HashMap<String, Object>();
                            lat = location.getLatitude();
                            longi = location.getLongitude();
                            updates.put("latitude", lat);
                            updates.put("longitude", longi);
                            try {
                                Address address = geocoder.getFromLocation(lat, longi, 1).get(0);
                                String city = address.getLocality();
                                String country = address.getCountryName();
                                updates.put("city", city);
                                updates.put("country", country);
                                loginRef.child(username).updateChildren(updates);
                            }catch(java.io.IOException e){
                                updates.put("city", "null");
                                updates.put("country", "null");
                                loginRef.child(username).updateChildren(updates);
                            }
                        }
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopService(new Intent(getApplicationContext(), NotificationService.class));
        setContentView(R.layout.activity_messaging);
        welcome = (TextView)findViewById(R.id.welcome);
        onlineUsers = (TextView)findViewById(R.id.online);
        logout = (Button)findViewById(R.id.logout);
        messageBox = (EditText)findViewById(R.id.message);
        sendButton = (ImageButton)findViewById(R.id.sendButton);
        onlineUserActivity = (Button)findViewById(R.id.online);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(getApplicationContext());

        Intent intent = getIntent();

        users = new TreeSet<String>();

        username = intent.getStringExtra(getString(R.string.username_key));

        welcome.setText(getString(R.string.welcome, username));

        database = FirebaseDatabase.getInstance();

        loginRef = database.getReference("users");

        messageRef = database.getReference("messages");

        getOnlineUsers();

        sendButton.setOnClickListener(sendMessage);

        onlineUserActivity.setOnClickListener(online);

        loginRef.addValueEventListener(changeOnline);

        updateLastLocation();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> updates = new HashMap<String, Object>();
                DatabaseReference newRef = loginRef.child(username).getRef();
                updates.put("online","false");
                newRef.updateChildren(updates);
                finish();
            }
        });

    }

    @Override
    protected void onStop(){
        Map<String, Object> updates = new HashMap<String, Object>();
        DatabaseReference newRef = loginRef.child(username).getRef();
        updates.put("online","false");
        newRef.updateChildren(updates);
        super.onStop();
        Intent send = new Intent(getApplicationContext(), NotificationService.class);
        send.putExtra("id",lastMessage);
        startService(send);
    }

    @Override
    protected void onStart(){
        Map<String, Object> updates = new HashMap<String, Object>();
        DatabaseReference newRef = loginRef.child(username).getRef();
        updates.put("online","true");
        newRef.updateChildren(updates);
        stopService(new Intent(getApplicationContext(), NotificationService.class));
        super.onStart();
    }


    private void getOnlineUsers(){
        loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.child("online").getValue().toString().equals("true")){
                        users.add(ds.getKey());
                    }
                }
                //onlineUsers.setText(getString(R.string.online, users.toString().substring(1, users.toString().length()-1)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
