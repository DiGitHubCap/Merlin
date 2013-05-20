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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

public class SorcererManager implements Runnable
  {
    final Map<Player, Sorcerer> sorcerers =
        new ConcurrentHashMap<Player, Sorcerer>();
    private final Merlin plugin = Merlin.getInstance();
    
    public void run ()
      {
        int iter = 0;
        while (plugin.isEnabled())
          {
            iter = 0;
            for (final Sorcerer sorcerer : sorcerers.values())
              {
                if (sorcerer == null)
                  break;
                plugin.getServer().getScheduler()
                    .scheduleSyncDelayedTask(plugin, new Runnable()
                      {
                        public void run ()
                          {
                            if (sorcerer.getPlayer().isOnline())
                              {
                                sorcerer.addMana(sorcerer.getLevel() + 1);
                              }
                          }
                      }, iter);
                iter++;
              }
            try
              {
                Thread.sleep(2500);
              }
            catch (InterruptedException e)
              {
                e.printStackTrace();
              }
          }
      }
    
    public Collection<Sorcerer> getSorcerers ()
      {
        return Collections.unmodifiableCollection(sorcerers.values());
      }
    
    public Sorcerer getSorcerer (String name)
      {
        return this.getSorcerer(plugin.getServer().getPlayer(name));
      }
    
    public Sorcerer getSorcerer (Player player)
      {
        if (player == null)
          return null;
        
        if (sorcerers.containsKey(player))
          {
            return sorcerers.get(player);
          }
        else
          {
            return this.loadSorcerer(player);
          }
      }
    
    public void startManaRegeneration ()
      {
        new Thread(this).start();
      }
    
    public Sorcerer loadSorcerer (Player player)
      {
        Sorcerer sorcerer = new StandardSorcerer(player);
        sorcerers.put(player, sorcerer);
        return sorcerer;
      }
    
    public void unloadSorcerer (Player player)
      {
        if ( !sorcerers.containsKey(player))
          return;
        
        sorcerers.get(player).unload();
        sorcerers.remove(player);
      }
  }
