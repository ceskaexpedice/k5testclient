/*
 * Copyright (C) 2013 alberto
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
package cz.incad.kramerius.k5.k5velocity.tools;

import cz.incad.kramerius.k5.k5velocity.K5APIRetriever;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.tools.config.DefaultKey;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author alberto
 */
@DefaultKey("vc")
public class VirtualCollectionsTool {

    public static final Logger LOGGER = Logger.getLogger(VirtualCollectionsTool.class.getName());

    HttpServletRequest req;
    String language;
    Map<String, String> cols = new HashMap<String, String>();

    public void configure(Map props) {
        LOGGER.log(Level.INFO, "Configuring vc..");

        req = (HttpServletRequest) props.get("request");
        if (req.getSession().getAttribute("language") != null) {
            language = (String) req.getSession().getAttribute("language");
        } else {
            language = req.getLocale().getLanguage();
        }
        try {
            JSONArray json = new JSONArray(K5APIRetriever.getAsString("/vc/" + language));
            for (int i = 0; i < json.length(); i++) {
                JSONObject jo = json.getJSONObject(i);
                cols.put(jo.getString("pid"), jo.getString(language));
            }
        } catch (IOException ex) {
            Logger.getLogger(VirtualCollectionsTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(VirtualCollectionsTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getName(String pid) {
        if (cols.containsKey(pid)) {
            return cols.get(pid);
        } else {
            return pid;
        }
    }
    

//    private ArrayList languageCodes(){
//        ArrayList l = new ArrayList<String>();
//        String[] langs = kConfiguration.getPropertyList("interface.languages");
//        for (int i = 0; i < langs.length; i++) {
//                    String lang = langs[++i];
//            l.add(lang);
//        }
//        return l;
//    }
//    public List<VirtualCollection> getVirtualCollections() throws Exception {
//        //ArrayList l = new ArrayList<String>(Arrays.asList(kConfiguration.getPropertyList("interface.languages")));
//        
//        
//        return VirtualCollectionsManager.getVirtualCollections(this.fedoraAccess, languageCodes());
//    }
//    
//    public List<VirtualCollection> getVirtualCollectionsLocale() throws Exception {
//        Locale locale = this.localeProvider.get();
//        ArrayList<String> l = new ArrayList<String>();
//        l.add(locale.getLanguage());
//        return VirtualCollectionsManager.getVirtualCollections(this.fedoraAccess, l);
//    }
//    
//    public VirtualCollection getCurrent(){
//         return this.virtualCollectionProvider.get();
//        
//    } 
//    
//    public boolean getCanLeaveCurrent(){
//        return this.getCurrent().isCanLeave();
//    }
//    
//    public String getCurrentText(){
//         VirtualCollection c = this.getCurrent();
//         return c.getDescriptionLocale(this.localeProvider.get().getLanguage());
//    } 
//    
//    
//    public List<String> getHomeTabs() throws Exception{
//         String[] tabs = kConfiguration.getPropertyList("search.home.tabs");
//         if (!this.loggedUsersSingleton.isLoggedUser(this.requestProvider)) {
//             tabs= filterLogged(tabs);
//         }
//         
//         // Mozne hodnoty custom,mostDesirables,newest,facets,browseAuthor,browseTitle,info
//         // Pokud mame nastavenou sbirku NEzobrazime mostDesirables, custom, browseAuthor,browseTitle
//         
//         ArrayList<String> validTabs = new ArrayList<String>();
//         VirtualCollection vc = getCurrent();
//         for(String tab:tabs){
//             if(vc == null ||
//                 tab.equals("info") ||
//                 tab.equals("facets") ||
//                 tab.equals("newest") ||
//                 (tab.equals("collections") && vc==null && getVirtualCollections().size()>0 ) ){
//                 validTabs.add(tab);
//             }
//         }
//         
//         return validTabs;
//    }
//
//    private String[] filterLogged(String[] tabs) {
//        List<String> mustBeLoggedList = new ArrayList<String>(Arrays.asList(kConfiguration.getPropertyList("search.home.tabs.onlylogged")));
//        List<String> alist = new ArrayList<String>();
//        for (int i = 0; i < tabs.length; i++) {
//            if (!mustBeLoggedList.contains(tabs[i])) {
//                alist.add(tabs[i]);
//            }
//        }
//        return (String[]) alist.toArray(new String[alist.size()]);
//    }
}
