package v3.estruturart.com.br.estruturaart.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.model.TbCidade;
import v3.estruturart.com.br.estruturaart.model.TbPedido;
import v3.estruturart.com.br.estruturaart.model.TbUsuario;
import v3.estruturart.com.br.estruturaart.service.Client;
import v3.estruturart.com.br.estruturaart.utility.AsyncResponse;
import v3.estruturart.com.br.estruturaart.utility.AsyncTaskCustom;
import v3.estruturart.com.br.estruturaart.utility.CustomAdapterListPedidos;
import v3.estruturart.com.br.estruturaart.utility.MaskEditUtil;
import v3.estruturart.com.br.estruturaart.utility.Util;

public class Home extends AbstractActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, AsyncResponse {

    public ArrayList<TbPedido> dataModels = new ArrayList<TbPedido>();
    public String buscaFiltro = "";
    public Calendar dateBusca = Calendar.getInstance();
    public SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    public String message = "";

    private static final int ASYNC_FIND_PEDIDOS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicia dependencias antes do layout
        super.complementOnCreate();
        setContentView(R.layout.activity_home);

        // Inicia o menu
        initNavigationBar().setNavigationItemSelectedListener(this);

        dateBusca.setTime(new Date());
        initListView();
        initBusca();
        getBootstrapButton(R.id.btPrev).setOnClickListener(this);
        getBootstrapButton(R.id.btNext).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btPrev) {
            dateBusca.add(Calendar.MONTH,-1);
            showDataString();
            initListView();
        } else if (view.getId() == R.id.btNext) {
            dateBusca.add(Calendar.MONTH,+1);
            showDataString();
            initListView();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return onNavigationItemSelectedActions(item);
    }

    public void initListView() {
        AsyncTaskCustom async = new AsyncTaskCustom(ASYNC_FIND_PEDIDOS);
        async.delegate = (AsyncResponse) this;
        async.execute();
    }

    public void initBusca() {
        getEditText(R.id.edBuscaPedido).addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    getBootstrapButton(R.id.btNext).setVisibility(View.GONE);
                    getBootstrapButton(R.id.btPrev).setVisibility(View.GONE);
                    getTextView(R.id.nomeFiltro).setText("");

                    Integer.parseInt(getEditText(R.id.edBuscaPedido).getText().toString());
                } catch (NumberFormatException e) {
                    getBootstrapButton(R.id.btNext).setVisibility(View.VISIBLE);
                    getBootstrapButton(R.id.btPrev).setVisibility(View.VISIBLE);
                    showDataString();
                }

                buscaFiltro = getEditText(R.id.edBuscaPedido).getText().toString();

                // Busca intercalada
                if (buscaFiltro.length() > 2 && buscaFiltro.length() % 2 == 0) {
                    initListView();
                }

                if (buscaFiltro.length() == 0) {
                    initListView();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public String onExecTask(String result, int id) {
        // id ou nome, ou data
        if (id == ASYNC_FIND_PEDIDOS) {
            String idPedido = "";
            String nome = "";
            String dataFiltro = "";

            // Filtrando algo
            if (!buscaFiltro.equals("")) {
                try {
                    // A busca Ã© por pedido
                    Integer.parseInt(buscaFiltro);
                    nome = "";
                    dataFiltro = "";
                    idPedido = buscaFiltro;
                } catch (NumberFormatException e) {
                    // A busca Ã© por nome e com data
                    idPedido = "";
                    nome = buscaFiltro;
                    dateBusca.set(Calendar.DAY_OF_MONTH, 1);
                    dataFiltro = dateFormat.format(dateBusca.getTime());
                }
            } else {
                // NÃ£o tem busca Ã© so busca por data
                idPedido = "";
                nome = buscaFiltro;
                dateBusca.set(Calendar.DAY_OF_MONTH, 1);
                dataFiltro = dateFormat.format(dateBusca.getTime());
            }

            Client client = new Client(this);
            client.getParameter().put("id", idPedido);
            client.getParameter().put("nome", nome);
            client.getParameter().put("data_filtro", dataFiltro);

            dataModels = ((ArrayList<TbPedido>) client.fromPost("/find-pedidos", new TypeToken<ArrayList<TbPedido>>() {
            }.getType()));

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
        getProgressBar(R.id.progressBar1).setVisibility(View.VISIBLE);
        return null;
    }

    @Override
    public String onPosTask(String result, int id) {
        if (id == ASYNC_FIND_PEDIDOS) {
            final Activity ac = (Activity)this;
            ListView listView = (ListView)findViewById(R.id.listPedidos);

            if (dataModels.size() == 0) {
                getTextView(R.id.numPedidoEncontrado).setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                listView.setVisibility(View.VISIBLE);
                getTextView(R.id.numPedidoEncontrado).setVisibility(View.GONE);
            }

            CustomAdapterListPedidos adapter = new CustomAdapterListPedidos(dataModels, getApplicationContext());
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TbPedido dataModel = (TbPedido)dataModels.get(position);
                Intent intent = new Intent((Context) ac, DetalhePedido.class);
                intent.putExtra("id", String.valueOf(dataModel.getId()));
                ac.startActivity(intent);
//                Snackbar.make(view, dataModel.getName()+"\n"+dataModel.getName()+" API: "+dataModel.getData(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
                }
            });
        } else if (id == ASYNC_ORCAMENTO_ACCESS) {
            sincronizeOrcamentoPost();
        }

        if (!message.equals("")) {
            showMessage(this, message);
            message = "";
        }

        getProgressBar(R.id.progressBar1).setVisibility(View.GONE);
        return null;
    }

    public void showDataString() {
        getTextView(R.id.nomeFiltro).setText(
            Util.getMesNomeAbreviado(dateBusca.get(Calendar.MONTH), dateBusca.get(Calendar.YEAR))
        );
    }
}
