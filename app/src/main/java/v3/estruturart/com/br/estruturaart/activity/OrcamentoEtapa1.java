package v3.estruturart.com.br.estruturaart.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.ValidationHolder;
import com.basgeekball.awesomevalidation.utility.custom.CustomErrorReset;
import com.basgeekball.awesomevalidation.utility.custom.CustomValidation;
import com.basgeekball.awesomevalidation.utility.custom.CustomValidationCallback;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.model.CepModel;
import v3.estruturart.com.br.estruturaart.model.TbCidade;
import v3.estruturart.com.br.estruturaart.model.TbEstado;
import v3.estruturart.com.br.estruturaart.model.TbUsuario;
import v3.estruturart.com.br.estruturaart.service.Client;
import v3.estruturart.com.br.estruturaart.utility.MaskEditUtil;
import v3.estruturart.com.br.estruturaart.utility.ProgressLoading;

public class OrcamentoEtapa1 extends AbstractActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private TbUsuario usuarioCompra = new TbUsuario();
    private TbCidade cidadeAutocomplete = new TbCidade();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Metodos iniciais
        super.complementOnCreate();

        // Atribui qual é a view
        setContentView(R.layout.activity_orcamento_etapa1);

        initNavigationBar().setNavigationItemSelectedListener(this);

        loadMasks();
        loadEstados();
        loadCidades(new TbEstado());
        initValidationForm();
        bindCep();
        bindCpfCnpj();
        getBootstrapButton(R.id.btAvancarEtapa1).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btAvancarEtapa1:
                validarSubmitEtapa1();
            break;
            default: break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return onNavigationItemSelectedActions(item);
    }

    public void loadMasks() {
        final TextWatcher[] cpfCnpjCallback = {MaskEditUtil.mask((EditText) getTextView(R.id.etCpfCnpj), MaskEditUtil.FORMAT_CPF)};
        getTextView(R.id.etCpfCnpj).addTextChangedListener(cpfCnpjCallback[0]);
        getTextView(R.id.etCpfCnpj).setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (MaskEditUtil.unmask(getTextView(R.id.etCpfCnpj).getText().toString()).length() > 11) {
                        getTextView(R.id.etCpfCnpj).removeTextChangedListener(cpfCnpjCallback[0]);
                        cpfCnpjCallback[0] = MaskEditUtil.mask((EditText) getTextView(R.id.etCpfCnpj), MaskEditUtil.FORMAT_CNPJ);
                        getTextView(R.id.etCpfCnpj).addTextChangedListener(cpfCnpjCallback[0]);
                    } else {
                        getTextView(R.id.etCpfCnpj).removeTextChangedListener(cpfCnpjCallback[0]);
                        cpfCnpjCallback[0] = MaskEditUtil.mask((EditText) getTextView(R.id.etCpfCnpj), MaskEditUtil.FORMAT_CPF);
                        getTextView(R.id.etCpfCnpj).addTextChangedListener(cpfCnpjCallback[0]);
                    }
                }

                return false;
            }
        });


        getTextView(R.id.edCelular).addTextChangedListener(MaskEditUtil.mask((EditText) getTextView(R.id.edCelular), MaskEditUtil.FORMAT_CELULAR));
        getTextView(R.id.etCep).addTextChangedListener(MaskEditUtil.mask((EditText) getTextView(R.id.etCep), MaskEditUtil.FORMAT_CEP));
    }

    public void bindCep() {
        final Client client = new Client(this);
        getTextView(R.id.etCep).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean enableElem = true;
                cidadeAutocomplete = new TbCidade();

                System.out.println("TOTAL LENGTH CEP: " + MaskEditUtil.unmask(getTextView(R.id.etCep).getText().toString()).length());
                if (MaskEditUtil.unmask(charSequence.toString()).length() == 8) {
                    CepModel cepModel = new CepModel();
                    client.getParameter().put("cep", MaskEditUtil.unmask(getTextView(R.id.etCep).getText().toString()));
                    cepModel = (CepModel) client.fromPost("/find-cep-object", CepModel.class);

                    if (cepModel.getId() > 0) {
                        enableElem = false;
                        getEditText(R.id.etLogradouro).setText(cepModel.getLogradouro());
                        getPositionSpinnerByListObject(getSpinner(R.id.spEstado), cepModel.getCidade().getEstado());
                        cidadeAutocomplete = cepModel.getCidade();
                        loadCidades(cepModel.getCidade().getEstado());
                        getEditText(R.id.tvBairro).setText(cepModel.getBairro());
                        getEditText(R.id.tvNumero).setFocusable(true);
                        getEditText(R.id.tvNumero).setFocusableInTouchMode(true);
                        getEditText(R.id.tvNumero).requestFocus();
                    }
                } else {
                    enableElem = true;
                    getEditText(R.id.etLogradouro).setText("");
                    getSpinner(R.id.spEstado).setSelection(0);
                    getSpinner(R.id.spCidade).setSelection(0);
                    getEditText(R.id.tvBairro).setText("");
                    getEditText(R.id.tvNumero).setText("");
                }

                getEditText(R.id.etLogradouro).setEnabled(enableElem);
                getSpinner(R.id.spEstado).setEnabled(enableElem);
                getEditText(R.id.tvBairro).setEnabled(enableElem);
            }


            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void test(final AppCompatActivity ctx) {
        final Client client = new Client(this);
        boolean enableElem = true;
        if (
            MaskEditUtil.unmask(getTextView(R.id.etCpfCnpj).getText().toString()).length() == 11
            || MaskEditUtil.unmask(getTextView(R.id.etCpfCnpj).getText().toString()).length() == 14
        ) {
            TbUsuario usuario = new TbUsuario();
            client.getParameter().put("cpf_cnpj", MaskEditUtil.unmask(getTextView(R.id.etCpfCnpj).getText().toString()));
            usuario = (TbUsuario) client.fromPost("/find-cpf-cnpj", TbUsuario.class);
            usuarioCompra = new TbUsuario();
            if (usuario.getId() > 0) {
                enableElem = false;
                ((TextView)ctx.findViewById(R.id.edRgInscricaoEstadual)).setText(usuario.getRgIncricaoEstadual());
//                getEditText(R.id.edRgInscricaoEstadual).setText(usuario.getRgIncricaoEstadual());
//                getEditText(R.id.edNomeCompleto).setText(usuario.getNome());
//                getEditText(R.id.edCelular).setText(usuario.getTelefone());
//                getEditText(R.id.edEmail).setText(usuario.getEmail());
//                usuarioCompra = usuario;
            }
        } else {
            enableElem = true;
//            getEditText(R.id.edRgInscricaoEstadual).setText("");
//            getEditText(R.id.edNomeCompleto).setText("");
//            getEditText(R.id.edCelular).setText("");
//            getEditText(R.id.edEmail).setText("");
        }

//        getEditText(R.id.edRgInscricaoEstadual).setEnabled(enableElem);
//        getEditText(R.id.edNomeCompleto).setEnabled(enableElem);
//        getEditText(R.id.edCelular).setEnabled(enableElem);
//        getEditText(R.id.edEmail).setEnabled(enableElem);
    }

    public void bindCpfCnpj() {

        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar1);
        final OrcamentoEtapa1 orcamentoEtapa1 = (OrcamentoEtapa1)this;

        getTextView(R.id.etCpfCnpj).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final CharSequence charSequence1 = charSequence;
                (new ProgressLoading(progressBar, orcamentoEtapa1)).execute();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void initValidationForm() {
        getValidator(0).addValidation(
            this,
            R.id.etCpfCnpj,
            "([0-9]{2}[\\\\.]?[0-9]{3}[\\\\.]?[0-9]{3}[\\\\/]?[0-9]{4}[-]?[0-9]{2})|([0-9]{3}[\\\\.]?[0-9]{3}[\\\\.]?[0-9]{3}[-]?[0-9]{2})",
            R.string.orc_cpf_cnpj_erro
        );

        getValidator(0).addValidation(
            this,
            R.id.edRgInscricaoEstadual,
            "[a-zA-Z 0-9]+",
            R.string.orc_rg_ins_est_erro
        );

        getValidator(0).addValidation(
            this,
            R.id.edNomeCompleto,
            "[A-Z][a-z]* [A-Z][a-z]*",
            R.string.orc_nome_erro
        );

        getValidator(0).addValidation(
            this,
            R.id.edCelular,
            "/^(?:(?:\\+|00)?(55)\\s?)?(?:\\(?([1-9][0-9])\\)?\\s?)?(?:((?:9\\d|[2-9])\\d{3})\\-?(\\d{4}))$/",
            R.string.orc_celular_erro
        );

        getValidator(0).addValidation(
            this,
            R.id.edEmail,
            "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$",
            R.string.orc_email_erro
        );

        getValidator(0).addValidation(
            this,
            R.id.etCep,
            "[a-zA-Z 0-9]+",
            R.string.orc_cep_erro
        );


        getValidator(0).addValidation(
            this,
            R.id.etLogradouro,
            "[a-zA-Z 0-9]+",
            R.string.orc_logradouro_erro
        );

        getValidator(0).addValidation(
            this,
            R.id.tvNumero,
            "[0-9]+",
            R.string.orc_numero_erro
        );

        getValidator(0).addValidation(
            this,
            R.id.tvBairro,
            "[a-zA-Z 0-9]+",
            R.string.orc_bairro_erro
        );

        getValidator(0).addValidation(this, R.id.spEstado, new CustomValidation() {
            @Override
            public boolean compare(ValidationHolder validationHolder) {
                if (((Spinner) validationHolder.getView()).getSelectedItem().toString().equals("Selecione")) {
                    return false;
                } else {
                    return true;
                }
            }
        }, new CustomValidationCallback() {
            @Override
            public void execute(ValidationHolder validationHolder) {
                TextView textViewError = (TextView) ((Spinner) validationHolder.getView()).getSelectedView();
                textViewError.setError(validationHolder.getErrMsg());
                textViewError.setTextColor(Color.RED);
            }
        }, new CustomErrorReset() {
            @Override
            public void reset(ValidationHolder validationHolder) {
                TextView textViewError = (TextView) ((Spinner) validationHolder.getView()).getSelectedView();
                textViewError.setError(null);
                textViewError.setTextColor(Color.BLACK);
            }
        }, R.string.orc_estado_erro);

        getValidator(0).addValidation(this, R.id.spCidade, new CustomValidation() {
            @Override
            public boolean compare(ValidationHolder validationHolder) {
                if (((Spinner) validationHolder.getView()).getSelectedItem().toString().equals("Selecione")) {
                    return false;
                } else {
                    return true;
                }
            }
        }, new CustomValidationCallback() {
            @Override
            public void execute(ValidationHolder validationHolder) {
                TextView textViewError = (TextView) ((Spinner) validationHolder.getView()).getSelectedView();
                textViewError.setError(validationHolder.getErrMsg());
                textViewError.setTextColor(Color.RED);
            }
        }, new CustomErrorReset() {
            @Override
            public void reset(ValidationHolder validationHolder) {
                TextView textViewError = (TextView) ((Spinner) validationHolder.getView()).getSelectedView();
                textViewError.setError(null);
                textViewError.setTextColor(Color.BLACK);
            }
        }, R.string.orc_cidade_erro);
    }

    public void loadEstados() {
        List<TbEstado> estados = new ArrayList<TbEstado>();
        Client client = new Client(this);
        TbEstado estadoDefault = new TbEstado();
        estadoDefault.setNome("Selecione");

        estados.add(estadoDefault);
        estados.addAll((List<TbEstado>) client.fromGet("/find-estados", new TypeToken<ArrayList<TbEstado>>(){}.getType()));
        final ArrayAdapter<TbEstado> dataAdapter = new ArrayAdapter<TbEstado>(this, android.R.layout.simple_spinner_item, estados);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        getSpinner(R.id.spEstado).setAdapter(dataAdapter);

        getSpinner(R.id.spEstado).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TbEstado estado = (TbEstado) dataAdapter.getItem(i);
                loadCidades(estado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    public void loadCidades(TbEstado estado) {
        List<TbCidade> cidades = new ArrayList<TbCidade>();
        TbCidade cidadeDefault = new TbCidade();
        cidadeDefault.setNome("Selecione");
        cidades.add(cidadeDefault);

        if (estado.getId() > 0) {
            Client client = new Client(this);
            client.getParameter().put("estado_id", String.valueOf(estado.getId()));
            cidades.addAll((List<TbCidade>) client.fromPost("/find-cidades", new TypeToken<ArrayList<TbCidade>>(){}.getType()));
            getSpinner(R.id.spCidade).setFocusable(true);
            getSpinner(R.id.spCidade).setFocusableInTouchMode(true);
            getSpinner(R.id.spCidade).requestFocus();
        }

        ArrayAdapter<TbCidade> dataAdapter = new ArrayAdapter<TbCidade>(this, android.R.layout.simple_spinner_item, cidades);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        getSpinner(R.id.spCidade).setAdapter(dataAdapter);

        if (cidadeAutocomplete.getId() > 0) {
            getPositionSpinnerByListObject(getSpinner(R.id.spCidade), cidadeAutocomplete);
        }
    }

    public void validarSubmitEtapa1() {
        if (getValidator(0).validate()) {
            // OK AVANCAR ETAPA 2
            // CHAMAR SERVICE
        }
    }
}