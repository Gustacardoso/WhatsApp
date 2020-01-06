package empresasconsultoria.gcsapps.com.whatsapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

import empresasconsultoria.gcsapps.com.whatsapp.R;
import empresasconsultoria.gcsapps.com.whatsapp.config.ConfiguracaoFirebase;
import empresasconsultoria.gcsapps.com.whatsapp.helper.Base64Custom;
import empresasconsultoria.gcsapps.com.whatsapp.helper.Permissao;
import empresasconsultoria.gcsapps.com.whatsapp.helper.Preferencias;
import empresasconsultoria.gcsapps.com.whatsapp.model.Usuario;

public class LoginActivity extends AppCompatActivity {

   // private DatabaseReference referencefirebase;
      private EditText email;
      private EditText senha;
      private Button button;
      private Usuario usuario;
      private  FirebaseAuth autenticacao;
    private DatabaseReference firebase;
      private ValueEventListener  valueEventListenerUsuario;
      private String identificadorUsuarioLogado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verificarUsuarioLogad();

        email = findViewById(R.id.id_emailLogin);
        senha = findViewById(R.id.id_senhaLogin);
        button = findViewById(R.id.id_botaoLogar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = new Usuario();
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());
                validadarLogin();
            }
        });
       // referencefirebase = ConfiguracaoFirebase.getFirebase();
       // referencefirebase.child("gustao").setValue("43300");
    }

    public void abrirCadastroUsuario(View view){
        Intent intent = new Intent(LoginActivity.this,Cadastro_usuario.class);
        startActivity(intent);
    }
    public  void validadarLogin(){
          autenticacao = ConfiguracaoFirebase.getAutenticacao();
          autenticacao.signInWithEmailAndPassword(
                  usuario.getEmail(),
                  usuario.getSenha()
          ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                  if (task.isSuccessful()){


                      identificadorUsuarioLogado = Base64Custom.codificarBase64(usuario.getEmail());

                      firebase = ConfiguracaoFirebase.getFirebase()
                              .child("usuarios")
                              .child(identificadorUsuarioLogado);

                      valueEventListenerUsuario = new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Usuario  usuarioRecuparado= dataSnapshot.getValue(Usuario.class);
                              final Preferencias preferencias = new Preferencias(LoginActivity.this);
                              preferencias.salvarDados(identificadorUsuarioLogado,usuarioRecuparado.getNome() );
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      };
                      firebase.addValueEventListener( valueEventListenerUsuario);

                      // tudo isso que fiz foi para logar o  usuario
                      abrirtelaPrincipal();
                       Toast.makeText(LoginActivity.this,"Usuario Logado",Toast.LENGTH_LONG).show();
                  }else{
                      Toast.makeText(LoginActivity.this,"Erro: ",Toast.LENGTH_LONG).show();
                  }
              }
          });
    }
    public void abrirtelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void verificarUsuarioLogad(){
        autenticacao = ConfiguracaoFirebase.getAutenticacao();
        if (autenticacao.getCurrentUser() != null){
            abrirtelaPrincipal(); // caso o usuario  ja eta logado iremos abri  atela principal, que nesse caso é ain activity
        }
    }
}
