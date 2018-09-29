package v3.estruturart.com.br.estruturaart.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.model.Orcamento;

@SuppressLint("ValidFragment")
public class DatePickerCustom extends DialogFragment {

    protected Orcamento orcamento = new Orcamento();
    protected Context ctx;

    @SuppressLint("ValidFragment")
    public DatePickerCustom(Orcamento orcamento, Context ctx) {
        this.orcamento = orcamento;
        this.ctx = ctx;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        c.setTime(this.orcamento.getPrevEntrega());
        Dialog d = new DatePickerDialog(getActivity(), R.style.DialogTheme, dateSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        d.setTitle(R.string.orc_prev_entrega_picker);

        return d;
    }

    private DatePickerDialog.OnDateSetListener dateSetListener =
        new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, datePicker.getYear());
                c.set(Calendar.MONTH, datePicker.getMonth());
                c.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                orcamento.setPrevEntrega(c.getTime());
                SimpleDateFormat sm = new SimpleDateFormat("dd/MM/yyyy");
                (((EditText)((Activity)ctx).findViewById(R.id.prevEntrega))).setText(sm.format(c.getTime()));
            }
        };
}