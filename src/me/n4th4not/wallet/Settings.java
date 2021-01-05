package me.n4th4not.wallet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class Settings {

    private final Main a;
    private final List<LevelLimitation> b = new ArrayList<>();
    private short c;
    private ItemStack d;
    private ItemStack e;
    private boolean f;

    Settings(Main var0) {
        this.a = var0;
        a();
    }

    public LevelLimitation getLevel(int var0) {
        return this.b.get(var0);
    }
    public int getMaxLevel() {
        return this.b.size()-1;
    }
    public int getWalletSlot() {
        return this.c;
    }
    public ItemStack getWalletIcon() {
        return this.d.clone();
    }
    public ItemStack getCoinIcon() {
        return this.e.clone();
    }
    public boolean useLogger() {
        return this.f;
    }

    public void reload() {
        for (Player var0 : Bukkit.getOnlinePlayers()) this.a.a(var0,10);
        a();
        for (Player var0 : Bukkit.getOnlinePlayers()) this.a.a(var0,2);
    }

    void a() {
        this.a.saveDefaultConfig();
        this.b.clear();
        this.a.reloadConfig();
        ConfigurationSection var0 = this.a.getConfig().getConfigurationSection("levels");
        if (var0 == null) {
            this.a.getLogger().severe("Invalid 'config.yml' file !");
            return;
        }
        for (String var1 : var0.getKeys(false)) {
            LevelLimitation var2 = new LevelLimitation(var0.getCurrentPath() + '.' + var1);
            if (var2.a > 1) this.b.add(var2);
        }
        if (this.b.isEmpty()) {
            this.a.getLogger().severe("No levels in your 'config.yml' :'(");
            Bukkit.getPluginManager().disablePlugin(this.a);
            return;
        }
        for (int var1 = 0; var1 < this.b.size(); var1++) {
            try {Bukkit.getPluginManager().addPermission(
                    new org.bukkit.permissions.Permission(Permission.WALLET_LV + var1, PermissionDefault.FALSE));}
            catch (IllegalArgumentException ignored) {}
        }
        this.b.sort(null);
        this.c = ((Number) Math.min(Math.max(this.a.getConfig().getInt("wallet.slot",10),9),InventoryType.PLAYER.getDefaultSize())).shortValue();
        this.d = a("wallet.icon");
        a(this.d,1);
        this.e = a("money.icon");
        a(this.e,2);
        this.f = this.a.getConfig().getBoolean("log");
        this.a.getLogger().info("==================== Settings loaded ====================");
        this.a.getLogger().info("Wallets levels:");
        for (int var1 = 0; var1 < this.b.size(); var1++) {
            LevelLimitation var2 = this.b.get(var1);
            this.a.getLogger().info(var1 + ": ceiling:" + var2.a + ", price: " + var2.b);
        }
        this.a.getLogger().info("Wallet slot: " + this.c);
        this.a.getLogger().info("Wallet icon: " + this.d.toString());
        this.a.getLogger().info("Coin icon: " + this.e.toString());
        this.a.getLogger().info("Log: " + this.f);
        this.a.getLogger().info("=========================================================");
    }

    private ItemStack a(String var0) {
        ItemStack var1 = new ItemStack(Material.DIRT);
        Material var2 = Material.getMaterial(this.a.getConfig().getString(var0 + ".material","DIRT"));
        if (var2 != null) {
            var1.setType(var2);
            ItemMeta var3 = var1.getItemMeta();
            if (var3 != null) {
                var3.setDisplayName(Utilities.Text.b(this.a.getConfig().getString(var0 + ".name")));
                var3.setLore(Utilities.Text.b(this.a.getConfig().getStringList(var0 + ".description")));
                if (this.a.getConfig().getBoolean(var0 + ".glowing")) {
                    var3.addEnchant(Enchantment.ARROW_DAMAGE,1,true);
                    var3.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                var1.setItemMeta(var3);
            }
        }
        return var1;
    }
    private void a(ItemStack var0, int var1) {
        ItemMeta var2 = var0.getItemMeta();
        if (var2 != null) var2.getPersistentDataContainer().set(Utilities.Wallet.A,PersistentDataType.BYTE,(byte) var1);
        else this.a.getLogger().warning("The identifier " + var1 + " cannot be apply on " + var0);
        var0.setItemMeta(var2);
    }

    public void save() {
        this.a.getConfig().set("levels",null);
        ConfigurationSection var0 = this.a.getConfig().createSection("levels");
        int var1 = 0;
        for (LevelLimitation var2 : this.b) {
            ConfigurationSection var3 = var0.createSection(String.valueOf(var1++));
            var3.set("ceiling",var2.a);
            if (var2.b > 0) var3.set("price",var2.b);
        }
        this.a.saveConfig();
    }

    public final class LevelLimitation
        implements Comparable<LevelLimitation> {
        private int a;
        // 1 > not purchasable
        private int b;

        private LevelLimitation(String var0) {
            this.a = Settings.this.a.getConfig().getInt(var0 + ".ceiling",-1);
            this.b = Settings.this.a.getConfig().getInt(var0 + ".price",-1);
        }

        public int getCeiling() {
            return this.a;
        }
        public LevelLimitation setCeiling(int var0) {
            this.a = var0;
            return this;
        }

        public int getPrice() {
            return this.b;
        }
        public LevelLimitation setPrice(int var0) {
            this.b = var0;
            return this;
        }

        @Override
        public int compareTo(LevelLimitation var0) {
            return Integer.compare(this.a,var0.a);
        }
    }
}