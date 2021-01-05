package me.n4th4not.wallet;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public final class Permission {
    public static final String PREFIX = "wallet";
    public static final String GAMEMASTER = PREFIX + "s";
    public static final String GIVE_MONEY = PREFIX + ".give";
    public static final String TRANSFER_BETWEEN_WALLETS = PREFIX + ".transfer";
    public static final String DEPOSIT_TO_BANK = PREFIX + ".deposit.bank";
    public static final String DEPOSIT_TO_ACCOUNT = PREFIX + ".deposit.account";
    public static final String WITHDRAW_FROM_BANK = PREFIX + ".withdraw.bank";
    public static final String WITHDRAW_FROM_ACCOUNT = PREFIX + ".withdraw.account";
    public static final String DEPOSIT_TO_ANY_BANK = PREFIX + ".deposit.banks";
    public static final String DEPOSIT_TO_ANY_ACCOUNT = PREFIX + ".deposit.accounts";
    public static final String WITHDRAW_FROM_ANY_BANK = PREFIX + ".withdraw.banks";
    public static final String WITHDRAW_FROM_ANY_ACCOUNT = PREFIX + ".withdraw.accounts";
    public static final String REMOVE_MONEY = PREFIX + ".clear";
    public static final String IGNORE_DEATH = PREFIX + ".safe";
    public static final String NO_WALLET = PREFIX + ".disabled";
    public static final String WALLET_LV = PREFIX + ".lv";
    public static final String WALLET_UPGRADE = PREFIX + ".upgrade";
    public static final String WALLET_UPGRADE_OTHERS = WALLET_UPGRADE + ".others";

    public static boolean a(CommandSender var0, String var1) {
        return var0 instanceof ConsoleCommandSender || var0.hasPermission(var1);
    }
    public static boolean a(CommandSender var0, Player var1) {
        return var0 instanceof Player && ((Player) var0).getUniqueId().equals(var1.getUniqueId());
    }
}