package v3.estruturart.com.br.estruturaart.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.ValidationHolder;
import com.basgeekball.awesomevalidation.utility.custom.CustomErrorReset;
import com.basgeekball.awesomevalidation.utility.custom.CustomValidation;
import com.basgeekball.awesomevalidation.utility.custom.CustomValidationCallback;

import java.util.ArrayList;
import java.util.List;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.utility.MaskEditUtil;

public class OrcamentoEtapa1 extends AbstractActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

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
        getTextView(R.id.etCep).setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (getTextView(R.id.etCep).getText().length() == 8) {
                        // @TODO FIND CEP
                        // @TODO Implementar o showloadind
                    }
                }

                return false;
            }
        });
    }

    public void bindCpfCnpj() {
        getTextView(R.id.etCpfCnpj).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (
                    MaskEditUtil.unmask(getTextView(R.id.etCpfCnpj).getText().toString()).length() == 11
                    || MaskEditUtil.unmask(getTextView(R.id.etCpfCnpj).getText().toString()).length() == 14
                ) {
                    // @TODO FIND USER BY CPF OR CNPJ
                    // @TODO Implementar o showloadind
                }
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
        //@TODO implementar listagem aqui de cidades
        getSpinner(R.id.spEstado).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //@TODO consultar as estados aqui
        List<String> categories = new ArrayList<String>();
        categories.add("Selecione");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        getSpinner(R.id.spEstado).setAdapter(dataAdapter);
    }

    public void loadCidades() {
        //@TODO implementar listagem aqui de estados
        getSpinner(R.id.spCidade).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //@TODO consultar as estados aqui
        List<String> categories = new ArrayList<String>();
        categories.add("Selecione");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        getSpinner(R.id.spCidade).setAdapter(dataAdapter);
    }

    public void validarSubmitEtapa1() {
        if (getValidator(0).validate()) {
            // OK AVANCAR ETAPA 2
            // CHAMAR SERVICE
        }
    }
}