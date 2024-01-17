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
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rejowan.cutetoast.CuteToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import benicio.solucoes.studiom.adapter.AdapterAgendamento;
import benicio.solucoes.studiom.databinding.ActivityComissaoBinding;
import benicio.solucoes.studiom.databinding.ActivityHistoricoBinding;
import benicio.solucoes.studiom.databinding.LoadingScreenBinding;
import benicio.solucoes.studiom.models.AgendamentoModel;

public class HistoricoActivity extends AppCompatActivity {

    private ActivityHistoricoBinding mainBinding;
    private String idCliente;
    private Bundle b;

    private DatabaseReference refAgendamentos = FirebaseDatabase.getInstance().getReference().child("agendamentos");
    private RecyclerView recyclerAgendamento;
    private AdapterAgendamento adapterAgendamento;
    private List<AgendamentoModel> agendamentos = new ArrayList<>();
    private Dialog dialogCarregando;
    private SimpleDateFormat sdf ;
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityHistoricoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        sdf  = new SimpleDateFormat("dd-MM-yyyy");

        getSupportActionBar().setTitle("Histórico");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        b = getIntent().getExtras();
        idCliente = b.getString("idCliente", "");

        configurarDialogCarregando();
        configurarRecyclerAgendamento();
        procurarAgendamentos(null, null);

        mainBinding.filtrarBtn.setOnClickListener( view -> {
            try{
                Date dataInicial = sdf.parse(mainBinding.dataInicial.getText().toString());
                Date dataFinal = sdf.parse(mainBinding.dataFinal.getText().toString());

                procurarAgendamentos(dataInicial, dataFinal);
            }catch (Exception e){
                CuteToast.ct(this, "Insira Datas Válidas", CuteToast.LENGTH_LONG, CuteToast.ERROR, true).show();
            }
        });

    }

    @Override
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
    private void configurarRecyclerAgendamento(){
        recyclerAgendamento = mainBinding.recyclerAgendamentos;
        recyclerAgendamento.setLayoutManager(new LinearLayoutManager(this));
        recyclerAgendamento.setHasFixedSize(true);
        recyclerAgendamento.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterAgendamento = new AdapterAgendamento(agendamentos,this, dialogCarregando);
        recyclerAgendamento.setAdapter(adapterAgendamento);
    }

    private void procurarAgendamentos(Date dataInicial, Date dataFinal){
        dialogCarregando.show();
        refAgendamentos.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                agendamentos.clear();
                dialogCarregando.dismiss();
                if ( snapshot.exists() ){
                    for ( DataSnapshot dado : snapshot.getChildren() ){
                        AgendamentoModel agendamento = dado.getValue(AgendamentoModel.class);
                        if ( agendamento.getIdCliente().equals(idCliente)){
                            if ( dataFinal == null && dataInicial == null){
                                agendamentos.add(agendamento);
                            }else{
                                try{
                                    Date dataDoAgendamento = sdf.parse(agendamento.getData());
                                    if ( dataDoAgendamento.after(dataInicial) && dataDoAgendamento.before(dataFinal) ){
                                        agendamentos.add(agendamento);
                                    }
                                }catch(Exception ignored){}
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