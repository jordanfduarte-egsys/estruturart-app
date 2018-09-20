package v3.estruturart.com.br.estruturaart.model;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import v3.estruturart.com.br.estruturaart.utility.Param;

public abstract class AbstractModel
{
    private SimpleDateFormat simpleDateFormat;
    private NumberFormat numberFormat;
    private List<Param> validation = new ArrayList<Param>();

    public abstract boolean isValid() throws SQLException;
    /**
     * @return the simpleDateFormat
     */
    public SimpleDateFormat getSimpleDateFormat(String pattern)
    {
        return simpleDateFormat = new SimpleDateFormat(pattern);
    }

    /**
     * @param simpleDateFormat the simpleDateFormat to set
     */
    public void setSimpleDateFormat(SimpleDateFormat simpleDateFormat)
    {
        this.simpleDateFormat = simpleDateFormat;
    }

    public String formatMoney(float money)
    {
        if (numberFormat == null) {
            numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        }

        return numberFormat.format(money).replace("R$", "").trim();
    }

    public List<Param> getValidation() {
        return validation;
    }

    public Param getFirstMessage() {
        Param param = new Param(0, "");
        try {
            param = getValidation().get(0);
        } catch (IndexOutOfBoundsException e) {
        }

        return param;
    }
}
