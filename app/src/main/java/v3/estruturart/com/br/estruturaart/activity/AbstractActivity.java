package v3.estruturart.com.br.estruturaart.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.model.TbUsuario;
import v3.estruturart.com.br.estruturaart.service.Client;

public class AbstractActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected boolean isValidUser = true;
    private List<AwesomeValidation> validators = new ArrayList<AwesomeValidation>();

    protected void complementOnCreate() {
        TypefaceProvider.registerDefaultIconSets();
    }

    public void onStart() {
        super.onStart();
        if (isValidUser && !isLogin()) {
            startActivity(new Intent(this, Login.class));
        }
    }

    protected NavigationView initNavigationBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        );
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        TextView tv = (TextView)navigationView.getHeaderView(0).findViewById(R.id.textViewNameLogin);
        tv.setText(getUsuarioLogado().getNome());
        return navigationView;
    }

    protected Button getButton(int btnName) {
        return ((Button)findViewById(btnName));
    }

    protected Spinner getSpinner(int spinnerId) {return (Spinner)findViewById(spinnerId);}

    protected BootstrapButton getBootstrapButton(int btnName) {
        return ((BootstrapButton)findViewById(btnName));
    }

    public EditText getEditText(int editName) {
        return (EditText)findViewById(editName);
    }

    protected TextView getTextView(int tvName) {return (TextView)findViewById(tvName);}

    protected AwesomeValidation getValidator(int formIntName) {
        try {
            validators.get(formIntName);
        } catch ( IndexOutOfBoundsException e ) {
            validators.add(formIntName, new AwesomeValidation(ValidationStyle.BASIC));
        }

        return validators.get(formIntName);
    }

    protected void showMessage(Context ctx, CharSequence text) {
        Toast toast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    protected SharedPreferences getSession(String sharedPrefferenceName) {
        return getSharedPreferences(sharedPrefferenceName, Context.MODE_PRIVATE);
    }

    protected boolean isLogin() {
        return !getSession(Login.class.toString()).getString("login", "0").equals("0");
    }

    public boolean onNavigationItemSelectedActions(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_pedidos) {
            home(item);
        } else if (id == R.id.nav_orcamentos) {
            orcamento(item);
        } else if (id == R.id.nav_sair) {
            logout(item);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout(MenuItem item) {
        SharedPreferences.Editor editor = getSession(Login.class.toString()).edit();
        editor.putString("login", "0");
        editor.putString("senha", "");
        editor.apply();
        editor.commit();

        this.startActivity(new Intent(this, Login.class));
    }

    private void orcamento(MenuItem item) {
        this.startActivity(new Intent(this, OrcamentoEtapa1.class));
    }

    private void home(MenuItem item) {
        this.startActivity(new Intent(this, Home.class));
    }

    protected void setIsValidUser(boolean isValidUser) {
        this.isValidUser = isValidUser;
    }

    protected TbUsuario getUsuarioLogado() {
        Gson gson = new Gson();
        return (TbUsuario) gson.fromJson(
            getSession(Login.class.toString()).getString("loginJson", ""),
            TbUsuario.class
        );
    }

    protected void getPositionSpinnerByListObject(Spinner sp, Object ob) {
        for (int i = 0; i < sp.getCount(); i++) {
            System.out.println("ESTADO SP: " + sp.getItemAtPosition(i).toString());
            System.out.println("ESTADO CP: " + sp.getItemAtPosition(i).toString());
            if (sp.getItemAtPosition(i).toString().equals(ob.toString())) {
                sp.setSelection(i);
                break;
            }
        }
    }
}
