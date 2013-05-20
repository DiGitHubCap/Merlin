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

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public final class LevelMetadata implements MetadataValue
  {
    private final int level;
    
    public LevelMetadata(int level)
      {
        this.level = level;
      }
    
    public boolean asBoolean ()
      {
        return false;
      }
    
    public byte asByte ()
      {
        return 0;
      }
    
    public double asDouble ()
      {
        return level;
      }
    
    public float asFloat ()
      {
        return level;
      }
    
    public int asInt ()
      {
        return level;
      }
    
    public long asLong ()
      {
        return level;
      }
    
    public short asShort ()
      {
        return (short) level;
      }
    
    public String asString ()
      {
        return String.valueOf(level);
      }
    
    public Plugin getOwningPlugin ()
      {
        return Merlin.getInstance();
      }
    
    public void invalidate ()
      {
      }
    
    public Object value ()
      {
        return Integer.valueOf(level);
      }
  }
