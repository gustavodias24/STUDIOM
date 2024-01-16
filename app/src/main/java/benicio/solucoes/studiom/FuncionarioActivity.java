package benicio.solucoes.studiom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import benicio.solucoes.studiom.databinding.ActivityFuncionarioBinding;
import benicio.solucoes.studiom.databinding.ActivityMenuAdminBinding;
import benicio.solucoes.studiom.databinding.LoadingScreenBinding;
import benicio.solucoes.studiom.models.UserModel;

public class FuncionarioActivity extends AppCompatActivity {

    private UserModel funcionarioLogado;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ActivityFuncionarioBinding mainBinding;
    private SharedPreferences preferences;
    private Dialog dialogCarregando;
    private DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("users");
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityFuncionarioBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        configurarDialogCarregando();
        preferences = getSharedPreferences("usuario_prefs", MODE_PRIVATE);
        editor = preferences.edit();

        mainBinding.sair.setOnClickListener( view -> {
            finish();
            auth.signOut();
            editor.putBoolean("online", false).apply();
            editor.putBoolean("admin", false).apply();
        });

        dialogCarregando.show();

        refUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialogCarregando.dismiss();

                if ( snapshot.exists() ){
                    for ( DataSnapshot dado : snapshot.getChildren()){
                        UserModel funcionarioAtual = dado.getValue(UserModel.class);

                        if ( funcionarioAtual.getEmail().trim().equals(auth.getCurrentUser().getEmail().trim())){
                            funcionarioLogado = funcionarioAtual;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void irParaAgendamentos(View view){
        Intent i = new Intent(this, AgendamentosActivity.class);
        i.putExtra("idFuncionario", funcionarioLogado.getId());
        i.putExtra("t", 0);
        startActivity(i);
    }

    public void irParaMeusAgendamentos(View view){
        Intent i = new Intent(this, AgendamentosActivity.class);
        i.putExtra("idFuncionario", funcionarioLogado.getId());
        i.putExtra("t", 1);
        startActivity(i);
    }
    private void configurarDialogCarregando() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        LoadingScreenBinding dialogBinding = LoadingScreenBinding.inflate(getLayoutInflater());
        dialogCarregando = b.setView(dialogBinding.getRoot()).create();
    }
}