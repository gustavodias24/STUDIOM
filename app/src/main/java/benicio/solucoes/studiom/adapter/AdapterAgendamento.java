package benicio.solucoes.studiom.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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

import benicio.solucoes.studiom.CadastroActivity;
import benicio.solucoes.studiom.R;
import benicio.solucoes.studiom.databinding.LayoutCadastroAgendamentoBinding;
import benicio.solucoes.studiom.models.AgendamentoModel;
import benicio.solucoes.studiom.models.ClienteModel;

public class AdapterAgendamento extends RecyclerView.Adapter<AdapterAgendamento.MyViewHolder>{

    private DatabaseReference refClientes = FirebaseDatabase.getInstance().getReference().child("clientes");
    private DatabaseReference refAgendamentos = FirebaseDatabase.getInstance().getReference().child("agendamentos");
    List<AgendamentoModel> agendamentos;
    Activity a;

    Dialog dialogCarregando;
    Dialog dialogEdicao = null;

    boolean paraFuncionario = false;

    String idFuncionario;


    public AdapterAgendamento(List<AgendamentoModel> agendamentos, Activity a, Dialog dialogCarregando) {
        this.agendamentos = agendamentos;
        this.a = a;
        this.dialogCarregando = dialogCarregando;
    }

    public AdapterAgendamento(List<AgendamentoModel> agendamentos,
                              Activity a,
                              Dialog dialogCarregando,
                              boolean paraFuncionario,
                              String idFuncionario) {
        this.agendamentos = agendamentos;
        this.a = a;
        this.dialogCarregando = dialogCarregando;
        this.paraFuncionario = paraFuncionario;
        this.idFuncionario = idFuncionario;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_agendamento, parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AgendamentoModel agendamento = agendamentos.get(position);

        if (agendamento.getStatus() == 0){
            holder.chamarNoZap.setVisibility(View.VISIBLE);
            holder.editar.setVisibility(View.VISIBLE);
            holder.realizarAula.setVisibility(View.VISIBLE);

            holder.concluir.setVisibility(View.GONE);
            holder.desmarcar.setVisibility(View.GONE);
        }else if ( agendamento.getStatus() == 1){
            holder.chamarNoZap.setVisibility(View.VISIBLE);
            holder.editar.setVisibility(View.VISIBLE);
            holder.concluir.setVisibility(View.VISIBLE);
            holder.desmarcar.setVisibility(View.VISIBLE);

            holder.realizarAula.setVisibility(View.GONE);
        }else if ( agendamento.getStatus() == 2){
            holder.chamarNoZap.setVisibility(View.VISIBLE);
            holder.editar.setVisibility(View.VISIBLE);

            holder.concluir.setVisibility(View.GONE);
            holder.desmarcar.setVisibility(View.GONE);
            holder.realizarAula.setVisibility(View.GONE);
        }

        if ( !paraFuncionario ){
            holder.chamarNoZap.setVisibility(View.VISIBLE);
            holder.editar.setVisibility(View.VISIBLE);
            holder.remover.setVisibility(View.VISIBLE);

            holder.concluir.setVisibility(View.GONE);
            holder.desmarcar.setVisibility(View.GONE);
            holder.realizarAula.setVisibility(View.GONE);
        }else{
            holder.remover.setVisibility(View.GONE);
        }

        if ( agendamento.getStatus() == 1){
            if (agendamento.getIdProfessor().equals(idFuncionario)){
                holder.desmarcar.setOnClickListener(view -> {
                    agendamento.setStatus(0);
                    agendamento.setIdProfessor("");
                    dialogCarregando.show();

                    refAgendamentos.child(agendamento.getId()).setValue(agendamento).addOnCompleteListener(
                            task -> {
                                dialogCarregando.dismiss();
                                if ( task.isSuccessful() ){
                                    this.notifyDataSetChanged();
                                    CuteToast.ct(a, "Aula Desmarcada", CuteToast.LENGTH_LONG, CuteToast.SUCCESS, true).show();
                                }
                            }
                    );
                });

                holder.concluir.setOnClickListener(view -> {
                    agendamento.setStatus(2);
                    dialogCarregando.show();
                    refAgendamentos.child(agendamento.getId()).setValue(agendamento).addOnCompleteListener(
                            task -> {
                                dialogCarregando.dismiss();
                                if ( task.isSuccessful() ){
                                    CuteToast.ct(a, "Aula Concluída", CuteToast.LENGTH_LONG, CuteToast.SUCCESS, true).show();
                                }
                            }
                    );
                });
            }else{
                holder.desmarcar.setVisibility(View.GONE);
                holder.concluir.setVisibility(View.GONE);
            }
        }



        holder.realizarAula.setOnClickListener( view -> {
            dialogCarregando.show();
            agendamento.setIdProfessor(idFuncionario);
            agendamento.setStatus(1);
            refAgendamentos.child(agendamento.getId()).setValue(agendamento).addOnCompleteListener(
                    task -> {
                        dialogCarregando.dismiss();
                        if ( task.isSuccessful() ){
                            CuteToast.ct(a, "Aula Agendada com Você", CuteToast.LENGTH_LONG, CuteToast.SUCCESS, true).show();
                        }
                    }
            );
        });

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
                this.notifyDataSetChanged();
            });
        });

        holder.infos.setText(agendamento.toString());


        holder.editar.setOnClickListener( view -> {

            AlertDialog.Builder b = new AlertDialog.Builder(a);
            LayoutCadastroAgendamentoBinding cadastroAgendamentoBinding = LayoutCadastroAgendamentoBinding.inflate(a.getLayoutInflater());

            cadastroAgendamentoBinding.selecionarCliente.setVisibility(View.GONE);
            cadastroAgendamentoBinding.textClienteSelecionado.setVisibility(View.GONE);
            cadastroAgendamentoBinding.cadastrarAgendamento.setText("ATUALIZAR");

            cadastroAgendamentoBinding.dataAgendamento.setText(agendamento.getData());

            if ( !agendamento.getHora().isEmpty() ){

                String isAm = agendamento.getHora().split(" ")[1];
                cadastroAgendamentoBinding.horaAgendamento.setText(agendamento.getHora().split(" ")[0]);

                if ( isAm.equals("AM")){
                    cadastroAgendamentoBinding.radioAM.setChecked(true);
                }else{
                    cadastroAgendamentoBinding.radioPM.setChecked(true);
                }
            }

            cadastroAgendamentoBinding.radio12.setVisibility(View.GONE);
            cadastroAgendamentoBinding.radio8.setVisibility(View.GONE);
            cadastroAgendamentoBinding.textView.setVisibility(View.GONE);

            cadastroAgendamentoBinding.cadastrarAgendamento.setOnClickListener(atualizarView -> {
                dialogCarregando.show();
                agendamento.setData(
                        cadastroAgendamentoBinding.dataAgendamento.getText().toString()
                );

                agendamento.setHora(
                        cadastroAgendamentoBinding.horaAgendamento.getText().toString() + " " +
                        (cadastroAgendamentoBinding.radioAM.isChecked() ? "AM" : "PM")
                );

                refAgendamentos.child(agendamento.getId()).setValue(agendamento).addOnCompleteListener( task ->{
                    if ( task.isSuccessful() ){
                        CuteToast.ct(a, "Atualizado com Sucesso", CuteToast.LENGTH_LONG, CuteToast.SUCCESS, true).show();
                        dialogEdicao.dismiss();
                    }

                    dialogCarregando.dismiss();
                });

            });
            b.setView(cadastroAgendamentoBinding.getRoot());
            dialogEdicao = b.create();
            dialogEdicao.show();
        });

    }

    @Override
    public int getItemCount() {
        return agendamentos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        Button chamarNoZap, remover, editar, realizarAula, concluir, desmarcar;
        TextView infos;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            chamarNoZap = itemView.findViewById(R.id.avisarAlunoZap);
            remover = itemView.findViewById(R.id.remove_agendamento_btn);
            editar = itemView.findViewById(R.id.editar_agendamento_btn);
            realizarAula = itemView.findViewById(R.id.selecionar_agendamento_btn);
            infos = itemView.findViewById(R.id.textInfoAgendamento);
            concluir = itemView.findViewById(R.id.concluir_agendamento_btn);
            desmarcar = itemView.findViewById(R.id.desmarcar_agendamento_btn);

        }
    }
}
