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

import java.lang.reflect.Field;

public class Reflection
  {
    private Reflection()
      {
      }
    
    public static Object getValue (Object instance, String fieldName)
      {
        try
          {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
          }
        catch (NoSuchFieldException e)
          {
            e.printStackTrace();
          }
        catch (IllegalAccessException e)
          {
            e.printStackTrace();
          }
        return null;
      }
    
    public static void setValue (Object instance, String fieldName, Object value)
      {
        try
          {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
          }
        catch (NoSuchFieldException e)
          {
            e.printStackTrace();
          }
        catch (IllegalAccessException e)
          {
            e.printStackTrace();
          }
      }
  }
