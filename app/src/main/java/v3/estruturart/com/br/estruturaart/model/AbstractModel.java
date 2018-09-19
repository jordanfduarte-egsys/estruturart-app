package v3.estruturart.com.br.estruturaart.model;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class AbstractModel
{
    private SimpleDateFormat simpleDateFormat;
    private NumberFormat numberFormat;
    private List<String> validation = new ArrayList<String>();

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

    public List<String> getValidation() {
        return validation;
    }

    public String getFirstMessage() {
        String message = "";
        try {
            message = getValidation().get(0);
        } catch (IndexOutOfBoundsException e) {
            message = "";
        }

        return message;
    }
}
