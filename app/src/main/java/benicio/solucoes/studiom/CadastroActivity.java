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

import benicio.solucoes.studiom.adapter.AdapterGeneric;
import benicio.solucoes.studiom.databinding.ActivityCadastroBinding;
import benicio.solucoes.studiom.databinding.LayoutCadastroAgendamentoBinding;
import benicio.solucoes.studiom.databinding.LayoutCadastroClienteBinding;
import benicio.solucoes.studiom.databinding.LayoutCadastroFuncionarioBinding;
import benicio.solucoes.studiom.databinding.LoadingScreenBinding;
import benicio.solucoes.studiom.models.UserModel;

public class CadastroActivity extends AppCompatActivity {
    private List<UserModel> funcionarios = new ArrayList<>();
    private RecyclerView recyclerCadastro;
    private AdapterGeneric adapterGeneric;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("users");
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

        bundle = getIntent().getExtras();
        t = bundle.getInt("t", 1);

        switch (t){
            case 1:
                configurarDialogCadastroCliente();
                break;
            case 2:
                configurarDialogCadastroFuncionario();
                break;
            default:
                configurarDialogCadastroAgendamento();

        }

        recyclerCadastro = mainBinding.recyclerCadastro;
        recyclerCadastro.setLayoutManager(new LinearLayoutManager(this));
        recyclerCadastro.setHasFixedSize(true);
        recyclerCadastro.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mainBinding.fabCadastro.setOnClickListener(view -> dialogCadastro.show());
        configurarDialogCarregando();

        if ( t == 2){
            configurarRecyclerFuncionario();
            configurarListenerUsuario();
        }

    }

    private void configurarDialogCadastroCliente(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Cadastro de cliente");
        LayoutCadastroClienteBinding cadastroClienteBinding = LayoutCadastroClienteBinding.inflate(getLayoutInflater());
        b.setView(cadastroClienteBinding.getRoot());
        dialogCadastro = b.create();
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