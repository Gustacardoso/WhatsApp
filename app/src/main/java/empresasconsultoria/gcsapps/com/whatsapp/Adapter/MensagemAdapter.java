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
import empresasconsultoria.gcsapps.com.whatsapp.helper.Preferencias;
import empresasconsultoria.gcsapps.com.whatsapp.model.Mensagem;

public class MensagemAdapter extends ArrayAdapter <Mensagem> {

    private ArrayList<Mensagem> mensagens;
    private Context context;

    public MensagemAdapter(@NonNull Context c, @NonNull ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.mensagens = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        //verificando e a lista esta preencida
        if (mensagens != null){
            //recuperar os dados do usuario remetente
            Preferencias preferencias = new Preferencias(context);
            String idUsuarioRemetente = preferencias.geIndetificador();


           //inicializa o objeto para  montagem do  layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
         //recuperar mensagem
            Mensagem mensagem = mensagens.get(position);

            //se para ver quem esta enviando  a mensagem

            if (idUsuarioRemetente.equals(mensagem.getIdUsuario())){
                //montar a view a parir de xml
                view = inflater.inflate(R.layout.item_mensagem_direita, parent , false);
            }else{
                //montar a view a parir de xml
                view = inflater.inflate(R.layout.item_mensagem_esquerda, parent , false);
            }


            //recuperar o elemento para exibição

            TextView textomensagem = view.findViewById(R.id.tv_mensagem);
            textomensagem.setText(mensagem.getMensagem());
        }

        return view;
    }
}
