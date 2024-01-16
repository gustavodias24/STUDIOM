package benicio.solucoes.studiom.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import benicio.solucoes.studiom.ComissaoActivity;
import benicio.solucoes.studiom.R;
import benicio.solucoes.studiom.models.UserModel;

public class AdapterGeneric extends RecyclerView.Adapter<AdapterGeneric.MyViewHolder> {

    private DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("users");
    List<UserModel> funcionarios;
    Activity a;
    Dialog dialogCarregando;

    public AdapterGeneric(List<UserModel> funcionarios, Activity a, Dialog dialogCarregando) {
        this.funcionarios = funcionarios;
        this.a = a;
        this.dialogCarregando = dialogCarregando;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_generic, parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserModel funcionario = funcionarios.get(position);

        holder.info.setText(
                funcionario.toString()
        );

        holder.removerButton.setOnClickListener(view -> {
            dialogCarregando.show();
            funcionarios.remove(position);
            this.notifyDataSetChanged();
            refUser.child(funcionario.getId()).setValue(null).addOnCompleteListener( task -> {
                dialogCarregando.dismiss();
            });
        });

        holder.agendamentos.setOnClickListener(view -> {
            Intent i = new Intent(a, ComissaoActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            a.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return funcionarios.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView info;
        Button removerButton, agendamentos;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            info = itemView.findViewById(R.id.textInfoGeneric);
            removerButton = itemView.findViewById(R.id.remove_item_btn);
            agendamentos = itemView.findViewById(R.id.agendamentos_comissoes_btn2);
        }
    }
}
