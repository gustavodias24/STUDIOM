package benicio.solucoes.studiom.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rejowan.cutetoast.CuteToast;

import java.util.List;

import benicio.solucoes.studiom.R;
import benicio.solucoes.studiom.models.AgendamentoModel;
import benicio.solucoes.studiom.models.ClienteModel;

public class AdapterAgendamento extends RecyclerView.Adapter<AdapterAgendamento.MyViewHolder>{

    private DatabaseReference refClientes = FirebaseDatabase.getInstance().getReference().child("clientes");
    private DatabaseReference refAgendamentos = FirebaseDatabase.getInstance().getReference().child("agendamentos");
    List<AgendamentoModel> agendamentos;
    Activity a;

    Dialog dialogCarregando;

    public AdapterAgendamento(List<AgendamentoModel> agendamentos, Activity a, Dialog dialogCarregando) {
        this.agendamentos = agendamentos;
        this.a = a;
        this.dialogCarregando = dialogCarregando;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_agendamento, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AgendamentoModel agendamento = agendamentos.get(position);

        String textAviso = "?text=Ol%C3%A1%2C+a+sua+aula+est%C3%A1+pr%C3%B3xima+de+come%C3%A7ar%2C+tudo+bem%3F";

        holder.chamarNoZap.setOnClickListener( view -> {
            dialogCarregando.show();

            refClientes.child(agendamento.getIdCliente()).get().addOnCompleteListener( task -> {
               dialogCarregando.dismiss();
               if ( task.isSuccessful() ){
                   ClienteModel cliente = task.getResult().getValue(ClienteModel.class);
                   Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(cliente.getLinkDoZap() + textAviso));
                   i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   a.startActivity(i);
               }
            });
        });

        holder.remover.setOnClickListener(view -> {
            dialogCarregando.show();
            refAgendamentos.child(agendamento.getId()).setValue(null).addOnCompleteListener(task -> {
                dialogCarregando.dismiss();
                CuteToast.ct(a, "Aula Removida", CuteToast.LENGTH_LONG, CuteToast.SUCCESS, true).show();
            });
        });

        holder.infos.setText(agendamento.toString());

    }

    @Override
    public int getItemCount() {
        return agendamentos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        Button chamarNoZap, remover, editar, realizarAula;
        TextView infos;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            chamarNoZap = itemView.findViewById(R.id.avisarAlunoZap);
            remover = itemView.findViewById(R.id.remove_agendamento_btn);
            editar = itemView.findViewById(R.id.editar_agendamento_btn);
            realizarAula = itemView.findViewById(R.id.selecionar_agendamento_btn);
            infos = itemView.findViewById(R.id.textInfoAgendamento);
        }
    }
}
