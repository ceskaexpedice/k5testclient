/*
 * Copyright (C) 2014 alberto
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package cz.incad.kramerius.k5.k5velocity.views;

import cz.incad.kramerius.k5.k5velocity.K5APIRetriever;
import cz.incad.kramerius.k5.k5velocity.views.itms.ContextMenuItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author alberto
 */
public class ContextMenu {
    
    JSONObject rights;
    HttpServletRequest req;
    public void configure(Map props) {
        req = (HttpServletRequest) props.get("request");
    }
    
    private JSONObject rights() throws JSONException, IOException{
        return new JSONObject(K5APIRetriever.getAsString("/rights", req));
    }
    
    public String getRights(){
        try {
            return rights().toString();
        } catch (JSONException ex) {
            Logger.getLogger(ContextMenu.class.getName()).log(Level.SEVERE, null, ex);
            return "1";
        } catch (IOException ex) {
            Logger.getLogger(ContextMenu.class.getName()).log(Level.SEVERE, null, ex);
            return "2";
        }
    }
    
    private boolean isAllowed(String name){
        try {
            return rights.has(name) && rights.getBoolean(name);
        } catch (JSONException ex) {
            Logger.getLogger(ContextMenu.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public List<ContextMenuItem> getItems(){
        List<ContextMenuItem> list =  new ArrayList<ContextMenuItem>();
        try {
            rights = rights();
            if(isAllowed("reindex")){
                list.add(new ContextMenuItem("javascript:reindex();", "administrator.menu.reindex"));
            }
            if(isAllowed("sort")){
                list.add(new ContextMenuItem("javascript:serverSort();", "administrator.menu.sort", false));
            }
            if(isAllowed("reindex")){
                list.add(new ContextMenuItem("javascript:deletefromindex();", "administrator.menu.deletefromindex"));
            }
            if(isAllowed("deleteuuid")){
                list.add(new ContextMenuItem("javascript:deletePid();", "administrator.menu.delete"));
            }
            if(isAllowed("setpublic") && isAllowed("setprivate")){
                list.add(new ContextMenuItem("javascript:changeFlag.change();", "administrator.menu.setpublic"));
            }
            if(isAllowed("export")){
                list.add(new ContextMenuItem("javascript:exportFOXML();", "administrator.menu.exportFOXML"));
                
                String iso3country = "";
                String iso3lang =  "";
                list.add(new ContextMenuItem("javascript:parametrizedProcess.open('parametrized_static_export',{'country':'"+iso3country+"','lang':'"+iso3lang+"'}); javascript:hideAdminMenu();",
                "administrator.menu.export", false));
            }
            if(isAllowed("manage_lr_process")){
                list.add(new ContextMenuItem("javascript:generateDeepZoomTiles();", "administrator.menu.generateDeepZoomTiles"));
                list.add(new ContextMenuItem("javascript:deleteGeneratedDeepZoomTiles();", "administrator.menu.deleteGeneratedDeepZoomTiles"));
            }
            if(isAllowed("show_statictics")){
                list.add(new ContextMenuItem("javascript:statistics.showContextDialog();", "administrator.menu.dialogs.statistics.title"));
            }
            if(isAllowed("rightsadmin")){
                list.add(new ContextMenuItem("javascript:securedActionsTableForCtxMenu('read,administrate');", "administrator.menu.showrights"));
            }
            if(isAllowed("administrate")){
                list.add(new ContextMenuItem("javascript:securedStreamsTableForCtxMenu('read,administrate');", "administrator.menu.showstremrights"));
            }
            if(isAllowed("editor")){
                list.add(new ContextMenuItem("javascript:openEditor();", "administrator.menu.editor", false));
            }
            if(isAllowed("virtualcollections")){
                list.add(new ContextMenuItem("javascript:vcAddToVirtualCollection();", "administrator.menu.virtualcollection.add"));
            }
            
            
            
        } catch (JSONException ex) {
            Logger.getLogger(ContextMenu.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ContextMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
}
