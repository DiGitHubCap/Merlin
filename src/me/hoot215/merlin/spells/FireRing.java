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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

@SpellHandler(name = "Fire Ring",
    description = "Creates a ring of fire around your target",
    permission = "merlin.cast.fire.firering")
public class FireRing implements MagicSpell
  {
    public boolean cast (Sorcerer sorcerer, Trigger trigger, Event event)
      {
        PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
        interactEvent.setCancelled(true);
        final Location origin;
        if (trigger.isBlockTriggered())
          {
            origin = interactEvent.getClickedBlock().getLocation().add(0, 1, 0);
          }
        else
          {
            origin = sorcerer.getPlayer().getLocation();
          }
        int radius = 5;
        final World world = origin.getWorld();
        final int blockX = origin.getBlockX();
        final int blockY = origin.getBlockY();
        final int blockZ = origin.getBlockZ();
        int iter = 0;
        int x = radius, z = 0;
        int radiusError = 1 - x;
        while (x >= z)
          {
            final int finalX = x, finalZ = z;
            Merlin.getInstance().getServer().getScheduler()
                .scheduleSyncDelayedTask(Merlin.getInstance(), new Runnable()
                  {
                    public void run ()
                      {
                        setFires(world, blockX, blockY, blockZ, finalX, finalZ);
                      }
                  }, iter);
            z++;
            if (radiusError < 0)
              {
                radiusError += 2 * z + 1;
              }
            else
              {
                x--;
                radiusError += 2 * (z - x) + 1;
              }
            iter += 3;
          }
        return true;
      }
    
    private void setFires (World world, int blockX, int blockY, int blockZ,
      int x, int z)
      {
        Location[] loc = new Location[8];
        loc[0] = new Location(world, blockX + x, blockY, blockZ + z);
        loc[1] = new Location(world, blockX + z, blockY, blockZ + x);
        loc[2] = new Location(world, blockX - x, blockY, blockZ + z);
        loc[3] = new Location(world, blockX - z, blockY, blockZ + x);
        loc[4] = new Location(world, blockX - x, blockY, blockZ - z);
        loc[5] = new Location(world, blockX - z, blockY, blockZ - x);
        loc[6] = new Location(world, blockX + x, blockY, blockZ - z);
        loc[7] = new Location(world, blockX + z, blockY, blockZ - x);
        for (int i = 0; i < 8; i++)
          {
            setFire(loc[i], 3);
            Merlin.getInstance().createParticles(loc[i], true, Particle.FLAME,
                0.1f, 3);
          }
      }
    
    private void setFire (Location loc, int depth)
      {
        Block block = loc.getBlock();
        Block blockDown = loc.getBlock().getRelative(BlockFace.DOWN);
        if (block.getType() != Material.AIR
            || blockDown.getType() == Material.AIR)
          {
            Block highest = loc.getWorld().getHighestBlockAt(loc);
            Block highestUp = highest.getRelative(BlockFace.UP);
            if (Math.abs(loc.getY() - highestUp.getLocation().getY()) > depth
                || highest.getType() == Material.AIR)
              return;
            highestUp.setType(Material.FIRE);
          }
        else
          {
            block.setType(Material.FIRE);
          }
      }
  }
