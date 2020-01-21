package trap1.bhaleraoomkar.chatroomfinalproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationService extends Service {
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    private FirebaseDatabase database;
    private DatabaseReference loginRef;
    private DatabaseReference messageRef;
    private long lastMessage;

    ValueEventListener messageNotification = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.child("lastMessage/username").getValue() != null && dataSnapshot.child("lastMessage/message").getValue() != null) {
                String user = dataSnapshot.child("lastMessage/username").getValue().toString();
                String message = dataSnapshot.child("lastMessage/message").getValue().toString();
                long id = (Long)dataSnapshot.child("lastMessage/id").getValue();
                if(id != lastMessage) {
                    createNotification(user);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    @Override
    public IBinder onBind (Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        lastMessage = intent.getLongExtra("id", 0);
        return START_STICKY;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        database = FirebaseDatabase.getInstance();
        messageRef = database.getReference("messages");
        messageRef.addValueEventListener(messageNotification);

    }

    @Override
    public void onDestroy(){
        messageRef.removeEventListener(messageNotification);
        super.onDestroy();
    }

    private void createNotification(String name) {
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),default_notification_channel_id);
        mBuilder.setContentTitle("New Message");
        mBuilder.setContentText("from " + name);
        mBuilder.setSmallIcon(R.drawable. ic_launcher_foreground);
        mBuilder.setAutoCancel(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME",importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int)System.currentTimeMillis(), mBuilder.build());
    }



}
