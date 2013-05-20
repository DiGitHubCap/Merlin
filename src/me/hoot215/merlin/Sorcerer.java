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

public interface Sorcerer
  {
    public Player getPlayer ();
    
    public String getName ();
    
    public Magicks getMagicks ();
    
    public Spell getCurrentSpell ();
    
    public int getLevel ();
    
    public int getMana ();
    
    public int getMaxMana ();
    
    public boolean hasMana (int mana);
    
    public void setCurrentSpell (Spell spell);
    
    public void addMana (int mana);
    
    public void takeMana (int mana);
    
    public void setMana (int mana);
    
    public void addLevels (int levels);
    
    public void removeLevels (int levels);
    
    public void setLevel (int level);
    
    public void recalculateLevel();
    
    public void unload ();
  }
