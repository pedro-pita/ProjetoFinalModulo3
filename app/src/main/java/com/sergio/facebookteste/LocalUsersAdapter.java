package com.sergio.facebookteste;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Login;
import com.sergio.facebookteste.Repository.UsersLocalRep;

import java.util.List;

public class LocalUsersAdapter extends RecyclerView.Adapter<LocalUsersAdapter.LocalUsersViewHolder> {

    List<Login> users;
    Context ctx;
    DatabaseHelper db;
    UsersLocalRep usersLocalRep;
    Login user;

    @Override
    public LocalUsersAdapter.LocalUsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        LocalUsersAdapter.LocalUsersViewHolder avh = new LocalUsersAdapter.LocalUsersViewHolder(v, ctx);
        return avh;
    }

    @Override
    public void onBindViewHolder(LocalUsersAdapter.LocalUsersViewHolder holder, int position) {
        holder.login.setText(users.get(position).getLogin());
        holder.imageView.setImageUrl("https://www.aho.org.af/wp-content/uploads/2015/06/profile-2092113_960_720.png",VolleySingleton.getmInstance(ctx).getImageLoader());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public LocalUsersAdapter(List<Login> users, Context ctx) {
        this.users = users;
        this.ctx = ctx;
    }

    public class LocalUsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context ctx;
        TextView id, login;
        ImageButton remove, edit;
        NetworkImageView imageView;
        LocalUsersViewHolder(View itemView, Context ctx) {
            super(itemView);
            this.ctx = ctx;
            login = (TextView) itemView.findViewById(R.id.nomeView);
            edit = (ImageButton) itemView.findViewById(R.id.edit);
            remove = (ImageButton) itemView.findViewById(R.id.remove);
            imageView = (NetworkImageView) itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
            edit.setOnClickListener(this);
            remove.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String loginToUse = login.getText().toString();
            switch (view.getId()) {
                case R.id.remove:
                    removeUser(getPosition(), loginToUse);
                    break;
                case R.id.edit:
                    redirectToEdit(loginToUse);
                    break;
                default:
                    Intent intentDetails = new Intent(ctx, LocalUsersDetails.class);
                    intentDetails.putExtra("login", login.getText().toString()); //nenhum user tem o mesmo login
                    ctx.startActivity(intentDetails);
            }
        }
    }

    private void removeUser(int position, String login){
        db = new DatabaseHelper(ctx);
        usersLocalRep = new UsersLocalRep(db.openConnection());
        user = usersLocalRep.getLocalUser(login);

        if(user.getLevel().equals("1")){
            displayToast("Desculpe mas este utilizador não pode ser removido");
        }else{
            users.remove(position);
            notifyItemRemoved(position);
            usersLocalRep.removeLocalUser(login);
        }
    }

    private void redirectToEdit(String login){
        Intent intentEdit = new Intent(ctx, LocalUsersEdit.class);
        intentEdit.putExtra("user", login);
        ctx.startActivity(intentEdit);
    }
    private void displayToast(String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}


