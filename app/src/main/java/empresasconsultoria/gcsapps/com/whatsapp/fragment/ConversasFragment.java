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

import empresasconsultoria.gcsapps.com.whatsapp.Adapter.ConversaAdapter;
import empresasconsultoria.gcsapps.com.whatsapp.R;
import empresasconsultoria.gcsapps.com.whatsapp.activity.ConversaActivity;
import empresasconsultoria.gcsapps.com.whatsapp.config.ConfiguracaoFirebase;
import empresasconsultoria.gcsapps.com.whatsapp.helper.Base64Custom;
import empresasconsultoria.gcsapps.com.whatsapp.helper.Preferencias;
import empresasconsultoria.gcsapps.com.whatsapp.model.Contato;
import empresasconsultoria.gcsapps.com.whatsapp.model.Conversa;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private ListView listView;
   private ArrayAdapter<Conversa> adapter;
   private ArrayList<Conversa> conversas;
   private DatabaseReference firebase;
   private ValueEventListener valueEventListenerConversas;

    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        conversas = new ArrayList<>();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);
       listView = view.findViewById(R.id.lv_conversas);
        adapter = new ConversaAdapter(getActivity(),conversas);
        listView.setAdapter(adapter);
        //recupear os dados do  usuario

        Preferencias preferencias = new Preferencias(getActivity());
        String idusuarioLogado = preferencias.geIndetificador();


        //recuperar as conversas do firebase
        firebase = ConfiguracaoFirebase.getFirebase()
                .child("conversas")
                .child(idusuarioLogado);

        valueEventListenerConversas = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 conversas.clear();
                 for (DataSnapshot dados: dataSnapshot.getChildren()){
                     Conversa conversa = dados.getValue(Conversa.class);
                     conversas.add(conversa);

                 }
                 adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ConversaActivity.class);
                //recuperar dados a serem passsados
                Conversa conversa = conversas.get(position);

                //enviar dados para conversa activity
                intent.putExtra("nome", conversa.getNome());
                String email = Base64Custom.decodificarBase64(conversa.getIdUsuario());
                intent.putExtra("email", email);

                startActivity(intent);
            }
            });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //adicionar esse evento pouco antes de carregar esse evento
        firebase.addValueEventListener(valueEventListenerConversas);
    }

    @Override
    public void onStop() {
        super.onStop();
        //caso  nao estaja mais sendo  usado
        firebase.removeEventListener(valueEventListenerConversas);

    }
}
