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

package me.hoot215.merlin.util;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public final class MagicalMath
  {
    private MagicalMath()
      {
      }
    
    public static BlockFace getBlockFace (float yaw)
      {
        if (yaw >= 0 && yaw < 22.5)
          return BlockFace.NORTH;
        else if (yaw >= 22.5 && yaw < 67.5)
          return BlockFace.NORTH_EAST;
        else if (yaw >= 67.5 && yaw < 112.5)
          return BlockFace.EAST;
        else if (yaw >= 112.5 && yaw < 157.5)
          return BlockFace.SOUTH_EAST;
        else if (yaw >= 157.5 && yaw < 202.5)
          return BlockFace.SOUTH;
        else if (yaw >= 202.5 && yaw < 247.5)
          return BlockFace.SOUTH_WEST;
        else if (yaw >= 247.5 && yaw < 292.5)
          return BlockFace.WEST;
        else if (yaw >= 292.5 && yaw < 337.5)
          return BlockFace.NORTH_WEST;
        else if (yaw >= 337.5 && yaw < 360)
          return BlockFace.NORTH;
        else
          return BlockFace.SELF;
      }
    
    public static int getCardinalDirection (float yaw)
      {
        if (yaw >= 0 && yaw < 22.5)
          // North
          return 7;
        else if (yaw >= 22.5 && yaw < 67.5)
          // North-east
          return 6;
        else if (yaw >= 67.5 && yaw < 112.5)
          // East
          return 3;
        else if (yaw >= 112.5 && yaw < 157.5)
          // South-east
          return 0;
        else if (yaw >= 157.5 && yaw < 202.5)
          // South
          return 1;
        else if (yaw >= 202.5 && yaw < 247.5)
          // South-west
          return 2;
        else if (yaw >= 247.5 && yaw < 292.5)
          // West
          return 5;
        else if (yaw >= 292.5 && yaw < 337.5)
          // North-west
          return 8;
        else if (yaw >= 337.5 && yaw < 360)
          // North
          return 7;
        else
          return 4;
      }
    
    public static int rotateClockwise (int direction)
      {
        switch ( direction )
          {
            case 0 :
              return 1;
            case 1 :
              return 2;
            case 2 :
              return 5;
            case 3 :
              return 0;
            case 5 :
              return 8;
            case 6 :
              return 3;
            case 7 :
              return 6;
            case 8 :
              return 7;
            default :
              return 4;
          }
      }
    
    public static int rotateClockwise (int direction, int times)
      {
        if (times < 1)
          return -1;
        if (times == 1)
          return rotateClockwise(direction);
        else
          return rotateClockwise(rotateClockwise(direction), times - 1);
      }
    
    public static int rotateCounterClockwise (int direction)
      {
        switch ( direction )
          {
            case 0 :
              return 3;
            case 1 :
              return 0;
            case 2 :
              return 1;
            case 3 :
              return 6;
            case 5 :
              return 2;
            case 6 :
              return 7;
            case 7 :
              return 8;
            case 8 :
              return 5;
            default :
              return 4;
          }
      }
    
    public static int rotateCounterClockwise (int direction, int times)
      {
        if (times < 1)
          return -1;
        if (times == 1)
          return rotateCounterClockwise(times);
        else
          return rotateCounterClockwise(rotateCounterClockwise(direction),
              times - 1);
      }
    
    // Essentials Start
    public static int getExpAtLevel (Player player)
      {
        return getExpAtLevel(player.getLevel());
      }
    
    public static int getExpAtLevel (int level)
      {
        if (level > 29)
          {
            return 62 + (level - 30) * 7;
          }
        if (level > 15)
          {
            return 17 + (level - 15) * 3;
          }
        return 17;
      }
    
    public static int getExpUntilNextLevel (Player player)
      {
        int exp = (int) Math.round(getExpAtLevel(player) * player.getExp());
        int nextLevel = player.getLevel();
        return getExpAtLevel(nextLevel) - exp;
      }
    // Essentials End
  }
