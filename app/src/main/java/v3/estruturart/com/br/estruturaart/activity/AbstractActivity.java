package v3.estruturart.com.br.estruturaart.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;

import v3.estruturart.com.br.estruturaart.service.Client;
import v3.estruturart.com.br.estruturaart.utility.AsyncResponse;
import v3.estruturart.com.br.estruturaart.utility.AsyncTaskCustom;
import v3.estruturart.com.br.estruturaart.utility.AwesomeValidationCustom;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.model.Orcamento;
import v3.estruturart.com.br.estruturaart.model.TbUsuario;
import v3.estruturart.com.br.estruturaart.utility.GsonDeserializeExclusion;
import v3.estruturart.com.br.estruturaart.utility.JsonModel;

public class AbstractActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected boolean isValidUser = true;
    protected Activity activity;
    private List<AwesomeValidationCustom> validators = new ArrayList<AwesomeValidationCustom>();
    private String message = "";
    private String typeMessage = "success";

    protected static final int ASYNC_ORCAMENTO_ACCESS = 9191944;

    protected void complementOnCreate() {

        TypefaceProvider.registerDefaultIconSets();
    }

    public void onStart() {
        super.onStart();
        if (isValidUser && !isLogin()) {
            startActivity(new Intent(this, Login.class));
        }
    }

    protected NavigationView initNavigationBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        );
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        TextView tv = (TextView)navigationView.getHeaderView(0).findViewById(R.id.textViewNameLogin);
        tv.setText(getUsuarioLogado().getNome());

        List<Orcamento> orcamentos = gutSincronizeOrcamento();
        if (orcamentos.size() > 0) {
            navigationView.getMenu().getItem(2).setTitle(String.format("Sincronizar (%s)", orcamentos.size()));
            navigationView.getMenu().getItem(2).setVisible(true);
        }

        return navigationView;
    }

    protected Button getButton(int btnName) {
        return ((Button)findViewById(btnName));
    }

    protected Spinner getSpinner(int spinnerId) {return (Spinner)findViewById(spinnerId);}

    protected BootstrapButton getBootstrapButton(int btnName) {
        return ((BootstrapButton)findViewById(btnName));
    }

    public EditText getEditText(int editName) {
        return (EditText)findViewById(editName);
    }

    public EditText getEditText(int editName, Context ctx) {
        return (EditText)(((Activity)ctx).findViewById(editName));
    }

    public ProgressBar getProgressBar(int progress) {
        return (ProgressBar)findViewById(progress);
    }

    protected TextView getTextView(int tvName) {return (TextView)findViewById(tvName);}

    protected AwesomeValidationCustom getValidator(int formIntName) {
        try {
            validators.get(formIntName);
        } catch ( IndexOutOfBoundsException e ) {
            System.out.println("CATCH FORM");
            validators.add(formIntName, new AwesomeValidationCustom(ValidationStyle.BASIC));
        }

        return validators.get(formIntName);
    }

    protected void showMessage(Context ctx, CharSequence text) {
        Toast toast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    protected SharedPreferences getSession(String sharedPrefferenceName) {
        return getSharedPreferences(sharedPrefferenceName, Context.MODE_PRIVATE);
    }

    protected boolean isLogin() {
        return !getSession(Login.class.toString()).getString("login", "0").equals("0");
    }

    public boolean onNavigationItemSelectedActions(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_pedidos) {
            home(item);
        } else if (id == R.id.nav_orcamentos) {
            orcamento(item);
        } else if (id == R.id.nav_sair) {
            logout(item);
        } else if (id == R.id.nav_sincronizar) {
            item.setCheckable(false);
            modalSincronizar();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void modalSincronizar() {
        View rowModelo = getLayoutInflater().inflate(R.layout.confirmar_sincronizacao, null);
        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setCancelable(true);
        settingsDialog.setCanceledOnTouchOutside(true);
        settingsDialog.setContentView(rowModelo);

        ((BootstrapButton)rowModelo.findViewById(R.id.limpar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpar();
                settingsDialog.dismiss();
            }
        });
        ((BootstrapButton)rowModelo.findViewById(R.id.cancelar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDialog.dismiss();
            }
        });
        ((BootstrapButton)rowModelo.findViewById(R.id.confirmar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Orcamento> orcamentos = gutSincronizeOrcamento();
                for (Orcamento orc : orcamentos) {
                    orc.setHasError(false);
                }
                putSincronizeOrcamento(orcamentos);

                sincronizar();
                settingsDialog.dismiss();
            }
        });

        settingsDialog.show();
    }

    protected void limpar() {
        putSincronizeOrcamento(new ArrayList<Orcamento>());
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        List<Orcamento> orcamentos = gutSincronizeOrcamento();
        navigationView.getMenu().getItem(2).setTitle(String.format("Sincronizar (%s)", orcamentos.size()));

        if (orcamentos.size() == 0) {
            navigationView.getMenu().getItem(2).setVisible(false);
        }
    }

    protected void sincronizar() {
        AsyncTaskCustom async = new AsyncTaskCustom(ASYNC_ORCAMENTO_ACCESS);
        async.delegate = (AsyncResponse) this;
        async.execute();
    }

    protected void sincronizeOrcamentoInBackground() {

        if (getNextOrcamentoSincronizacao() instanceof Orcamento) {
            Object orc = getNextOrcamentoSincronizacao();
            if (orc instanceof Orcamento) {
                Orcamento orcamentoAux = (Orcamento)orc;
                Client client = new Client(this);
                client.getParameter().put("orcamento", orcamentoAux.toJson());
                client.getParameter().put("is_orcamento", orcamentoAux.getIsOrcamento() ? "0" : "1");
                JsonModel jsonModel = (JsonModel) client.fromPost("/salvar-orcamento", JsonModel.class);

                if (!client.getMessage().equals("")) {
                    message = client.getMessage();
                    typeMessage = "error";

                    List<Orcamento> orcamentos = removeOrcamentoSincronizacao(orcamentoAux);
                    orcamentoAux.setHasError(true);
                    orcamentos.add(orcamentoAux);

                    System.out.println("DEU ERRO: ");
                    putSincronizeOrcamento(orcamentos);
                } else if (!jsonModel.getStatus()) {
                    message = jsonModel.getMessage();
                    typeMessage = "error";

                    List<Orcamento> orcamentos = removeOrcamentoSincronizacao(orcamentoAux);
                    orcamentoAux.setHasError(true);
                    orcamentos.add(orcamentoAux);
                    putSincronizeOrcamento(orcamentos);
                } else {
                    List<Orcamento> orcamentos = removeOrcamentoSincronizacao(orcamentoAux);
                    putSincronizeOrcamento(orcamentos);
                    typeMessage = "success";
                    if (orcamentoAux.getIsOrcamento()) {
                        message = "Orçamento salvo com sucesso!";
                    } else {
                        message = "Pedido salvo com sucesso!";
                    }
                }
            }
        }
    }
    protected List<Orcamento> removeOrcamentoSincronizacao(Orcamento orc) {
        List<Orcamento> orcamentos = gutSincronizeOrcamento();

        List<Orcamento> orcamentosAyx = new ArrayList<Orcamento>();
        for (Orcamento orc1 : orcamentos) {
            System.out.println("OB1: " + orc1);
            System.out.println("OB2: " + orc);
            if (!orc1.equals(orc)) {
                orcamentosAyx.add(orc1);
            }
        }

        System.out.println("Antes: " + orcamentos.size() + " DEPOIS: " + orcamentosAyx.size());
        return orcamentosAyx;
    }

    protected Object getNextOrcamentoSincronizacao() {
        List<Orcamento> orcamentos = gutSincronizeOrcamento();
        for (Orcamento orc : orcamentos) {
            if (!orc.hasError()) {
                return orc;
            }
        }

        return new Object();
    }

    protected void sincronizeOrcamentoPost() {
        if (!message.equals("") && typeMessage.equals("success")) {
            showMessageSuccess(this, message);
            message = "";
        } else {
            showMessage(this, message);
            message = "";
            typeMessage = "success";
        }

        if (getNextOrcamentoSincronizacao() instanceof Orcamento) {
            sincronizar();
        } else {
            // Valida o menu
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

            List<Orcamento> orcamentos = gutSincronizeOrcamento();
            navigationView.getMenu().getItem(2).setTitle(String.format("Sincronizar (%s)", orcamentos.size()));

            if (orcamentos.size() == 0) {
                navigationView.getMenu().getItem(2).setVisible(false);
            }
        }
    }

    private void logout(MenuItem item) {
        SharedPreferences.Editor editor = getSession(Login.class.toString()).edit();
        editor.putString("login", "0");
        editor.putString("senha", "");
        editor.apply();
        editor.commit();

        putOrcamentoSession(new Orcamento(), Orcamento.class.getName().toString());
        this.startActivity(new Intent(this, Login.class));
    }

    private void orcamento(MenuItem item) {
        Orcamento orcamento = (Orcamento)getOrcamentoSession(Orcamento.class.getName().toString());

        if (orcamento.isValidEtapa1() && orcamento.isValidEtapa2()) {
            this.startActivity(new Intent(this, OrcamentoEtapa3.class));
        } else if (orcamento.isValidEtapa1()) {
            this.startActivity(new Intent(this, OrcamentoEtapa2.class));
        } else {
            this.startActivity(new Intent(this, OrcamentoEtapa1.class));
        }
    }

    private void home(MenuItem item) {
        this.startActivity(new Intent(this, Home.class));
    }

    protected void setIsValidUser(boolean isValidUser) {
        this.isValidUser = isValidUser;
    }

    protected TbUsuario getUsuarioLogado() {
        Gson gson = new Gson();
        return (TbUsuario) gson.fromJson(
            getSession(Login.class.toString()).getString("loginJson", ""),
            TbUsuario.class
        );
    }

    protected void getPositionSpinnerByListObject(Spinner sp, Object ob) {
        for (int i = 0; i < sp.getCount(); i++) {
            System.out.println("ESTADO SP: " + sp.getItemAtPosition(i).toString());
            System.out.println("ESTADO CP: " + sp.getItemAtPosition(i).toString());
            if (sp.getItemAtPosition(i).toString().equals(ob.toString())) {
                sp.setSelection(i);
                break;
            }
        }
    }

    protected Orcamento getOrcamentoSession(String name) {
        Gson gson = new GsonBuilder()
            .addDeserializationExclusionStrategy(new GsonDeserializeExclusion()).create();
//        Gson gson = new Gson();
        String json = getSession("orcamentoSession").getString(name, "");

        System.out.println("JSON\n\n\n");
        System.out.println("JSON\n\n\n" + json);

        if (json.equals("")) {
            json = gson.toJson(new Orcamento());
        }
         return (Orcamento) gson.fromJson(json, Orcamento.class);
    }

    protected void putOrcamentoSession(Orcamento orcamento, String name) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = getSession("orcamentoSession").edit();
        editor.putString(name, gson.toJson(orcamento));
        editor.apply();
        editor.commit();
    }

    protected void putSincronizeOrcamento(List<Orcamento> orcamentos) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = getSession("sincronizeOrcamento").edit();
        editor.putString("orcamentos", gson.toJson(orcamentos));
        editor.apply();
        editor.commit();
    }

    protected List<Orcamento> gutSincronizeOrcamento() {
        Gson gson = new GsonBuilder()
            .addDeserializationExclusionStrategy(new GsonDeserializeExclusion()).create();
        String json = getSession("sincronizeOrcamento").getString("orcamentos", "");

        System.out.println("JSON\n\n\n");
        System.out.println("JSON\n\n\n" + json);

        if (json.equals("")) {
            json = gson.toJson(new ArrayList<Orcamento>());
        }

        return (List<Orcamento>) gson.fromJson(
            json,
            new TypeToken<ArrayList<Orcamento>>() {}.getType()
        );
    }

    public void setActivityAux(Activity aux) {
        this.activity = aux;
    }

    public void showMessageSuccess(Context ctx, CharSequence text) {
        // Get the custom layout view.
        View toastView = getLayoutInflater().inflate(R.layout.toast_success, null);

        TextView tv = (TextView)toastView.findViewById(R.id.customToastText);
        tv.setText(text);

        // Initiate the Toast instance.
        Toast toast = new Toast(ctx);
        // Set custom view in toast.
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0,0);
        toast.show();
    }

    public void showMessageError(Context ctx, CharSequence text) {
        // Get the custom layout view.
        View toastView = getLayoutInflater().inflate(R.layout.toast_error, null);

        TextView tv = (TextView)toastView.findViewById(R.id.customToastText);
        tv.setText(text);

        // Initiate the Toast instance.
        Toast toast = new Toast(ctx);
        // Set custom view in toast.
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0,0);
        toast.show();
    }
}
