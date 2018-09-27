package v3.estruturart.com.br.estruturaart.helper;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TableRow;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.ArrayList;
import java.util.List;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.model.TbModelo;
import v3.estruturart.com.br.estruturaart.utility.MaskEditUtil;

/**
 * Helper para construção do datatable de listagem
 */
public class TableListOrcamentoEtapa3 extends AbstractHelper {
    private List<TbModelo> modelos = new ArrayList<TbModelo>();
    private TableLayout table;

    /**
     * @param modelos
     * @param table
     */
    public TableListOrcamentoEtapa3(List<TbModelo> modelos, TableLayout table) {
        this.modelos = modelos;
        this.table = table;
    }

    /**
     * Cria-se o datatable
     * @param content
     * @param content
     */
    public void create(Context content) {
        // Recupera a Activity
        final Activity ac = (Activity)content;

        // Tem itens retornados
        if (modelos.size() > 0) {
            for (TbModelo modelo : modelos) {
                View rowModelo = ac.getLayoutInflater().inflate(R.layout.lista_itens_etapa3, null);

                getTextView(R.id.nomeModeloAdd, rowModelo).setText(modelo.getNomeNovoString());
                if (modelo.getIsPintura()) {
                    getTextView(R.id.precoItemEtapa3, rowModelo).setText(
                        "Preço: R$ " + modelo.getPrecoItemTotalComAcrescimoComPinturaString()
                    );

                    getTextView(R.id.isPinturaString, rowModelo).setText("Com Pintura");
                } else {
                    getTextView(R.id.precoItemEtapa3, rowModelo).setText(
                        "Preço: R$ " + modelo.getPrecoItemTotalComAcrescimoSemPinturaString()
                    );

                    getTextView(R.id.isPinturaString, rowModelo).setText("Sem pintura");
                }

                getTextView(R.id.quantidadeCompra, rowModelo).setText("Qtd: " + modelo.getQuantidadeCompra());
                getTextView(R.id.descricaoItem, rowModelo).setText(modelo.getDescricao());

                table.addView(rowModelo);
            }
        }
    }
}
