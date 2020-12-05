package me.n4th4not.wallet;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class Utilities {
    public static final class Text {
        public static String a(Object var0) {return var0 == null? "" : var0.toString();}
        public static String a(Object var0, String var1) {return var0 == null? var1 : var0.toString();}
    }
    public static final class Dir {
        public static final File a = JavaPlugin.getPlugin(Main.class).getDataFolder();
        public static final File b = new File(a,"messages.yml");
        public static final File c = new File(a,"wallets.log");

        public static boolean a(File var0, boolean var1) {
            Objects.requireNonNull(var0,"Argument 1 cannot be null!");
            File var2 = var1? var0.getParentFile() : var0;
            try {
                if (var2 != null && !(var2.mkdirs() || var2.isDirectory())) {
                    System.err.println("Impossible to create directories: " + var0.getParentFile().getPath());
                    return true;
                }
                if (var1 && !(var0.createNewFile() || var0.isFile())) {
                    System.err.println("A directory with this name already exists : " + var0.getPath());
                    return true;
                }
            }
            catch (IOException ex) {ex.printStackTrace();}
            return false;
        }
    }
}