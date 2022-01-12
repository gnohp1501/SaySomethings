package com.example.saysomethings.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.saysomethings.MainChatActivity;
import com.example.saysomethings.MessActivity;
import com.example.saysomethings.Model.Chat;
import com.example.saysomethings.Model.User;
import com.example.saysomethings.R;
import com.google.firebase.auth.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;
    String thelastmess;

    public UserAdapter(Context mContext, List<User> mUsers,boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        if(user.getImageurl().equals("default"))
        {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }else
        {
            Glide.with(mContext).load(user.getImageurl()).into(holder.imageView);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessActivity.class);
                intent.putExtra("userid",user.getId());
                mContext.startActivity(intent);
            }
        });
        if(isChat)
        {
            if(user.getStatus().equals("online"))
            {
                holder.status_on.setVisibility(View.VISIBLE);
                holder.status_off.setVisibility(View.GONE);
            }
            else
            {
                holder.status_on.setVisibility(View.GONE);
                holder.status_off.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            holder.status_on.setVisibility(View.GONE);
            holder.status_off.setVisibility(View.GONE);
        }
        if(isChat)
        {
            lastMess(user.getId(), holder.last_mess);
        }
        else
        {
            holder.last_mess.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView imageView;
        public CircleImageView status_on;
        public CircleImageView status_off;
        public TextView last_mess;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.listusername_user);
            imageView=itemView.findViewById(R.id.listimage_user);
            status_on=itemView.findViewById(R.id.status_on);
            status_off=itemView.findViewById(R.id.status_off);
            last_mess = itemView.findViewById(R.id.last_mess);
        }
    }
    public void lastMess(String userid,TextView last_mess)
    {
        thelastmess = "default";
        final FirebaseUser  firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren())
                {
                    Chat chat = snap.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)
                    ||chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid()))
                    {
                        thelastmess = chat.getMess();
                    }
                }
                switch (thelastmess)
                {
                    case "default":
                        last_mess.setText("No message !");
                        break;
                    default:
                        last_mess.setText(thelastmess);
                        break;
                }
                thelastmess = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
