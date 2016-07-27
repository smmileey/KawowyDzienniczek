package pl.kawowydzienniczek.kawowydzienniczek.Data;

import java.util.HashMap;

import pl.kawowydzienniczek.kawowydzienniczek.Constants.LoginErrors;

public class LoginData {

    private String token;
    private boolean isValid;
    private HashMap<LoginErrors, String> errors;

    public LoginData(boolean valid, String tkn, HashMap<LoginErrors, String> err){
        token = tkn;
        errors = err;
        isValid = valid;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getToken() {
        return token;
    }

    public HashMap<LoginErrors,String> getErrors() {
        return errors;
    }
}
