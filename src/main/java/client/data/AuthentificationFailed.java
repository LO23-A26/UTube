package client.data;

public class AuthentificationFailed extends RuntimeException{
    public AuthentificationFailed(){
        super("Authentification Failed");
    }

    public AuthentificationFailed(String error){
        super(error);
    }
}
