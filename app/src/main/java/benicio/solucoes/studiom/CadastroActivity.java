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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rejowan.cutetoast.CuteToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import benicio.solucoes.studiom.adapter.AdapterCliente;
import benicio.solucoes.studiom.adapter.AdapterGeneric;
import benicio.solucoes.studiom.databinding.ActivityCadastroBinding;
import benicio.solucoes.studiom.databinding.LayoutCadastroAgendamentoBinding;
import benicio.solucoes.studiom.databinding.LayoutCadastroClienteBinding;
import benicio.solucoes.studiom.databinding.LayoutCadastroFuncionarioBinding;
import benicio.solucoes.studiom.databinding.LayoutExibirClientesBinding;
import benicio.solucoes.studiom.databinding.LoadingScreenBinding;
import benicio.solucoes.studiom.models.AgendamentoModel;
import benicio.solucoes.studiom.models.ClienteModel;
import benicio.solucoes.studiom.models.UserModel;

public class CadastroActivity extends AppCompatActivity {
    private List<UserModel> funcionarios = new ArrayList<>();
    private List<ClienteModel> clientes = new ArrayList<>();
    private RecyclerView recyclerCadastro;
    private AdapterGeneric adapterGeneric;
    private AdapterCliente adapterCliente;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("users");
    private DatabaseReference refClientes = FirebaseDatabase.getInstance().getReference().child("clientes");
    private DatabaseReference refAgendamentos = FirebaseDatabase.getInstance().getReference().child("agendamentos");
    private ActivityCadastroBinding mainBinding;
    private Bundle bundle;
    private Dialog dialogCadastro, dialogCarregando;
    private int t; // tipo de listagem

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Cadastro");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configurarDialogCarregando();

        recyclerCadastro = mainBinding.recyclerCadastro;
        recyclerCadastro.setLayoutManager(new LinearLayoutManager(this));
        recyclerCadastro.setHasFixedSize(true);
        recyclerCadastro.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        bundle = getIntent().getExtras();
        t = bundle.getInt("t", 1);

        switch (t){
            case 1:
                configurarDialogCadastroCliente();
                configurarRecyclerCliente();
                configurarListenerCliente();
                break;
            case 2:
                configurarDialogCadastroFuncionario();
                configurarRecyclerFuncionario();
                configurarListenerUsuario();
                break;
            default:
                configurarDialogCadastroAgendamento();

        }

        mainBinding.fabCadastro.setOnClickListener(view -> dialogCadastro.show());

    }

    private void configurarDialogCadastroCliente(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Cadastro de cliente");
        LayoutCadastroClienteBinding cadastroClienteBinding = LayoutCadastroClienteBinding.inflate(getLayoutInflater());

        cadastroClienteBinding.button.setOnClickListener( view -> {
            dialogCarregando.show();
            String nome, numero;
            String linkZap;

            nome = cadastroClienteBinding.nomeClienteField.getText().toString();
            numero = cadastroClienteBinding.whatsappField.getText().toString();
            linkZap = "https://wa.me/"  + numero;
            String id = UUID.randomUUID().toString();

            ClienteModel novoCliente = new ClienteModel(
                    id, nome, numero, linkZap
            );

            refClientes.child(id).setValue(novoCliente).addOnCompleteListener(task -> {
                dialogCarregando.dismiss();
                if ( task.isSuccessful() ){
                    cadastroClienteBinding.nomeClienteField.setText("");
                    cadastroClienteBinding.whatsappField.setText("");
                    dialogCadastro.dismiss();
                    CuteToast.ct(this, "Cadastrado com Sucesso!", Toast.LENGTH_LONG, CuteToast.SUCCESS, true).show();
                }
            });
        });

        b.setView(cadastroClienteBinding.getRoot());
        dialogCadastro = b.create();
    }

    private void configurarRecyclerCliente(){
        adapterCliente = new AdapterCliente(clientes,this, dialogCarregando);
        recyclerCadastro.setAdapter(adapterCliente);
    }

    private void configurarListenerCliente(){
        dialogCarregando.show();

        refClientes.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clientes.clear();
                dialogCarregando.dismiss();
                if ( snapshot.exists() ){
                    for ( DataSnapshot dado : snapshot.getChildren() ){
                        ClienteModel cliente = dado.getValue(ClienteModel.class);
                        clientes.add(cliente);
                    }
                    adapterCliente.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogCarregando.dismiss();
            }
        });
    }
    private void configurarDialogCadastroFuncionario(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Cadastro de Funcionário");
        LayoutCadastroFuncionarioBinding cadastroFuncionarioBinding = LayoutCadastroFuncionarioBinding.inflate(getLayoutInflater());

        cadastroFuncionarioBinding.cadastrar.setOnClickListener(view -> {
            dialogCarregando.show();
            String senha,email,nome = "vazio";
            int comissao = 0;

            nome = cadastroFuncionarioBinding.nomeFunc.getText().toString().trim();
            email = cadastroFuncionarioBinding.emailFunc.getText().toString().trim();
            senha = cadastroFuncionarioBinding.senhFunc.getText().toString().trim();
            try{
                comissao = Integer.parseInt(
                        cadastroFuncionarioBinding.comissaoField.getText().toString()
                );
            }catch (Exception e){}


            String id = UUID.randomUUID().toString();

            UserModel novoUsuario = new UserModel(
                    nome,
                    id,
                    email,
                    senha,
                    comissao
            );
            if ( !email.isEmpty() &&  !senha.isEmpty()){
                auth.createUserWithEmailAndPassword(email,senha).addOnCompleteListener( taskCriar -> {
                    if  (taskCriar.isSuccessful() ){
                        refUser.child(id).setValue(novoUsuario).addOnCompleteListener(taskUser -> {
                            if ( taskUser.isSuccessful() ){
                                cadastroFuncionarioBinding.nomeFunc.setText("");
                                cadastroFuncionarioBinding.emailFunc.setText("");
                                cadastroFuncionarioBinding.senhFunc.setText("");
                                cadastroFuncionarioBinding.comissaoField.setText("0");
                                CuteToast.ct(this, "Usuário Criado!", Toast.LENGTH_LONG, CuteToast.SUCCESS, true).show();
                                dialogCadastro.dismiss();
                                dialogCarregando.dismiss();

                            }else{
                                exibirError("Problema de conexão com o banco");
                            }
                        });

                    }else{
                        try {
                            throw Objects.requireNonNull(taskCriar.getException());
                        } catch (FirebaseAuthWeakPasswordException e) {
                            exibirError("Senha fraca!");
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            exibirError("Email inválido!");
                        } catch (FirebaseAuthUserCollisionException e) {
                            exibirError("Já existe um usuário com o mesmo email!");
                        } catch (Exception e) {
                            exibirError(e.getMessage());
                        }
                    }
                });
            }else{
                exibirError("Preencha o E-mail e Senha!");
            }

        });


        b.setView(cadastroFuncionarioBinding.getRoot());
        dialogCadastro = b.create();
    }
    private void configurarRecyclerFuncionario(){
        adapterGeneric = new AdapterGeneric(funcionarios,this, dialogCarregando);
        recyclerCadastro.setAdapter(adapterGeneric);
    }
    private void configurarListenerUsuario(){
        dialogCarregando.show();
        refUser.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                funcionarios.clear();
                dialogCarregando.dismiss();
                if ( snapshot.exists() ){
                    for ( DataSnapshot dado : snapshot.getChildren() ){
                        UserModel user = dado.getValue(UserModel.class);
                        funcionarios.add(user);
                    }
                    adapterGeneric.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogCarregando.dismiss();
            }
        });
    }
    private void exibirError(String erro){
        dialogCarregando.dismiss();
        CuteToast.ct(this, erro, Toast.LENGTH_LONG, CuteToast.ERROR, true).show();
    }
    private void configurarDialogCadastroAgendamento(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Cadastro de Agendamento");
        LayoutCadastroAgendamentoBinding cadastroAgendamentoBinding = LayoutCadastroAgendamentoBinding.inflate(getLayoutInflater());

        cadastroAgendamentoBinding.selecionarCliente.setOnClickListener(view -> {
            dialogCarregando.show();
            refClientes.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    dialogCarregando.dismiss();
                    if ( snapshot.exists() ){
                        Dialog dialogSelecao;
                        AlertDialog.Builder builderSelecao = new AlertDialog.Builder(CadastroActivity.this);
                        builderSelecao.setTitle("Selecione um Cliente");
                        LayoutExibirClientesBinding exibirClientesBinding = LayoutExibirClientesBinding.inflate(getLayoutInflater());
                        RecyclerView recyclerExibicao = exibirClientesBinding.recyclerExibirCliente;

                        recyclerExibicao.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerExibicao.setHasFixedSize(true);
                        recyclerExibicao.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

                        List<ClienteModel> listaExibicao = new ArrayList<>();
                        for ( DataSnapshot dado : snapshot.getChildren()){
                            listaExibicao.add(dado.getValue(ClienteModel.class));
                        }

                        builderSelecao.setView(exibirClientesBinding.getRoot());
                        dialogSelecao = builderSelecao.create();

                        AdapterCliente adapterExibicao = new AdapterCliente(listaExibicao,
                                CadastroActivity.this,
                                dialogCarregando,
                                true,
                                dialogSelecao, cadastroAgendamentoBinding);
                        recyclerExibicao.setAdapter(adapterExibicao);


                        dialogSelecao.show();
                    }else{
                        CuteToast.ct(getApplicationContext(), "Nenhum Cliente Encontrado", CuteToast.LENGTH_LONG, CuteToast.WARN, true).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialogCarregando.dismiss();
                }
            });
        });

        cadastroAgendamentoBinding.cadastrarAgendamento.setOnClickListener( view -> {
            String data, hora, pacote;

            data = cadastroAgendamentoBinding.dataAgendamento.getText().toString();
            String pmORam = cadastroAgendamentoBinding.radioAM.isChecked() ? "AM" : "PM";
            hora = cadastroAgendamentoBinding.horaAgendamento.getText().toString() + " " + pmORam ;
            pacote = cadastroAgendamentoBinding.radio12.isChecked() ? "12 Sessões / 260 Reais" : "8 Sessões / 210 Reais";

            int maxAula = cadastroAgendamentoBinding.radio12.isChecked() ? 12 : 8;

            int status = 0; // pendente
            int aula = 1;

            String[] infoCliente = cadastroAgendamentoBinding.textClienteSelecionado.getText().toString().split("\n");
            String idCliente = "", nomeCliente = "";

            if ( infoCliente.length == 3 ){
                idCliente = infoCliente[2];
                nomeCliente = infoCliente[1];

            }
            while(aula <= maxAula){
                String id = UUID.randomUUID().toString();
                AgendamentoModel agendamentoModel = new AgendamentoModel(
                        idCliente,
                        nomeCliente,
                        data,
                        hora,
                        pacote,
                        id,
                        status,
                        aula
                );

                refAgendamentos.child(id).setValue(agendamentoModel);

                data = "";
                aula++;
            }

            CuteToast.ct(this, "Agendamento Realizado", CuteToast.LENGTH_LONG, CuteToast.SUCCESS, true).show();
            dialogCadastro.dismiss();
            cadastroAgendamentoBinding.dataAgendamento.setText("");
            cadastroAgendamentoBinding.horaAgendamento.setText("");
            cadastroAgendamentoBinding.textClienteSelecionado.setText("Cliente Selecionado: ");

        });

        b.setView(cadastroAgendamentoBinding.getRoot());
        dialogCadastro = b.create();
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
}