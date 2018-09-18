package v3.estruturart.com.br.estruturaart.utility;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import v3.estruturart.com.br.estruturaart.activity.OrcamentoEtapa1;
import v3.estruturart.com.br.estruturaart.service.Client;

public class ProgressLoading extends AsyncTask<Integer, Void, Void> {
    private ProgressBar progressBar;
    private OrcamentoEtapa1 activity;

    public ProgressLoading(ProgressBar progressBar, OrcamentoEtapa1 activity) {
        this.progressBar = progressBar;
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        activity.test((AppCompatActivity)activity);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Void result) {
        progressBar.setVisibility(View.GONE);
    }
}