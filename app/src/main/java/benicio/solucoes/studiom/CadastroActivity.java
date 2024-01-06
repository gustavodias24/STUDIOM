package benicio.solucoes.studiom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import benicio.solucoes.studiom.databinding.ActivityCadastroBinding;
import benicio.solucoes.studiom.databinding.LayoutCadastroClienteBinding;

public class CadastroActivity extends AppCompatActivity {

    private ActivityCadastroBinding mainBinding;
    private Bundle bundle;
    private Dialog dialogCadastro;
    private int t;
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
        }

        mainBinding.fabCadastro.setOnClickListener(view -> dialogCadastro.show());


    }

    private void configurarDialogCadastroCliente(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Cadastro de cliente");
        LayoutCadastroClienteBinding cadastroClienteBinding = LayoutCadastroClienteBinding.inflate(getLayoutInflater());
        b.setView(cadastroClienteBinding.getRoot());
        dialogCadastro = b.create();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}