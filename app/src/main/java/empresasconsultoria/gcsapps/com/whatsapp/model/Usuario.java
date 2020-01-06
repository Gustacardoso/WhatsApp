package empresasconsultoria.gcsapps.com.whatsapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import empresasconsultoria.gcsapps.com.whatsapp.config.ConfiguracaoFirebase;

public class Usuario  {

    private String id;
    private String nome;
    private String email;
    private String senha;

    public  Usuario(){

    }
    public void  Salvar(){
        DatabaseReference referenceFirebase = ConfiguracaoFirebase.getFirebase();
        referenceFirebase.child("usuarios").child(getId()).setValue(this); // o this foi coloca para pegar os atributos da propria classe
    }

  @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude // fazendo  uma notaçao
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
