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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.hoot215.merlin.MagicSpell;
import me.hoot215.merlin.Merlin;
import me.hoot215.merlin.Sorcerer;
import me.hoot215.merlin.SpellHandler;
import me.hoot215.merlin.Trigger;
import me.hoot215.merlin.util.Particle;

@SpellHandler(name = "Spark",
    description = "Sparks a fire at your target or lights entities on fire",
    permission = "merlin.cast.fire.spark")
public class Spark implements MagicSpell
  {
    public boolean cast (Sorcerer sorcerer, Trigger trigger, Event event)
      {
        if (trigger.isInteractTriggered())
          {
            PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
            interactEvent.setCancelled(true);
            if (trigger == Trigger.LEFT_CLICK_BLOCK
                || trigger == Trigger.RIGHT_CLICK_BLOCK)
              {
                Block block = interactEvent.getClickedBlock();
                Material mat = block.getType();
                if ( !mat.isTransparent() && mat.isSolid())
                  {
                    
                    Block blockUp =
                        interactEvent.getClickedBlock().getRelative(
                            BlockFace.UP);
                    if (blockUp.getType() == Material.AIR)
                      {
                        blockUp.setType(Material.FIRE);
                      }
                    else
                      return false;
                  }
                else
                  return false;
                Merlin.getInstance().createParticles(
                    block.getLocation().add(0.5, 1.5, 0.5), false,
                    Particle.FLAME, 0.1f, 5);
              }
          }
        else if (trigger.isDamageTriggered())
          {
            EntityDamageByEntityEvent damageEvent =
                (EntityDamageByEntityEvent) event;
            damageEvent.getEntity().setFireTicks(120);
            Merlin.getInstance().createParticles(
                damageEvent.getEntity().getLocation().add(0, 1.5, 0), false,
                Particle.FLAME, 0.1f, 5);
          }
        return true;
      }
  }
