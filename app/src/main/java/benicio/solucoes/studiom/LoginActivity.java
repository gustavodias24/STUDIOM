package benicio.solucoes.studiom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import com.squareup.picasso.Picasso;

import benicio.solucoes.studiom.databinding.ActivityLoginBinding;
import benicio.solucoes.studiom.databinding.ActivityMainBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mainBinding;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Picasso.get().load(R.raw.logo).into(mainBinding.logo);

        preferences = getSharedPreferences("usuario_prefs", MODE_PRIVATE);
        editor = preferences.edit();

        mainBinding.entrar.setOnClickListener(view -> {

            String email = mainBinding.emailField.getEditText().getText().toString();
            String senha = mainBinding.senhaField.getEditText().getText().toString();

            if ( email.equals("adm") && senha.equals("adm@233")){
                finish();
                editor.putBoolean("online", true).apply();
                editor.putBoolean("admin", true).apply();
                startActivity(new Intent(this, MenuAdminActivity.class));
            }else{

            }

        });




    }
}