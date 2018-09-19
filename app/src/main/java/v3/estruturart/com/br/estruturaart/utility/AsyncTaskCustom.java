package v3.estruturart.com.br.estruturaart.utility;

import android.os.AsyncTask;
import android.os.SystemClock;

public class AsyncTaskCustom extends AsyncTask<String, Void, String> {
    private int id;
    public AsyncResponse delegate = null;

    public AsyncTaskCustom(int id) {
        this.id = id;
    }

    @Override
    protected void onPreExecute() {
        delegate.onPreTask("", id);
    }

    @Override
    protected String doInBackground(String... params) {
        return delegate.onExecTask("", id);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        delegate.onPosTask(result, id);
    }
}
