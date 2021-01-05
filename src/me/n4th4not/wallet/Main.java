package me.n4th4not.wallet;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public final class Main
    extends JavaPlugin {

    static File A;

    final Settings a;
    final Logger b;
    final Messages c;
    Economy d = null;
    net.milkbowl.vault.permission.Permission e = null;

    public Main() {
        A = getDataFolder();
        this.a = new Settings(this);
        this.b = new Logger(this);
        this.c = new Messages(this);
    }

    @Override
    public void onEnable() {
        try {Class.forName("org.bukkit.persistence.PersistentDataContainer");}
        catch (ClassNotFoundException ex) {
            getLogger().severe("This server version is not supported!");
            return;
        }
        if (!setupEconomy()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        PluginCommand var0 = getCommand("wallet");
        if (var0 == null) {
            getLogger().warning("'wallet' command is not registered in to 'plugin.yml' !");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        var0.setUsage(Utilities.Text.b(var0.getUsage()));
        new CommandWallet(this,var0);
        Bukkit.getPluginManager().registerEvents(new EventsListener(this),this);
        for (Player var2 : Bukkit.getOnlinePlayers()) a(var2,1);
    }

    @Override
    public void onDisable() {
        for (Player var0 : Bukkit.getOnlinePlayers()) a(var0,14);
        this.b.clear();
        //this.a.save();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault is required !");
            return false;
        }
        RegisteredServiceProvider<Economy> var0 = getServer().getServicesManager().getRegistration(Economy.class);
        if (var0 != null) this.d = var0.getProvider();
        else {
            getLogger().warning("Vault not provide the economy, are you sure you have installed an economy plugin??");
            return false;
        }
        RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> var1 = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (var1 != null) this.e = var1.getProvider();
        else {
            getLogger().warning("Vault not provide permissions, are you sure you have installed an permission manager plugin??");
            return false;
        }
        return true;
    }

    // 0b 0 00 00
    //    ↑ ↑  ↑_ 0 -> none, 1 -> chat, 2 -> title
    //    | `---- 0 -> give, 1 -> force give, 2 -> clear slot, 3 -> check all N clear
    //    `------ 0 -> use gamemode constraint, 1 -> ignore gamemode
    // -1 = error, 0 = no given, 1 = okay
    int a(Player var0, int var1) {
        int var2 = Math.min(a(var0),this.a.getMaxLevel());
        if (var2 < 0) var1 = 8 | (var1 & 3);
        switch ((var1 >> 2) & 3) {
            case 0:
                if (var0.hasPermission(Permission.NO_WALLET) || (var1 >> 4 & 1) == 0 && !EventsListener.a(var0.getGameMode()))
                    return 0;
            case 1:
                ItemStack var3 = var0.getInventory().getItem(this.a.getWalletSlot());
                ItemStack var4 = this.a.getWalletIcon();
                ItemMeta var5 = var4.getItemMeta();
                if (var5 != null) {
                    int var6 = Utilities.Wallet.a(var0);
                    int var7 = this.a.getLevel(var2).getCeiling();
                    //erase placeholders
                    var5.setDisplayName(Utilities.Placeholder.a(var5.getDisplayName(),0,var2,var6,var7 - var6,var7));
                    List<String> var8 = var5.getLore();
                    if (var8 != null) {
                        var8.replaceAll(var9 -> Utilities.Placeholder.a(var9,0,var2,var6,var7 - var6,var7));
                        var5.setLore(var8);
                    }
                }
                else getLogger().severe("Invalid Wallet icon !");
                var4.setItemMeta(var5);
                var0.getInventory().setItem(this.a.getWalletSlot(),var4);
                if (!Utilities.Wallet.a(var3) && var3 != null && !var0.getInventory().addItem(var3).isEmpty()) {
                    var0.getInventory().setItem(this.a.getWalletSlot(),var3);
                    EventsListener.A.add(var0.getUniqueId());
                    if ((var1 & 3) == 1) this.c.sendMessage(var0,"00");
                    else if ((var1 & 3) == 2) var0.sendTitle(this.c.getMessage("01"),this.c.getMessage("02"),10,35,15);
                    return -1;
                }
                EventsListener.A.remove(var0.getUniqueId());
                if ((var1 & 3) == 1) this.c.sendMessage(var0,"03");
                else if ((var1 & 3) == 2) var0.sendTitle(this.c.getMessage("04"),this.c.getMessage("05"),10,35,15);
                break;
            case 2:
                if (Utilities.Wallet.a(var0.getInventory().getItem(this.a.getWalletSlot()))) {
                    var0.getInventory().clear(this.a.getWalletSlot());
                    if ((var1 & 3) == 1) this.c.sendMessage(var0,"06");
                    else if ((var1 & 3) == 2) var0.sendTitle(this.c.getMessage("07"),this.c.getMessage("08"),10,35,15);
                }
                break;
            case 3:
                boolean var6 = false;
                for (int var7 = 0; var7 < var0.getInventory().getSize(); var7++) {
                    if (Utilities.Wallet.a(var0.getInventory().getItem(var7))) {
                        var0.getInventory().clear(var7);
                        var6 = true;
                    }
                }
                if (var6) {
                    if ((var1 & 3) == 1) this.c.sendMessage(var0,"06");
                    else if ((var1 & 3) == 2) var0.sendTitle(this.c.getMessage("07"),this.c.getMessage("08"),10,35,15);
                }
        }
        return 1;
    }

    /**
     * Give/Remove/Clear the money from the player wallet.
     * If {@code var0} had not a wallet, no have the permission or {@code var2} < 0 then the result will be always 0.
     *
     * @param var0 source
     * @param var1 action: 0=give, 1=remove, 2=clear
     * @param var2 amount
     * @return affected items
     * @throws IllegalArgumentException Wrong action
     */
    public int b(Player var0, int var1, int var2) {
        if (var0.hasPermission(Permission.NO_WALLET) || var2 < 0
                || EventsListener.A.contains(var0.getUniqueId()) && a(var0,2) == -1) return 0;
        int var3 = a(var0);
        if (var3 == -1) return 0;
        Settings.LevelLimitation var4 = this.a.getLevel(var3);
        int var5 = Utilities.Wallet.a(var0);
        switch (var1) {
            case 0:
                if (var5 + var2 > var4.getCeiling()) {
                    var2 = var4.getCeiling() - var5;
                    var5 = var4.getCeiling();
                }
                else var5 += var2;
                break;
            case 1:
                if ((var5 -= var2) < 0) {
                    var2 = Utilities.Wallet.a(var0);
                    var5 = 0;
                }
                break;
            case 2:
                var2 = var5;
                var5 = 0;
                break;
            default:
                throw new IllegalArgumentException("Argument 2: Wrong action ID");
        }
        var0.getPersistentDataContainer().set(Utilities.Wallet.B,PersistentDataType.INTEGER,Math.max(var5,0));
        a(var0,0);
        return var2;
    }

    /**
     * Get wallet level of a player and update {@link me.n4th4not.wallet.Utilities.Wallet#C}.
     *
     * @param var0 target
     * @return level
     */
    public int a(Player var0) {
        if (var0.hasPermission(Permission.NO_WALLET)) {
            var0.getPersistentDataContainer().remove(Utilities.Wallet.C);
            return -1;
        }
        int var1 = this.a.getMaxLevel();
        for (; var1 > -1; var1--) {
            if (var0.hasPermission(Permission.WALLET_LV + var1)) {
                var0.getPersistentDataContainer().set(Utilities.Wallet.C,PersistentDataType.INTEGER,var1);
                break;
            }
        }
        if (var1 == -1) var0.getPersistentDataContainer().remove(Utilities.Wallet.C);
        return var1;
    }
}