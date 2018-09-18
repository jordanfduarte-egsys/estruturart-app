package v3.estruturart.com.br.estruturaart.utility;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import v3.estruturart.com.br.estruturaart.activity.OrcamentoEtapa1;
import v3.estruturart.com.br.estruturaart.service.Client;

public class ProgressLoading extends AsyncTask<Integer, Void, Void> {
    private Context activity;

    public ProgressLoading(Context activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        //((ProgressBar)((Activity activity)findViewById(R.id.progressBar1))).setVisibility(View.VISIBLE);
        //activity.test((AppCompatActivity)activity);

        Activity activity = (Activity)activity;
        Client client = new Client(activity);
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
                ((TextView)activity.findViewById(R.id.edRgInscricaoEstadual)).setText(usuario.getRgIncricaoEstadual());
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
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ((ProgressBar)((Activity activity)findViewById(R.id.progressBar1))).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Void result) {
        ((ProgressBar)((Activity activity)findViewById(R.id.progressBar1))).setVisibility(View.GONE);
    }
}