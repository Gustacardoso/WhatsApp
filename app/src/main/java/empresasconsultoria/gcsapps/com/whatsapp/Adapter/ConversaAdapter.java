package empresasconsultoria.gcsapps.com.whatsapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import empresasconsultoria.gcsapps.com.whatsapp.R;
import empresasconsultoria.gcsapps.com.whatsapp.model.Conversa;

public class ConversaAdapter extends ArrayAdapter<Conversa> {
    private ArrayList<Conversa> conversas;
    private Context  context;

    public ConversaAdapter(@NonNull Context c, @NonNull ArrayList<Conversa> objects) {
        super(c, 0, objects);
        this.context= c;
        this.conversas = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        //verificars se a lsta esta vazia
        if(conversas !=null){
          //iniciar objeto ára montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            //montagem da view apartir do xml
            view = inflater.inflate(R.layout.lista_conversa,parent,false);
            //recuparer elemento para exibição
            TextView nome = view.findViewById(R.id.tv_titulo);
            TextView ultimamensagem = view.findViewById(R.id.tv_subTitulo);

            Conversa conversa = conversas.get(position);
            nome.setText(conversa.getNome());
            ultimamensagem.setText(conversa.getMensagem());


        }
        return view;
    }
}
