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
import com.sergio.facebookteste.Model.Escola;
import com.sergio.facebookteste.Repository.UsersLocalRep;
import com.sergio.facebookteste.geofence.navigation.NavigationActivity;

import java.util.List;

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.FavoritosViewHolder> {

    List<Escola> escolas;
    Context ctx;
    DatabaseHelper db;
    UsersLocalRep usersLocalRep;

    @Override
    public FavoritosAdapter.FavoritosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        FavoritosViewHolder avh = new FavoritosViewHolder(v, ctx);
        return avh;
    }

    @Override
    public void onBindViewHolder(FavoritosAdapter.FavoritosViewHolder holder, int position) {
        holder.nome.setText(String.valueOf(escolas.get(position).getNome()));
        holder.img.setImageUrl(escolas.get(position).getImagem(), VolleySingleton.getmInstance(ctx).getImageLoader());
    }

    @Override
    public int getItemCount() {
        return escolas.size();
    }

    public FavoritosAdapter(List<Escola> users, Context ctx) {
        this.escolas = users;
        this.ctx = ctx;
    }

    public class FavoritosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context ctx;
        TextView id, login, nome;
        ImageButton remove, edit;
        NetworkImageView img;

        FavoritosViewHolder(View itemView, Context ctx) {
            super(itemView);
            this.ctx = ctx;
            nome = (TextView) itemView.findViewById(R.id.nomeView);
            edit = (ImageButton) itemView.findViewById(R.id.edit);
            remove = (ImageButton) itemView.findViewById(R.id.remove);
            img = (NetworkImageView) itemView.findViewById(R.id.imageView);
            edit.setVisibility(View.GONE);
            remove.setVisibility(View.GONE);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                default:
                    Intent intentMap = new Intent(ctx, NavigationActivity.class);
                    intentMap.putExtra("nome", nome.getText().toString());
                    ctx.startActivity(intentMap);
            }
        }
    }
}

