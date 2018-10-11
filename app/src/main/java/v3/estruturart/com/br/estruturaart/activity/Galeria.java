package v3.estruturart.com.br.estruturaart.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Gallery;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.helper.GalleryAdapter;
import v3.estruturart.com.br.estruturaart.model.TbModelo;
import v3.estruturart.com.br.estruturaart.model.TbPedido;
import v3.estruturart.com.br.estruturaart.model.TbPedidoItem;
import v3.estruturart.com.br.estruturaart.model.TbPedidoItemFoto;
import v3.estruturart.com.br.estruturaart.service.Client;
import v3.estruturart.com.br.estruturaart.utility.AsyncResponse;
import v3.estruturart.com.br.estruturaart.utility.AsyncTaskCustom;
import v3.estruturart.com.br.estruturaart.utility.JsonModel;

public class Galeria extends AbstractActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, AsyncResponse {

    private static final int ASYNC_IMAGEM = 1;
    public List<TbPedidoItemFoto> fotos = new ArrayList<TbPedidoItemFoto>();
    public TbPedidoItem tbPedidoItem = new TbPedidoItem();
    public String message = "";
    public GalleryAdapter galeriaAdapter;
    public int positionAux = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Metodos iniciais
        super.complementOnCreate();

        // Atribui qual é a view
        setContentView(R.layout.activity_galeria);

        galeriaAdapter = new GalleryAdapter(this, fotos);
        tbPedidoItem.setId(Integer.parseInt(getIntent().getExtras().getString("id")));

        //Busca o pedido
        initFotos();
        findImagem();

        initNavigationBar().setNavigationItemSelectedListener(this);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return onNavigationItemSelectedActions(item);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public String onExecTask(String result, int id) {
        if (id == ASYNC_IMAGEM) {
            TbPedidoItemFoto fotoAux = new TbPedidoItemFoto();
            if (fotos.size() > 0) {
                fotoAux = fotos.get(fotos.size() - 1);
            }

            Client client = new Client(this);
            client.getParameter().put("id", String.valueOf(tbPedidoItem.getId()));
            client.getParameter().put("idmaisquatro", String.valueOf(fotoAux.getId()));

            ArrayList<TbPedidoItemFoto> list = (ArrayList<TbPedidoItemFoto>) client.fromPost(
                "/find-imagem",
                new TypeToken<ArrayList<TbPedidoItemFoto>>(){}.getType()
            );

            fotos.addAll(list);
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
        if (id == ASYNC_IMAGEM) {
            popularFotos();
            if (fotos.size() == 0) {
                showMessage(this, "Nenhuma imagem cadastrada!");
                finish();
            }
        }

        if (!message.equals("")) {
            showMessage(this, message);;
        }

        getProgressBar(R.id.progressBar1).setVisibility(View.GONE);


        if (id == ASYNC_ORCAMENTO_ACCESS) {
            sincronizeOrcamentoPost();
        }

        return null;
    }

    public void findImagem() {
        AsyncTaskCustom async = new AsyncTaskCustom(ASYNC_IMAGEM);
        async.delegate = (AsyncResponse) this;
        async.execute();
    }

    public void popularFotos() {
        initFotos();
    }

    public void initFotos() {
        ViewPager ga = (ViewPager)findViewById(R.id.gallery);
        //showMessage(this, "TOTAL: " + fotos.size() + " Posicao: " + positionAux);
        ga.setAdapter(galeriaAdapter);
        ga.removeOnPageChangeListener(onChangeListener);
        ga.setCurrentItem(positionAux);
        ga.addOnPageChangeListener(onChangeListener);

    }

    private ViewPager.OnPageChangeListener onChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //showMessage(Galeria.this, "POSICAO: " + position);
            positionAux = position;
            if (position == fotos.size() - 1) {
                findImagem();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
