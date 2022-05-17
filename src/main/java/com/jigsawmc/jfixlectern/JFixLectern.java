package com.jigsawmc.jfixlectern;

import com.google.common.collect.Lists;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JFixLectern extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {
    private final Pattern pattern = Pattern.compile("\\d+.\\d+");

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        final long time = System.currentTimeMillis();

        this.getLogger().info(Color("&bLoading JFixLectern &fVersion: &7" + getDescription().getVersion()));
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.getLogger().info(Color("&aHooked into &bPlaceholderAPI"));
        }

        this.getServer().getPluginManager().registerEvents(this, this);
        final PluginCommand command = this.getCommand("jfixlectern");
        if (command == null) {
            this.getLogger().severe("The plugin.yml is missing!");
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        command.setExecutor(this);
        this.getLogger().info(Color(String.format("&aJFixLectern Loaded in &b%dms", System.currentTimeMillis() - time)));
    }


    @Override
    public void onLoad() {
        final Matcher matcher = pattern.matcher(this.getServer().getBukkitVersion());
        if (!matcher.find()) {
            this.getLogger().severe(Color("The server version has not been found!"));
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        if (Double.parseDouble(matcher.group()) >= 1.14) return;
        this.getLogger().warning(Color("The server version is lower than 1.14, the plugin is not required!"));
        this.getPluginLoader().disablePlugin(this);
    }

    @Override
    public void onDisable() {
        this.getLogger().info(Color("&cJFixLectern plugin unloaded."));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void inventoryClickEvent(InventoryClickEvent event) {
        if (!event.getInventory().getType().equals(InventoryType.LECTERN)) return;
        event.setCancelled(true);
        final Player p = (Player) event.getWhoClicked();

        String message = Color(this.getConfig().getString("message", "kick {player} &e&lTrying to crash the server"))
                .replace("{player}", p.getName());

        List<String> commands = this.getConfig().getStringList("commands");

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(p, message);
            commands = PlaceholderAPI.setPlaceholders(p, commands);
        }

        this.getLogger().warning(message);
        this.getServer().broadcast(message, "jfl.alarm");

        if (p.hasPermission("jfl.cmd")) return;
        commands.forEach(command -> this.getServer().dispatchCommand(this.getServer().getConsoleSender(),
                Color(command).replace("{player}", p.getName())));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final long time = System.currentTimeMillis();
        sender.sendMessage(Color("&e[JFixLectern] Reloading..."));
        this.onEnable();
        sender.sendMessage(Color(String.format("&a[JFixLectern] Reloaded in &b%dms", System.currentTimeMillis() - time)));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("jfl.reload")) return null;
        return Lists.newArrayList("reload");
    }

    public String Color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
