package empresasconsultoria.gcsapps.com.whatsapp.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferencias {
    private Context context;
    private SharedPreferences preferences;
    private final String NOME_ARQUIVO = "whatsapp.preferencias";
    private  final int MODE = 0;
    private SharedPreferences.Editor editor;
    private final String CHAVE_IDENTIFICADOR= "identificadorUsuarioLogado";
    private final String CHAVE_NOME= "nomeUsuarioLogado";

    public Preferencias (Context contextoParametros){

        context = contextoParametros;
        preferences = context.getSharedPreferences(NOME_ARQUIVO,MODE);
        editor = preferences.edit();
    }

    public void salvarDados (String identificadorUsuario,String nomeUsuario){

        editor.putString(CHAVE_IDENTIFICADOR,identificadorUsuario);
        editor.putString(CHAVE_NOME,nomeUsuario);
       editor.commit();
    }

    public String geIndetificador(){
        return preferences.getString(CHAVE_IDENTIFICADOR,null);
        //podemos recupar o identificadr que nois salvamos no mainactivity
    }
    public String geNome(){
        return preferences.getString(CHAVE_NOME,null);
        //podemos recupar o identificadr nome que nois salvamos no mainactivity
    }

}
