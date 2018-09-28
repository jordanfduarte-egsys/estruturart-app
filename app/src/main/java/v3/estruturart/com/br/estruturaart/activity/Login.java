package v3.estruturart.com.br.estruturaart.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.model.TbUsuario;
import v3.estruturart.com.br.estruturaart.service.Client;
import v3.estruturart.com.br.estruturaart.utility.AsyncResponse;
import v3.estruturart.com.br.estruturaart.utility.AsyncTaskCustom;

public class Login extends AbstractActivity implements View.OnClickListener, AsyncResponse {
	private static final int ASYNC_LOGIN = 4;
	private TbUsuario usuarioModel = new TbUsuario();
	
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
			AsyncTaskCustom async = new AsyncTaskCustom(ASYNC_LOGIN);
			async.delegate = (AsyncResponse) view.getContext();
			async.execute();
        }
    }
	
	@Override
    public String onExecTask(String result, int id) {
        if (id == ASYNC_LOGIN){			
            String usuario = getEditText(R.id.input_email).getText().toString();
            String senha = getEditText(R.id.input_password).getText().toString();
			Client client = new Client(this);
            client.getParameter().put("email", usuario);
            client.getParameter().put("senha", senha);
            usuarioModel = (TbUsuario) client.fromPost("/find-usuario", TbUsuario.class);

			if (client.hasError()) {
				usuarioModel.setMessage(client.getMessage());
			}
		}

		return "";
	}
	
	@Override
    public String onPreTask(String result, int id) {
        getProgressBar(R.id.progressBar1).setVisibility(View.VISIBLE);
        return null;
    }
	
	 @Override
    public String onPosTask(String result, int id) {
		getProgressBar(R.id.progressBar1).setVisibility(View.GONE);
		
		if (!usuarioModel.getMessage().equals("")) {
			showMessage(this, usuarioModel.getMessage());
			usuarioModel = new TbUsuario();
		} else {		
			if (usuarioModel.getId() > 0) {
                SharedPreferences.Editor editor = getSession(Login.class.toString()).edit();
                String usuario = getEditText(R.id.input_email).getText().toString();
                String senha = getEditText(R.id.input_password).getText().toString();
				editor.putString("login", usuario);
				editor.putString("senha", senha);
				editor.putString("loginJson", usuarioModel.toJson());
				editor.apply();
				editor.commit();

				this.startActivity(new Intent(this, Home.class));
			} else {
				showMessage(this, "Usuário ou senha informado inválido. Verifique!");
			}
		}

		return "";
	}
}
