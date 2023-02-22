package net.mineup.mercado.utils.time;

import java.util.*;
import org.bukkit.entity.*;
import java.util.concurrent.*;

public class PrivateDelayUtilities
{
    protected HashMap<String, Long> cache;
    protected HashMap<String, Integer> timeDuration;
    
    public PrivateDelayUtilities() {
        this.cache = new HashMap<String, Long>();
        this.timeDuration = new HashMap<String, Integer>();
    }
    
    public void removeDelay(Player p) {
        if (this.cache.containsKey(p.getName())) {
            this.cache.remove(p.getName());
        }
        if (this.timeDuration.containsKey(p.getName())) {
            this.timeDuration.remove(p.getName());
        }
    }
    
    public void setDelay(Player p, int tempo) {
        long timestamp = System.currentTimeMillis() / 1000L;
        this.cache.put(p.getName(), timestamp);
        this.timeDuration.put(p.getName(), tempo);
    }
    
    public boolean noHasDelay(Player p) {
        if (!this.cache.containsKey(p.getName())) {
            return true;
        }
        long _temp = System.currentTimeMillis() / 1000L;
        long currentTime = this.cache.get(p.getName());
        if (currentTime == 0L) {
            return true;
        }
        if (_temp - currentTime > this.timeDuration.get(p.getName())) {
            this.removeDelay(p);
            return true;
        }
        return false;
    }
    
    private long invertTime(Player p) {
        long _temp = System.currentTimeMillis() / 1000L;
        long t = this.cache.get(p.getName());
        long i = _temp - t - this.timeDuration.get(p.getName());
        return (i < 0L) ? Math.abs(i) : i;
    }
    
    public String getDelay(Player p) {
        long seconds = this.invertTime(p);
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
