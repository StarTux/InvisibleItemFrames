package com.cavetale.invisibleitemframes;

import com.cavetale.core.event.block.PlayerBlockAbilityQuery;
import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public final class InvisibleItemFramesPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    void onProjectileCollide(ProjectileCollideEvent event) {
        if (!(event.getCollidedWith() instanceof ItemFrame)) return;
        ItemFrame itemFrame = (ItemFrame) event.getCollidedWith();
        Projectile proj = event.getEntity();
        // Check player
        if (!(proj.getShooter() instanceof Player)) return;
        Player player = (Player) proj.getShooter();
        if (!player.hasPermission("invisibleitemframes.use")) return;
        // Check projectile
        if (proj instanceof Arrow) {
            Arrow arrow = (Arrow) proj;
            PotionData pot = arrow.getBasePotionData();
            if (pot.getType() != PotionType.INVISIBILITY) return;
        } else if (proj instanceof ThrownPotion) {
            ThrownPotion thrown = (ThrownPotion) proj;
            boolean hasInvisibility = false;
            for (PotionEffect pot : thrown.getEffects()) {
                if (PotionEffectType.INVISIBILITY.equals(pot.getType())) {
                    hasInvisibility = true;
                    break;
                }
            }
            if (!hasInvisibility) return;
        } else {
            return;
        }
        // Check build perms
        if (!PlayerBlockAbilityQuery.Action.BUILD.query(player, itemFrame.getLocation().getBlock())) return;
        itemFrame.setVisible(false);
        if (proj instanceof Arrow) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ItemFrame)) return;
        if (!(event.getDamager() instanceof Player)) return;
        ItemFrame itemFrame = (ItemFrame) event.getEntity();
        if (itemFrame.isVisible()) return;
        itemFrame.setVisible(true);
    }
}
