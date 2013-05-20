/*
 * Magic plugin inspired by Merlin.
 * Copyright (C) 2013 Andrew Stevanus (Hoot215) <hoot893@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.hoot215.merlin.spells;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.hoot215.merlin.MagicSpell;
import me.hoot215.merlin.Merlin;
import me.hoot215.merlin.Sorcerer;
import me.hoot215.merlin.SpellHandler;
import me.hoot215.merlin.Trigger;
import me.hoot215.merlin.util.Fireworks;

@SpellHandler(name = "Smite",
    description = "Calls down a lighting storm upon your target",
    permission = "merlin.cast.fire.smite")
public class Smite implements MagicSpell
  {
    private final HashSet<Byte> transparentBlocks = new HashSet<Byte>();
    private final Map<Sorcerer, Long> cooldowns =
        new WeakHashMap<Sorcerer, Long>();
      
      {
        Collections.addAll(
            transparentBlocks,
            new Byte[] {(byte) Material.AIR.getId(),
                (byte) Material.WATER.getId(),
                (byte) Material.STATIONARY_WATER.getId(),
                (byte) Material.LAVA.getId(),
                (byte) Material.STATIONARY_LAVA.getId()});
      }
    
    public boolean cast (Sorcerer sorcerer, Trigger trigger, Event event)
      {
        if (isOnCooldown(sorcerer))
          return false;
        cooldowns.put(sorcerer, System.currentTimeMillis() + 1000);
        if (trigger.isInteractTriggered())
          {
            PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
            interactEvent.setCancelled(true);
            Location origin;
            if (trigger.isBlockTriggered())
              {
                origin = interactEvent.getClickedBlock().getLocation();
              }
            else
              {
                Block originBlock =
                    sorcerer.getPlayer().getTargetBlock(transparentBlocks, 100);
                if (originBlock.getType() == Material.AIR)
                  {
                    sorcerer.getPlayer().sendMessage(
                        ChatColor.RED + "No block in sight");
                    return false;
                  }
                origin = originBlock.getLocation();
              }
            bigStrike(origin);
          }
        else if (trigger.isDamageTriggered())
          {
            EntityDamageByEntityEvent damageEvent =
                (EntityDamageByEntityEvent) event;
            littleStrike(damageEvent.getEntity().getLocation());
          }
        return true;
      }
    
    private boolean isOnCooldown (Sorcerer sorcerer)
      {
        return ! ( !cooldowns.containsKey(sorcerer) || cooldowns.get(sorcerer) < System
            .currentTimeMillis());
      }
    
    private void littleStrike (Location loc)
      {
        loc.getWorld().strikeLightning(loc);
      }
    
    private void bigStrike (final Location loc)
      {
        final int radius = 8;
        final int radiusSq = radius * radius;
        final int blockX = loc.getBlockX();
        final int blockY = loc.getBlockY();
        final int blockZ = loc.getBlockZ();
        final int fireworksY = blockY + 15;
        int iter = 0;
        for (float x = -radius; x <= radius; x += 0.5, iter += 2)
          {
            final float finalX = x;
            Merlin.getInstance().getServer().getScheduler()
                .scheduleSyncDelayedTask(Merlin.getInstance(), new Runnable()
                  {
                    public void run ()
                      {
                        float z1 =
                            (float) Math.sqrt(radiusSq - finalX * finalX);
                        Fireworks.detonateFirework(new Location(loc.getWorld(),
                            blockX + finalX, fireworksY, blockZ + z1), true,
                            false, FireworkEffect.Type.BALL, Color.AQUA,
                            Color.AQUA);
                        if (finalX != radius && finalX != -radius)
                          {
                            float z2 = -z1;
                            Fireworks.detonateFirework(
                                new Location(loc.getWorld(), blockX + finalX,
                                    fireworksY, blockZ + z2), true, false,
                                FireworkEffect.Type.BALL, Color.AQUA,
                                Color.AQUA);
                          }
                      }
                  }, iter);
          }
        final int xMax = blockX + radius;
        final int zMax = blockZ + radius;
        Merlin.getInstance().getServer().getScheduler()
            .scheduleSyncDelayedTask(Merlin.getInstance(), new Runnable()
              {
                public void run ()
                  {
                    littleStrike(loc);
                    for (int x = blockX - radius; x <= xMax; x += 2)
                      {
                        for (int z = blockZ - radius; z <= zMax; z += 2)
                          {
                            Location strike =
                                new Location(loc.getWorld(), x, blockY, z);
                            if (strike.distanceSquared(loc) < radiusSq)
                              {
                                littleStrike(strike);
                              }
                          }
                      }
                  }
              }, iter);
      }
  }
