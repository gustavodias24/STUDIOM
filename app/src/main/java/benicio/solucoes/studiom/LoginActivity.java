package benicio.solucoes.studiom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.rejowan.cutetoast.CuteToast;
import com.squareup.picasso.Picasso;

import benicio.solucoes.studiom.databinding.ActivityLoginBinding;
import benicio.solucoes.studiom.databinding.ActivityMainBinding;
import benicio.solucoes.studiom.databinding.LoadingScreenBinding;

public class LoginActivity extends AppCompatActivity {

    private Dialog dialogCarregando;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
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

        configurarDialogCarregando();
        Picasso.get().load(R.raw.logo).into(mainBinding.logo);

        preferences = getSharedPreferences("usuario_prefs", MODE_PRIVATE);
        editor = preferences.edit();

        mainBinding.entrar.setOnClickListener(view -> {
            dialogCarregando.show();

            String email = mainBinding.emailField.getEditText().getText().toString();
            String senha = mainBinding.senhaField.getEditText().getText().toString();
            if ( !email.isEmpty() && !senha.isEmpty() ){
                if ( email.equals("adm") && senha.equals("adm@233")){
                    finish();
                    editor.putBoolean("online", true).apply();
                    editor.putBoolean("admin", true).apply();
                    startActivity(new Intent(this, MenuAdminActivity.class));
                }else{
                    auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
                        if( task.isSuccessful() ){
                            dialogCarregando.dismiss();
                            finish();
                            editor.putBoolean("online", true).apply();
                            editor.putBoolean("admin", false).apply();
                            startActivity(new Intent(this, FuncionarioActivity.class));
                        }else{
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseNetworkException e){
                                exibirError("Problema de conexão da internet!");
                            }
                            catch (FirebaseAuthInvalidUserException e){
                                exibirError("Usuário não cadastrado!");
                            }
                            catch (FirebaseAuthInvalidCredentialsException e){
                                exibirError("Credenciais inválidas!");
                            }
                            catch (Exception e){
                                exibirError(e.getMessage());
                            }
                        }
                    });
                }

            }

        });
    }

    private void exibirError(String erro){
        dialogCarregando.dismiss();
        CuteToast.ct(this, erro, Toast.LENGTH_LONG, CuteToast.ERROR, true).show();
    }

    private void configurarDialogCarregando() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        LoadingScreenBinding dialogBinding = LoadingScreenBinding.inflate(getLayoutInflater());
        dialogCarregando = b.setView(dialogBinding.getRoot()).create();
    }
}