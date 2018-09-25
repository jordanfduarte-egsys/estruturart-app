package v3.estruturart.com.br.estruturaart.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.helper.TableListOrcamentoEtapa2;
import v3.estruturart.com.br.estruturaart.model.Orcamento;
import v3.estruturart.com.br.estruturaart.model.TbModelo;
import v3.estruturart.com.br.estruturaart.service.Client;
import v3.estruturart.com.br.estruturaart.utility.AsyncResponse;
import v3.estruturart.com.br.estruturaart.utility.AsyncTaskCustom;
import v3.estruturart.com.br.estruturaart.utility.Param;

public class OrcamentoEtapa2 extends AbstractActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,  AsyncResponse {

    private static final int ASYNC_FIND_MODELO = 1;

    private List<TbModelo> modelos = new ArrayList<TbModelo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Metodos iniciais
        super.complementOnCreate();

        // Atribui qual é a view
        setContentView(R.layout.activity_orcamento_etapa2);

        initNavigationBar().setNavigationItemSelectedListener(this);

        bindBuscarModelo();

        getValidator(0).addValidation(
            this,
            R.id.etBuscaModelo,
            "([0-9]{2}[\\\\.]?[0-9]{3}[\\\\.]?[0-9]{3}[\\\\/]?[0-9]{4}[-]?[0-9]{2})|([0-9]{3}[\\\\.]?[0-9]{3}[\\\\.]?[0-9]{3}[-]?[0-9]{2})",
            R.string.orc_cpf_cnpj_erro
        );
        getBootstrapButton(R.id.btAvancarEtapa2).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btAvancarEtapa2:
                validarFormularioEtapa2();
                break;
            default: break;
        }
    }

    @Override
    public String onExecTask(String result, int id) {
        switch (id) {
            case ASYNC_FIND_MODELO:
                if (getEditText(R.id.etBuscaModelo).getText().toString().length() > 2) {
                    Client client = new Client(this);
                    client.getParameter().put("nome", getEditText(R.id.etBuscaModelo).getText().toString());
                    modelos = (List<TbModelo>) client.fromPost("/buscar-modelo", new TypeToken<ArrayList<TbModelo>>(){}.getType());
                }
            break;
        }

        return null;
    }

    @Override
    public String onPreTask(String result, int id) {
        getProgressBar(R.id.progressBar1).setVisibility(View.VISIBLE);
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public String onPosTask(String result, int id) {
        switch (id) {
            case ASYNC_FIND_MODELO:
                onFindModeloTask();
            break;
        }

        getProgressBar(R.id.progressBar1).setVisibility(View.GONE);
        return null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return onNavigationItemSelectedActions(item);
    }

    public void bindBuscarModelo() {
        final Activity ac = (Activity)this;
        getTextView(R.id.etBuscaModelo).addTextChangedListener(new TextWatcher() {
            //@TODO back pressed ?
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                AsyncTaskCustom async = new AsyncTaskCustom(ASYNC_FIND_MODELO);
                async.delegate = (AsyncResponse) ac;
                async.execute();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int ii, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onFindModeloTask() {
        //Orcamento orcamento = (Orcamento)getOrcamentoSession(Orcamento.class.getName().toString());
        // @TODO POPULA A TABELA COM A BUSCA E FAZ BIND NOS BUTONS
        TableListOrcamentoEtapa2 tableListOrcamentoEtapa2 = new TableListOrcamentoEtapa2(
            modelos,
            (TableLayout)findViewById(R.id.tbListItensFilter),
            (TableLayout)findViewById(R.id.tbListItensAdicionados)
        );

        tableListOrcamentoEtapa2.create(this);
        modelos.clear();
        getValidator(0).clearElement(getEditText(R.id.etBuscaModelo));
    }

    public void validarFormularioEtapa2() {
        Orcamento orcamento = (Orcamento)getOrcamentoSession(Orcamento.class.getName().toString());
        orcamento.getModelos().clear();
        TableLayout itens = (TableLayout)findViewById(R.id.tbListItensAdicionados);
        for (int i = 0, j = itens.getChildCount(); i < j; i++) {
            View view = itens.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                if (row.getTag() instanceof TbModelo) {
                    orcamento.setModelo((TbModelo)row.getTag());
                }
            }
        }

        getValidator(0).clearElement(getEditText(R.id.etBuscaModelo));
        if (orcamento.isValid(Orcamento.ETAPA2)) {
            putOrcamentoSession(orcamento, Orcamento.class.getName().toString());
            //this.startActivity(new Intent(this, OrcamentoEtapa2.class));

            showMessage(this, "TOTAL: " + orcamento.getModelos().size());
        } else {
            for (Param param : orcamento.getValidation()) {
                Object ob = findViewById(param.getIndex());
                System.out.println("ERRO 2: " + (String)param.getValue());
                System.out.println("CLASS 2: " + ob.getClass().getName());

                if (ob instanceof EditText) {
                    getValidator(0).validateElement(getEditText(param.getIndex()), (String)param.getValue());
                }
            }
        }
    }
}