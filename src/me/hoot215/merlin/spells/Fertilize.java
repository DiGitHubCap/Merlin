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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;

import me.hoot215.merlin.MagicSpell;
import me.hoot215.merlin.Merlin;
import me.hoot215.merlin.Sorcerer;
import me.hoot215.merlin.SpellHandler;
import me.hoot215.merlin.Trigger;
import me.hoot215.merlin.util.Particle;

@SpellHandler(name = "Fertilize",
    description = "Fertilizes the crops around you",
    permission = "merlin.cast.earth.fertilize")
public class Fertilize implements MagicSpell
  {
    public boolean cast (Sorcerer sorcerer, Trigger trigger, Event event)
      {
        ((PlayerInteractEvent) event).setCancelled(true);
        boolean changedBlocks = false;
        Player player = sorcerer.getPlayer();
        Location loc = player.getLocation();
        int xMax = loc.getBlockX() + 10;
        int yMin = loc.getBlockY() - 10;
        int zMax = loc.getBlockZ() + 10;
        for (int x = loc.getBlockX() - 5; x <= xMax; x++)
          {
            for (int y = loc.getBlockY() + 5; y >= yMin; y--)
              {
                innerLoop : for (int z = loc.getBlockZ() - 5; z <= zMax; z++)
                  {
                    Block block = loc.getWorld().getBlockAt(x, y, z);
                    Material mat = block.getType();
                    switch ( mat )
                      {
                        case CROPS :
                        case MELON_STEM :
                        case PUMPKIN_STEM :
                        case CARROT :
                        case POTATO :
                          if (block.getData() < 7)
                            {
                              block.setData((byte) (block.getData() + 1));
                              Merlin.getInstance().createParticles(
                                  block.getLocation(), true,
                                  Particle.HAPPY_VILLAGER, 1);
                              changedBlocks = true;
                            }
                          break;
                        case SUGAR_CANE_BLOCK :
                          Block blockUp = block.getRelative(BlockFace.UP);
                          if (blockUp.getType() != Material.AIR)
                            break;
                          int height = 1;
                          Block b = block.getRelative(BlockFace.DOWN);
                          while (b.getType() == Material.SUGAR_CANE_BLOCK)
                            {
                              height++;
                              if (height >= 3)
                                continue innerLoop;
                              b = b.getRelative(BlockFace.DOWN);
                            }
                          blockUp.setType(Material.SUGAR_CANE_BLOCK);
                          Merlin.getInstance().createParticles(
                              block.getLocation(), true,
                              Particle.HAPPY_VILLAGER, 5);
                          changedBlocks = true;
                          break;
                        case NETHER_WARTS :
                          if (block.getData() < 3)
                            {
                              block.setData((byte) (block.getData() + 1));
                              Merlin.getInstance().createParticles(
                                  block.getLocation(), true,
                                  Particle.HAPPY_VILLAGER, 5);
                            }
                          break;
                        case COCOA :
                          BlockState state = block.getState();
                          CocoaPlant plant = (CocoaPlant) state.getData();
                          switch ( plant.getSize() )
                            {
                              case SMALL :
                                plant.setSize(CocoaPlantSize.MEDIUM);
                                state.update();
                                Merlin.getInstance().createParticles(
                                    block.getLocation(), true,
                                    Particle.HAPPY_VILLAGER, 5);
                                changedBlocks = true;
                                break;
                              case MEDIUM :
                                plant.setSize(CocoaPlantSize.LARGE);
                                state.update();
                                Merlin.getInstance().createParticles(
                                    block.getLocation(), true,
                                    Particle.HAPPY_VILLAGER, 5);
                                changedBlocks = true;
                                break;
                              default :
                                break;
                            }
                          break;
                        default :
                          break;
                      }
                  }
              }
          }
        if ( !changedBlocks)
          {
            sorcerer.getPlayer().sendMessage(
                ChatColor.RED + "There is nothing near you to be fertilized");
          }
        return changedBlocks;
      }
  }
