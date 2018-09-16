package v3.estruturart.com.br.estruturaart.model;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public abstract class AbstractModel
{
    private SimpleDateFormat simpleDateFormat;
    private NumberFormat numberFormat;

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
}
