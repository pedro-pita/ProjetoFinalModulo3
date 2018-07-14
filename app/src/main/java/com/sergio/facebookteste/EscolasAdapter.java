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
import com.facebook.AccessToken;
import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Escola;
import com.sergio.facebookteste.Model.Session;
import com.sergio.facebookteste.Repository.EscolasRep;
import com.sergio.facebookteste.Repository.UsersLocalRep;
import com.sergio.facebookteste.geofence.navigation.NavigationActivity;

import java.util.List;

public class EscolasAdapter extends RecyclerView.Adapter<EscolasAdapter.EscolasViewHolder> {

    List<Escola> escolas;
    Context ctx;
    DatabaseHelper db;
    EscolasRep escolasRep;
    Session ss;
    UsersLocalRep usersLocalRep;

    @Override
    public EscolasAdapter.EscolasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        EscolasViewHolder avh = new EscolasViewHolder(v, ctx);
        return avh;
    }

    @Override
    public void onBindViewHolder(EscolasAdapter.EscolasViewHolder holder, int position) {
        holder.nome.setText(escolas.get(position).getNome());
        holder.img.setImageUrl(escolas.get(position).getImagem(), VolleySingleton.getmInstance(ctx).getImageLoader());
    }

    @Override
    public int getItemCount() {
        return escolas.size();
    }

    public EscolasAdapter(List<Escola> users, Context ctx) {
        this.escolas = users;
        this.ctx = ctx;
    }

    public class EscolasViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context ctx;
        TextView id, login, nome;
        ImageButton remove, edit;
        NetworkImageView img;

        EscolasViewHolder(View itemView, Context ctx) {
            super(itemView);
            this.ctx = ctx;
            nome = (TextView) itemView.findViewById(R.id.nomeView);
            edit = (ImageButton) itemView.findViewById(R.id.edit);
            remove = (ImageButton) itemView.findViewById(R.id.remove);
            img = (NetworkImageView) itemView.findViewById(R.id.imageView);
            if(!(verificarPermissões())){
                edit.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
            remove.setOnClickListener(this);
            edit.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String nomeToUse = nome.getText().toString();
            switch (view.getId()) {
                case R.id.remove:
                    removeEscola(getPosition(), nomeToUse);
                    break;
                case R.id.edit:
                    redirectToEdit(nomeToUse);
                    break;
                default:
                    Intent intentMap = new Intent(ctx, NavigationActivity.class);
                    intentMap.putExtra("nome", nome.getText().toString()); //nenhuma escola tem o mesmo nome
                    ctx.startActivity(intentMap);
            }
        }
    }

    private void removeEscola(int position, String nome){
        db = new DatabaseHelper(ctx);
        escolasRep = new EscolasRep(db.openConnection());
        escolas.remove(position);
        notifyItemRemoved(position);
        escolasRep.removeEscola(nome);
    }

    private void redirectToEdit(String nome){
        Intent intentEdit = new Intent(ctx, EscolaEdit.class);
        intentEdit.putExtra("escola", nome);
        ctx.startActivity(intentEdit);
    }
    private boolean verificarPermissões(){
        //verificar isLoggedin se sim adicionar menu logout se não faz o try catch, se der certo apresenta logout se não apresenta edit
        ss = new Session(ctx);
        db = new DatabaseHelper(ctx);
        usersLocalRep = new UsersLocalRep(db.openConnection());
        if(!(isLoggedIn())){
            try {
                if((usersLocalRep.getLocalUser(ss.getLogin()).getLevel().equals("1")) && (!(usersLocalRep.getLocalUser(ss.getLogin()).getLevel().equals("2")))){
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }else{
            return false;
        }
        return false;
    }
    private boolean isLoggedIn(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        return isLoggedIn;
    }
}

