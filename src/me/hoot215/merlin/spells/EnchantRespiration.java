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
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import me.hoot215.merlin.MagicSpell;
import me.hoot215.merlin.Sorcerer;
import me.hoot215.merlin.Spell;
import me.hoot215.merlin.SpellHandler;
import me.hoot215.merlin.Trigger;

@SpellHandler(name = "Enchant Respiration",
    description = "Enchants a helmet with Respiration",
    permission = "merlin.cast.enchantment.respiration")
public class EnchantRespiration implements MagicSpell
  {
    private final Set<Material> helmets = new HashSet<Material>();
      
      {
        Collections.addAll(helmets,
            new Material[] {Material.LEATHER_HELMET, Material.IRON_HELMET,
                Material.GOLD_HELMET, Material.DIAMOND_HELMET});
      }
    
    public boolean cast (Sorcerer sorcerer, Trigger trigger, Event event)
      {
        ItemStack hand = sorcerer.getPlayer().getItemInHand();
        if (isHelmet(hand))
          {
            int level =
                (int) Math.ceil(sorcerer.getMagicks().getLevel(
                    Spell.Type.ENCHANTMENT) / 5.0);
            if (level > 3)
              {
                level = 3;
              }
            hand.addEnchantment(Enchantment.OXYGEN, level);
            return true;
          }
        else
          {
            sorcerer.getPlayer()
                .sendMessage(
                    ChatColor.RED
                        + "That item cannot be enchanted with this spell");
            return false;
          }
      }
    
    private boolean isHelmet (ItemStack item)
      {
        return helmets.contains(item.getType());
      }
  }
