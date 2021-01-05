package me.n4th4not.wallet;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.block.Container;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class EventsListener
    implements Listener {

    private final Main a;
    //Players who have been blocked due to not being able to get a wallet
    static final List<UUID> A = new ArrayList<>();
    //Log cache -- chests (user/amount transfer to inv)
    private final Map<UUID,Integer> B = new HashMap<>();
    private final static Class<?> C;

    static {
        Class<?> var0;
        try {var0 = Utilities.Reflection.b("inventory.CraftInventoryCustom");}
        catch (Exception ex) {
            ex.printStackTrace();
            var0 = void.class;
        }
        C = var0;
    }

    EventsListener(Main var0) {
        this.a = var0;
    }

    @EventHandler
    public void a(PlayerInteractEvent var0) {
        var0.setCancelled(var0.isBlockInHand() && Utilities.Wallet.a(var0.getItem()));
    }

    @EventHandler
    public void a(CraftItemEvent var0) {
        for (ItemStack var1 : var0.getInventory().getMatrix()) {
            if (Utilities.Wallet.b(var1)) {
                var0.setResult(Event.Result.DENY);
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void a(InventoryClickEvent var0) {
        int var1 = this.a.a((Player) var0.getWhoClicked());
        if (!var0.getWhoClicked().hasPermission(Permission.NO_WALLET)) {
            if (Utilities.Wallet.b(var0.getCurrentItem())) {
                switch (var0.getAction()) {
                    case MOVE_TO_OTHER_INVENTORY:
                    case HOTBAR_SWAP:
                    case HOTBAR_MOVE_AND_READD: //For ViaBackwards compatibility (tested on 1.12.2)
                        var0.setCancelled(true);
                        if (!a(var0.getWhoClicked().getGameMode())
                                || var1 == -1) return;
                        int var2 = this.a.b((Player) var0.getWhoClicked(),0,var0.getCurrentItem().getAmount());
                        if (var2 == 0) this.a.c.sendMessageWithCooldown(var0.getWhoClicked(),"43");
                        else if (var2 == var0.getCurrentItem().getAmount()) var0.setCurrentItem(null);
                        else {
                            ItemStack var3 = var0.getCurrentItem();
                            var3.setAmount(var3.getAmount() - var2);
                            var0.setCurrentItem(var3);
                            this.a.c.sendMessage(var0.getWhoClicked(),"09");
                        }
                        B.merge(var0.getWhoClicked().getUniqueId(),-var2,Integer::sum);
                        break;
                    case DROP_ALL_SLOT:
                        if (var1 != -1 || !a(var0.getWhoClicked().getGameMode())) {
                            this.a.c.sendMessageWithCooldown(var0.getWhoClicked(),"47");
                            var0.setCancelled(true);
                            return;
                        }
                    case PICKUP_ALL:
                    case SWAP_WITH_CURSOR:
                        B.merge(var0.getWhoClicked().getUniqueId(),-var0.getCurrentItem().getAmount(),Integer::sum);
                        break;
                    case DROP_ONE_SLOT:
                        if (var1 != -1 || !a(var0.getWhoClicked().getGameMode())) {
                            this.a.c.sendMessageWithCooldown(var0.getWhoClicked(),"47");
                            var0.setCancelled(true);
                            return;
                        }
                    case PICKUP_ONE:
                        B.merge(var0.getWhoClicked().getUniqueId(),-1,Integer::sum);
                        break;
                    case PLACE_ONE:
                        B.merge(var0.getWhoClicked().getUniqueId(),1,Integer::sum);
                        break;
                    case PICKUP_HALF:
                        B.merge(var0.getWhoClicked().getUniqueId(),-Math.round((var0.getCurrentItem().getAmount() + 0.5F) /2),Integer::sum);
                        break;
                    case PICKUP_SOME:
                        B.merge(var0.getWhoClicked().getUniqueId(),-(var0.getCursor().getMaxStackSize() - var0.getCursor().getAmount()),Integer::sum);
                        break;
                    case PLACE_SOME:
                        B.merge(var0.getWhoClicked().getUniqueId(),var0.getCurrentItem().getMaxStackSize() - var0.getCurrentItem().getAmount(),Integer::sum);
                        break;
                    case PLACE_ALL:
                        if (a(var0.getClickedInventory())) var0.setCancelled(true);
                        else B.merge(var0.getWhoClicked().getUniqueId(),var0.getCursor().getAmount(),Integer::sum);
                        break;
                    case CLONE_STACK:
                        if (var0.getWhoClicked().hasPermission(Permission.GIVE_MONEY)) {
                            B.merge(var0.getWhoClicked().getUniqueId(),-this.a.a.getCoinIcon().getMaxStackSize(),Integer::sum);
                            break;
                        }
                    default:
                        var0.setCancelled(true);
                }

            }
            else if (Utilities.Wallet.b(var0.getCursor())) {
                //Actions when player have some coins in his cursor
                switch (var0.getAction()) {
                    case SWAP_WITH_CURSOR:
                        var0.setCancelled(true);
                        if (Utilities.Wallet.a(var0.getCurrentItem()) && a(var0.getWhoClicked().getGameMode())) {
                            int var2 = this.a.b((Player) var0.getWhoClicked(),0,var0.isRightClick()? 1 : var0.getCursor().getAmount());
                            if (var2 == 0) this.a.c.sendMessage(var0.getWhoClicked(),"43");
                            else if (var2 != var0.getCursor().getAmount()) {
                                ItemStack var3 = var0.getCursor();
                                var3.setAmount(var3.getAmount() - var2);
                                var0.getWhoClicked().setItemOnCursor(var3);
                            }
                            else var0.getWhoClicked().setItemOnCursor(null);
                        }
                        else if (!a(var0.getClickedInventory())) {
                            B.merge(var0.getWhoClicked().getUniqueId(),var0.getCursor().getAmount(),Integer::sum);
                            var0.setCancelled(false);
                        }
                        break;
                    case PLACE_ALL:
                        if (a(var0.getClickedInventory())) var0.setCancelled(true);
                        else B.merge(var0.getWhoClicked().getUniqueId(),var0.getCursor().getAmount(),Integer::sum);
                        break;
                    case PLACE_ONE:
                        if (a(var0.getClickedInventory())) var0.setCancelled(true);
                        else B.merge(var0.getWhoClicked().getUniqueId(),1,Integer::sum);
                    case DROP_ONE_CURSOR:
                    case DROP_ALL_CURSOR:
                        if (var1 != -1 || a(var0.getWhoClicked().getGameMode())) break;
                        this.a.c.sendMessageWithCooldown(var0.getWhoClicked(),"47");
                    default:
                        var0.setCancelled(true);
                }
            }
            else if (Utilities.Wallet.a(var0.getCurrentItem())) {
                if (!a(var0.getWhoClicked().getGameMode())
                        || var1 == -1) {
                    if (a(var0.getClickedInventory(),var0.getWhoClicked())) var0.setCancelled(true);
                    else var0.setCurrentItem(null);
                    return;
                }
                var0.setCancelled(true);
                ItemStack var2 = this.a.a.getCoinIcon();
                int var3 = Math.min(var2.getMaxStackSize(),Utilities.Wallet.a((Player) var0.getWhoClicked()));
                if (var3 == 0) return;
                switch (var0.getAction()) {
                    case MOVE_TO_OTHER_INVENTORY:
                        if (a(var0.getInventory())) return;
                    case DROP_ALL_SLOT:
                    case PICKUP_ALL:
                        break;
                    case DROP_ONE_SLOT:
                        var3 = 1;
                        break;
                    case PICKUP_HALF:
                        var3 = Math.round((var3 + 0.5F) / 2);
                        break;
                    default:
                        return;
                }
                int var4 = this.a.b((Player) var0.getWhoClicked(),1,var3);
                if (var4 == 0) return;
                var2.setAmount(var4);
                switch (var0.getAction()) {
                    case DROP_ALL_SLOT:
                    case DROP_ONE_SLOT:
                        if (!Utilities.Mob.a(var0.getWhoClicked(),var2)) {
                            this.a.b((Player) var0.getWhoClicked(),0,var4);
                            var0.getWhoClicked().closeInventory();
                            ((Player) var0.getWhoClicked()).sendTitle(this.a.c.getMessage("0A"), this.a.c.getMessage("0B"), 10, 35, 15);
                        }
                        return;
                    case PICKUP_ALL:
                    case PICKUP_HALF:
                        var0.getWhoClicked().setItemOnCursor(var2);
                        break;
                    case MOVE_TO_OTHER_INVENTORY:
                        try {
                            int var5 = var0.getInventory().addItem(var2).values().toArray(new ItemStack[0])[0].getAmount();
                            this.a.b((Player) var0.getWhoClicked(),0,var5);
                            B.merge(var0.getWhoClicked().getUniqueId(),var4 - var5,Integer::sum);
                        }
                        catch (ArrayIndexOutOfBoundsException ex) {
                            B.merge(var0.getWhoClicked().getUniqueId(),var4,Integer::sum);
                        }
                }
                ((Player) var0.getWhoClicked()).updateInventory();
            }
        }
        else if (Utilities.Wallet.a(var0.getCurrentItem())) {
            if (a(var0.getClickedInventory(),var0.getWhoClicked())) var0.setCancelled(true);
            else {
                var0.setCurrentItem(null);
                ((Player) var0.getWhoClicked()).updateInventory();
            }
        }
    }

    @EventHandler
    public void a(InventoryDragEvent var0) {
        if (Utilities.Wallet.b(var0.getOldCursor())
                && !var0.getWhoClicked().hasPermission(Permission.NO_WALLET)) {
            for (int var1 : var0.getRawSlots()) {
                if (a(var0.getView().getInventory(var1))) {
                    var0.setResult(Event.Result.DENY);
                    break;
                }
            }
            if (var0.getResult() != Event.Result.DENY)
                B.merge(var0.getWhoClicked().getUniqueId(),var0.getOldCursor().getAmount() - (var0.getCursor() == null? 0 : var0.getCursor().getAmount()),Integer::sum);
        }
    }

    @EventHandler
    public void a(InventoryCloseEvent var0) {
        if (var0.getPlayer().hasPermission(Permission.NO_WALLET)) return;
        Integer var1 = B.remove(var0.getPlayer().getUniqueId());
        if (Utilities.Wallet.b(var0.getPlayer().getItemOnCursor())) {
            ItemStack var3 = var0.getPlayer().getItemOnCursor();
            if (a(var0.getPlayer().getGameMode())) {
                int var2 = this.a.b((Player) var0.getPlayer(),0,var3.getAmount());
                if (var2 != var0.getPlayer().getItemOnCursor().getAmount()) {
                    if (var2 == 0) this.a.c.sendMessage(var0.getPlayer(), "43");
                    else this.a.c.sendMessage(var0.getPlayer(), "09");
                }
                var3.setAmount(var3.getAmount() - var2);
            }
            if (!a(var0.getInventory()) && var3.getAmount() != 0) {
                try {
                    var3.setAmount(var0.getInventory().addItem(var3).values().toArray(new ItemStack[0])[0].getAmount());
                    var1+=var3.getAmount();
                }
                catch (ArrayIndexOutOfBoundsException ex) {var3.setAmount(0);}
            }
            if (var3.getAmount() != 0) {
                if (!a(var0.getPlayer().getGameMode())) this.a.c.sendMessageWithCooldown(var0.getPlayer(),"46");
                else if (!Utilities.Mob.a(var0.getPlayer(),var3)) {
                    this.a.b.write(Utilities.Placeholder.a("%%0 (%%1) destroyed %%2 coin" + (var3.getAmount() > 1? "s":"") + " because coins into his cursor cannot be stored in his wallet, the current inventory and cannot be dropped (cancelled) at the world '%%6' at %%3 %%4 %%5",30,var0.getPlayer(),var3.getAmount()));
                    this.a.c.sendMessage(var0.getPlayer(),"44",0,var3.getAmount());
                }
            }
            var0.getPlayer().setItemOnCursor(null);
        }
        if (var1 != null && var1 != 0) {
            if (var1 < 0)
                this.a.b.write(Utilities.Placeholder.a("%%0 (%%1) got %%2 coin" + (var1 < -1? "s":"") + " by looting the %%3 in the world '%%7' at %%4 %%5 %%6",226,var0.getPlayer(),-var1,var0.getInventory()));
            else this.a.b.write(Utilities.Placeholder.a("%%0 (%%1) put %%2 coin" + (var1 > 1? "s":"") + " in the %%3 in the world '%%7' at %%4 %%5 %%6",226,var0.getPlayer(),var1,var0.getInventory()));
        }

    }

    @EventHandler
    public void a(PlayerDropItemEvent var0) {
        if (Utilities.Wallet.b(var0.getItemDrop().getItemStack()) && !var0.getPlayer().hasPermission(Permission.NO_WALLET))
            this.a.b.write(Utilities.Placeholder.a("%%0 (%%1) dropped %%6 coin" + (var0.getItemDrop().getItemStack().getAmount() > 1 ? "s" : "") + " in %%5 from %%2 %%3 %%4", 30, var0.getPlayer(), var0.getItemDrop().getItemStack().getAmount()));
        else var0.setCancelled(Utilities.Wallet.a(var0.getItemDrop().getItemStack()));
    }

    @EventHandler
    public void a(BlockBreakEvent var0) {
        if (var0.getBlock().getState() instanceof Container
                && !var0.getPlayer().hasPermission(Permission.NO_WALLET)
                && (!a(var0.getPlayer().getGameMode()) || Utilities.Wallet.b(var0.getPlayer()) == -1)) {
            for (ItemStack var1 : ((Container) var0.getBlock().getState()).getInventory().getContents()) {
                if (Utilities.Wallet.b(var1)) {
                    var0.setCancelled(true);
                    this.a.c.sendMessageWithCooldown(var0.getPlayer(),"48");
                    break;
                }
            }
        }
    }

    @EventHandler
    public void a(EntityPickupItemEvent var0) {
        if (var0.getItem().isDead()) return;
        if (Utilities.Wallet.a(var0.getItem().getItemStack())) {
            var0.setCancelled(true);
            var0.getItem().getWorld().createExplosion(var0.getItem().getLocation(),1.5f,false,false,var0.getItem());
        }
        else if (Utilities.Wallet.b(var0.getItem().getItemStack()) && var0.getEntity() instanceof Player
                && !var0.getEntity().hasPermission(Permission.NO_WALLET)) {
            var0.setCancelled(true);
            if (a(((Player) var0.getEntity()).getGameMode())) {
                int var1 = this.a.b((Player) var0.getEntity(), 0, var0.getItem().getItemStack().getAmount());
                if (var1 != 0) {
                    this.a.b.write(Utilities.Placeholder.a("%%0 (%%1) have picked up %%6 coin" + (var1 > 1 ? "s" : "") + " in the world '%%5' at %%2 %%3 %%4",30,var0.getEntity(),var1));
                    if (var1 == var0.getItem().getItemStack().getAmount()) var0.getItem().remove();
                    else {
                        ItemStack var2 = var0.getItem().getItemStack();
                        var2.setAmount(var0.getItem().getItemStack().getAmount() - var1);
                        var0.getItem().setItemStack(var2);
                        this.a.c.sendMessageWithCooldown(var0.getEntity(),"09");
                    }
                    ((Player) var0.getEntity()).incrementStatistic(Statistic.PICKUP,var0.getItem().getItemStack().getType(),var1);
                    var0.getItem().getWorld().playSound(var0.getItem().getLocation(),Sound.ENTITY_ITEM_PICKUP,0.66F,0.75F);
                }
                else this.a.c.sendMessageWithCooldown(var0.getEntity(),"43");
            }
            else this.a.c.sendMessageWithCooldown(var0.getEntity(),"3A");
        }
    }

    @EventHandler
    public void a(PlayerDeathEvent var0) {
        var0.getDrops().removeIf(Utilities.Wallet::a);
        if (a(var0.getEntity().getGameMode()) && !(var0.getKeepInventory()
                || var0.getEntity().hasPermission(Permission.IGNORE_DEATH)
                || var0.getEntity().hasPermission(Permission.NO_WALLET))) {
            int var1 = this.a.b(var0.getEntity(),2,0);
            if (var1 != 0) {
                this.a.b.write(Utilities.Placeholder.a("%%0 (%%1) was killed and dropped %%6 coin" + (var1 > 1 ? "s" : "") + " in the world '%%5' at %%2 %%3 %%4",30,var0.getEntity(),var1,var0.getEntity().getLastDamageCause() == null? "null" : var0.getEntity().getLastDamageCause().getCause()));
                ItemStack var2 = this.a.a.getCoinIcon();
                while (var1 > var2.getMaxStackSize()) {
                    var2.setAmount(var2.getMaxStackSize());
                    var0.getDrops().add(var2.clone());
                    var1 -= var2.getMaxStackSize();
                }
                var2.setAmount(var1);
                var0.getDrops().add(var2);
            }
        }
    }

    @EventHandler
    public void a(PlayerRespawnEvent var0) {
        this.a.a(var0.getPlayer(),0);
    }

    @EventHandler
    public void a(PlayerGameModeChangeEvent var0) {
        this.a.a(var0.getPlayer(),a(var0.getNewGameMode())? 17 : 9);
    }

    @EventHandler
    public void a(PlayerJoinEvent var0) {
        Bukkit.getScheduler().runTaskLater(this.a,() -> this.a.a(var0.getPlayer(),1),10L);
    }

    @EventHandler
    public void a(PlayerQuitEvent var0) {
        this.a.a(var0.getPlayer(),12);
    }

    /**
     * @param var0 target current game mode
     * @return is allowed
     */
    public static boolean a(GameMode var0) {
        return var0 == GameMode.SURVIVAL || var0 == GameMode.ADVENTURE;
    }

    /**
     * @param var0 target
     * @return is blacklisted
     */
    public static boolean a(Inventory var0) {
        if (var0 == null || var0.getHolder() == null || C.isInstance(var0)) return true;
        switch (var0.getType()) {
            case CHEST:
            case BARREL:
            case BLAST_FURNACE:
            case BREWING:
            case DISPENSER:
            case DROPPER:
            case HOPPER:
            case FURNACE:
            case SHULKER_BOX:
                return false;
            default:
                return true;
        }
    }

    /**
     * Is invsee inventory
     */
    public static boolean a(Inventory var0, HumanEntity var1) {
        return var0 instanceof PlayerInventory && var0.getHolder() instanceof Player
                && !((Player) var0.getHolder()).getUniqueId().equals(var1.getUniqueId());
    }
}