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

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import me.hoot215.merlin.MagicSpell;
import me.hoot215.merlin.Sorcerer;
import me.hoot215.merlin.Spell.Type;
import me.hoot215.merlin.Merlin;
import me.hoot215.merlin.SpellHandler;
import me.hoot215.merlin.Trigger;

@SpellHandler(name = "Blink", description = "Teleports you to your target",
    permission = "merlin.cast.teleportation.blink")
public class Blink implements MagicSpell
  {
    private final HashSet<Byte> transparentBlocks = new HashSet<Byte>();
      
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
        PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
        interactEvent.setCancelled(true);
        if (trigger.isBlockTriggered())
          {
            Location target =
                interactEvent.getClickedBlock().getLocation().add(0.5, 1, 0.5);
            target.setPitch(sorcerer.getPlayer().getLocation().getPitch());
            target.setYaw(sorcerer.getPlayer().getLocation().getYaw());
            int cost = getCost(sorcerer.getPlayer().getLocation(), target);
            if (cost > sorcerer.getMana())
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED
                        + "You do not have enough mana to teleport there");
                return false;
              }
            target.getWorld().playEffect(sorcerer.getPlayer().getEyeLocation(),
                Effect.ENDER_SIGNAL, 0);
            if (cost >= 900)
              {
                Merlin.getInstance().shatterGlass(sorcerer, cost);
              }
            sorcerer.getPlayer().teleport(target);
            sorcerer.takeMana(cost);
            addExp(sorcerer, cost / 10);
            sorcerer.getMagicks().addExp(Type.TELEPORTATION, cost / 10);
            target.getWorld().playEffect(sorcerer.getPlayer().getEyeLocation(),
                Effect.ENDER_SIGNAL, 0);
            return true;
          }
        else
          {
            Location target =
                sorcerer.getPlayer().getTargetBlock(transparentBlocks, 150)
                    .getLocation().add(0.5, 1, 0.5);
            target.setPitch(sorcerer.getPlayer().getLocation().getPitch());
            target.setYaw(sorcerer.getPlayer().getLocation().getYaw());
            int cost = getCost(sorcerer.getPlayer().getLocation(), target);
            if (cost > sorcerer.getMana())
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED
                        + "You do not have enough mana to teleport there");
                return false;
              }
            target.getWorld().playEffect(sorcerer.getPlayer().getEyeLocation(),
                Effect.ENDER_SIGNAL, 0);
            if (cost >= 900)
              {
                Merlin.getInstance().shatterGlass(sorcerer, cost);
              }
            sorcerer.getPlayer().teleport(target);
            sorcerer.takeMana(cost);
            addExp(sorcerer, cost / 10);
            target.getWorld().playEffect(sorcerer.getPlayer().getEyeLocation(),
                Effect.ENDER_SIGNAL, 0);
            return true;
          }
      }
    
    private int getCost (Location loc1, Location loc2)
      {
        return ((int) loc1.distanceSquared(loc2)) * 10;
      }
    
    private void addExp (Sorcerer sorcerer, int exp)
      {
        if (sorcerer.getMagicks().addExp(Type.TELEPORTATION, exp))
          {
            sorcerer.getPlayer().sendMessage(
                ChatColor.GREEN + "You have just levelled up in "
                    + ChatColor.BLUE + Type.TELEPORTATION.getName()
                    + ChatColor.GREEN + "! New level: " + ChatColor.DARK_PURPLE
                    + sorcerer.getMagicks().getLevel(Type.TELEPORTATION));
            sorcerer.addLevels(1);
            sorcerer.getPlayer().sendMessage(
                ChatColor.DARK_AQUA + "You are now level " + ChatColor.AQUA
                    + sorcerer.getLevel());
            sorcerer.getPlayer().playSound(sorcerer.getPlayer().getLocation(),
                Sound.LEVEL_UP, 1, 0.5f);
          }
      }
  }
