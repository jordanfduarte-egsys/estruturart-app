package v3.estruturart.com.br.estruturaart.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import v3.estruturart.com.br.estruturaart.R;

public class Login extends AbstractActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Metodos iniciais
        super.complementOnCreate();

        // Atribui qual é a view
        setContentView(R.layout.activity_login);

        getValidator(0).addValidation(
            this,
            R.id.input_email,
            "[a-zA-Z 0-9]+",
            R.string.login_erro_email
        );

        getValidator(0).addValidation(
            this,
            R.id.input_password,
            "[a-zA-Z 0-9]+",
            R.string.login_erro_password
        );

        // atribui um evento click
        getBootstrapButton(R.id.btn_login).setOnClickListener(this);
    }

    public void onStart() {
        super.onStart();

        // Com essa validação so vai entrar nessa tela quando tiver o login na sessão
        if (!getSession(this.getClass().toString()).getString("login", "0").equals("0")) {
            showMessage(
                this,
                "Usuário logado: " + getSession(this.getClass().toString())
                    .getString("login", "")
                    .toLowerCase()
            );

            startActivity(new Intent(this, Home.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                this.onLogin(view);
            break;
        }
    }

    public void onLogin(View view) {
        if (getValidator(0).validate()) {
            SharedPreferences.Editor editor = getSession(this.getClass().toString()).edit();
            editor.putString("login", getEditText(R.id.input_email).getText().toString());
            editor.putString("senha", getEditText(R.id.input_password).getText().toString());
            editor.apply();
            editor.commit();

            view.getContext().startActivity(new Intent(view.getContext(), Home.class));
        }
    }
}
