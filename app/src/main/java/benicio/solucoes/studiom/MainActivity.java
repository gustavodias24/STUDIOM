package benicio.solucoes.studiom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import benicio.solucoes.studiom.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    private static final int SPLASH_DURATION = 3000;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private SharedPreferences preferences;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        Picasso.get().load(R.raw.logo).into(mainBinding.logo);

        // Animação de fade in
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1500); // 1,5 segundos
        mainBinding.logo.startAnimation(fadeIn);

        preferences = getSharedPreferences("usuario_prefs", MODE_PRIVATE);

        // Inicia a próxima atividade após 3 segundos
        new Handler().postDelayed(() -> {
            Intent intent;
            if ( !preferences.getBoolean("online", false) ) {
                intent = new Intent(getApplicationContext(), LoginActivity.class);
            }else{
                if( preferences.getBoolean("admin", false)){
                    intent = new Intent(this, MenuAdminActivity.class);
                }else{
                    intent = new Intent(this, FuncionarioActivity.class);
                }
            }

            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}