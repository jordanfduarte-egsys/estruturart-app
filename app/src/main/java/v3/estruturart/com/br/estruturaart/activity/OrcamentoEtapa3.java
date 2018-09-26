package v3.estruturart.com.br.estruturaart.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.text.Editable;
import android.text.Selection;
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
import v3.estruturart.com.br.estruturaart.helper.TableListOrcamentoEtapa3;
import v3.estruturart.com.br.estruturaart.model.Orcamento;
import v3.estruturart.com.br.estruturaart.model.TbModelo;
import v3.estruturart.com.br.estruturaart.service.Client;
import v3.estruturart.com.br.estruturaart.utility.AsyncResponse;
import v3.estruturart.com.br.estruturaart.utility.AsyncTaskCustom;
import v3.estruturart.com.br.estruturaart.utility.MaskEditUtil;
import v3.estruturart.com.br.estruturaart.utility.Param;
import v3.estruturart.com.br.estruturaart.utility.Util;

public class OrcamentoEtapa3 extends AbstractActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,  AsyncResponse {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Metodos iniciais
        super.complementOnCreate();

        // Atribui qual é a view
        setContentView(R.layout.activity_orcamento_etapa3);

        initNavigationBar().setNavigationItemSelectedListener(this);

        getBootstrapButton(R.id.btVoltar).setOnClickListener(this);
        getBootstrapButton(R.id.btCriarOrcamento).setOnClickListener(this);
        getBootstrapButton(R.id.btCriarPedido).setOnClickListener(this);
        initResumo();
        loadValues();
        initMask();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btVoltar:
            break;
            case R.id.btCriarOrcamento:
            break;
            case R.id.btCriarPedido:
            break;
        }
    }

    @Override
    public String onExecTask(String result, int id) {
        return null;
    }

    @Override
    public String onPreTask(String result, int id) {
        getProgressBar(R.id.progressBar1).setVisibility(View.VISIBLE);
        return null;
    }

    @Override
    public String onPosTask(String result, int id) {
        getProgressBar(R.id.progressBar1).setVisibility(View.GONE);
        return null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return onNavigationItemSelectedActions(item);
    }

    public void validarFormularioEtapa3() {
        Orcamento orcamento = (Orcamento)getOrcamentoSession(Orcamento.class.getName().toString());
        orcamento.getModelos().clear();

        if (orcamento.isValid(Orcamento.ETAPA3)) {
            putOrcamentoSession(orcamento, Orcamento.class.getName().toString());
            //this.startActivity(new Intent(this, OrcamentoEtapa2.class));
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

    public void initResumo() {
        Orcamento orcamento = (Orcamento)getOrcamentoSession(Orcamento.class.getName().toString());
        TableListOrcamentoEtapa3 tableListOrcamentoEtapa3 = new TableListOrcamentoEtapa3(
            orcamento.getModelos(),
            (TableLayout)findViewById(R.id.tbListItensResumo)
        );

        tableListOrcamentoEtapa3.create(this);
    }

    public void loadValues() {
        Orcamento orcamento = (Orcamento)getOrcamentoSession(Orcamento.class.getName().toString());
        getEditText(R.id.precoTotalItens).setText(orcamento.getPrecoItensGeralString());
        getEditText(R.id.qtdTotalItens).setText(orcamento.getModelos().size());
        getEditText(R.id.prevEntrega).setText(orcamento.getPrevEntregaString());
        getEditText(R.id.maxDesconto).setText(orcamento.getPorcentagemMaximaSomaString() + " %");
        getEditText(R.id.desconto).setText(String.valueOf(orcamento.getDesconto()) + " %");
        getEditText(R.id.subTotal).setText("R$ " + String.valueOf(orcamento.getPrecoSubTotalString()));
    }

    public void initMask() {
        getEditText(R.id.prevEntrega).addTextChangedListener(MaskEditUtil.mask((EditText) getEditText(R.id.prevEntrega), MaskEditUtil.FORMAT_DATE));

        EditText desconto = getEditText(R.id.desconto);
        desconto.addTextChangedListener(Util.getMaskValidatorFloat(desconto));
    }
}