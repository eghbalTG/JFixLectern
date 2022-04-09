package com.jigsawmc.jfixlectern;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class JFixLectern extends JavaPlugin implements Listener , CommandExecutor, TabCompleter {
    public boolean papi = false;
    public String msg = "&cPlayer &e{player} &cTrying to crash server";
    public String cmd = "kick {player} &cTrying to crash server";
    @Override
    public void onEnable(){
        String vers = getServer().getBukkitVersion();
        double sversion = Double.parseDouble(vers.split("\\.")[0]+"."+vers.split("\\.")[1]);
        if(sversion<1.14){
            getLogger().warning(Rang("&e[FixLectern] Version less than 1.14 does not require this plugin !"));
        }else{
            getServer().getPluginManager().registerEvents(this,this);
            Objects.requireNonNull(getCommand("jfixlectern")).setExecutor(this);
            reloadplugin();
        }
    }
    @Override
    public void onDisable() {
        consolemsg("&cJFixLectern plugin unloaded. &eGood Bye");
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void InventoryClickEvent(InventoryClickEvent e){
        if(e.getInventory().getType().equals(InventoryType.LECTERN)){
            e.setCancelled(true);
            try {
                Player p = (Player) e.getWhoClicked();
                String txt = Rang(msg).replace("{player}",p.getName());
                String command = Rang(cmd).replace("{player}",p.getName());
                if(papi){
                    txt = PlaceholderAPI.setPlaceholders(p,txt);
                    command = PlaceholderAPI.setPlaceholders(p,command);
                }
                getLogger().warning(txt);
                if(!p.hasPermission("jfl.cmd")) getServer().dispatchCommand(getServer().getConsoleSender(),command) ;
                for(Player P : getServer().getOnlinePlayers()){
                    if(P.hasPermission("jfl.alarm")){
                        P.sendMessage(txt);
                    }
                }
            }catch (Exception ex){
                String ltxt = Rang(msg).replace("{player}",e.getWhoClicked().getName());
                getLogger().warning(ltxt);
            }
        }
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(label.equalsIgnoreCase("jfixlectern") && sender.hasPermission("jfp.reload")){
            long time = System.currentTimeMillis();
            sender.sendMessage(Rang("&e[JFixLectern] Reloading ..."));
            reloadplugin();
            sender.sendMessage(Rang("&a[JFixLectern] Reloaded on &b"+(System.currentTimeMillis()-time)+" ms"));
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if(sender.hasPermission("jfl.reload")) list.add("reload");
        return list;
    }
    public void reloadplugin(){
        long time = System.currentTimeMillis();
        consolemsg("[JFL] &bLoading JFixLectern &fVersion: &7"+getDescription().getVersion());
        saveDefaultConfig();
        try {
            msg = getConfig().getString("message");
            cmd = getConfig().getString("command");
        } catch (Exception ex){
            consolemsg("&e[JFL] &cError loading config.ym !");
        }
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            papi=true;
            consolemsg("&e[+] &aHook To &bPlaceholderAPI");
        }
        consolemsg("[JFL] &aJFixLectern Loaded on &b"+(System.currentTimeMillis()-time)+" &ems");
    }
    public void consolemsg(String txt){
        getServer().getConsoleSender().sendMessage(Rang(txt));
    }
    public String Rang(String txt){
        return ChatColor.translateAlternateColorCodes('&',txt);
    }
}
