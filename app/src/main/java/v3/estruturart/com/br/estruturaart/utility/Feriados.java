package v3.estruturart.com.br.estruturaart.utility;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Feriados
{

    //#https://github.com/dlew/joda-time-android
    static
    {
        Calendar cal = Calendar.getInstance();
        for (AtomicInteger year = new AtomicInteger(
                cal.getTime().getYear() - 1);
             year.get() <=
                     cal.getTime().getYear() + 1;
                year.getAndIncrement()) {
            // new year
            datas.add(date(year.get(), 1, 1));
            // carnival
            datas.add(easter(year.get()).minusDays(48));
            datas.add(easter(year.get()).minusDays(47));
            datas.add(easter(year.get()).minusDays(46));
            // tiradentes
            datas.add(date(year.get(), 4, 21));
            // good friday
            datas.add(easter(year.get()).minusDays(2));
            // labour
            datas.add(date(year.get(), 5, 1));
            // corpus christi
            datas.add(easter(year.get()).plusDays(60));
            // independence
            datas.add(date(year.get(), 9, 7));
            // aparedica
            if (year.get() >= 1980) {
                datas.add(date(year.get(), 10, 12));
            }
            // Servidor publico (LOCAL)
            datas.add(date(year.get(), 10, 28));
            // dead
            datas.add(date(year.get(), 11, 2));
            // republic
            datas.add(date(year.get(), 11, 15));
            // Dia do Evangélico (LOCAL)
            datas.add(date(year.get(), 11, 30));
            // christmas
            datas.add(date(year.get(), 12, 25));
        }
    }


    //#http://www.guj.com.br/t/resolvido-alguem-ja-usou-o-jollyday-para-calcular-duas-datas-sem-contar-os-feriados/135674/8
    public boolean verificaFeriado(Date dataInicio) throws ParseException
    {
        int totalHorasNesseDia = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dataInicio);

        Calendar cal1 = Calendar.getInstance();
        int year = cal1.get(Calendar.YEAR);

        //Lista todos os feriados do ano de 2011, pelo jollyday
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