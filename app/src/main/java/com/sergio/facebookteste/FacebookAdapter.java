package com.sergio.facebookteste;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Login;
import com.sergio.facebookteste.Repository.UsersFacebookRep;

import java.util.List;

public class FacebookAdapter extends RecyclerView.Adapter<FacebookAdapter.FacebookViewHolder> {

    List<Login> users;
    Context ctx;
    DatabaseHelper db;
    UsersFacebookRep usersFacebookRep;
    @Override
    public FacebookAdapter.FacebookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        FacebookAdapter.FacebookViewHolder avh = new FacebookAdapter.FacebookViewHolder(v, ctx);
        return avh;
    }

    @Override
    public void onBindViewHolder(FacebookAdapter.FacebookViewHolder holder, int position) {
        holder.idFacebook = users.get(position).getIdFacebook();
        holder.nome.setText(users.get(position).getNome());
        holder.img.setImageUrl(users.get(position).getUrl(), VolleySingleton.getmInstance(ctx).getImageLoader());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public FacebookAdapter(List<Login> users, Context ctx) {
        this.users = users;
        this.ctx = ctx;
    }

    public class FacebookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context ctx;
        String idFacebook;
        TextView nome;
        ImageButton remove, edit;
        NetworkImageView img;

        FacebookViewHolder(View itemView, Context ctx) {
            super(itemView);
            this.ctx = ctx;
            nome = (TextView) itemView.findViewById(R.id.nomeView);
            edit = (ImageButton) itemView.findViewById(R.id.edit);
            remove = (ImageButton) itemView.findViewById(R.id.remove);
            img = (NetworkImageView) itemView.findViewById(R.id.imageView);
            edit.setVisibility(View.GONE);
            itemView.setOnClickListener(this);
            remove.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.remove:
                    removeFacebookUser(getPosition(), idFacebook);
                    break;
                default:
                    Intent intentDetails = new Intent(ctx, FacebookUsersDetails.class);
                    intentDetails.putExtra("idFacebook", idFacebook); //nenhum utilizador tem o mesmo idFacebook
                    ctx.startActivity(intentDetails);
            }
        }
    }

    private void removeFacebookUser(int position, String idFacebook){
        db = new DatabaseHelper(ctx);
        usersFacebookRep= new UsersFacebookRep(db.openConnection());
        users.remove(position);
        notifyItemRemoved(position);
        usersFacebookRep.removeFacebookUser(idFacebook);
    }

}
