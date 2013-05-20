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

import org.bukkit.entity.Player;

public class StandardSorcerer implements Sorcerer
  {
    private final Player player;
    private final String name;
    private final Magicks magicks;
    private int level;
    
    public StandardSorcerer(Player player)
      {
        this.player = player;
        name = player.getName();
        magicks = new Magicks(name);
        level = player.getLevel();
      }
    
    public Player getPlayer ()
      {
        return player;
      }
    
    public String getName ()
      {
        return name;
      }
    
    public Magicks getMagicks ()
      {
        return magicks;
      }
    
    public Spell getCurrentSpell ()
      {
        return magicks.getSpell(player.getInventory().getHeldItemSlot());
      }
    
    public int getLevel ()
      {
        return level;
      }
    
    public int getMana ()
      {
        return (int) (player.getExp() * 100 * (this.getLevel() + 1));
      }
    
    public int getMaxMana ()
      {
        return (player.getLevel() + 1) * 100;
      }
    
    public boolean hasMana (int mana)
      {
        return this.getMana() >= mana;
      }
    
    public void updateLevel ()
      {
        level = player.getLevel();
      }
    
    public void setCurrentSpell (Spell spell)
      {
        magicks.setSpell(player.getInventory().getHeldItemSlot(), spell);
      }
    
    public void addLevels (int levels)
      {
        if (levels <= 0)
          return;
        this.setLevel(this.getLevel() + levels);
      }
    
    public void removeLevels (int levels)
      {
        if (levels <= 0)
          return;
        this.setLevel(this.getLevel() - levels);
      }
    
    public void setLevel (int level)
      {
        if (level < 0)
          {
            level = 0;
          }
        this.level = level;
        player.setLevel(level);
      }
    
    public void addMana (int mana)
      {
        if (mana <= 0)
          return;
        this.setMana(this.getMana() + mana);
      }
    
    public void takeMana (int mana)
      {
        if (mana <= 0)
          return;
        this.setMana(this.getMana() - mana);
      }
    
    public void setMana (int mana)
      {
        if (mana < 0)
          {
            player.setExp(0);
          }
        else if (mana >= this.getMaxMana())
          {
            player.setExp(0.99999f);
          }
        else
          {
            player.setExp(mana / 100f / (this.getLevel() + 1));
          }
      }
    
    public void recalculateLevel ()
      {
        int level = 0;
        for (Spell.Type type : Spell.Type.values())
          {
            level += magicks.getLevel(type);
          }
        this.setLevel(level);
      }
    
    public void unload ()
      {
        Merlin.getInstance().getDataHandler().offload(this);
      }
  }
