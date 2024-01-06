package benicio.solucoes.studiom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import benicio.solucoes.studiom.databinding.ActivityLoginBinding;
import benicio.solucoes.studiom.databinding.ActivityMenuAdminBinding;

public class MenuAdminActivity extends AppCompatActivity {

    private ActivityMenuAdminBinding mainBinding;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMenuAdminBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mainBinding.btnCadCliente.setOnClickListener( view -> irTelaCadastro(1));
        mainBinding.btnCadFuncionario.setOnClickListener( view -> irTelaCadastro(2));
        mainBinding.btnCadAgendamento.setOnClickListener( view -> irTelaCadastro(3));

        preferences = getSharedPreferences("usuario_prefs", MODE_PRIVATE);
        editor = preferences.edit();

        mainBinding.sair.setOnClickListener( view -> {
            finish();
            editor.putBoolean("online", false).apply();
            editor.putBoolean("admin", false).apply();
        });
    }

    public void irTelaCadastro(int t){
        Intent i = new Intent(this, CadastroActivity.class);
        i.putExtra("t", t);
        startActivity(i);
    }
}