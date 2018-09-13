package v3.estruturart.com.br.estruturaart.activity;

import android.os.Bundle;
import android.view.View;
import v3.estruturart.com.br.estruturaart.R;

public class Home extends AbstractActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.complementOnCreate();
        setContentView(R.layout.activity_home);
    }

    @Override
    public void onClick(View view) {

    }
}
