package net.mineup.mercado.signgui;

import org.bukkit.event.*;
import org.bukkit.entity.*;

public class SignGUICloseEvent extends Event
{
    public static HandlerList handlers;
    private Player p;
    private String[] signtext;
    
    static {
        SignGUICloseEvent.handlers = new HandlerList();
    }
    
    public HandlerList getHandlers() {
        return SignGUICloseEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return SignGUICloseEvent.handlers;
    }
    
    public SignGUICloseEvent(final Player p, final String[] signtext) {
        this.p = p;
        this.signtext = signtext;
    }
    
    public Player getPlayer() {
        return this.p;
    }
    
    public String[] getSignText() {
        return this.signtext;
    }
}
