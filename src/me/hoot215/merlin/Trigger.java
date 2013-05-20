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

import org.bukkit.event.block.Action;

public enum Trigger
  {
    LEFT_CLICK_AIR(true, false, false, true, true, false),
    LEFT_CLICK_BLOCK(true, false, true, false, true, false),
    RIGHT_CLICK_AIR(false, true, false, true, true, false),
    RIGHT_CLICK_BLOCK(false, true, true, false, true, false),
    DAMAGE(false, false, false, false, false, true),
    DAMAGED(false, false, false, false, false, true),
    DAMAGED_BY_ENTITY(false, false, false, false, false, true);
    
    private boolean leftTrigger;
    private boolean rightTrigger;
    private boolean blockTrigger;
    private boolean airTrigger;
    private boolean interactTrigger;
    private boolean damageTrigger;
    
    Trigger(boolean leftTrigger, boolean rightTrigger, boolean blockTrigger,
      boolean airTrigger, boolean interactTrigger, boolean damageTrigger)
      {
        this.leftTrigger = leftTrigger;
        this.rightTrigger = rightTrigger;
        this.blockTrigger = blockTrigger;
        this.airTrigger = airTrigger;
        this.interactTrigger = interactTrigger;
        this.damageTrigger = damageTrigger;
      }
    
    public boolean isLeftTriggered ()
      {
        return leftTrigger;
      }
    
    public boolean isRightTriggered ()
      {
        return rightTrigger;
      }
    
    public boolean isBlockTriggered ()
      {
        return blockTrigger;
      }
    
    public boolean isAirTriggered ()
      {
        return airTrigger;
      }
    
    public boolean isInteractTriggered ()
      {
        return interactTrigger;
      }
    
    public boolean isDamageTriggered ()
      {
        return damageTrigger;
      }
    
    public static Trigger asTrigger (Action action)
      {
        switch ( action )
          {
            case LEFT_CLICK_AIR :
              return LEFT_CLICK_AIR;
            case LEFT_CLICK_BLOCK :
              return LEFT_CLICK_BLOCK;
            case RIGHT_CLICK_AIR :
              return RIGHT_CLICK_AIR;
            case RIGHT_CLICK_BLOCK :
              return RIGHT_CLICK_BLOCK;
            default :
              return null;
          }
      }
  }
