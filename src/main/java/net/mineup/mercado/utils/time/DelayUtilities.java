package net.mineup.mercado.utils.time;

import java.util.*;
import org.bukkit.entity.*;
import java.util.concurrent.*;

public class DelayUtilities
{
    protected static HashMap<String, Long> cache;
    protected static HashMap<String, Integer> timeDuration;
    
    static {
        DelayUtilities.cache = new HashMap<String, Long>();
        DelayUtilities.timeDuration = new HashMap<String, Integer>();
    }
    
    public static void removeDelay(Player p) {
        if (DelayUtilities.cache.containsKey(p.getName())) {
            DelayUtilities.cache.remove(p.getName());
        }
        if (DelayUtilities.timeDuration.containsKey(p.getName())) {
            DelayUtilities.timeDuration.remove(p.getName());
        }
    }
    
    public static void setDelay(Player p, int tempo) {
        long timestamp = System.currentTimeMillis() / 1000L;
        DelayUtilities.cache.put(p.getName(), timestamp);
        DelayUtilities.timeDuration.put(p.getName(), tempo);
    }
    
    public static boolean noHasDelay(Player p) {
        if (!DelayUtilities.cache.containsKey(p.getName())) {
            return true;
        }
        long _temp = System.currentTimeMillis() / 1000L;
        long currentTime = DelayUtilities.cache.get(p.getName());
        if (currentTime == 0L) {
            return true;
        }
        if (_temp - currentTime > DelayUtilities.timeDuration.get(p.getName())) {
            removeDelay(p);
            return true;
        }
        return false;
    }
    
    private static long invertTime(Player p) {
        long _temp = System.currentTimeMillis() / 1000L;
        long t = DelayUtilities.cache.get(p.getName());
        long i = _temp - t - DelayUtilities.timeDuration.get(p.getName());
        return (i < 0L) ? Math.abs(i) : i;
    }
    
    public static String getDelay(Player p) {
        long seconds = invertTime(p);
        int dias = (int)TimeUnit.SECONDS.toDays(seconds);
        long horas = TimeUnit.SECONDS.toHours(seconds) - dias * 24;
        long minutos = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.SECONDS.toHours(seconds) * 60L;
        long segundos = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toMinutes(seconds) * 60L;
        String Final = "";
        if (dias != 0) {
            Final = String.valueOf(Final) + dias + ((dias == 1) ? " Dia " : " Dias ");
        }
        if (horas != 0L) {
            Final = String.valueOf(Final) + horas + ((horas == 1L) ? " Hora " : " Horas");
        }
        if (minutos != 0L) {
            Final = String.valueOf(Final) + minutos + ((minutos == 1L) ? " Minuto " : " Minutos ");
        }
        if (segundos != 0L) {
            Final = String.valueOf(Final) + segundos + ((segundos == 1L) ? " Segundo" : " Segundos");
        }
        if (Final.equalsIgnoreCase("")) {
            Final = "0 segundos";
        }
        return Final;
    }
}
