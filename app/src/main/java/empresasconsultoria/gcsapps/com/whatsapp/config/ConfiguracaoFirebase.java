package empresasconsultoria.gcsapps.com.whatsapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class ConfiguracaoFirebase {

    private static DatabaseReference referenceFirebase;
    private static FirebaseAuth autenticacao;

    public static DatabaseReference getFirebase(){

        if (referenceFirebase == null) { //caso ja tenho configurando  o firabase
            referenceFirebase = FirebaseDatabase.getInstance().getReference(); //vamos retorna a eferencia
        }
        return referenceFirebase;
    }
    public static FirebaseAuth getAutenticacao(){
        if (autenticacao == null){
            autenticacao =  FirebaseAuth.getInstance();
        }
        return autenticacao;
    }
}
