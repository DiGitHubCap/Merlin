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

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.hoot215.merlin.MagicSpell;
import me.hoot215.merlin.Merlin;
import me.hoot215.merlin.Sorcerer;
import me.hoot215.merlin.SpellHandler;
import me.hoot215.merlin.Trigger;
import me.hoot215.merlin.util.Particle;

@SpellHandler(name = "Heal",
    description = "Heals you (right click) or those around you (left click)",
    permission = "merlin.cast.healing.heal")
public class Heal implements MagicSpell
  {
    private final Map<Sorcerer, Long> COOLDOWNS =
        new WeakHashMap<Sorcerer, Long>();
    
    public boolean cast (Sorcerer sorcerer, Trigger trigger, Event event)
      {
        ((PlayerInteractEvent) event).setCancelled(true);
        if (isOnCooldown(sorcerer))
          {
            sorcerer.getPlayer().sendMessage(
                ChatColor.RED
                    + "You are on cooldown for "
                    + ((int) ( (COOLDOWNS.get(sorcerer) - System
                        .currentTimeMillis()) / 1000)) + " more seconds");
            return false;
          }
        if (trigger.isLeftTriggered())
          {
            boolean affected = false;
            for (Entity entity : sorcerer.getPlayer().getNearbyEntities(10, 10,
                10))
              {
                if ( ! (entity instanceof Player)
                    || entity == sorcerer.getPlayer())
                  continue;
                Player player = (Player) entity;
                if (isHealthy(player))
                  continue;
                heal(player);
                Merlin.getInstance().createParticles(player.getLocation(),
                    false, Particle.HEART, 3);
                affected = true;
              }
            if (affected)
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.LIGHT_PURPLE
                        + "Those around you have been healed");
                COOLDOWNS.put(sorcerer, System.currentTimeMillis() + 60000L);
              }
            else
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED + "No one is near you");
              }
            return affected;
          }
        else
          {
            if (isHealthy(sorcerer.getPlayer()))
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED + "You are perfectly healthy");
                return false;
              }
            heal(sorcerer.getPlayer());
            Merlin.getInstance().createParticles(
                sorcerer.getPlayer().getLocation(), false, Particle.HEART, 3);
            COOLDOWNS.put(sorcerer, System.currentTimeMillis() + 60000L);
            return true;
          }
      }
    
    private boolean isOnCooldown (Sorcerer sorcerer)
      {
        return ! ( !COOLDOWNS.containsKey(sorcerer) || COOLDOWNS.get(sorcerer) < System
            .currentTimeMillis());
      }
    
    private boolean isHealthy (Player player)
      {
        if (player.getHealth() < 20)
          return false;
        for (PotionEffect effect : player.getActivePotionEffects())
          {
            PotionEffectType type = effect.getType();
            if (type == PotionEffectType.POISON
                || type == PotionEffectType.WEAKNESS
                || type == PotionEffectType.WITHER)
              return false;
          }
        return true;
      }
    
    private void heal (Player player)
      {
        if (player.getHealth() < 20)
          {
            player.addPotionEffect(new PotionEffect(
                PotionEffectType.REGENERATION, 200, 1));
          }
        player.removePotionEffect(PotionEffectType.POISON);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        player.removePotionEffect(PotionEffectType.WITHER);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "You have been healed");
      }
  }
