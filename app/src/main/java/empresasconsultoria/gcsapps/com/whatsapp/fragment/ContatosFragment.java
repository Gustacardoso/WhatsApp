package empresasconsultoria.gcsapps.com.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import empresasconsultoria.gcsapps.com.whatsapp.Adapter.ContatoAdapter;
import empresasconsultoria.gcsapps.com.whatsapp.R;
import empresasconsultoria.gcsapps.com.whatsapp.activity.ConversaActivity;
import empresasconsultoria.gcsapps.com.whatsapp.config.ConfiguracaoFirebase;
import empresasconsultoria.gcsapps.com.whatsapp.helper.Preferencias;
import empresasconsultoria.gcsapps.com.whatsapp.model.Contato;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

   private ListView listView;
   private ArrayAdapter adapter;
   private ArrayList<Contato> contatos;
   private DatabaseReference firebase;
   private ValueEventListener valueEventListenerContatos;

    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerContatos);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerContatos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //instanciar os objetos
        contatos = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_contatos, container, false);
       //montar listview e o adapter
         listView = view.findViewById(R.id.lv_contatos);
         /*adapter = new ArrayAdapter(getActivity(),
                 R.layout.lista_contatos,
                 contatos);
                */
    adapter = new ContatoAdapter(getActivity(),contatos);
         listView.setAdapter(adapter);

         //recuperar contatos do firebase
    Preferencias preferencias = new Preferencias(getActivity());
    String identificadorUsuarioLogado = preferencias.geIndetificador();
        firebase = ConfiguracaoFirebase.getFirebase()
                .child("contatos")
                .child(identificadorUsuarioLogado);

        //Listener para recuperar contatos
        valueEventListenerContatos  = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //limpar os contatos
                contatos.clear();

                //Listar os contatos
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Contato contato = dados.getValue(Contato.class);
                    contatos.add(contato);
                }

                adapter.notifyDataSetChanged();//avisando a adapter que os dados mudaram
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),ConversaActivity.class);
                //recuperar dados a serem passsados
                Contato contato = contatos.get(position);

                //enviar dados para conversa activity
                intent.putExtra("nome",contato.getNome());
                intent.putExtra("email",contato.getEmail());

                startActivity(intent);
            }
        });


        return view;
    }

}
