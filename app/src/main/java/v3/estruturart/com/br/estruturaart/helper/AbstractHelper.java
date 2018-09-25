package v3.estruturart.com.br.estruturaart.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.beardedhen.androidbootstrap.BootstrapButton;

public abstract class AbstractHelper {
    protected Button getButton(int btnName, Activity ctx) {
        return ((Button)ctx.findViewById(btnName));
    }

    protected Spinner getSpinner(int spinnerId, Activity ctx) {return (Spinner)ctx.findViewById(spinnerId);}

    protected BootstrapButton getBootstrapButton(int btnName, View ct) {
        return ((BootstrapButton)ct.findViewById(btnName));
    }

    public EditText getEditText(int editName, View ct) {
        return (EditText)ct.findViewById(editName);
    }

    public EditText getEditText(int editName, Activity ct) {
        return (EditText)ct.findViewById(editName);
    }

    public ProgressBar getProgressBar(int progress, Activity ct) {
        return (ProgressBar)ct.findViewById(progress);
    }

    protected TextView getTextView(int tvName, View ct) {return (TextView)ct.findViewById(tvName);}

    protected void showMessage(Context ctx, CharSequence text) {
        Toast toast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    protected Bitmap stringToBitmap(String content) {
        byte[] decodedString = Base64.decode(content, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
