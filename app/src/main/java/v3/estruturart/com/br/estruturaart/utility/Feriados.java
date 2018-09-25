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


@TargetApi(Build.VERSION_CODES.O)
public class Feriados
{
    static final private List<LocalDate> datas = new ArrayList<LocalDate>();

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

    static LocalDate easter(int year)
    {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(year, month, day);
    }

    private static LocalDate date(int year, int month, int day)
    {
        return LocalDate.of(year, month, day);
    }

    public boolean verificaFeriado(Date data) throws ParseException
    {
        if (datas.contains(data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))
            return true;

        return false;
    }

    public static List<LocalDate> getDatas()
    {
        return datas;
    }
}