package benicio.solucoes.studiom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.studiom.adapter.AdapterAgendamento;
import benicio.solucoes.studiom.databinding.ActivityAgendamentosBinding;
import benicio.solucoes.studiom.databinding.ActivityComissaoBinding;
import benicio.solucoes.studiom.databinding.LoadingScreenBinding;
import benicio.solucoes.studiom.models.AgendamentoModel;

public class AgendamentosActivity extends AppCompatActivity {

    private DatabaseReference refAgendamentos = FirebaseDatabase.getInstance().getReference().child("agendamentos");
    private ActivityAgendamentosBinding mainBinding;
    private RecyclerView recyclerAgendamento;
    private AdapterAgendamento adapterAgendamento;
    private List<AgendamentoModel> agendamentos = new ArrayList<>();
    private Dialog dialogCarregando;
    private Bundle b;
    private int t;
    private String idFuncionario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityAgendamentosBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        b = getIntent().getExtras();
        idFuncionario = b.getString("idFuncionario", "");
        t = b.getInt("t", 0);

        getSupportActionBar().setTitle("Agendamentos");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configurarDialogCarregando();
        configurarRecyclerAgendamento();
        configurarListenerAgendamento();

    }

    private void configurarRecyclerAgendamento(){
        recyclerAgendamento = mainBinding.recyclerAgendamentos;
        recyclerAgendamento.setLayoutManager(new LinearLayoutManager(this));
        recyclerAgendamento.setHasFixedSize(true);
        recyclerAgendamento.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterAgendamento = new AdapterAgendamento(agendamentos,this, dialogCarregando, true, idFuncionario);
        recyclerAgendamento.setAdapter(adapterAgendamento);
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void configurarDialogCarregando() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        LoadingScreenBinding dialogBinding = LoadingScreenBinding.inflate(getLayoutInflater());
        dialogCarregando = b.setView(dialogBinding.getRoot()).create();
    }

    private void configurarListenerAgendamento(){
        dialogCarregando.show();
        refAgendamentos.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                agendamentos.clear();
                dialogCarregando.dismiss();
                if ( snapshot.exists() ){
                    for ( DataSnapshot dado : snapshot.getChildren() ){
                        AgendamentoModel agendamento = dado.getValue(AgendamentoModel.class);

                        if (t == 0) {
                            if ( agendamento.getStatus() == 0){
                                agendamentos.add(agendamento);
                            }
                        }else{
                            if ( agendamento.getStatus() != 0){
                                if ( agendamento.getIdProfessor().equals(idFuncionario)){
                                    agendamentos.add(agendamento);
                                }
                            }
                        }

                    }
                    adapterAgendamento.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogCarregando.dismiss();
            }
        });
    }
}