package v3.estruturart.com.br.estruturaart.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.model.TbUsuario;
import v3.estruturart.com.br.estruturaart.service.Client;

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
            "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$",
            R.string.login_erro_email
        );

        getValidator(0).addValidation(
            this,
            R.id.input_password,
            "(.|\\s)*\\S(.|\\s)*",
            R.string.login_erro_password
        );

        // atribui um evento click
        getBootstrapButton(R.id.btn_login).setOnClickListener(this);
    }

    public void onStart() {
        setIsValidUser(false);
        super.onStart();

        System.out.println("CLASS JORDAN: " + this.getClass().toString());
        // Com essa validação so vai entrar nessa tela quando tiver o login na sessão
        if (isLogin()) {
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
            String usuario = getEditText(R.id.input_email).getText().toString();
            String senha = getEditText(R.id.input_password).getText().toString();

            SharedPreferences.Editor editor = getSession(this.getClass().toString()).edit();
            Client client = new Client(view.getContext());
            client.getParameter().put("email", usuario);
            client.getParameter().put("senha", senha);
            TbUsuario usuarioModel = (TbUsuario) client.fromPost("/find-usuario", TbUsuario.class);

            if (client.hasError()) {
                showMessage(view.getContext(), "Sistema temporariamente indisponível. Tente novamente mais tarde!");
                System.out.println("Ex: " + client.getMessage());
            } else if (usuarioModel.getId() > 0) {
                editor.putString("login", usuario);
                editor.putString("senha", senha);
                editor.putString("loginJson", client.getJson());
                editor.apply();
                editor.commit();

                view.getContext().startActivity(new Intent(view.getContext(), Home.class));
            } else {
                showMessage(view.getContext(), "Usuário ou senha informada inválida. Verifique!");
            }
        }
    }
}
