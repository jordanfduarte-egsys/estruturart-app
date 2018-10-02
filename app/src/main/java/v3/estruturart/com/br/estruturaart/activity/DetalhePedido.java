package v3.estruturart.com.br.estruturaart.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TableLayout;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.ValidationHolder;
import com.basgeekball.awesomevalidation.utility.custom.CustomErrorReset;
import com.basgeekball.awesomevalidation.utility.custom.CustomValidation;
import com.basgeekball.awesomevalidation.utility.custom.CustomValidationCallback;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.model.CepModel;
import v3.estruturart.com.br.estruturaart.model.Orcamento;
import v3.estruturart.com.br.estruturaart.model.TbCidade;
import v3.estruturart.com.br.estruturaart.model.TbEndereco;
import v3.estruturart.com.br.estruturaart.model.TbEstado;
import v3.estruturart.com.br.estruturaart.model.TbPedido;
import v3.estruturart.com.br.estruturaart.model.TbPedidoItem;
import v3.estruturart.com.br.estruturaart.model.TbPerfil;
import v3.estruturart.com.br.estruturaart.model.TbUsuario;
import v3.estruturart.com.br.estruturaart.service.Client;
import v3.estruturart.com.br.estruturaart.utility.AsyncResponse;
import v3.estruturart.com.br.estruturaart.utility.AsyncTaskCustom;
import v3.estruturart.com.br.estruturaart.utility.MaskEditUtil;
import v3.estruturart.com.br.estruturaart.utility.Param;

public class DetalhePedido extends AbstractActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, AsyncResponse {

    private TbPedido pedido = new TbPedido();
    private String message = "";
    private static final int ASYNC_SEND_PHOTO = 1;
    private static final int ASYNC_PEDIDO = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Metodos iniciais
        super.complementOnCreate();

        // Atribui qual é a view
        setContentView(R.layout.activity_detalhe_pedido);

        // Recupera o id do pedido
        pedido.setId(Integer.parseInt(getIntent().getExtras().getString("id")));

        //Busca o pedido
        findPedido();

        initNavigationBar().setNavigationItemSelectedListener(this);
    }
        @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public String onExecTask(String result, int id) {
        if (id == ASYNC_PEDIDO) {
            Client client = new Client(this);
            client.getParameter().put("id", String.valueOf(pedido.getId()));
            pedido = (TbPedido) client.fromPost("/detalhe-pedido", TbPedido.class);

            if (!client.getMessage().equals("")) {
                message = client.getMessage();
            }
        }
        return null;
    }

    @Override
    public String onPreTask(String result, int id) {
        getProgressBar(R.id.progressBar1).setVisibility(View.VISIBLE);
        return null;
    }

    @Override
    public String onPosTask(String result, int id) {
        if (id == ASYNC_PEDIDO) {
            popularPedido();
        }

        if (!message.equals("")) {
            showMessage(this, message);
            message = "";
        }
        getProgressBar(R.id.progressBar1).setVisibility(View.GONE);
        return null;
    }

    public void findPedido() {
        AsyncTaskCustom async = new AsyncTaskCustom(ASYNC_PEDIDO);
        async.delegate = (AsyncResponse) this;
        async.execute();
    }

    public void popularPedido() {
        getTextView(R.id.numPedido).setText("Pedido #" + pedido.getIdString());
        getTextView(R.id.totalItens).setText("Total itens: " + pedido.getItens().size());
        getTextView(R.id.precoItensTotal).setText("Preço total itens: R$ " + pedido.getPrecoGeralString());
        getTextView(R.id.desconto).setText("Desconto: " + pedido.getDescontoGeralString() + "%");
        getTextView(R.id.valorMaoObra).setText("Mão obra: R$ " + pedido.getValorMaoObraString());
        getTextView(R.id.precoTotalPedido).setText("Preço total: R$ " + pedido.getPrecoGeralMaisMaoObraString());
        getTextView(R.id.enderecoCompleto1).setText(String.format("%s %s", pedido.getEndereco().getLogradouro(), pedido.getEndereco().getNumero()));
        getTextView(R.id.enderecoCompleto2).setText(String.format("Cep: %s %s/%s - %s", pedido.getEndereco().getCep(), pedido.getEndereco().getCidade().getNome(), pedido.getEndereco().getCidade().getEstado().getNome(), pedido.getEndereco().getComplemento()));
        getTextView(R.id.nomeClienteCpf).setText(String.format("%s - %s", pedido.getUsuario().getNome(), pedido.getUsuario().getCpfCnpjString()));
        getTextView(R.id.statusNome).setText(pedido.getStatusPedido().getNome());
        getTextView(R.id.previsao).setText("Prev. Insta: " + pedido.getDataPrevisaoInstalacaoString());
        getTextView(R.id.previsao).setBackgroundColor(getResources().getColor(pedido.getCorPrevisaoInstalacaoInt()));
        if (!pedido.getObservacao().equals("")) {
            getTextView(R.id.observacao).setText(pedido.getObservacao());
        } else {
            getTextView(R.id.observacao).setVisibility(View.GONE);
        }

        TableLayout tl = (TableLayout)findViewById(R.id.tbListItensDetalhePedido);
        for (TbPedidoItem item : pedido.getItens()) {
            View rowModelo = getLayoutInflater().inflate(R.layout.lista_itens_detalhe_pedido, null);

            ((TextView)rowModelo.findViewById(R.id.numItem)).setText("#" + item.getIdString());
            ((TextView)rowModelo.findViewById(R.id.nomeModelo)).setText("#" + item.getModelo().getNomeString() + " - " + item.getModelo().getDimensao());
            ((TextView)rowModelo.findViewById(R.id.isPinturaString)).setText((item.getIsPintura() ? "Com pintura" : "Sem pintura"));
            ((TextView)rowModelo.findViewById(R.id.descModelo)).setText(item.getModelo().getDescricao());
            ((TextView)rowModelo.findViewById(R.id.precoItem)).setText("Preço: R$ " + item.getPrecoItemMaisPintura());
            ((TextView)rowModelo.findViewById(R.id.quantidadeCompra)).setText("Qtd: " + item.getQuantidade() + "un.");

            tl.addView(rowModelo);

            BootstrapButton btn = (BootstrapButton)rowModelo.findViewById(R.id.btPhoto);
            btn.setTag(item);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modalGaleriaOuFoto(view);
                }
            });
        }
    }

    public void modalGaleriaOuFoto(View v) {

    }
}