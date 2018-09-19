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
import v3.estruturart.com.br.estruturaart.model.Orcamento;
import v3.estruturart.com.br.estruturaart.model.TbCidade;
import v3.estruturart.com.br.estruturaart.model.TbEndereco;
import v3.estruturart.com.br.estruturaart.model.TbEstado;
import v3.estruturart.com.br.estruturaart.model.TbPerfil;
import v3.estruturart.com.br.estruturaart.model.TbUsuario;
import v3.estruturart.com.br.estruturaart.service.Client;
import v3.estruturart.com.br.estruturaart.utility.AsyncResponse;
import v3.estruturart.com.br.estruturaart.utility.AsyncTaskCustom;
import v3.estruturart.com.br.estruturaart.utility.MaskEditUtil;

public class OrcamentoEtapa1 extends AbstractActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,  AsyncResponse {

    private TbUsuario usuarioCompra = new TbUsuario();
    private CepModel cepModelCompra = new CepModel();
    private TbCidade cidadeAutocomplete = new TbCidade();
    private List<TbEstado> estados = new ArrayList<TbEstado>();
    private List<TbCidade> cidades = new ArrayList<TbCidade>();
    private static final int ASYNC_FIND_CPF_CNPJ = 1;
    private static final int ASYNC_FIND_CEP = 2;
    private static final int ASYNC_FIND_ESTADO = 3;
    private static final int ASYNC_FIND_CIDADE = 4;

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
        loadCidades();
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
        final Activity ac = (Activity)this;
        getTextView(R.id.etCep).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                AsyncTaskCustom async = new AsyncTaskCustom(ASYNC_FIND_CEP);
                async.delegate = (AsyncResponse) ac;
                async.execute();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public String onFindEstadoTask() {
        ArrayAdapter<TbEstado> dataAdapter = (ArrayAdapter<TbEstado>)getSpinner(R.id.spEstado).getAdapter();
        dataAdapter.addAll(estados);
        getSpinner(R.id.spEstado).setAdapter(dataAdapter);
        return null;
    }

    public String onFindCidadeTask() {
        ArrayAdapter<TbCidade> dataAdapter = (ArrayAdapter<TbCidade>)getSpinner(R.id.spCidade).getAdapter();
        dataAdapter.clear();
        TbCidade cidadeDefault = new TbCidade();
        cidadeDefault.setNome("Selecione");
        dataAdapter.add(cidadeDefault);
        dataAdapter.addAll(cidades);
        getSpinner(R.id.spCidade).setAdapter(dataAdapter);

        if (cidadeAutocomplete.getId() > 0) {
            getPositionSpinnerByListObject(getSpinner(R.id.spCidade), cidadeAutocomplete);
        }

        getSpinner(R.id.spCidade).setFocusable(true);
        getSpinner(R.id.spCidade).setFocusableInTouchMode(true);
        getSpinner(R.id.spCidade).requestFocus();
        return null;
    }

    public String onFindCepTask() {
        boolean enableElem = true;
        cidadeAutocomplete = new TbCidade();
        if (cepModelCompra instanceof CepModel && cepModelCompra.getId() > 0) {
            enableElem = false;
            getEditText(R.id.etLogradouro).setText(cepModelCompra.getLogradouro());
            getPositionSpinnerByListObject(getSpinner(R.id.spEstado), cepModelCompra.getCidade().getEstado());
            cidadeAutocomplete = cepModelCompra.getCidade();
            loadCidades();
            getEditText(R.id.tvBairro).setText(cepModelCompra.getBairro());
            getEditText(R.id.tvNumero).setFocusable(true);
            getEditText(R.id.tvNumero).setFocusableInTouchMode(true);
            getEditText(R.id.tvNumero).requestFocus();
        } else {
            enableElem = true;
            getEditText(R.id.etLogradouro).setText("");
            getSpinner(R.id.spEstado).setSelection(0);
            getSpinner(R.id.spCidade).setSelection(0);
            getEditText(R.id.tvBairro).setText("");
            getEditText(R.id.tvNumero).setText("");
        }

        if (!enableElem) {
            getValidator(0).validate();
        }
        getEditText(R.id.etLogradouro).setEnabled(enableElem);
        getSpinner(R.id.spEstado).setEnabled(enableElem);
        getEditText(R.id.tvBairro).setEnabled(enableElem);

        return null;
    }

    public String onFindCpfCnpjTask() {
        boolean enableElem = true;

        if (usuarioCompra instanceof TbUsuario && usuarioCompra.getId() > 0) {
            enableElem = false;
            getEditText(R.id.edRgInscricaoEstadual).setText(usuarioCompra.getRgIncricaoEstadual());
            getEditText(R.id.edNomeCompleto).setText(usuarioCompra.getNome());
            getEditText(R.id.edCelular).setText(usuarioCompra.getTelefone());
            getEditText(R.id.edEmail).setText(usuarioCompra.getEmail());
        } else {
            enableElem = true;
            getEditText(R.id.edRgInscricaoEstadual).setText("");
            getEditText(R.id.edNomeCompleto).setText("");
            getEditText(R.id.edCelular).setText("");
            getEditText(R.id.edEmail).setText("");
        }

        if (!enableElem) {
            getValidator(0).validate();
        }
        getEditText(R.id.edRgInscricaoEstadual).setEnabled(enableElem);
        getEditText(R.id.edNomeCompleto).setEnabled(enableElem);
        getEditText(R.id.edCelular).setEnabled(enableElem);
        getEditText(R.id.edEmail).setEnabled(enableElem);

        return null;
    }

    public void bindCpfCnpj() {
        final Activity ac = (Activity)this;
        getTextView(R.id.etCpfCnpj).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                AsyncTaskCustom async = new AsyncTaskCustom(ASYNC_FIND_CPF_CNPJ);
                async.delegate = (AsyncResponse) ac;
                async.execute();
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
            "^(?:(?:\\+|00)?(55)\\s?)?(?:\\(?([1-9][0-9])\\)?\\s?)?(?:((?:9\\d|[2-9])\\d{3})\\-?(\\d{4}))$",
            R.string.orc_celular_erro//^(\([0-9]{2}\))\s([9]{1})?([0-9]{4})-([0-9]{4})$
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
            "^[0-9]{2}[0-9]{3}-[0-9]{3}$",
            R.string.orc_cep_erro
        );


        getValidator(0).addValidation(
            this,
            R.id.etLogradouro,
            "[a-zA-Z\\wÀ-ú 0-9]+",
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
            "[a-zA-Z\\wÀ-ú 0-9]+",
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
        final Activity ac = (Activity)this;
        List<TbEstado> estados = new ArrayList<TbEstado>();
        TbEstado estadoDefault = new TbEstado();
        estadoDefault.setNome("Selecione");
        estados.add(estadoDefault);
        final ArrayAdapter<TbEstado> dataAdapter = new ArrayAdapter<TbEstado>(this, android.R.layout.simple_spinner_item, estados);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        getSpinner(R.id.spEstado).setAdapter(dataAdapter);
        getSpinner(R.id.spEstado).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cidadeAutocomplete.setEstado((TbEstado) dataAdapter.getItem(i));
                if (cidadeAutocomplete.getEstado().getId() > 0) {
                    AsyncTaskCustom async = new AsyncTaskCustom(ASYNC_FIND_CIDADE);
                    async.delegate = (AsyncResponse) ac;
                    async.execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        AsyncTaskCustom async = new AsyncTaskCustom(ASYNC_FIND_ESTADO);
        async.delegate = (AsyncResponse) this;
        async.execute();
    }

    public void loadCidades() {
        List<TbCidade> cidades = new ArrayList<TbCidade>();
        TbCidade cidadeDefault = new TbCidade();
        cidadeDefault.setNome("Selecione");
        cidades.add(cidadeDefault);
        ArrayAdapter<TbCidade> dataAdapter = new ArrayAdapter<TbCidade>(this, android.R.layout.simple_spinner_item, cidades);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        getSpinner(R.id.spCidade).setAdapter(dataAdapter);
    }

    public void validarSubmitEtapa1() {
        if (getValidator(0).validate()) {
            TbUsuario usuario = new TbUsuario();
            TbEndereco endereco = new TbEndereco();
            Orcamento orcamento = (Orcamento)getOrcamentoSession(Orcamento.class.getName().toString());

            String tipoPessoa = "2";
            String tipoPessoaName = "Pessoa jurídica";
            if (MaskEditUtil.unmask(getEditText(R.id.etCpfCnpj).getText().toString()).length() == 11)  {
                tipoPessoa = "1";
                tipoPessoaName = "Pessoa física";
            }

            if (usuarioCompra.getId() > 0) {
                usuario = usuarioCompra;
                usuario.setTipoPessoaNome(tipoPessoaName);
            } else {
                usuario.setNome(getEditText(R.id.edNomeCompleto).getText().toString());
                usuario.setCpfCnpj(getEditText(R.id.etCpfCnpj).getText().toString());
                usuario.setEmail(getEditText(R.id.edEmail).getText().toString());
                usuario.setRgIncricaoEstadual(getEditText(R.id.edRgInscricaoEstadual).getText().toString());
                usuario.setTelefone(getEditText(R.id.edCelular).getText().toString());
                usuario.setPerfilId(TbPerfil.CLIENTE);
                usuario.setTipoPessoa(tipoPessoa);
                usuario.setTipoPessoaNome(tipoPessoaName);
            }

            endereco.setCep(MaskEditUtil.unmask(getEditText(R.id.etCep).getText().toString()));
            endereco.setLogradouro(getEditText(R.id.etLogradouro).getText().toString());
            endereco.setBairro(getEditText(R.id.tvBairro).getText().toString());
            endereco.setNumero(getEditText(R.id.tvNumero).getText().toString());
            endereco.setCidadeId(((TbCidade)getSpinner(R.id.spCidade).getSelectedItem()).getId());
            endereco.setEstadoId(((TbEstado)getSpinner(R.id.spEstado).getSelectedItem()).getId());

            orcamento.setUsuario(usuario);
            orcamento.setEndereco(endereco);

            // @TODO Validar todos esses campos com isValid()
            if (orcamento.isValid(Orcamento.ETAPA1)) {
                putOrcamentoSession(orcamento, Orcamento.class.getName().toString());
                // Instancia a nova INTENT
            } else {
                String mensagem = orcamento.getFirstMessage();
                showMessage(this, mensagem);
            }
        }
    }

    @Override
    public String onExecTask(String result, int id) {
        if (id == ASYNC_FIND_CPF_CNPJ){
            usuarioCompra = new TbUsuario();

            if (
                MaskEditUtil.unmask(getEditText(R.id.etCpfCnpj).getText().toString()).length() == 11
                || MaskEditUtil.unmask(getEditText(R.id.etCpfCnpj).getText().toString()).length() == 14
            ) {
                Client client = new Client(this);
                client.getParameter().put("cpf_cnpj", MaskEditUtil.unmask(getEditText(R.id.etCpfCnpj).getText().toString()));
                usuarioCompra = (TbUsuario) client.fromPost("/find-cpf-cnpj", TbUsuario.class);
            }
        } else if (id == ASYNC_FIND_CEP) {
            cepModelCompra = new CepModel();
            if (MaskEditUtil.unmask(getEditText(R.id.etCep).getText().toString().toString()).length() == 8) {
                Client client = new Client(this);
                client.getParameter().put("cep", MaskEditUtil.unmask(getTextView(R.id.etCep).getText().toString()));
                cepModelCompra = (CepModel) client.fromPost("/find-cep-object", CepModel.class);
            }
        } else if (id == ASYNC_FIND_ESTADO) {
            Client client = new Client(this);
            estados = (List<TbEstado>) client.fromGet("/find-estados", new TypeToken<ArrayList<TbEstado>>(){}.getType());
        } else if (id == ASYNC_FIND_CIDADE) {
            if (cidadeAutocomplete.getEstado().getId() > 0) {
                Client client = new Client(this);
                client.getParameter().put("estado_id", String.valueOf(cidadeAutocomplete.getEstado().getId()));
                cidades = ((List<TbCidade>) client.fromPost("/find-cidades", new TypeToken<ArrayList<TbCidade>>() {
                }.getType()));
            }
        }

        return null;
    }

    @Override
    public String onPreTask(String result, int id) {
        getProgressBar(R.id.progressBar1).setVisibility(View.GONE);
        return null;
    }

    @Override
    public String onPosTask(String result, int id) {
        if (id == ASYNC_FIND_CPF_CNPJ) {
            onFindCpfCnpjTask();
        } else if (id == ASYNC_FIND_CEP) {
            onFindCepTask();
        } else if (id == ASYNC_FIND_ESTADO) {
            onFindEstadoTask();
        } else if (id == ASYNC_FIND_CIDADE) {
            onFindCidadeTask();
        }

        getProgressBar(R.id.progressBar1).setVisibility(View.GONE);
        return null;
    }
}