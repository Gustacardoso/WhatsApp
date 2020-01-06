package empresasconsultoria.gcsapps.com.whatsapp.activity;



import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import empresasconsultoria.gcsapps.com.whatsapp.Adapter.MensagemAdapter;
import empresasconsultoria.gcsapps.com.whatsapp.R;
import empresasconsultoria.gcsapps.com.whatsapp.config.ConfiguracaoFirebase;
import empresasconsultoria.gcsapps.com.whatsapp.helper.Base64Custom;
import empresasconsultoria.gcsapps.com.whatsapp.helper.Preferencias;
import empresasconsultoria.gcsapps.com.whatsapp.model.Conversa;
import empresasconsultoria.gcsapps.com.whatsapp.model.Mensagem;

public class ConversaActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editMessagem;
    private ImageButton btMensagem;
    private DatabaseReference firebase;
    private ListView listView;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter <Mensagem> adapter;
    private ValueEventListener valueEventListenermensagem;

    //dados de destinatarios
    private String nomeUsuarioDestinatario;
    private  String idUsuarioDestinatario;
     //dados do remetente
    private  String  idUsuarioRemetente;
    private String nomeUsuarioRementente;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

       toolbar = findViewById(R.id.tb_conversa);
       editMessagem = findViewById(R.id.edit_mesnsagem);
       btMensagem = findViewById(R.id.bt_enviar);
       listView = findViewById(R.id.lv_conversas);

       //dados do usuario logado
        Preferencias preferencias = new Preferencias(ConversaActivity.this);
       idUsuarioRemetente = preferencias.geIndetificador(); //esse metodo ele recuera o identificador do  usuaria logado
        nomeUsuarioRementente = preferencias.geNome();


        //Bundle serve para recupar os dados que seram passados de outra classe
       Bundle extra = getIntent().getExtras();

       if (extra != null){//verificando  se existe dados
             nomeUsuarioDestinatario = extra.getString("nome");
             String emailDestinatario = extra.getString("email");
             idUsuarioDestinatario = Base64Custom.codificarBase64(emailDestinatario);
       }

        //configurar toolbar
        toolbar.setTitle(nomeUsuarioDestinatario); // o titulo da toolbar nesse caso nome do usuario
       toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
       setSupportActionBar(toolbar);

       //monta listview e adpter
        mensagens = new ArrayList<>();
         /*adapter = new ArrayAdapter(
                 ConversaActivity.this,
                 android.R.layout.simple_list_item_1,
                 mensagens
         );*/
         adapter = new MensagemAdapter(ConversaActivity.this,mensagens);
         listView.setAdapter(adapter);
        //recuperar as mensagens do firebase
        firebase = ConfiguracaoFirebase.getFirebase()
                .child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);

        //criar lisener para mensagem

        valueEventListenermensagem = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //limpar mensagens
                mensagens.clear();

                    //recuparar as mensagens
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        firebase.addValueEventListener(valueEventListenermensagem);

       //enviar messagem
        btMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoMessagem = editMessagem.getText().toString();

                if (textoMessagem.isEmpty()){
                    Toast.makeText(getApplicationContext(),"digite uma messsagem para enviar",Toast.LENGTH_LONG).show();
                }else {
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setMensagem(textoMessagem);

                    Boolean RetornoMensagemRemetente =  salvaMensagem(idUsuarioRemetente,idUsuarioDestinatario, mensagem);
                   if (!RetornoMensagemRemetente){
                       Toast.makeText(
                               ConversaActivity.this,
                               "Prolema ao enviar a mensagem, tente novamente",
                               Toast.LENGTH_LONG
                       ).show();
                   }else {
                       //salvamos mensagens para o destinatario
                       Boolean RetornoMensagemDestinatario = salvaMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                       if (!RetornoMensagemDestinatario) {
                           Toast.makeText(
                                   ConversaActivity.this,
                                   "Prolema ao enviar mensagem para o destinatario, tente novamente",
                                   Toast.LENGTH_LONG
                           ).show();
                       }
                   }

                   //salvar a conversa para o remetente
                    Conversa conversa = new Conversa();
                   conversa.setIdUsuario(idUsuarioDestinatario);
                   conversa.setNome(nomeUsuarioDestinatario);
                   conversa.setMensagem(textoMessagem);
                  Boolean retornoConversaRemetente =   salvarConversa(idUsuarioRemetente,idUsuarioDestinatario,conversa);
                     if (!retornoConversaRemetente){
                         Toast.makeText(ConversaActivity.this,
                                 "problema ao salvar conversa, tete novamente",Toast.LENGTH_LONG).show();

                     }else{
                         //salvar a conversa para o destinatario
                         conversa = new Conversa();
                         conversa.setIdUsuario(idUsuarioRemetente);
                         conversa.setNome(nomeUsuarioRementente);//usuario logado
                         conversa.setMensagem(textoMessagem);

                        Boolean retornoconversaDestinatario =  salvarConversa(idUsuarioDestinatario,idUsuarioRemetente,conversa);
                         if (!retornoconversaDestinatario){
                             Toast.makeText(ConversaActivity.this,
                                        "problema ao salvar conversa para o destinatario, tente novamente",
                                     Toast.LENGTH_LONG).show();
                         }
                     }
                    editMessagem.setText("");
                }
            }
        });
    }
    private boolean salvaMensagem(String idRemetente,String idDestinatario, Mensagem mensagem){
        try {
            firebase = ConfiguracaoFirebase.getFirebase().child("mensagens");
            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue(mensagem);


         return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean salvarConversa(String idRemetente, String idDestinatario , Conversa conversa) {
        try {
            firebase = ConfiguracaoFirebase.getFirebase().child("conversa");
            firebase.child(idRemetente).child(idDestinatario).setValue(conversa);
            return true;
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenermensagem);
        //removendo  o evento listener caso  o usuario nao esteja na conversa
    }
}
