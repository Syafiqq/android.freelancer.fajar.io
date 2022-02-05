package io.localhost.freelancer.statushukum.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.localhost.freelancer.statushukum.R;

public class MenuModel {
    public static HashSet<MenuModelType> lawMenus = new HashSet<MenuModelType>();
    public static HashSet<MenuModelType> lawMenusWhitelist = new HashSet<MenuModelType>();

    static {
        lawMenus.add(MenuModelType.UU);
        lawMenus.add(MenuModelType.TAP_MPR);
        lawMenus.add(MenuModelType.UU_DARURAT);
        lawMenus.add(MenuModelType.PERPU);
        lawMenus.add(MenuModelType.PP);
        lawMenus.add(MenuModelType.PERPRES);
        lawMenusWhitelist.add(MenuModelType.UU);
        lawMenusWhitelist.add(MenuModelType.UU_DARURAT);
        lawMenusWhitelist.add(MenuModelType.PERPU);
    }

    public static int getMenuResourceId(MenuModelType type) {
        switch (type) {
            case UU: return R.id.nav_menu_dashboard_rule_uu;
            case TAP_MPR: return R.id.nav_menu_dashboard_rule_tap_mpr;
            case UU_DARURAT: return R.id.nav_menu_dashboard_rule_uu_darurat;
            case PERPU: return R.id.nav_menu_dashboard_rule_perpu;
            case PP: return R.id.nav_menu_dashboard_rule_pp;
            case PERPRES: return R.id.nav_menu_dashboard_rule_perpres;
        }
        return R.id.nav_menu_dashboard_rule_uu;
    }

    public static String getDatabaseCategory(MenuModelType type) {
        switch (type) {
            case UU: return "1";
            case TAP_MPR: return "2";
            case UU_DARURAT: return "3";
            case PERPU: return "4";
            case PP: return "5";
            case PERPRES: return "6";
        }
        return "1";
    }
}
