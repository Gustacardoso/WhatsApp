package empresasconsultoria.gcsapps.com.whatsapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import empresasconsultoria.gcsapps.com.whatsapp.Adapter.TabAdapter;
import empresasconsultoria.gcsapps.com.whatsapp.R;
import empresasconsultoria.gcsapps.com.whatsapp.config.ConfiguracaoFirebase;
import empresasconsultoria.gcsapps.com.whatsapp.helper.Base64Custom;
import empresasconsultoria.gcsapps.com.whatsapp.helper.Preferencias;
import empresasconsultoria.gcsapps.com.whatsapp.helper.SlidingTabLayout;
import empresasconsultoria.gcsapps.com.whatsapp.model.Contato;
import empresasconsultoria.gcsapps.com.whatsapp.model.Usuario;

public class MainActivity extends AppCompatActivity {

    private Button buttonDeslogar;
    private FirebaseAuth UsuarioAutenticacao;
    private Toolbar toolbar;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private String identificadorContato;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UsuarioAutenticacao = ConfiguracaoFirebase.getAutenticacao(); // iniciando a autentificação do usuario
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("WhasApp"); // titulo para a toolbar
        setSupportActionBar(toolbar);// metodo  de suport para a sua toolbar

        slidingTabLayout = findViewById(R.id.stl_tabs);
        viewPager = findViewById(R.id.vp_pagina);

        //configurar sliding tabs
         slidingTabLayout.setDistributeEvenly(true);// vai distribur as tab corretamente
        //slidingTabLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.colorBranco));
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this,R.color.colorBrancoee));

        //configurar adapter
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); // isso para inflamar o menu
        inflater.inflate(R.menu.menu_main,menu);  // aparecer o menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // metodo  no menu dos itens
        switch (item.getItemId()) {
            case R.id.item_sair:
                deslogarUsuario();
                return true;
            case R.id.item_configuracoes:
                return true;
            case R.id.item_add:
                abrirCadastroContato();
                return true;
                default:
            return super.onOptionsItemSelected(item);
        }

    }
  private void abrirCadastroContato(){
      AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

      //configuraçao do dialog
       alertDialog.setTitle("Novo Contato"); //seria o titulo do testo
      alertDialog.setMessage("E-mail do usúario");
      alertDialog.setCancelable(false);

      //criando  a caixa de texto.
      final EditText editText = new EditText(MainActivity.this);
      alertDialog.setView(editText);
      //configura botões
      alertDialog.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
             String emailContato = editText.getText().toString();
             //vaida se o email foi digitado
              if (emailContato.isEmpty()) {
                  Toast.makeText(MainActivity.this, "Preencha o email", Toast.LENGTH_LONG).show();
              }else {
                  //verficiar se o usuario  esta cadastrado no nosso app
                      identificadorContato = Base64Custom.codificarBase64(emailContato);

                      //recuparar instancia firebase
                      firebase = ConfiguracaoFirebase.getFirebase().child("usuarios").child(identificadorContato);

                      firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              if (dataSnapshot.getValue() != null){
                                  //recupar identificaro usuario logado (base64)
                                  Usuario usuario = dataSnapshot.getValue(Usuario.class); //para passar os dados da classe usuario
                                  Preferencias preferencias = new Preferencias(MainActivity.this);
                                  String identificadoUsuarioLogado = preferencias.geIndetificador();
                                  firebase = ConfiguracaoFirebase.getFirebase();
                                  firebase = firebase.child("contatos")
                                          .child(identificadoUsuarioLogado)
                                          .child(identificadorContato);

                                  Contato contato = new Contato();

                                  contato.setIdentificadorUsuario(identificadorContato);
                                  contato.setNome(usuario.getNome());
                                  contato.setEmail(usuario.getEmail());


                                  firebase.setValue(contato);
                                  Toast.makeText(MainActivity.this,"sucesso cadastro do contato.",Toast.LENGTH_LONG).show();

                              }else{
                                  Toast.makeText(MainActivity.this,"Usuario nao possui cadastro.",Toast.LENGTH_LONG).show();
                              }

                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      });
                  }

              }

      });
      alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
      });
      alertDialog.create();
      alertDialog.show();
    }
    private void deslogarUsuario(){
        UsuarioAutenticacao.signOut();//delogando o usuario do firabase
        Intent intent = new Intent(MainActivity.this , LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
