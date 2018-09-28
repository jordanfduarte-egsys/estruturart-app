package v3.estruturart.com.br.estruturaart.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.gson.reflect.TypeToken;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

import static android.graphics.Color.WHITE;

public class OrcamentoEtapa3 extends AbstractActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,  AsyncResponse {
    public final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

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
        initValidator();
        loadValues();
        initMask();
        bindDesconto();
        bindMaoObra();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btVoltar:
                this.startActivity(new Intent(this, OrcamentoEtapa2.class));
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

    public void initValidator() {
        getValidator(0).addValidation(
            this,
            R.id.prevEntrega,
            "([0-2][0-9]|3[0-1])/(0[0-9]|1[0-2])/[0-9]{4}",
            R.string.orc_dta_prev
        );

        getValidator(0).addValidation(
            this,
            R.id.desconto,
            "^[+-]?([0-9]*[,])?[0-9]+$",
            R.string.orc_desconto
        );
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
        System.out.println("\n\n\nJSON=================\n\n\n");
        System.out.println(orcamento.toJson());
        System.out.println("\n\nJSON=================\n\n");
        getEditText(R.id.precoTotalItens).setText("R$ " + orcamento.getPrecoItensGeralString());
        getEditText(R.id.qtdTotalItens).setText(String.valueOf(orcamento.getModelos().size()));
        getEditText(R.id.prevEntrega).setText(orcamento.getPrevEntregaString());
        getEditText(R.id.maxDesconto).setText(orcamento.getPorcentagemMaximaSomaString() + " %");
        getEditText(R.id.desconto).setText(orcamento.getDescontoString());
        getEditText(R.id.subTotal).setText("R$ " + String.valueOf(orcamento.getPrecoSubTotalString()));
        getEditText(R.id.maoObra).setText(orcamento.getValorMaoObraString());
    }

    public void initMask() {
        getEditText(R.id.prevEntrega).addTextChangedListener(MaskEditUtil.mask((EditText) getEditText(R.id.prevEntrega), MaskEditUtil.FORMAT_DATE));

        EditText desconto = getEditText(R.id.desconto);
        desconto.addTextChangedListener(Util.getMaskValidatorFloat(desconto));
    }

    public void bindDesconto() {
        final Orcamento orcamento = (Orcamento)getOrcamentoSession(Orcamento.class.getName().toString());
        final Context v = this;
        getEditText(R.id.desconto).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                float totalItensSemPintura = orcamento.getTotalItensAcrescimoSemPintura();
                String maoObraStr = getEditText(R.id.maoObra, v).getText().toString().replace(",", ".").replace(".", "").replace("R$", "").trim();
                float maoObra = Float.valueOf(maoObraStr.equals("") ? "0" : maoObraStr);
                float porcentagemDesconto = Float.valueOf(getEditText(R.id.desconto, v).getText().toString().replace(".", ",").replace(",", "."));
                float totalPintura = orcamento.getTotalPintura();
                float subTotal = calcSubTotal(totalItensSemPintura, totalPintura, porcentagemDesconto, maoObra);


                getEditText(R.id.subTotal, v).setText("R$ " + numberFormat.format(subTotal).replace("R$", "").trim());
                float porcentagemMaxima = Float.valueOf(getEditText(R.id.maxDesconto, v).getText().toString().replace(".", ",").replace(",", ".").replace("%", "").trim());

                if (porcentagemDesconto > porcentagemMaxima) {
                    getEditText(R.id.subTotal, v).setTextColor(Color.RED);
                } else {
                    getEditText(R.id.subTotal, v).setTextColor(WHITE);
                }
            }
        });
    }

    public void bindMaoObra() {
        final Orcamento orcamento = (Orcamento)getOrcamentoSession(Orcamento.class.getName().toString());
        final Context v = this;
        getEditText(R.id.maoObra).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                float totalItensSemPintura = orcamento.getTotalItensAcrescimoSemPintura();
                String maoObraStr = getEditText(R.id.maoObra, v).getText().toString().replace(",", ".").replace(".", "").replace("R$", "").trim();
                float maoObra = Float.valueOf(maoObraStr.equals("") ? "0" : maoObraStr);
                float porcentagemDesconto = Float.valueOf(getEditText(R.id.desconto, v).getText().toString().replace(".", ",").replace(",", "."));
                float totalPintura = orcamento.getTotalPintura();
                float subTotal = calcSubTotal(totalItensSemPintura, totalPintura, porcentagemDesconto, maoObra);


                getEditText(R.id.subTotal, v).setText("R$ " + numberFormat.format(subTotal).replace("R$", "").trim());
                float porcentagemMaxima = Float.valueOf(getEditText(R.id.maxDesconto, v).getText().toString().replace(".", ",").replace(",", ".").replace("%", "").trim());

                if (porcentagemDesconto > porcentagemMaxima) {
                    getEditText(R.id.subTotal, v).setTextColor(Color.RED);
                } else {
                    getEditText(R.id.subTotal, v).setTextColor(WHITE);
                }
            }
        });
    }

    /**
    * float totalItensSemPintura = orcamento.getTotalItensAcrescimoSemPintura();
    * float maoObra = getEditText(R.id.maoObra).getText().toString().replace(".", ",").replace(",", ".");
    * float porcentagemDesconto = getEditText(R.id.desconto).getText().replace(".", ",").replace(",", ".");
    * float totalPintura = orcamento.getTotalPintura();
    * float subTotal = calcSubTotal(totalItensSemPintura, totalPintura, porcentagemDesconto, maoObra)
    *
    * NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    *
    * getEditText(R.id.subTotal).setText("R$ " + numberFormat.format(subTotal).replace("R$", "").trim());
    * float porcentagemMaxima = getEditText(R.id.maxDesconto).setText().replace(".", ",").replace(",", ".");
    * if (porcentagemDesconto > porcentagemMaxima) {
    *    getEditText(R.id.subTotal).setTextColor(Color.RED);
    * } else {
    *    getEditText(R.id.subTotal).setTextColor(R.android.color.transparent);
    * }
    *
    *
    **/
    public float calcSubTotal(float totalItensSemPintura, float totalPintura, float porcentagemDesconto, float maoObra) {
        float total = totalItensSemPintura - ((totalItensSemPintura * porcentagemDesconto) / 100);
        total += totalPintura + maoObra;

        return total;
    }
}