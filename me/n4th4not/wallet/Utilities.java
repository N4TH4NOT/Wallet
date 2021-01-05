package me.n4th4not.wallet;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

public final class Utilities {
    static final Random A = new Random();
    public static final class Text {
        public static final SimpleDateFormat a = new SimpleDateFormat("yyyyMMdd_HHmmss");
        public static final SimpleDateFormat b = new SimpleDateFormat("MM/dd/yyyy HH'h'mm''ss");
        public static final SimpleDateFormat c = new SimpleDateFormat("HH:mm:ss");

        /**
         * Get {@link Enum#name()} is {@code var0} is constant or {@link Object#toString()} otherwise if
         * {@code var0} is {@code null} returns an empty string.
         *
         * @param var0 Object to convert
         * @param <T> Any
         * @return Not null string
         */
        public static <T> String a(T var0) {
            return a(var0,"");
        }

        /**
         * Same thing as {@link Text#a(Object)} but if {@code null} returns {@code var1}.
         *
         * @param var0 Object to convert
         * @param var1 Default value
         * @param <T> Any
         * @return Not null string
         */
        public static <T> String a(T var0, String var1) {
            return var0 == null? var1 : var0.getClass().isEnum()? d(((Enum<?>) var0).name()) : var0.toString();
        }

        /**
         * Colorized your text ^^
         *
         * @param var0 Text
         * @param var1 Char to translate
         * @return {@code null} only if {@code var0} is {@code null}
         */
        public static String b(String var0, char var1) {
            if (var0 == null) return null;
            StringBuilder var2 = new StringBuilder(var0);
            for (int var3 = var2.indexOf(String.valueOf(var1)); var3 != -1 && var3 < var2.length()-1; var3 = var2.indexOf(String.valueOf(var1),var3+1)) {
                if (var3 != 0 && var2.charAt(var3-1) == '\\') {
                    var2.deleteCharAt(var3 - 1);
                    var3--;
                }
                else if ("0123456789AaBbCcDdEeFfKkLlMmNnOoKkRr".indexOf(var2.charAt(var3+1)) != -1)
                    var2.setCharAt(var3,'§');
            }
            return var2.toString();
        }

        /**
         * Same as {@link Text#b(String, char)} but the character to translate is preset to '{@code &}'.
         *
         * @param var0 Text
         * @return {@code null} only if {@code var0} is {@code null}
         */
        public static String b(String var0) {return b(var0,'&');}

        /**
         * Same as {@link Text#b(String)} but for a list.
         *
         * @param var0 Text
         * @return {@code null} only if {@code var0} is {@code null}
         */
        public static List<String> b(List<String> var0) {
            if (var0 == null) return null;
            var0.replaceAll(Text::b);
            return var0;
        }

        /**
         * Inverse process of {@link Text#b(String)} so it remove color of text.
         *
         * @param var0 Text
         * @return {@code null} only if {@code var0} is {@code null}
         */
        public static String c(String var0) {
            return var0 == null? null : var0.replace("&","\\&").replace("§","&");
        }


        /**
         * Inverse process of {@link Text#c(String)} but for a list.
         *
         * @param var0 Text
         * @return <{@code null} only if {@code var0} is {@code null}
         */
        public static List<String> c(List<String> var0) {
            if (var0 == null) return null;
            var0.replaceAll(Text::c);
            return var0;
        }

        /**
         * Auto-completion.
         *
         * @param var0 current arg
         * @param var1 available args
         * @return All {@code var1} starting with {@code var0}
         */
        public static List<String> d(String var0, String... var1) {
            //List all elements from var1 that startWith var0
            return Collections.singletonList(var0);
        }

        public static String d(String var0) {
            if (var0 == null) return null;
            String var1 = var0.toLowerCase();
            if (var1.length() > 0)
                var1 = var1.replaceFirst("^.", String.valueOf(Character.toUpperCase(var1.charAt(0))));
            return var1;
        }
    }
    public static final class Dir {
        /**
         * File to extract messages
         */
        public static final File a = new File(Main.A,"messages.yml");
        /**
         * Runtime log file
         */
        public static final File b = new File(Main.A,"wallets.log");
        /**
         * Archived log files
         */
        public static final File c = new File(Main.A,"history");

        /**
         * Creates file and these folders.
         *
         * @param var0 file/folder(s) to create
         * @param var1 is file
         * @return success
         */
        public static boolean a(File var0, boolean var1) {
            Objects.requireNonNull(var0,"Argument 1 cannot be null!");
            File var2 = var1? var0.getParentFile() : var0;
            try {
                if (var2 != null && !(var2.mkdirs() || var2.isDirectory())) {
                    System.err.println("Impossible to create directories: " + var0.getParentFile().getPath());
                    return false;
                }
                if (var1 && !(var0.createNewFile() || var0.isFile())) {
                    System.err.println("A directory with this name already exists : " + var0.getPath());
                    return false;
                }
            }
            catch (IOException ex) {ex.printStackTrace();}
            return true;
        }
    }
    public static final class Wallet {
        /**
         * <h2>ITEMS ONLY!</h2>
         * Tag to know the type of item.
         * @see Wallet#a(ItemStack)
         * @see Wallet#b(ItemStack)
         */
        public static final NamespacedKey A = new NamespacedKey("wallet","type");
        /**
         * <h2>PLAYERS ONLY!</h2>
         * Tag to know to amount of stored money.
         * @see Wallet#b(Player)
         */
        public static final NamespacedKey B = new NamespacedKey("wallet","money");
        /**
         * <h2>PLAYERS ONLY!</h2>
         * Tag to quickly know the level of the player's wallet so as not to be bound to a fixed and unmodifiable
         * storage space and not to recalculate the permission level for each action.
         * @see Wallet#a(Player)
         */
        public static final NamespacedKey C = new NamespacedKey("wallet","level");

        /**
         * @param var0 source
         * @return is a wallet
         */
        public static boolean a(ItemStack var0) {
            try {return Objects.requireNonNull(var0.getItemMeta()).getPersistentDataContainer().getOrDefault(A,PersistentDataType.BYTE,(byte) 0) == (byte) 1;}
            catch (Exception ex) {return false;}
        }

        /**
         * @param var0 source
         * @return amount of stored money
         */
        public static int a(Player var0) {
            try {return var0.getPersistentDataContainer().getOrDefault(B,PersistentDataType.INTEGER,0);}
            catch (Exception ex) {return 0;}
        }

        /**
         * @param var0 source
         * @return is a coin
         */
        public static boolean b(ItemStack var0) {
            try {return Objects.requireNonNull(var0.getItemMeta()).getPersistentDataContainer().getOrDefault(A,PersistentDataType.BYTE,(byte) 0) == (byte) 2;}
            catch (Exception ex) {return false;}
        }

        /**
         * @param var0 source
         * @return level, if -1 then no level
         */
        public static int b(Player var0) {
            try {return var0.getPersistentDataContainer().getOrDefault(C,PersistentDataType.INTEGER,-1);}
            catch (Exception ex) {return -1;}
        }
    }
    public static final class Placeholder {

        /**
         * This not fill-up information but it is for user friendly part.
         *
         * @param var0 target
         * @return user friendly name
         */
        public static String a(CommandSender var0) {
            if (var0 instanceof Player) return Utilities.Text.a(((Player) var0).getDisplayName(),var0.getName());
            else if (var0 instanceof Entity) {
                if (((Entity) var0).getCustomName() == null) return var0.getName();
                else return ((Entity) var0).getCustomName() + "§r (" + var0.getName() + ")";
            }
            return var0.getName();
        }

        // var1 -> indicator to include info
        // var2 -> placeholders values
        public static String a(String var0, int var1, Object... var2) {
            String var3 = Text.a(var0);
            if (!(var0.isEmpty() || var2 == null || var2.length == 0)) {
                byte var4 = 0; //placeholder num
                byte var5 = 0; //index of var1
                for (Object var6 : var2) {
                    if (var6 instanceof Player) { //3 bytes
                        var3 = var3.replace("%%" + var4++, (var1 >> var5 & 1) == 1 ? Utilities.Text.a(((Player) var6).getDisplayName(), ((Player) var6).getName()) : ((Player) var6).getName());
                        if ((var1 >> var5 + 1 & 1) == 1)
                            var3 = var3.replace("%%" + var4++, ((Player) var6).getUniqueId().toString());
                        if ((var1 >> var5 + 2 & 1) == 1) var6 = ((Player) var6).getLocation();
                        var5+=3;
                    }
                    else if (var6 instanceof Entity) { //3 bytes
                        var3 = var3.replace("%%" + var4++, (var1 >> var5 & 1) == 1 ? Utilities.Text.a(((Entity) var6).getCustomName(), ((Entity) var6).getName()) : ((Entity) var6).getName());
                        var3 = var3.replace("%%" + var4++, (var1 >> var5 + 1 & 1) == 1 ? Utilities.Text.a(((Entity) var6).getType()) : ((Entity) var6).getUniqueId().toString());
                        if ((var1 >> var5 + 2 & 1) == 1) var6 = ((Entity) var6).getLocation();
                        var5+=3;
                    }
                    else if (var6 instanceof EconomyResponse) { //2 bytes
                        var3 = var3.replace("%%" + var4++, String.valueOf((var1 >> var5 & 1) == 1? ((EconomyResponse) var6).amount : ((EconomyResponse) var6).balance));
                        if ((var1 >> var5 + 1 & 1) == 1)
                            var3 = var3.replace("%%" + var4++, ((EconomyResponse) var6).errorMessage);
                        var5+=2;
                    }
                    else if (var6 instanceof Inventory) { //2 bytes
                        var3 = var3.replace("%%" + var4++, Text.a(((Inventory) var6).getType()));
                        if ((var1 >> var5 & 1) == 1)
                            var3 = var3.replace("%%" + var4++, String.valueOf(((Inventory) var6).getSize()));
                        if ((var1 >> var5 + 1 & 1) == 1) var6 = ((Inventory) var6).getLocation();
                        var5+=2;
                    }
                    else if (var6 instanceof CommandSender) { //3 byte
                        var3 = var3.replace("%%" + var4++,Text.d(((CommandSender) var6).getName()));
                        if (var6 instanceof BlockCommandSender && (var1 >> var5 & 1) == 1)
                            var6 = ((BlockCommandSender) var6).getBlock().getLocation();
                        var5+=3;
                    }
                    else if (var6 instanceof Block) { //1 byte
                        var3 = var3.replace("%%" + var4++,Text.a(((Block) var6).getType()));
                        if ((var1 >> var5++ & 1) == 1) var6 = ((Block) var6).getLocation();
                    }
                    else if (var6 instanceof Number && (var1 >> var5++ & 1) == 1) { //1 byte
                        if (var6 instanceof Integer) var3 = var3.replace("%%" + var4++,Integer.toHexString((int) var6));
                        else if (var6 instanceof Long) var3 = var3.replace("%%" + var4++,Long.toHexString((long) var6));
                        else if (var6 instanceof Short || var6 instanceof Byte)
                            var3 = var3.replace("%%" + var4++,Integer.toHexString((int) var6));
                        else if (var6 instanceof Float) var3 = var3.replace("%%" + var4++,Float.toHexString((float) var6));
                        else if (var6 instanceof Double)
                            var3 = var3.replace("%%" + var4++,Double.toHexString((double) var6));
                        else if (var6 instanceof BigInteger)
                            var3 = var3.replace("%%" + var4++,((BigInteger) var6).toString(16));
                        else var3 = var3.replace("%%" + var4++,var6.toString());
                    }
                    else var3 = var3.replace("%%" + var4++,Utilities.Text.a(var6,"null")); // 0 byte

                    if (var6 instanceof Location) { //2 bytes
                        var3 = var3.replace("%%" + var4++, String.valueOf(((Location) var6).getBlockX()));
                        if ((var1 >> var5 & 1) == 1)
                            var3 = var3.replace("%%" + var4++, String.valueOf(((Location) var6).getBlockY()));
                        var3 = var3.replace("%%" + var4++, String.valueOf(((Location) var6).getBlockZ()));
                        if ((var1 >> var5 + 1 & 1) == 1) {
                            try {var3 = var3.replace("%%" + var4++, ((Location) var6).getWorld() == null ? "<not_exists>" : ((Location) var6).getWorld().getName());}
                            catch (IllegalArgumentException ex) {var3 = var3.replace("%%" + var4,"<unloaded>");}

                        }
                        var5+=2;
                    }
                }
            }
            return var3;
        }
    }
    public static final class Mob {

        /**
         * <h3>Code extracted from Minecraft 1.16.4 server</h3>
         * Drop normally an item from entity and create event. (SpawnReason == CUSTOM)
         *
         * @param var0 source
         * @param var1 item
         * @return success
         */
        public static boolean a(Entity var0, ItemStack var1) {
            if (var1.getType().isAir()) return false;
            else {
                Class<?> var2;
                try {var2 = Reflection.a("MathHelper");}
                catch (ClassNotFoundException e) {
                    System.err.println("Reflection failed : 'MathHelper' is not found in Minecraft server classes.");
                    return false;
                }
                Item var3 = var0.getWorld().dropItem(var0.getLocation().add(0,a(var0) - 0.30000001192092896D,0),var1);
                var3.setPickupDelay(40);
                var3.setThrower(var0.getUniqueId());
                Location var4 = var0.getLocation();
                try {
                    float var5 = var4.getPitch() * 0.017453292F;
                    float var6 = var4.getYaw() * 0.017453292F;
                    float var7 = (float) var2.getMethod("cos",float.class).invoke(null,var5);
                    float var8 = A.nextFloat() * 6.2831855F;
                    float var9 = 0.02F * A.nextFloat();
                    var3.setVelocity(new Vector(
                            (double)(-(float) var2.getMethod("sin",float.class).invoke(null,var6) * var7 * 0.3F) + Math.cos(var8) * (double)var9,
                            (double) -(float) var2.getMethod("sin",float.class).invoke(null,var5) * 0.3F + 0.1F + (A.nextFloat() - A.nextFloat()) * 0.1F,
                            (double)((float) var2.getMethod("cos",float.class).invoke(null,var6) * var7 * 0.3F) + Math.sin(var8) * (double)var9));
                }
                catch (Exception ex) {
                    var3.remove();
                    System.err.println("Fail to invoke 'sin' or 'cos' method from 'MathHelper' class.");
                    ex.printStackTrace();
                    return false;
                }
                Event var5;
                if (var0 instanceof Player) var5 = new PlayerDropItemEvent((Player) var0,var3);
                else var5 = new EntityDropItemEvent(var0,var3);
                Bukkit.getServer().getPluginManager().callEvent(var5);
                return !((Cancellable) var5).isCancelled();
            }
        }

        /**
         * <h3>Values extracted from Minecraft 1.16.4 server</h3>
         *
         * @param var0 target
         * @return head height
         */
        public static float a(Entity var0) {
            switch (var0.getType()) {
                case ARROW:
                case SPECTRAL_ARROW:
                    return 0.13F;
                case BOAT:
                    return (float) var0.getHeight();
                case ITEM_FRAME:
                    return 0F;
                case LEASH_HITCH:
                    return -0.0625F;
                case PRIMED_TNT:
                    return 0.15F;
                default:
                    return (var0 instanceof LivingEntity && var0.getPose() == Pose.SLEEPING ?
                            0.2F : (float) (var0.getHeight() * 0.85F));
            }
        }
    }
    public static final class Reflection {
        public static final String VER = Bukkit.getServer().getClass().getName().split("\\.")[3];
        public static final String MCS = "net.minecraft.server." + VER + '.';
        public static final String BKS = "org.bukkit.craftbukkit." + VER + '.';

        /**
         * Get the class from Minecraft server package.
         *
         * @param var0 name of class
         * @return the class
         * @throws ClassNotFoundException the name speaks for itself
         */
        public static Class<?> a(String var0) throws ClassNotFoundException {
            return Class.forName(MCS + var0);
        }

        /**
         * Get the class from Bukkit server package.
         *
         * @param var0 name of class with the subpackage name
         * @return the class
         * @throws ClassNotFoundException the name speaks for itself
         */
        public static Class<?> b(String var0) throws ClassNotFoundException {
            return Class.forName(BKS + var0);
        }
    }
    public static final class Array {

        /**
         * Remove all {@code null} objects (lazy way :3)
         * <p>Useful for: {@link Inventory#getContents()}</p>
         */
        @SafeVarargs
        @SuppressWarnings("unchecked")
        public static <T> T[] a(T... var0) {
            List<T> var1 = new ArrayList<>(var0.length);
            var1.addAll(Arrays.asList(var0));
            var1.removeIf(Objects::isNull);
            return (T[]) var1.toArray(new Object[0]);
        }
    }
}