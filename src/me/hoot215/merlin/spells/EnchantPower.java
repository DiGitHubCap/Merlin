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

@SpellHandler(name = "Enchant Power",
    description = "Enchants a bow with Power",
    permission = "merlin.cast.enchantment.power")
public class EnchantPower implements MagicSpell
  {
    public boolean cast (Sorcerer sorcerer, Trigger trigger, Event event)
      {
        ItemStack hand = sorcerer.getPlayer().getItemInHand();
        if (isTool(hand))
          {
            int level =
                (int) Math.ceil(sorcerer.getMagicks().getLevel(
                    Spell.Type.ENCHANTMENT) / 5.0);
            if (level > 5)
              {
                level = 5;
              }
            hand.addEnchantment(Enchantment.ARROW_DAMAGE, level);
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
    
    private boolean isTool (ItemStack item)
      {
        return item.getType() == Material.BOW;
      }
  }
