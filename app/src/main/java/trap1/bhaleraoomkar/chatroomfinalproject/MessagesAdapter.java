package trap1.bhaleraoomkar.chatroomfinalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<Message> messagesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView user;
        public TextView message;

        public MyViewHolder(View view) {
            super(view);
            user = (TextView) view.findViewById(R.id.name);
            message = (TextView) view.findViewById(R.id.userMessage);
        }
    }


    public MessagesAdapter(List<Message> messagesList) {
        this.messagesList = messagesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message m = messagesList.get(position);
        holder.user.setText(m.getName());
        holder.message.setText(m.getMessage());
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}