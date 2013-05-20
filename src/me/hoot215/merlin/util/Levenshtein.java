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

public final class Levenshtein
  {
    private Levenshtein()
      {
      }
    
    public static int getDistance (String s, String t)
      {
        if (s == null)
          {
            if (t == null)
              return 0;
            return t.length();
          }
        if (t == null)
          return s.length();
        if (s.equals(t))
          return 0;
        
        char[] sArray = s.toCharArray();
        char[] tArray = t.toCharArray();
        int max = sArray.length > tArray.length ? tArray.length : sArray.length;
        int distance = Math.abs(sArray.length - tArray.length);
        for (int i = 0; i < max; i++)
          {
            if (sArray[i] != tArray[i])
              {
                distance++;
              }
          }
        return distance;
      }
  }
