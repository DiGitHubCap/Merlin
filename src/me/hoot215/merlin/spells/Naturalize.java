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

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import me.hoot215.merlin.MagicSpell;
import me.hoot215.merlin.Merlin;
import me.hoot215.merlin.Sorcerer;
import me.hoot215.merlin.SpellHandler;
import me.hoot215.merlin.Trigger;
import me.hoot215.merlin.util.Particle;

@SpellHandler(name = "Naturalize",
    description = "Creates grass and flowers at your target",
    permission = "merlin.cast.earth.naturalize")
public class Naturalize implements MagicSpell
  {
    public boolean cast (Sorcerer sorcerer, Trigger trigger, Event event)
      {
        ((PlayerInteractEvent) event).setCancelled(true);
        boolean changedBlocks = false;
        Location loc = sorcerer.getPlayer().getLocation();
        int xMax = loc.getBlockX() + 2;
        int zMax = loc.getBlockZ() + 2;
        for (int x = loc.getBlockX() - 2; x <= xMax; x++)
          {
            for (int z = loc.getBlockZ() - 2; z <= zMax; z++)
              {
                Block block = loc.getWorld().getBlockAt(x, loc.getBlockY(), z);
                if (block.getType() == Material.AIR
                    && block.getRelative(BlockFace.DOWN).getType() == Material.GRASS)
                  {
                    int rand = new Random().nextInt(10);
                    if (rand == 0)
                      continue;
                    else if (rand < 4)
                      {
                        block.setType(Material.LONG_GRASS);
                        block.setData((byte) 1);
                        changedBlocks = true;
                      }
                    else if (rand < 7)
                      {
                        block.setType(Material.YELLOW_FLOWER);
                        changedBlocks = true;
                      }
                    else
                      {
                        block.setType(Material.RED_ROSE);
                        changedBlocks = true;
                      }
                    Merlin.getInstance().createParticles(block.getLocation(),
                        true, Particle.HAPPY_VILLAGER, 1);
                  }
                else
                  continue;
              }
          }
        return changedBlocks;
      }
  }
