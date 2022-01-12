package com.example.saysomethings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.saysomethings.Adapter.MessageAdapter;
import com.example.saysomethings.Fragments.ChatFragment;
import com.example.saysomethings.Fragments.UserFragment;
import com.example.saysomethings.Model.Chat;
import com.example.saysomethings.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PrimitiveIterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessActivity extends AppCompatActivity {

    CircleImageView image_user;
    TextView username_user;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    Intent intent;

    ImageButton button_send;
    EditText text_send;
    RecyclerView recyclerview_mess;
    String userid;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess);
        init();
        setFirebaseUser();
        //
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Mess"); //Thiết lập tiêu đề nếu muốn
        setButton_send();
        //

    }
    public void init()
    {
        image_user= findViewById(R.id.image_user);
        username_user = findViewById(R.id.username_user);
        button_send=findViewById(R.id.button_send);
        text_send=findViewById(R.id.text_send);
        recyclerview_mess=findViewById(R.id.recyclerview_mess);
        recyclerview_mess.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerview_mess.setLayoutManager(linearLayoutManager);
    }
    public void setFirebaseUser()
    {
        intent=getIntent();
        userid = intent.getStringExtra("userid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username_user.setText(user.getUsername());
                if(user.getImageurl().equals("default"))
                {
                    image_user.setImageResource(R.mipmap.ic_launcher);
                }else
                {
                    Glide.with(MessActivity.this).load(user.getImageurl()).into(image_user);
                }
                readMess(firebaseUser.getUid(),userid,user.getImageurl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        seenMess(userid);
    }
    private void sendMess(String sender, String receiver,String mess)
    {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dft = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String time = dtf.format(java.time.LocalTime.now());
        String date = dft.format(java.time.LocalDate.now());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("mess",mess);
        hashMap.put("date",date);
        hashMap.put("time",time);
        hashMap.put("isseen",false);
        reference.child("Chats").push().setValue(hashMap);

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userid);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    chatRef.child("id").setValue(userid);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void seenMess(String userid)
    {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren())
                {
                    Chat chat = snap.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) &&
                    chat.getSender().equals(userid))
                    {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snap.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void setButton_send()
    {
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = text_send.getText().toString();
                if(!msg.equals(""))
                {
                    sendMess(firebaseUser.getUid(),userid,msg);
                }
                else
                {
                    Toast.makeText(MessActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });
    }
    private void readMess(final String myid , final String userid , final String imageurl)
    {
        mChat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot snap : snapshot.getChildren())
                {
                    Chat chat = snap.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)
                    )
                    {
                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessActivity.this,mChat,imageurl);
                    recyclerview_mess.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void status(String status)
    {
        databaseReference =FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(seenListener);
        status("offline");
    }
}