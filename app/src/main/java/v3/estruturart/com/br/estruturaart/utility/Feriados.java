package v3.estruturart.com.br.estruturaart.utility;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import de.galgtonold.jollydayandroid.Holiday;
import de.galgtonold.jollydayandroid.HolidayCalendar;
import de.galgtonold.jollydayandroid.HolidayManager;

public class Feriados
{
    //#http://www.guj.com.br/t/resolvido-alguem-ja-usou-o-jollyday-para-calcular-duas-datas-sem-contar-os-feriados/135674/8
    public boolean verificaFeriado(Date dataInicio) throws ParseException
    {
        int totalHorasNesseDia = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dataInicio);

        Calendar cal1 = Calendar.getInstance();
        int year = cal1.get(Calendar.YEAR);

        // Lista todos os feriados do ano de 2011, pelo jollyday
        HolidayManager m = HolidayManager.getInstance(HolidayCalendar.BRAZIL);
        Set<Holiday> holidays = m.getHolidays(year, "br", "s");
        boolean feriado = false;

        // Verifica se o dia atual é um dia de feriado
        for (Holiday h : holidays){
            if (h.getDate().equals(new DateTime(cal.getInstance().getTime()))) {
                feriado = true;
                break;
            }
        }

        return feriado;
    }
}