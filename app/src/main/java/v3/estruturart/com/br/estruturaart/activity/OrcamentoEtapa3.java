package v3.estruturart.com.br.estruturaart.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import v3.estruturart.com.br.estruturaart.utility.CurrencyEditText;
import v3.estruturart.com.br.estruturaart.utility.DatePickerCustom;
import v3.estruturart.com.br.estruturaart.utility.JsonModel;
import v3.estruturart.com.br.estruturaart.utility.MaskEditUtil;
import v3.estruturart.com.br.estruturaart.utility.Param;
import v3.estruturart.com.br.estruturaart.utility.Util;

import static android.graphics.Color.WHITE;

public class OrcamentoEtapa3 extends AbstractActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, AsyncResponse {
    public final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final int ASYNC_SAVE = 4;
    private JsonModel jsonModel = new JsonModel();
    private String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Metodos iniciais
        super.complementOnCreate();

        // Atribui qual a view
        setContentView(R.layout.activity_orcamento_etapa3);

        initNavigationBar().setNavigationItemSelectedListener(this);

        initResumo();
        initValidator();
        loadValues();
        initMask();
        bindDesconto();
        bindMaoObra();
        bindPrevEntrega();

        getBootstrapButton(R.id.btVoltar).setOnClickListener(this);
        getBootstrapButton(R.id.btCriarOrcamento).setOnClickListener(this);
        getBootstrapButton(R.id.btCriarPedido).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btVoltar:
                this.startActivity(new Intent(this, OrcamentoEtapa2.class));
            break;
            case R.id.btCriarOrcamento:
                salvarOrcamento(true, view);
            break;
            case R.id.btCriarPedido:
                salvarOrcamento(false, view);
            break;
        }
    }

    @Override
    public String onExecTask(String result, int id) {
        if (id == ASYNC_SAVE) {
            Orcamento orcamento = (Orcamento)getOrcamentoSession(Orcamento.class.getName().toString());
            Client client = new Client(this);
            client.getParameter().put("orcamento", orcamento.toJson());
            client.getParameter().put("is_orcamento", orcamento.getIsOrcamento() ? "0" : "1");
            jsonModel = (JsonModel) client.fromPost("/salvar-orcamento", JsonModel.class);

            if (!client.getMessage().equals("")) {
                message = client.getMessage();
            }
        } else if (id == ASYNC_ORCAMENTO_ACCESS) {
            sincronizeOrcamentoInBackground();
        }

        return null;
    }

    @Override
    public String onPreTask(String result, int id) {
        if (id == ASYNC_SAVE) {

        }
        getProgressBar(R.id.progressBar1).setVisibility(View.VISIBLE);
        return null;
    }

    public  boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

    @Override
    public String onPosTask(String result, int id) {
        if (id == ASYNC_SAVE) {
            if (!message.equals("")) {
                //showMessage(this, message);

                @SuppressLint("ResourceType") Snackbar snackbar = Snackbar.make( findViewById(R.layout.activity_orcamento_etapa3), message, Snackbar.LENGTH_LONG)
                    .setAction("Salvar", null);

                View sbView = snackbar.getView();
                sbView.setBackgroundColor(Color.RED);
                snackbar.show();

                if (!verificaConexao()) {
                    Orcamento orcamento = (Orcamento)getOrcamentoSession(Orcamento.class.getName().toString());
                    List<Orcamento> orcamentos = gutSincronizeOrcamento();
                    orcamentos.add(orcamento);
                    putSincronizeOrcamento(orcamentos);

                    final Context ctx = this;
                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Sincronização")
                        .setIcon(android.R.drawable.ic_menu_camera)//@TODO TROCAR ICONE DE FOTO
                        .setMessage("Ocorreu um erro na sincronização. Tente sincronizar novamente mais tarde quando o dispositivo estiver acessível com a internet.")
                        .setIcon(android.R.drawable.stat_notify_error)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showMessage(ctx, "Acesso o menu lateral para tentar sincronizar novamente!");
                                ctx.startActivity(new Intent(ctx, Home.class));
                            }
                        });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));

                    putOrcamentoSession(new Orcamento(), Orcamento.class.getName().toString());
                }

                message = "";
            } else {
                if (!jsonModel.getStatus()) {
                    showMessage(this, jsonModel.getMessage());
                } else {
                    Orcamento orcamento = (Orcamento)getOrcamentoSession(Orcamento.class.getName().toString());
                    putOrcamentoSession(new Orcamento(), Orcamento.class.getName().toString());
                    if (orcamento.getIsOrcamento()) {
                        @SuppressLint("ResourceType") Snackbar snackbar = Snackbar.make(findViewById(R.layout.activity_orcamento_etapa3), "Orçamento realizado com sucesso!", Snackbar.LENGTH_LONG)
                            .setAction("Salvar", null);

                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(Color.GREEN);
                        snackbar.show();

                        //showMessage(this, "Orçamento realizado com sucesso!");
                    } else {
                        @SuppressLint("ResourceType") Snackbar snackbar = Snackbar.make(findViewById(R.layout.activity_orcamento_etapa3), "Pedido realizado com sucesso!", Snackbar.LENGTH_LONG)
                            .setAction("Salvar", null);

                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(Color.GREEN);
                        snackbar.show();

                        //showMessage(this, "Pedido realizado com sucesso!");
                    }
                    this.startActivity(new Intent(this, Home.class));
                }
            }
        } else if (id == ASYNC_ORCAMENTO_ACCESS) {
            sincronizeOrcamentoPost();
        }

        getProgressBar(R.id.progressBar1).setVisibility(View.GONE);
        return null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return onNavigationItemSelectedActions(item);
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

        getValidator(0).addValidation(
            this,
            R.id.maoObra,
            "(^(R\\$ )[+-]?([0-9]*[,])?[0-9]+$)",
            R.string.orc_mao_obra
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
        //getEditText(R.id.prevEntrega).addTextChangedListener(MaskEditUtil.mask((EditText) getEditText(R.id.prevEntrega), MaskEditUtil.FORMAT_DATE));

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

    public void salvarOrcamento(boolean isOrcamento, View v) {
        Orcamento orcamento = (Orcamento)getOrcamentoSession(Orcamento.class.getName().toString());
        if (getValidator(0).validate()) {
            float porcentagemDesconto = Float.valueOf(getEditText(R.id.desconto, v.getContext()).getText().toString().replace(".", ",").replace(",", "."));

            String maoObraStr = getEditText(R.id.maoObra, v.getContext()).getText().toString().replace(",", ".").replace(".", "").replace("R$", "").trim();
            float maoObra = Float.valueOf(maoObraStr.equals("") ? "0" : maoObraStr);
            String obs = getEditText(R.id.observacao).getText().toString();
            orcamento.setDesconto(porcentagemDesconto);
            orcamento.setValorMaoObra(maoObra);
            orcamento.setObservacao(obs);
            orcamento.setIsOrcamento(isOrcamento);

            String dateBr = getEditText(R.id.prevEntrega).getText().toString();
            if (Util.isDateValid(dateBr)) {
                try {
                    orcamento.setPrevEntrega(Util.toDate(dateBr));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (orcamento.isValid(Orcamento.ETAPA3)) {
                putOrcamentoSession(orcamento, Orcamento.class.getName().toString());
                AsyncTaskCustom async = new AsyncTaskCustom(ASYNC_SAVE);
                async.delegate = (AsyncResponse) v.getContext();
                async.execute();
            } else {
                for (Param param : orcamento.getValidation()) {
                    Object ob = findViewById(param.getIndex());
                    System.out.println("ERRO: " + (String)param.getValue());
                    System.out.println("CLASS: " + ob.getClass().getName());

                    if (ob instanceof Spinner) {
                        getValidator(0).validateElement(getSpinner(param.getIndex()), (String)param.getValue());
                    } else if (ob instanceof EditText) {
                        getValidator(0).validateElement(getEditText(param.getIndex()), (String)param.getValue());
                    }
                }
            }
        }
    }

    public void bindPrevEntrega() {
        final Orcamento orcamento = (Orcamento)getOrcamentoSession(Orcamento.class.getName().toString());
        final Context ctx = this;
        getEditText(R.id.prevEntrega).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    DatePickerCustom newFragment = new DatePickerCustom(orcamento, ctx);
                    newFragment.show(getSupportFragmentManager(), "date picker");
                    ((Activity)view.getContext()).findViewById(R.id.observacao).requestFocus();
                }
            }
        });
    }
}