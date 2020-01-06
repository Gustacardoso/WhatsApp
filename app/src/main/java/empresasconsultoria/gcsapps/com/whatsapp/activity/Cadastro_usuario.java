package empresasconsultoria.gcsapps.com.whatsapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import empresasconsultoria.gcsapps.com.whatsapp.R;
import empresasconsultoria.gcsapps.com.whatsapp.config.ConfiguracaoFirebase;
import empresasconsultoria.gcsapps.com.whatsapp.helper.Base64Custom;
import empresasconsultoria.gcsapps.com.whatsapp.helper.Preferencias;
import empresasconsultoria.gcsapps.com.whatsapp.model.Usuario;

public class Cadastro_usuario extends AppCompatActivity {

    private EditText nome;
    private EditText email;
    private EditText senha;
    private Button button;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        nome = findViewById(R.id.id_nomeCadastro);
        email = findViewById(R.id.id_EmailCadastro);
        senha = findViewById(R.id.id_SenhaCadastro);
        button = findViewById(R.id.id_botaoCadastro);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               usuario = new Usuario();
               usuario.setNome(nome.getText().toString());
               usuario.setEmail(email.getText().toString());
               usuario.setSenha(senha.getText().toString());
               cadastrarUsuario();
            }
        });
    }

    private void cadastrarUsuario(){
          autenticacao = ConfiguracaoFirebase.getAutenticacao();
          autenticacao.createUserWithEmailAndPassword(
                  usuario.getEmail(),
                  usuario.getSenha()
          ).addOnCompleteListener(Cadastro_usuario.this, new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                  if (task.isSuccessful()){

                      Toast.makeText(Cadastro_usuario.this,"Sucesso No Cadastro do usuario", Toast.LENGTH_LONG).show();

                      String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                      usuario.setId(identificadorUsuario); // para recuperar o id do firebase do usuario
                      usuario.Salvar();


                      Preferencias preferencias = new Preferencias(Cadastro_usuario.this);
                      preferencias.salvarDados(identificadorUsuario,usuario.getNome());
                      abrirLoginUsuario();
                  }else{
                      String erroExcecao = "";

                      try{
                          throw  task.getException();
                      }catch (FirebaseAuthWeakPasswordException e ){
                          erroExcecao = "Digite uma  senha mais forte, contendo mais carcteres e com letras e numero!";
                      }catch (FirebaseAuthInvalidCredentialsException e){
                          erroExcecao ="O e-maildigitado é invalido, digite um novo e-mail!";
                      }catch (FirebaseAuthUserCollisionException e ){
                          erroExcecao = "Esse e-mail ja esta cadastrado";
                      }catch (Exception e ){
                          erroExcecao = "erro ao efetuar o cadastro";
                          e.printStackTrace();
                      }
                      Toast.makeText(Cadastro_usuario.this,erroExcecao, Toast.LENGTH_LONG).show();
                  }
              }
          });
    }
    public void abrirLoginUsuario(){
        Intent intent = new Intent(Cadastro_usuario.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
