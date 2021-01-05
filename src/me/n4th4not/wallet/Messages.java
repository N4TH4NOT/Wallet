package me.n4th4not.wallet;

import me.n4th4not.wallet.Utilities.Dir;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Messages {

    private final Main a;
    private final Map<String,String> b = new HashMap<>(0x50);
    private final Map<UUID,long[]> c = new HashMap<>((int) (Bukkit.getMaxPlayers()*0.8D));


    public Messages(Main var0) {
        this.a = var0;
        reload();
    }

    public String getMessage(String var0) {
        String var1 = this.b.get(var0);
        if (var1 == null) this.a.getLogger().severe("That message '" + var0 + "' does not exists or it was misspelled.");
        return Utilities.Text.a(var1,"§cThis message should not appears, contact the staff!");
    }

    public void sendMessage(CommandSender var0, String var1, int var2, Object... var3) {
        String var4 = Utilities.Placeholder.a(getMessage(var1),var2,var3);
        if (!var4.isEmpty()) {
            for (String var5 : var4.split("\n")) var0.sendMessage(var5);
        }
    }
    public void sendMessage(CommandSender var0, String var1) {
        sendMessage(var0,var1,0);
    }

    public void sendMessageWithCooldown(CommandSender var0, String var1, int var2, Object... var3) {
        try {
            if (var0 instanceof Player) {
                int var4 = a(var1);
                long var5 = System.currentTimeMillis();
                long[] var6 = this.c.putIfAbsent(((Player) var0).getUniqueId(),new long[4]);
                if (var6 == null) var6 = this.c.get(((Player) var0).getUniqueId());
                else if (var6[var4] > var5 - 10000) return;
                var6[var4] = var5;
                this.c.put(((Player) var0).getUniqueId(),var6);
            }
        }
        catch (ArrayIndexOutOfBoundsException ignored) {}
        sendMessage(var0,var1,var2,var3);
    }

    public void sendMessageWithCooldown(CommandSender var0, String var1) {
        sendMessageWithCooldown(var0,var1,0);
    }

    public void reload() {
        InputStream var1 = a();
        if (var1 != null) {
            if (!Dir.a.exists() && Dir.a(Dir.a,true)) {
                try (OutputStream var2 = new FileOutputStream(Dir.a)) {
                    byte[] var3 = new byte[1024];
                    int var4;
                    while ((var4 = var1.read(var3)) > 0) var2.write(var3,0,var4);
                    var2.flush();
                }
                catch (IOException ex) {ex.printStackTrace();}
            }
        }
        else {
            this.a.getLogger().severe('\'' + Dir.a.getName() + "' is not found in this plugin resources, restore or re-download this plugin !");
            Bukkit.getPluginManager().disablePlugin(this.a);
            return;
        }
        FileConfiguration var0 = YamlConfiguration.loadConfiguration(Dir.a);
        for (String var2: var0.getKeys(false)) {
            Object var3 = var0.get(var2);
            if (!(var3 instanceof ConfigurationSection)) this.b.put(var2,Utilities.Text.b(Utilities.Text.a(var3)));
        }
    }

    private InputStream a() {
        try {
            URL var1 = Main.class.getClassLoader().getResource(Dir.a.getName());
            if (var1 == null) {
                this.a.getLogger().severe('\'' + Dir.a.getName() + "' is not found into plugin jar, restore the file or re-download the plugin!");
                return null;
            }
            else {
                URLConnection var2 = var1.openConnection();
                var2.setUseCaches(false);
                return var2.getInputStream();
            }
        }
        catch (IOException ex) {
            this.a.getLogger().severe("An error was occurred when try to load '" + Dir.a.getName() + "' !");
            ex.printStackTrace();
            return null;
        }
    }

    private int a(String var0) {
        switch (var0) {
            case "09":
            case "3A":
            case "43":
                return 0;
            case "46":
                return 1;
            case "47":
                return 2;
            case "48":
                return 3;
            default:
                return -1;
        }
    }
}
