package com.github.pashmentov96.reader;

import android.content.Context;
import android.content.SharedPreferences;

public class SomePreferences {

    private static final String FILENAME = "SomePreferences";

    private static final String VARIABLE_IS_LOGGED = "is_logged";

    private static final String VARIABLE_TOKEN = "token";

    private static final String VARIABLE_LANGUAGE = "language";

    private SharedPreferences sharedPreferences;

    public SomePreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
    }

    public int getVariableIsLogged() {
        return sharedPreferences.getInt(VARIABLE_IS_LOGGED, 0);
    }

    public void setVariableIsLogged(int variableIsLogged) {
        sharedPreferences.edit().putInt(VARIABLE_IS_LOGGED, variableIsLogged).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(VARIABLE_TOKEN, "Error");
    }

    public void setToken(String token) {
        sharedPreferences.edit().putString(VARIABLE_TOKEN, token).apply();
    }

    public String getVariableLanguage() {
        return sharedPreferences.getString(VARIABLE_LANGUAGE, "en");
    }

    public void setVariableLanguage(String language) {
        sharedPreferences.edit().putString(VARIABLE_LANGUAGE, language).apply();
    }


}
