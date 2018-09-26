package v3.estruturart.com.br.estruturaart.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.content.Context;
import android.widget.TableRow;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapThumbnail;
import java.util.ArrayList;
import java.util.List;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.model.Orcamento;
import v3.estruturart.com.br.estruturaart.model.TbModelo;
import v3.estruturart.com.br.estruturaart.utility.MaskEditUtil;
import v3.estruturart.com.br.estruturaart.utility.Util;

/**
 * Helper para construção do datatable de listagem
 */
public class TableListOrcamentoEtapa2 extends AbstractHelper {
    private List<TbModelo> modelos = new ArrayList<TbModelo>();
    private TableLayout table;
    private TableLayout itens;

    /**
     * @param modelos
     * @param table
     */
    public TableListOrcamentoEtapa2(List<TbModelo> modelos, TableLayout table, TableLayout itens) {
        this.modelos = modelos;
        this.table = table;
        this.itens = itens;
    }

    /**
     * Cria-se o datatable
     * @param content
     * @param content
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void create(Context content) {
        Log.i("3", "CRIANDO LISTA");
        // Recupera a Activity
        final Activity ac = (Activity)content;

        // Habilita ou desabilia a mensagem de filtragem
        TableRow trMessage = (TableRow)ac.findViewById(R.id.facaFiltro);
        trMessage.setVisibility(View.VISIBLE);

        // Remove todos os elementos do filtro
        for (int i = 0, j = table.getChildCount(); i < j; i++) {
            View view = table.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                if (row.getId() != R.id.facaFiltro) {
                    table.removeView(row);
                }
            }
        }

        // Tem itens retornados
        if (modelos.size() > 0) {
            // Desabilita a mensagem de filtragem
            trMessage.setVisibility(View.GONE);

            // Pega os elementos retornados
            for (TbModelo modelo : modelos) {
                View rowModelo = ac.getLayoutInflater().inflate(R.layout.lista_modelos, null);

                // Coloca a imagem no campo
                ImageView tb = (ImageView)rowModelo.findViewById(R.id.imgListaModelo);
                tb.setZ(999);
                tb.setImageBitmap(stringToBitmap(modelo.getBase64Image()));

                // Aplica um nome para o modelo
                ((TextView)rowModelo.findViewById(R.id.nomeModelo)).setText(modelo.getNomeString());

                // Mascar numerica
                getEditText(R.id.valorLargura, rowModelo).addTextChangedListener(Util.getMaskValidatorFloat(getEditText(R.id.valorLargura, rowModelo)));
                getEditText(R.id.valorAltura, rowModelo).addTextChangedListener(Util.getMaskValidatorFloat(getEditText(R.id.valorAltura, rowModelo)));

                // Apresenta a descrição
                ((TextView)rowModelo.findViewById(R.id.modeloDescricao)).setText(modelo.getDescricao());

                BootstrapButton btn = (BootstrapButton)rowModelo.findViewById(R.id.addModelo);
                final EditText edLargura = getEditText(R.id.valorLargura, rowModelo);
                final EditText edAltura = getEditText(R.id.valorAltura, rowModelo);

                edLargura.setTag(modelo);
                edAltura.setTag(modelo);
                btn.setTag(modelo);
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        TbModelo modeloLargura = (TbModelo)edLargura.getTag();
                        TbModelo modeloAltura = (TbModelo)edAltura.getTag();
                        float largura = modeloLargura.getLarguraNova();
                        float altura = modeloAltura.getAlturaNova();

                        try {
                            if (largura <= 0) {
                                edLargura.requestFocus();
                                throw new NumberFormatException("Informe a largura em 'mm'");
                            }

                            if (altura <= 0) {
                                edAltura.requestFocus();
                                throw new NumberFormatException("Informe a altura em 'mm'");
                            }

                            TbModelo modelo = ((TbModelo)v.getTag()).cloneCurrent();
                            modelo.setLarguraNova(largura);
                            modelo.setAlturaNova(altura);
                            v.setTag(modelo);
                            onClickAddModel(v);
                            showMessage(v.getContext(), "Item adicionado ao orçamento!");
                        } catch(NumberFormatException ex) {
                            showMessage(v.getContext(), ex.getMessage());
                        }
                    }
                });

                edLargura.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        TbModelo modeloLargura = (TbModelo)edLargura.getTag();
                        TbModelo modeloAltura = (TbModelo)edAltura.getTag();

                        try {
                            modeloLargura.setLarguraNova(Float.parseFloat(edLargura.getText().toString().replace(",", ".")));
                        } catch (NumberFormatException e) {
                            modeloLargura.setLarguraNova(0);
                        }

                        try {
                            modeloAltura.setAlturaNova(Float.parseFloat(edAltura.getText().toString().replace(",", ".")));
                        } catch (NumberFormatException e) {
                            modeloAltura.setAlturaNova(0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                edAltura.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        TbModelo modeloLargura = (TbModelo)edLargura.getTag();
                        TbModelo modeloAltura = (TbModelo)edAltura.getTag();

                        try {
                            modeloLargura.setLarguraNova(Float.parseFloat(edLargura.getText().toString().replace(",", ".")));
                        } catch (NumberFormatException e) {
                            modeloLargura.setLarguraNova(0);
                        }

                        try {
                            modeloAltura.setAlturaNova(Float.parseFloat(edAltura.getText().toString().replace(",", ".")));
                        } catch (NumberFormatException e) {
                            modeloAltura.setAlturaNova(0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                // Adiciona a view a tabela principal
                table.addView(rowModelo);
            }
        }
    }

    public void onClickAddModel(View v) {
        final TbModelo modelo = (TbModelo)v.getTag();
        final Activity ac = (Activity)v.getContext();
        final View rowModelo = ac.getLayoutInflater().inflate(R.layout.lista_itens_etapa2, null);

        // Habilita ou desabilia a mensagem de filtragem
        TableRow trMessage = (TableRow)ac.findViewById(R.id.nenhumItensAdicionado);
        trMessage.setVisibility(View.GONE);

        // Atribui algumas variaveis
        getTextView(R.id.nomeModeloAdd, rowModelo).setText(modelo.getNomeString());
        getTextView(R.id.medidaItem, rowModelo).setText(modelo.getMedidaString());
        getTextView(R.id.precoItem, rowModelo).setText(String.format("Preço: R$ " + modelo.getPrecoItemTotalComAcrescimoSemPinturaString()));
        getTextView(R.id.descricaoItem, rowModelo).setText(modelo.getDescricao());

        BootstrapButton btnDuplicar = getBootstrapButton(R.id.duplicarItem, rowModelo);
        BootstrapButton btnRemover = getBootstrapButton(R.id.removerItem, rowModelo);
        CheckBox isPintura = rowModelo.findViewById(R.id.pinturaItem);
        final EditText qtd = getEditText(R.id.qtdAdd, rowModelo);

        btnDuplicar.setTag(modelo);
        btnRemover.setTag(modelo);
        btnDuplicar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TbModelo modeloDuplicar = ((TbModelo)v.getTag()).cloneCurrent();
                modeloDuplicar.setAlturaNova(modeloDuplicar.getAlturaPadrao());
                modeloDuplicar.setLarguraNova(modeloDuplicar.getLarguraPadrao());
                modeloDuplicar.setIsPintura(false);
                modeloDuplicar.setQuantidadeCompra(1);
                v.setTag(modeloDuplicar);
                onClickAddModel(v);
                showMessage(v.getContext(), "Item duplicado no orçamento!");
            }
        });

        btnRemover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickRemoverModelo(v);
                showMessage(v.getContext(), "Item removido do orçamento!");
            }
        });

        isPintura.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                modelo.setIsPintura(compoundButton.isChecked());

                if (modelo.getIsPintura()) {
                    getTextView(R.id.precoItem, rowModelo).setText(
                        String.format("Preço: R$ " + modelo.getPrecoItemTotalComAcrescimoComPinturaString())
                    );
                } else {
                    getTextView(R.id.precoItem, rowModelo).setText(
                        String.format("Preço: R$ " + modelo.getPrecoItemTotalComAcrescimoSemPinturaString())
                    );
                }
            }
        });

        qtd.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    if (qtd.getText().toString().equals("0")) {
                        qtd.setText("1");
                        modelo.setQuantidadeCompra(1);
                        return true;
                    } else {
                        System.out.println("TOTAL QTD: " + qtd.getText().toString());
                        modelo.setQuantidadeCompra(Integer.parseInt(qtd.getText().toString()));
                    }

                    if (modelo.getIsPintura()) {
                        getTextView(R.id.precoItem, rowModelo).setText(
                            String.format("Preço: R$ " + modelo.getPrecoItemTotalComAcrescimoComPinturaString())
                        );
                    } else {
                        getTextView(R.id.precoItem, rowModelo).setText(
                            String.format("Preço: R$ " + modelo.getPrecoItemTotalComAcrescimoSemPinturaString())
                        );
                    }
                }

                return false;
            }
        });

        qtd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && qtd.getText().toString().equals("")) {
                    qtd.setText(String.valueOf(modelo.getQuantidadeCompra()));
                }
            }
        });

        modelo.setIndex(nextIndex());
        rowModelo.setTag(modelo);
        itens.addView(rowModelo);
    }

    public void onClickRemoverModelo(View v) {
        TbModelo modeloRemover = (TbModelo)v.getTag();

        for (int i = 0, j = itens.getChildCount(); i < j; i++) {
            View view = itens.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                if (row.getTag() instanceof TbModelo) {
                    TbModelo modelo = (TbModelo)row.getTag();
                    if (modelo.getIndex() == modeloRemover.getIndex()) {
                        itens.removeView(row);
                    }
                }
            }
        }

        if (itens.getChildCount() == 1) {
            Activity ac = (Activity)v.getContext();
            TableRow trMessage = (TableRow)ac.findViewById(R.id.nenhumItensAdicionado);
            trMessage.setVisibility(View.VISIBLE);
        }
    }

    public int nextIndex() {
        int maxIndex = 0;
        for (int i = 0, j = itens.getChildCount(); i < j; i++) {
            View view = itens.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                if (row.getTag() instanceof TbModelo) {
                    TbModelo modelo = (TbModelo)row.getTag();
                    if (modelo.getIndex() > maxIndex) {
                        maxIndex = modelo.getIndex();
                    }
                }
            }
        }

        return maxIndex + 1;
    }
}
