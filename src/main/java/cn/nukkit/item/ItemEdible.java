package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.item.food.Food;
import cn.nukkit.math.Vector3;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class ItemEdible extends Item {
    public ItemEdible(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    public ItemEdible(int id) {
        super(id);
    }

    public ItemEdible(int id, Integer meta) {
        super(id, meta);
    }

    public ItemEdible(int id, Integer meta, int count) {
        super(id, meta, count);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.getFoodData().getLevel() < player.getFoodData().getMaxLevel() || player.isCreative()) {
            return true;
        }
        player.getFoodData().sendFoodLevel();
        return false;
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        Food food = Food.getByRelative(this);
        
        if (food == null || tickUsed < food.getEatingTick()) {
            return false;
        }
        
        PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(player, this);
        
        player.getServer().getPluginManager().callEvent(consumeEvent);
        if (consumeEvent.isCancelled()) {
            player.getInventory().sendContents(player);
            return false;
        }
        
        if (food.eatenBy(player)) {
            player.completeUsingItem(item.getNetworkId(), CompletedUsingItemPacket.ACTION_EAT);
            
            if (player.isAdventure() || player.isSurvival()) {
                --this.count;
                player.getInventory().setItemInHand(this);
            }
        }
        
        return true;
    }
}
