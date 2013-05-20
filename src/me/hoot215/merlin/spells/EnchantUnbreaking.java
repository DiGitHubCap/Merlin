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

@SpellHandler(name = "Enchant Unbreaking",
    description = "Enchants an item with Unbreaking",
    permission = "merlin.cast.enchantment.unbreaking")
public class EnchantUnbreaking implements MagicSpell
  {
    private final Set<Material> items = new HashSet<Material>();
      
      {
        Collections.addAll(items, new Material[] {Material.SHEARS,
            Material.FISHING_ROD, Material.FLINT_AND_STEEL,
            Material.CARROT_STICK, Material.BOW, Material.WOOD_AXE,
            Material.WOOD_HOE, Material.WOOD_PICKAXE, Material.WOOD_SPADE,
            Material.WOOD_SWORD, Material.STONE_AXE, Material.STONE_HOE,
            Material.STONE_PICKAXE, Material.STONE_SPADE, Material.STONE_SWORD,
            Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
            Material.IRON_AXE, Material.IRON_HOE, Material.IRON_PICKAXE,
            Material.IRON_SPADE, Material.IRON_SWORD, Material.IRON_HELMET,
            Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS,
            Material.IRON_BOOTS, Material.GOLD_AXE, Material.GOLD_HOE,
            Material.GOLD_PICKAXE, Material.GOLD_SPADE, Material.GOLD_SWORD,
            Material.GOLD_HELMET, Material.GOLD_CHESTPLATE,
            Material.GOLD_LEGGINGS, Material.GOLD_BOOTS, Material.DIAMOND_AXE,
            Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE,
            Material.DIAMOND_SPADE, Material.DIAMOND_SPADE,
            Material.DIAMOND_SWORD, Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS});
      }
    
    public boolean cast (Sorcerer sorcerer, Trigger trigger, Event event)
      {
        ItemStack hand = sorcerer.getPlayer().getItemInHand();
        if (isItem(hand))
          {
            int level =
                (int) Math.ceil(sorcerer.getMagicks().getLevel(
                    Spell.Type.ENCHANTMENT) / 5.0);
            if (level > 3)
              {
                level = 3;
              }
            hand.addEnchantment(Enchantment.DURABILITY, level);
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
    
    private boolean isItem (ItemStack item)
      {
        return items.contains(item.getType());
      }
  }
