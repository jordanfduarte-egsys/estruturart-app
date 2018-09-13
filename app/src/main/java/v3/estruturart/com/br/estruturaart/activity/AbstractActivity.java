package v3.estruturart.com.br.estruturaart.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.util.ArrayList;
import java.util.List;

public class AbstractActivity extends AppCompatActivity {

    private List<AwesomeValidation> validators = new ArrayList<AwesomeValidation>();

    protected void complementOnCreate() {
        TypefaceProvider.registerDefaultIconSets();
    }

    protected Button getButton(int btnName) {
        return ((Button)findViewById(btnName));
    }

    protected BootstrapButton getBootstrapButton(int btnName) {
        return ((BootstrapButton)findViewById(btnName));
    }

    protected EditText getEditText(int editName) {
        return (EditText)findViewById(editName);
    }

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
}
