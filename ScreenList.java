package com.android.commands.monkey;

import java.util.ArrayList;
import java.util.List;
import com.android.commands.monkey.Screen;

/*
 * Created by Jiajun on 30/07/2018
 * This class represent a list of Screen, which is used to decide whether the current screen
 * is equal to any of the previous explored screens.
 */

public class ScreenList {
    
    private List<Screen> screens;
    
    public ScreenList() {
        screens = new ArrayList<Screen>();
    }
    
    public boolean add(Screen screen) {
        return this.screens.add(screen);
    }
    
    public boolean contains(Screen screen) {
        for(Screen s: screens) {
            if(s.structureAndVisibilityEquals(screen)) {
                return true;
            }
        }
        return false;
    }
}