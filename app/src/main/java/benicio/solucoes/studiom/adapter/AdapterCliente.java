package benicio.solucoes.studiom.adapter;

import android.annotation.SuppressLint;
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

import java.util.List;

import benicio.solucoes.studiom.R;
import benicio.solucoes.studiom.databinding.LayoutCadastroAgendamentoBinding;
import benicio.solucoes.studiom.models.ClienteModel;
import benicio.solucoes.studiom.models.UserModel;

public class AdapterCliente extends RecyclerView.Adapter<AdapterCliente.MyViewHolder>{

    private DatabaseReference refClientes = FirebaseDatabase.getInstance().getReference().child("clientes");
    List<ClienteModel> clientes;
    Activity a;
    Dialog dialogCarregando;

    boolean exibicao = false;
    Dialog dialogSelecao;

    LayoutCadastroAgendamentoBinding cadastroAgendamentoBinding;

    public AdapterCliente(List<ClienteModel> clientes, Activity a, Dialog dialogCarregando) {
        this.clientes = clientes;
        this.a = a;
        this.dialogCarregando = dialogCarregando;
    }

    public AdapterCliente(List<ClienteModel> clientes,
                          Activity a,
                          Dialog dialogCarregando,
                          boolean exibicao,
                          Dialog dialogSelecao,
                          LayoutCadastroAgendamentoBinding cadastroAgendamentoBinding
                          ) {
        this.clientes = clientes;
        this.a = a;
        this.dialogCarregando = dialogCarregando;
        this.exibicao = exibicao;
        this.dialogSelecao = dialogSelecao;
        this.cadastroAgendamentoBinding = cadastroAgendamentoBinding;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cliente, parent, false));
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ClienteModel cliente = clientes.get(position);
        holder.infoCliente.setText(cliente.getNome());

        holder.zapBtn.setOnClickListener( view -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(cliente.getLinkDoZap()));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            a.startActivity(i);
        });


        holder.removerBtn.setOnClickListener( view -> {
            clientes.remove(position);
            this.notifyDataSetChanged();
            dialogCarregando.show();
            refClientes.child(cliente.getId()).setValue(null).addOnCompleteListener(task -> {
                dialogCarregando.dismiss();
            });
        });

        if ( exibicao ){
            holder.removerBtn.setVisibility(View.GONE);
            holder.zapBtn.setVisibility(View.GONE);
            holder.selecionarBtn.setVisibility(View.VISIBLE);
        }

        holder.selecionarBtn.setOnClickListener(view -> {
            dialogSelecao.dismiss();
            cadastroAgendamentoBinding.textClienteSelecionado.setText("Cliente Selecionado:\n" + cliente.getNome() + "\n" + cliente.getId());
        });
    }

    @Override
    public int getItemCount() {
        return clientes.size();
    }

    public static class  MyViewHolder extends RecyclerView.ViewHolder {
        TextView infoCliente;
        Button removerBtn, zapBtn, selecionarBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            infoCliente = itemView.findViewById(R.id.textInfoNameCliente);
            removerBtn = itemView.findViewById(R.id.remove_cliente_btn);
            zapBtn = itemView.findViewById(R.id.chamarNoZap);
            selecionarBtn = itemView.findViewById(R.id.selecionar_cliente_btn);
        }
    }
}
