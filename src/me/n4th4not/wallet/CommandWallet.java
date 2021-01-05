package me.n4th4not.wallet;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandWallet
    implements CommandExecutor, TabCompleter {

    private final Main a;
    private final Map<UUID,Long> b = new HashMap<>();
//    private final String c;

    CommandWallet(Main var0, PluginCommand var1) {
        this.a = var0;
//        this.c = Utilities.Text.b(var1.getUsage());
        var1.setExecutor(this);
        var1.setTabCompleter(this);
    }
    @Override
    public boolean onCommand(CommandSender var0, Command var1, String var2, String[] var3) {
        if (var3.length == 0) {
            if (!(var0 instanceof Player)) this.a.c.sendMessage(var0,"0C");
            else if (var0.hasPermission(Permission.NO_WALLET)) this.a.c.sendMessage(var0,"0D");
            else {
                boolean var4 = var0.hasPermission(Permission.GAMEMASTER);
                if (var4 ||
                        this.b.getOrDefault(((Player) var0).getUniqueId(),0L)
                        < System.currentTimeMillis() - 300_000L) {
                    this.a.a((Player) var0,12);
                    if (this.a.a((Player) var0,1) == 0) this.a.c.sendMessage(var0,"41");
                    if (!var4) this.b.put(((Player) var0).getUniqueId(),System.currentTimeMillis());
                }
                else this.a.c.sendMessage(var0,"0E");
            }
        }
        else {
            switch (var3[0].toLowerCase()) {
                case "give":
                    if (!Permission.a(var0,Permission.GIVE_MONEY)) this.a.c.sendMessage(var0,"0F",0,"give");
                    else if (var3.length > 1) {
                        Player var4 = Bukkit.getPlayer(var3[1]); //target
                        if (var4 == null) this.a.c.sendMessage(var0,"10",0,var3[1]);
                        else {
                            boolean var5 = Permission.a(var0,var4);
                            int var6;
                            try {
                                var6 = Integer.parseInt(var3[2]); //amount
                                if (var6 < 0) {
                                    this.a.c.sendMessage(var0,"11");
                                    return true;
                                }
                            }
                            catch (NumberFormatException ex) {
                                this.a.c.sendMessage(var0,"15",0,var3[2]);
                                return true;
                            }
                            catch (Exception ex) {var6 = 1;}
                            if (var5 && var0.hasPermission(Permission.NO_WALLET)) {
                                int var7 = 0;
                                ItemStack var8 = this.a.a.getCoinIcon();
                                while (var6 > 0) {
                                    var8.setAmount(Math.min(var6,var8.getMaxStackSize()));
                                    var6-=var8.getAmount();
                                    var7+=var8.getAmount();
                                    if (!((Player) var0).getInventory().addItem(var8).isEmpty())
                                        break;
                                }
                                this.a.c.sendMessage(var0,"13",2,var4,var7);
                            }
                            else {
                                int var7 = this.a.b(var4,0,var6);
                                if (Utilities.Wallet.b(var4) == -1) this.a.c.sendMessage(var0,"4F",2,var4);
                                else if (var7 == 0) this.a.c.sendMessage(var0,"12",2,var4);
                                else {
                                    this.a.c.sendMessage(var0,"13",2,var4,var7);
                                    if (!var5) this.a.c.sendMessage(var4,"14",2,var0,var7);
                                }

                            }
                        }
                    }
                    else this.a.c.sendMessage(var0,"16");
                    break;
                case "transfer":
                    if (!Permission.a(var0,Permission.TRANSFER_BETWEEN_WALLETS)) this.a.c.sendMessage(var0,"0F",0,"transfer");
                    else if (var3.length > 3) {
                        Player var4 = Bukkit.getPlayer(var3[1]); //source
                        if (var4 == null) this.a.c.sendMessage(var0,"10",0,var3[1]);
                        else {
                            Player var5 = Bukkit.getPlayer(var3[2]); //target
                            if (var5 == null) this.a.c.sendMessage(var0,"10",0,var3[2]);
                            else {
                                try {
                                    int var6 = Integer.parseInt(var3[3]); //amount
                                    if (var6 < 0) this.a.c.sendMessage(var0,"11");
                                    else {
                                        var6 = this.a.b(var4,1,var6); //removed
                                        if (Utilities.Wallet.b(var4) == -1) this.a.c.sendMessage(var0,"4F",2,var4);
                                        else if (var6 == 0) this.a.c.sendMessage(var0,"17",2,var4);
                                        else {
                                            int var7 = this.a.b(var5,0,var6); //gave
                                            if (var7 == 0) {
                                                this.a.c.sendMessage(var0,Utilities.Wallet.b(var4) == -1? "4F" : "13",2,var5);
                                                this.a.b(var4,0,var6);
                                            }
                                            else {
                                                if (var7 != var6) this.a.b(var4,0,var6 - var7);
                                                this.a.c.sendMessage(var0,"18",18,var4,var5,var6);
                                                if (!Permission.a(var0,var4)) this.a.c.sendMessage(var4,"19",36,var0,var5,var6);
                                                if (!Permission.a(var0,var5)) this.a.c.sendMessage(var5,"1A",36,var0,var4,var6);
                                            }
                                        }
                                    }
                                }
                                catch (NumberFormatException ex) {this.a.c.sendMessage(var0,"15",0,var3[3]);}
                            }

                        }
                    }
                    else this.a.c.sendMessage(var0,"16");
                    break;
                case "clear":
                    if (!Permission.a(var0,Permission.REMOVE_MONEY)) this.a.c.sendMessage(var0,"0F",0,"clear");
                    else if (var3.length > 1) {
                        Player var4 = Bukkit.getPlayer(var3[1]); //target
                        if (var4 == null) this.a.c.sendMessage(var0,"10",0,var3[1]);
                        else if (var3.length > 2) {
                            try {
                                int var5 = Integer.parseInt(var3[2]); //amount
                                if (var5 < 0) this.a.c.sendMessage(var0,"11");
                                else {
                                    int var6 = this.a.b(var4,1,var5);
                                    if (Utilities.Wallet.b(var4) == -1) this.a.c.sendMessage(var0,"4F",2,var4);
                                    else if (var6 == 0) this.a.c.sendMessage(var0,"17",2,var4);
                                    else {
                                        this.a.c.sendMessage(var0,"1B",2,var4,var6);
                                        if (!Permission.a(var0,var4)) this.a.c.sendMessage(var4,"1C",4,var0,var6);
                                    }
                                }
                            }
                            catch (NumberFormatException ex) {this.a.c.sendMessage(var0,"15",0,var3[2]);}
                        }
                        else {
                            int var6 = this.a.b(var4,2,0);
                            if (Utilities.Wallet.b(var4) == -1) this.a.c.sendMessage(var0,"4F",2,var4);
                            else if (var6 == 0) this.a.c.sendMessage(var0,"17",2,var4);
                            else {
                                this.a.c.sendMessage(var0,"1B",2,var4,var6);
                                if (!Permission.a(var0,var4)) this.a.c.sendMessage(var4,"1C",4,var0,var6);
                            }
                        }
                    }
                    else this.a.c.sendMessage(var0,"16");
                    break;
                case "deposit": // <player|{bank <bank_name>}> <source_player_name> [amount]
                    if (var3.length > 2) {
                        boolean var4;
                        int var5;
                        Player var6;
                        EconomyResponse var7;
                        switch (var3[1].toLowerCase()) {
                            case "player":
                                var6 = Bukkit.getPlayer(var3[2]);
                                if (var6 == null) {
                                    this.a.c.sendMessage(var0,"10",0,var3[2]);
                                    return true;
                                }
                                else if (!Permission.a(var0,(var4 = Permission.a(var0,var6)) ?
                                        Permission.DEPOSIT_TO_ACCOUNT : Permission.DEPOSIT_TO_ANY_ACCOUNT)) {
                                    this.a.c.sendMessage(var0,"0F",0,"deposit player");
                                    return true;
                                }
                                try {var5 = this.a.b(var6,1,Integer.parseInt(var3[3]));}
                                catch (NumberFormatException ex) {
                                    this.a.c.sendMessage(var0,"15",0,var3[3]);
                                    return true;
                                }
                                catch (Exception ex) {var5 = this.a.b(var6,2,0);}
                                if (Utilities.Wallet.b(var6) == -1) this.a.c.sendMessage(var0,"4F",2,var6);
                                else if (var5 < 0) this.a.c.sendMessage(var0,"11");
                                else if (var5 == 0) this.a.c.sendMessage(var0,"17");
                                else {
                                    var7 = this.a.d.depositPlayer(var6,var5);
                                    if (var7.transactionSuccess()) {
                                        this.a.c.sendMessage(var0,"1D",2,var6,var5);
                                        if (!var4) this.a.c.sendMessage(var6,"1E",4,var0,var5);
                                    }
                                    else {
                                        this.a.b(var6,0,var5);
                                        this.a.c.sendMessage(var0,"1F",27,var6,var7);
                                    }
                                }
                                break;
                            case "bank":
                                if (!this.a.d.hasBankSupport()) this.a.c.sendMessage(var0,"49");
                                else if (var3.length > 3) {
                                    if (!this.a.d.bankBalance(var3[2]).transactionSuccess()) {
                                        this.a.c.sendMessage(var0,"20",0,var3[2]);
                                        return true;
                                    }
                                    else if ((var6 = Bukkit.getPlayer(var3[3])) == null) {
                                        this.a.c.sendMessage(var0,"10",0,var3[3]);
                                        return true;
                                    }
                                    else if (!Permission.a(var0,(var4 = Permission.a(var0,var6)) ?
                                            Permission.DEPOSIT_TO_BANK : Permission.DEPOSIT_TO_ANY_BANK)) {
                                        this.a.c.sendMessage(var0,"0F",0,"deposit bank");
                                        return true;
                                    }
                                    else if (!this.a.d.isBankMember(var3[2],var6).transactionSuccess()) {
                                        this.a.c.sendMessage(var0,"21",2,var6,var3[2]);
                                        return true;
                                    }
                                    else if (var3.length > 4) {
                                        try {var7 = this.a.d.depositPlayer(var6,(var5 = this.a.b(var6,1,Integer.parseInt(var3[4]))));}
                                        catch (NumberFormatException ex) {
                                            this.a.c.sendMessage(var0,"15",0,var3[4]);
                                            return true;
                                        }
                                    }
                                    else var7 = this.a.d.depositPlayer(var6,(var5 = this.a.b(var6,2,0)));
                                    if (var7.transactionSuccess()) {
                                        this.a.c.sendMessage(var0,"22",2,var6,var3[2],var5);
                                        if (!var4) this.a.c.sendMessage(var6,"23",4,var0,var3[2],var5);
                                    }
                                    else {
                                        this.a.b(var6,0,var5);
                                        this.a.c.sendMessage(var0,"24",26,var6,var3[2],var7);
                                    }
                                }
                                else this.a.c.sendMessage(var0,"16");
                                break;
                            default:
                                this.a.c.sendMessage(var0,"25");
                        }
                    }
                    else {//deposit [amount]
                        if (!Permission.a(var0,Permission.DEPOSIT_TO_ACCOUNT))
                            this.a.c.sendMessage(var0,"0F",0,"deposit");
                        else if (!(var0 instanceof Player)) this.a.c.sendMessage(var0,"0C");
                        else {
                            int var5;
                            try {var5 = this.a.b((Player) var0,1,Integer.parseInt(var3[1]));}
                            catch (NumberFormatException ex) {
                                this.a.c.sendMessage(var0,"15",0,var3[1]);
                                return true;
                            }
                            catch (Exception ex) {var5 = this.a.b((Player) var0,2,0);}
                            if (var5 < 0) this.a.c.sendMessage(var0,"11");
                            else if (Utilities.Wallet.b((Player) var0) == -1) this.a.c.sendMessage(var0,"4F",2,var0);
                            else if (var5 == 0) this.a.c.sendMessage(var0,"42");
                            else {
                                EconomyResponse var6 = this.a.d.depositPlayer((OfflinePlayer) var0,var5);
                                if (var6.transactionSuccess()) this.a.c.sendMessage(var0,"26",1,var6);
                                else {
                                    this.a.b((Player) var0,0,var5);
                                    this.a.c.sendMessage(var0,"27",3,var6);
                                }
                            }
                        }
                    }
                    break;
                case "withdraw": // <player|{bank <bank_name>}> <player_name> [amount]
                    if (var3.length > 2) {
                        boolean var4;
                        int var5;
                        Player var6;
                        EconomyResponse var7;
                        switch (var3[1].toLowerCase()) {
                            case "player":
                                var6 = Bukkit.getPlayer(var3[2]);
                                if (var6 == null) {
                                    this.a.c.sendMessage(var0,"10",0,var3[2]);
                                    return true;
                                }
                                else if (!Permission.a(var0,(var4 = Permission.a(var0,var6)) ?
                                        Permission.WITHDRAW_FROM_ACCOUNT : Permission.WITHDRAW_FROM_ANY_ACCOUNT)) {
                                    this.a.c.sendMessage(var0,"0F",0,"withdraw player");
                                    return true;
                                }
                                try {var5 = Integer.parseInt(var3[3]);}
                                catch (NumberFormatException ex) {
                                    this.a.c.sendMessage(var0,"15",0,var3[3]);
                                    return true;
                                }
                                catch (ArrayIndexOutOfBoundsException ex) {var5 = a(this.a.d.getBalance(var6));}
                                if (var5 < 0) {
                                    this.a.c.sendMessage(var0,"11");
                                    return true;
                                }
                                else var5 = this.a.b(var6,0,var5);
                                if (Utilities.Wallet.b((Player) var0) == -1) this.a.c.sendMessage(var0,"4F",2,var0);
                                else if (var5 == 0) this.a.c.sendMessage(var0,"12");
                                else if (this.a.d.has(var6,var5)) {
                                    if ((var7 = this.a.d.withdrawPlayer(var6,var5)).transactionSuccess()) {
                                        this.a.c.sendMessage(var0,"28",2,var6,var5);
                                        if (!var4) this.a.c.sendMessage(var6,"29",4,var0,var5);
                                    }
                                    else {
                                        this.a.b(var6,1,var5);
                                        this.a.c.sendMessage(var0,"2A",26,var6,var7);
                                    }
                                }
                                else this.a.c.sendMessage(var0,"2B",2,var6,var5 - this.a.d.getBalance(var6));
                                break;
                            case "bank":
                                if (!this.a.d.hasBankSupport()) this.a.c.sendMessage(var0,"49");
                                else if (var3.length > 3) {
                                    if (!this.a.d.bankBalance(var3[2]).transactionSuccess()) {
                                        this.a.c.sendMessage(var0,"20",0,var3[2]);
                                        return true;
                                    }
                                    else if ((var6 = Bukkit.getPlayer(var3[3])) == null) {
                                        this.a.c.sendMessage(var0,"10",0,var3[3]);
                                        return true;
                                    }
                                    else if (Permission.a(var0,(var4 = Permission.a(var0,var6)) ?
                                                    Permission.WITHDRAW_FROM_BANK : Permission.WITHDRAW_FROM_ANY_BANK)) {
                                        this.a.c.sendMessage(var0,"0F",0,"withdraw bank");
                                        return true;
                                    }
                                    else if (!this.a.d.isBankMember(var3[2],var6).transactionSuccess()) {
                                        this.a.c.sendMessage(var0,"21",2,var6,var3[2]);
                                        return true;
                                    }
                                    try {var5 = Integer.parseInt(var3[4]);}
                                    catch (NumberFormatException ex) {
                                        this.a.c.sendMessage(var0,"15",0,var3[4]);
                                        return true;
                                    }
                                    catch (Exception ex) {var5 = a(this.a.d.bankBalance(var3[2]).amount);}
                                    if (var5 < 0) {
                                        this.a.c.sendMessage(var0,"11");
                                        return true;
                                    }
                                    else var5 = this.a.b(var6,0,var5);
                                    if (Utilities.Wallet.b((Player) var0) == -1) this.a.c.sendMessage(var0,"4F",2,var0);
                                    else if (var5 == 0) this.a.c.sendMessage(var0,"12");
                                    else if (this.a.d.bankHas(var3[2],var5).transactionSuccess()) {
                                        if ((var7 = this.a.d.withdrawPlayer(var6, var5)).transactionSuccess()) {
                                            this.a.c.sendMessage(var0, "2C", 2, var6, var3[2], var5);
                                            if (!var4) this.a.c.sendMessage(var6,"2D",4,var0,var3[2],var5);
                                        }
                                        else {
                                            this.a.b(var6,1,var5);
                                            this.a.c.sendMessage(var0, "2E", 26, var6, var3[2], var7);
                                        }
                                    }
                                    else this.a.c.sendMessage(var0,"45",0,var3[2],var5 - this.a.d.bankBalance(var3[2]).amount);
                                }
                                else this.a.c.sendMessage(var0,"16");
                                break;
                            default:
                                this.a.c.sendMessage(var0,"25");
                        }
                    }
                    else {//withdraw [amount]
                        if (!(var0 instanceof Player)) this.a.c.sendMessage(var0,"0C");
                        else if (!Permission.a(var0,Permission.WITHDRAW_FROM_ACCOUNT))
                            this.a.c.sendMessage(var0,"0F",0,"withdraw");
                        else {
                            int var5;
                            try {var5 = Integer.parseInt(var3[1]);}
                            catch (NumberFormatException ex) {
                                this.a.c.sendMessage(var0,"15",0,var3[1]);
                                return true;
                            }
                            catch (Exception ex) {var5 = a(this.a.d.getBalance((OfflinePlayer) var0));}
                            if (var5 < 0) {
                                this.a.c.sendMessage(var0,"11");
                                return true;
                            }
                            else var5 = this.a.b((Player) var0,0,var5);
                            if (Utilities.Wallet.b((Player) var0) == -1) this.a.c.sendMessage(var0,"4F",2,var0);
                            else if (var5 == 0) this.a.c.sendMessage(var0,"43");
                            else if (this.a.d.has((OfflinePlayer) var0,var5)) {
                                EconomyResponse var6 = this.a.d.withdrawPlayer((OfflinePlayer) var0,var5);
                                if (var6.transactionSuccess()) this.a.c.sendMessage(var0,"2F",0,var5);
                                else {
                                    this.a.b((Player) var0,0,var5);
                                    this.a.c.sendMessage(var0,"30",3,var6);
                                }
                            }
                            else this.a.c.sendMessage(var0,"32",0,var5 - this.a.d.getBalance((OfflinePlayer) var0));
                        }
                    }
                    break;
                case "upgrade":
                    if (var3.length == 1) {
                        if (!(var0 instanceof Player)) this.a.c.sendMessage(var0,"0C");
                        else if (!Permission.a(var0,Permission.WALLET_UPGRADE)) this.a.c.sendMessage(var0,"0F",0,"upgrade");
                        else {
                            int var4 = this.a.a((Player) var0);
                            if (var4 >= this.a.a.getMaxLevel()) this.a.c.sendMessage(var0,"31",0,this.a.a.getMaxLevel());
                            else {
                                Settings.LevelLimitation var5 = this.a.a.getLevel(var4+1);
                                if (var5.getPrice() < 0) this.a.c.sendMessage(var0,"4C",0,var4+1);
                                else if (!this.a.d.has((OfflinePlayer) var0,var5.getPrice()))
                                    this.a.c.sendMessage(var0,"32",0,var5.getPrice() - this.a.d.getBalance((OfflinePlayer) var0));
                                else {
                                    EconomyResponse var6 = this.a.d.withdrawPlayer((OfflinePlayer) var0,var5.getPrice());
                                    if (var6.transactionSuccess()) {
                                        if (this.a.e.playerAdd(null,(Player) var0,Permission.WALLET_LV + (var4 + 1))) {
                                            ((Player) var0).getPersistentDataContainer().set(Utilities.Wallet.C,PersistentDataType.INTEGER,var4+1);
                                            this.a.a((Player) var0,0);
                                            this.a.c.sendMessage(var0,"33",0,var4+1,var5.getCeiling());
                                        }
                                        else {
                                            if (!this.a.d.depositPlayer((OfflinePlayer) var0,var5.getPrice()).transactionSuccess())
                                                this.a.c.sendMessage(var0,"34");
                                            this.a.c.sendMessage(var0,"35");
                                        }
                                    }
                                    else this.a.c.sendMessage(var0,"36",1,var6);
                                }
                            }
                        }
                    }
                    else if (var3.length > 2) { //upgrade <<player_name> <<level> [useMoney]|<useMoney>>
                        Player var4 = Bukkit.getPlayer(var3[1]);
                        if (var4 == null) this.a.c.sendMessage(var0,"10",0,var3[1]);
                        else if (!Permission.a(var0,Permission.WALLET_UPGRADE_OTHERS))
                            this.a.c.sendMessage(var0,"0F",0,"upgrade");
                        else {
                            int var5;
                            boolean var6 = false;
                            try {
                                var5 = Integer.parseInt(var3[2]);
                                if (var5 < 0) {
                                    this.a.c.sendMessage(var0,"11");
                                    return true;
                                }
                                if (var5 > this.a.a.getMaxLevel()) {
                                    this.a.c.sendMessage(var0,"37",0,var3[2],var5);
                                    return true;
                                }
                                else if (this.a.a(var4) > var5) {
                                    this.a.c.sendMessage(var0,"4B",0,var5,Utilities.Wallet.b(var4));
                                    return true;
                                }
                                else if (Utilities.Wallet.b(var4) == var5) {
                                    if (Utilities.Wallet.b(var4) == this.a.a.getMaxLevel()) this.a.c.sendMessage(var0,"4A",0,var3[2],var5);
                                    else this.a.c.sendMessage(var0,"4D",2,var4,var5);
                                    return true;
                                }
                                if (var3.length > 3) {
                                    switch (var3[3].toLowerCase()) {
                                        case "true":
                                        case "yes":
                                            var6 = true;
                                        case "false":
                                        case "no":
                                            break;
                                        default:
                                            this.a.c.sendMessage(var0,"38",0,var3[3]);
                                            return true;
                                    }
                                }
                            }
                            catch (NumberFormatException ex) {
                                if ((var5 = this.a.a(var4)) >= this.a.a.getMaxLevel()) {
                                    this.a.c.sendMessage(var0,"39",2,var4,this.a.a.getMaxLevel());
                                    return true;
                                }
                                var5++;
                                switch (var3[2].toLowerCase()) {
                                    case "true":
                                    case "yes":
                                        var6 = true;
                                    case "false":
                                    case "no":
                                        break;
                                    default:
                                        this.a.c.sendMessage(var0,"38",0,var3[2]);
                                        return true;
                                }
                            }
                            Settings.LevelLimitation var7;
                            if (!this.a.d.has(var4,(var7 = this.a.a.getLevel(var5)).getPrice()))
                                this.a.c.sendMessage(var0,"2B",2,var4,var7.getPrice() - this.a.d.getBalance(var4));
                            else {
                                EconomyResponse var8;
                                if (!var6 || (var8 = this.a.d.withdrawPlayer(var4,var7.getPrice())).transactionSuccess()) {
                                    if (this.a.e.playerAdd(null,var4,Permission.WALLET_LV + (var5))) {
                                        var4.getPersistentDataContainer().set(Utilities.Wallet.C, PersistentDataType.INTEGER,var5+1);
                                        this.a.a(var4,0);
                                        this.a.c.sendMessage(var0,"3B",2,var4,var5,var7.getCeiling());
                                        if (!Permission.a(var0,var4)) this.a.c.sendMessage(var4,"3C",4,var0,var5,var7.getCeiling());
                                    }
                                    else {
                                        if (var6 && !this.a.d.depositPlayer(var4,var7.getPrice()).transactionSuccess())
                                            this.a.c.sendMessage(var0,"3D",2,var4);
                                        this.a.c.sendMessage(var0,"3E",2,var4);
                                    }
                                }
                                else if (var7.getPrice() <= 0) this.a.c.sendMessage(var0,"4E",0,var5);
                                else this.a.c.sendMessage(var0,"3F",10,var4,var8);
                            }
                        }
                    }
                    break;
                case "reload":
                    if (!Permission.a(var0,Permission.GAMEMASTER)) this.a.c.sendMessage(var0,"0F",0,"reload");
                    else {
                        this.a.b.write("|| " + var0.getName() + (var0 instanceof Player? " (" + ((Player) var0).getUniqueId() + ")" : "") + " reloaded the plugin");
                        long var4 = System.currentTimeMillis();
                        this.a.a.reload();
                        this.a.b.write("|| config.yml reloaded in " + (double) (var4 = System.currentTimeMillis() - var4)/1000D + "ms");
                        this.a.c.reload();
                        this.a.b.write("|| messages.yml reloaded in " + (double) (System.currentTimeMillis() - var4)/1000D + "ms");
                        this.a.b.write("|| Done!");
                        this.a.c.sendMessage(var0,"40");
                    }
                    break;
                case "help":
                    var0.sendMessage(var1.getDescription() + ":");
                    return false;
                default:
                    this.a.c.sendMessage(var0,"25");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender var0, Command var1, String var2, String[] var3) {
        return var3.length == 1? Arrays.asList("give","clear","transfer","upgrade","reload","help") : null;
    }

    private int a(double var0) {
        return (int) Math.max(Integer.MIN_VALUE,Math.min(var0,Integer.MAX_VALUE));
    }
}