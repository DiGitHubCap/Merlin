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

package me.hoot215.merlin;

import java.util.ArrayList;
import java.util.List;

import me.hoot215.merlin.util.Particle;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

public class PlayerListener implements Listener
  {
    private final Merlin plugin = Merlin.getInstance();
    private final SorcererManager sorcererManager = plugin.getSorcererManager();
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin (PlayerJoinEvent event)
      {
        sorcererManager.loadSorcerer(event.getPlayer());
      }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit (PlayerQuitEvent event)
      {
        sorcererManager.unloadSorcerer(event.getPlayer());
      }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryMoveItem (InventoryMoveItemEvent event)
      {
        if ( ! (event.getInitiator() instanceof PlayerInventory)
            || ! (event.getDestination() instanceof BrewerInventory))
          return;
        if (event.getItem().getType() != Material.QUARTZ)
          return;
        event.setCancelled(true);
        int amount = event.getItem().getAmount();
        ItemStack[] inv = event.getInitiator().getContents();
        for (int i = 0; i < inv.length; i++)
          {
            ItemStack item = inv[i];
            if (item.getType() != Material.QUARTZ)
              continue;
            if (item.getAmount() == amount)
              {
                inv[i] = null;
              }
            else
              {
                item.setAmount(item.getAmount() - amount);
              }
          }
        event.getInitiator().setContents(inv);
        event.getDestination().setItem(3,
            new ItemStack(Material.QUARTZ, amount));
      }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onExpBottleEvent (ExpBottleEvent event)
      {
        for (MetadataValue meta : event.getEntity().getMetadata(
            "expbottlelevel"))
          {
            if (meta == null)
              return;
            if (meta.getOwningPlugin() != plugin)
              continue;
            event.setExperience(event.getExperience() * meta.asInt());
            break;
          }
      }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileLaunch (ProjectileLaunchEvent event)
      {
        Projectile projectile = event.getEntity();
        if (projectile.getType() != EntityType.THROWN_EXP_BOTTLE
            || ! (projectile.getShooter() instanceof Player))
          return;
        Player player = (Player) projectile.getShooter();
        ItemStack item = player.getInventory().getItemInHand();
        if (item.getType() != Material.EXP_BOTTLE)
          return;
        ItemMeta meta = item.getItemMeta();
        if ( !meta.hasLore())
          return;
        for (String s : meta.getLore())
          {
            if (s == null)
              return;
            if (s.startsWith("Level "))
              {
                projectile.setMetadata("expbottlelevel", new LevelMetadata(
                    Integer.parseInt(s.substring(s.indexOf(' ') + 1))));
              }
          }
      }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLevelChange (PlayerLevelChangeEvent event)
      {
        sorcererManager.getSorcerer(event.getPlayer()).setLevel(
            event.getNewLevel());
      }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerExpChange (PlayerExpChangeEvent event)
      {
        Player player = event.getPlayer();
        Sorcerer sorcerer = sorcererManager.getSorcerer(player);
        sorcerer.addMana(event.getAmount() * 10);
        event.setAmount(0);
      }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn (PlayerRespawnEvent event)
      {
        event.getPlayer().setLevel(
            sorcererManager.getSorcerer(event.getPlayer()).getLevel());
      }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage (EntityDamageEvent event)
      {
        if (event.getCause() == DamageCause.ENTITY_ATTACK)
          return;
        if (event.getEntity() instanceof Player)
          {
            Sorcerer sorcerer =
                sorcererManager.getSorcerer((Player) event.getEntity());
            Spell spell = sorcerer.getCurrentSpell();
            if (spell == null)
              return;
            int actionIndex = spell.isActivatedBy(Trigger.DAMAGED);
            if (actionIndex == -1)
              return;
            if ( !sorcerer.getPlayer().hasPermission(spell.getPermission()))
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED
                        + "You don't have permission to cast this spell!");
                return;
              }
            if (sorcerer.getLevel() < spell.getLevelRequirement())
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED + "You need to be level " + ChatColor.BLUE
                        + spell.getLevelRequirement() + ChatColor.RED
                        + " to use this spell");
                return;
              }
            if (sorcerer.getMagicks().getLevel(spell.getType()) < spell
                .getTypeLevelRequirement())
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED + "You need to be level " + ChatColor.BLUE
                        + spell.getTypeLevelRequirement() + ChatColor.RED
                        + " in " + ChatColor.GREEN + spell.getType().getName()
                        + ChatColor.RED + " to use this spell");
                return;
              }
            int cost = spell.getCost(actionIndex);
            if ( !sorcerer.hasMana(cost))
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED + "You need " + ChatColor.BLUE
                        + (cost - sorcerer.getMana()) + ChatColor.RED
                        + " more mana");
                return;
              }
            if (spell.cast(sorcerer, Trigger.DAMAGED, event))
              {
                sorcerer.takeMana(cost);
                if (cost >= 900)
                  {
                    plugin.shatterGlass(sorcerer, cost);
                  }
                if (sorcerer.getMagicks().addExp(spell.getType(), spell.getExp()))
                  {
                    sorcerer.recalculateLevel();
                    sorcerer.getPlayer().sendMessage(
                        ChatColor.GREEN + "You have just levelled up in "
                            + ChatColor.BLUE + spell.getType().getName()
                            + ChatColor.GREEN + "! New level: "
                            + ChatColor.DARK_PURPLE
                            + sorcerer.getMagicks().getLevel(spell.getType()));
                    sorcerer.getPlayer().sendMessage(
                        ChatColor.DARK_AQUA + "You are now level " + ChatColor.AQUA
                            + sorcerer.getLevel());
                    sorcerer.getPlayer()
                        .playSound(sorcerer.getPlayer().getLocation(),
                            Sound.LEVEL_UP, 1, 0.5f);
                  }
              }
          }
      }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity (EntityDamageByEntityEvent event)
      {
        if (event.getCause() != DamageCause.ENTITY_ATTACK)
          return;
        damagerCheck : if (event.getDamager() instanceof Player)
          {
            Sorcerer sorcerer =
                sorcererManager.getSorcerer((Player) event.getDamager());
            Spell spell = sorcerer.getCurrentSpell();
            if (spell == null)
              break damagerCheck;
            int actionIndex = spell.isActivatedBy(Trigger.DAMAGE);
            if (actionIndex == -1)
              break damagerCheck;
            if ( !sorcerer.getPlayer().hasPermission(spell.getPermission()))
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED
                        + "You don't have permission to cast this spell!");
                break damagerCheck;
              }
            if (sorcerer.getLevel() < spell.getLevelRequirement())
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED + "You need to be level " + ChatColor.BLUE
                        + spell.getLevelRequirement() + ChatColor.RED
                        + " to use this spell");
                break damagerCheck;
              }
            if (sorcerer.getMagicks().getLevel(spell.getType()) < spell
                .getTypeLevelRequirement())
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED + "You need to be level " + ChatColor.BLUE
                        + spell.getTypeLevelRequirement() + ChatColor.RED
                        + " in " + ChatColor.GREEN + spell.getType().getName()
                        + ChatColor.RED + " to use this spell");
                break damagerCheck;
              }
            int cost = spell.getCost(actionIndex);
            if ( !sorcerer.hasMana(cost))
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED + "You need " + ChatColor.BLUE
                        + (cost - sorcerer.getMana()) + ChatColor.RED
                        + " more mana");
                break damagerCheck;
              }
            if (spell.cast(sorcerer, Trigger.DAMAGE, event))
              {
                sorcerer.takeMana(cost);
                if (cost >= 900)
                  {
                    plugin.shatterGlass(sorcerer, cost);
                  }
                if (sorcerer.getMagicks().addExp(spell.getType(), spell.getExp()))
                  {
                    sorcerer.recalculateLevel();
                    sorcerer.getPlayer().sendMessage(
                        ChatColor.GREEN + "You have just levelled up in "
                            + ChatColor.BLUE + spell.getType().getName()
                            + ChatColor.GREEN + "! New level: "
                            + ChatColor.DARK_PURPLE
                            + sorcerer.getMagicks().getLevel(spell.getType()));
                    sorcerer.getPlayer().sendMessage(
                        ChatColor.DARK_AQUA + "You are now level " + ChatColor.AQUA
                            + sorcerer.getLevel());
                    sorcerer.getPlayer()
                        .playSound(sorcerer.getPlayer().getLocation(),
                            Sound.LEVEL_UP, 1, 0.5f);
                  }
              }
          }
        entityCheck : if (event.getEntity() instanceof Player)
          {
            Sorcerer sorcerer =
                sorcererManager.getSorcerer((Player) event.getEntity());
            Spell spell = sorcerer.getCurrentSpell();
            if (spell == null)
              break entityCheck;
            int actionIndex = spell.isActivatedBy(Trigger.DAMAGED_BY_ENTITY);
            if (actionIndex == -1)
              break entityCheck;
            if ( !sorcerer.getPlayer().hasPermission(spell.getPermission()))
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED
                        + "You don't have permission to cast this spell!");
                break entityCheck;
              }
            if (sorcerer.getLevel() < spell.getLevelRequirement())
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED + "You need to be level " + ChatColor.BLUE
                        + spell.getLevelRequirement() + ChatColor.RED
                        + " to use this spell");
                break entityCheck;
              }
            if (sorcerer.getMagicks().getLevel(spell.getType()) < spell
                .getTypeLevelRequirement())
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED + "You need to be level " + ChatColor.BLUE
                        + spell.getTypeLevelRequirement() + ChatColor.RED
                        + " in " + ChatColor.GREEN + spell.getType().getName()
                        + ChatColor.RED + " to use this spell");
                break entityCheck;
              }
            int cost = spell.getCost(actionIndex);
            if ( !sorcerer.hasMana(cost))
              {
                sorcerer.getPlayer().sendMessage(
                    ChatColor.RED + "You need " + ChatColor.BLUE
                        + (cost - sorcerer.getMana()) + ChatColor.RED
                        + " more mana");
                break entityCheck;
              }
            if (spell.cast(sorcerer, Trigger.DAMAGED_BY_ENTITY, event))
              {
                sorcerer.takeMana(cost);
                if (cost >= 900)
                  {
                    plugin.shatterGlass(sorcerer, cost);
                  }
                if (sorcerer.getMagicks().addExp(spell.getType(), spell.getExp()))
                  {
                    sorcerer.recalculateLevel();
                    sorcerer.getPlayer().sendMessage(
                        ChatColor.GREEN + "You have just levelled up in "
                            + ChatColor.BLUE + spell.getType().getName()
                            + ChatColor.GREEN + "! New level: "
                            + ChatColor.DARK_PURPLE
                            + sorcerer.getMagicks().getLevel(spell.getType()));
                    sorcerer.getPlayer().sendMessage(
                        ChatColor.DARK_AQUA + "You are now level " + ChatColor.AQUA
                            + sorcerer.getLevel());
                    sorcerer.getPlayer()
                        .playSound(sorcerer.getPlayer().getLocation(),
                            Sound.LEVEL_UP, 1, 0.5f);
                  }
              }
          }
      }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract (PlayerInteractEvent event)
      {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK
            || event.getAction() == Action.RIGHT_CLICK_BLOCK)
          {
            ItemStack hand = event.getPlayer().getItemInHand();
            Block block = event.getClickedBlock();
            if (block.getType() == Material.BREWING_STAND
                && hand.getType() == Material.QUARTZ)
              {
                event.setCancelled(true);
                BrewingStand brewingStand = (BrewingStand) block.getState();
                BrewerInventory inv = brewingStand.getInventory();
                ItemStack[] contents = inv.getContents();
                boolean affected = false;
                for (int i = 0; i < 3; i++)
                  {
                    ItemStack item = contents[i];
                    if (item != null && item.getType() == Material.POTION
                        && item.getDurability() == 0)
                      {
                        item.setType(Material.EXP_BOTTLE);
                        List<String> lore = new ArrayList<String>();
                        lore.add("Level 1");
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        affected = true;
                      }
                  }
                if (affected)
                  {
                    inv.setContents(contents);
                    if (hand.getAmount() == 1)
                      {
                        event.getPlayer().setItemInHand(null);
                      }
                    else
                      {
                        hand.setAmount(hand.getAmount() - 1);
                        event.getPlayer().setItemInHand(hand);
                      }
                    brewingStand.update();
                    plugin.createParticles(
                        brewingStand.getLocation().add(0.5, 1.0, 0.5), false,
                        Particle.WITCH_MAGIC, 10);
                  }
                else
                  {
                    event.getPlayer().sendMessage(
                        ChatColor.RED + "There are no water bottles"
                            + " in the brewing stand");
                  }
                return;
              }
          }
        
        Sorcerer sorcerer = sorcererManager.getSorcerer(event.getPlayer());
        Spell spell = sorcerer.getCurrentSpell();
        if (spell == null)
          return;
        int actionIndex =
            spell.isActivatedBy(Trigger.asTrigger(event.getAction()));
        if (actionIndex == -1)
          return;
        if ( !sorcerer.getPlayer().hasPermission(spell.getPermission()))
          {
            sorcerer.getPlayer()
                .sendMessage(
                    ChatColor.RED
                        + "You don't have permission to cast this spell!");
            return;
          }
        if (sorcerer.getLevel() < spell.getLevelRequirement())
          {
            sorcerer.getPlayer().sendMessage(
                ChatColor.RED + "You need to be level " + ChatColor.BLUE
                    + spell.getLevelRequirement() + ChatColor.RED
                    + " to use this spell");
            return;
          }
        if (sorcerer.getMagicks().getLevel(spell.getType()) < spell
            .getTypeLevelRequirement())
          {
            sorcerer.getPlayer().sendMessage(
                ChatColor.RED + "You need to be level " + ChatColor.BLUE
                    + spell.getTypeLevelRequirement() + ChatColor.RED + " in "
                    + ChatColor.GREEN + spell.getType().getName()
                    + ChatColor.RED + " to use this spell");
            return;
          }
        int cost = spell.getCost(actionIndex);
        if ( !sorcerer.hasMana(cost))
          {
            sorcerer.getPlayer().sendMessage(
                ChatColor.RED + "You need " + ChatColor.BLUE
                    + (cost - sorcerer.getMana()) + ChatColor.RED
                    + " more mana");
            return;
          }
        if (spell.cast(sorcerer, Trigger.asTrigger(event.getAction()), event))
          {
            sorcerer.takeMana(cost);
            if (cost >= 900)
              {
                plugin.shatterGlass(sorcerer, cost);
              }
            if (sorcerer.getMagicks().addExp(spell.getType(), spell.getExp()))
              {
                sorcerer.recalculateLevel();
                sorcerer.getPlayer().sendMessage(
                    ChatColor.GREEN + "You have just levelled up in "
                        + ChatColor.BLUE + spell.getType().getName()
                        + ChatColor.GREEN + "! New level: "
                        + ChatColor.DARK_PURPLE
                        + sorcerer.getMagicks().getLevel(spell.getType()));
                sorcerer.getPlayer().sendMessage(
                    ChatColor.DARK_AQUA + "You are now level " + ChatColor.AQUA
                        + sorcerer.getLevel());
                sorcerer.getPlayer()
                    .playSound(sorcerer.getPlayer().getLocation(),
                        Sound.LEVEL_UP, 1, 0.5f);
              }
          }
      }
  }
