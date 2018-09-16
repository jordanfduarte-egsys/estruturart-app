package v3.estruturart.com.br.estruturaart.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;

import v3.estruturart.com.br.estruturaart.R;

public class Home extends AbstractActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicia dependencias antes do layout
        super.complementOnCreate();
        setContentView(R.layout.activity_home);
        // Inicia o menu
        initNavigationBar().setNavigationItemSelectedListener(this);
    }

    @Override
    public void onClick(View view) {

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
}
