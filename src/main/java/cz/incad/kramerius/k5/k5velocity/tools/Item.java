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
import static cz.incad.kramerius.k5.k5velocity.tools.Search.LOGGER;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author alberto
 */
public class Item {

    public static final Logger LOGGER = Logger.getLogger(Item.class.getName());

    HttpServletRequest req;
    JSONObject json;
    JSONArray jContext;
    

    public void configure(Map props) {
            req = (HttpServletRequest) props.get("request");
            //String pid = req.getParameter("pid");
            //jContext = new JSONArray(K5APIRetriever.getAsString("/item/" + pid + "/context"));
    }

    public JSONArray getContext() {
        try {
            if (jContext == null) {
                String pid = req.getParameter("pid");
                jContext = new JSONArray(K5APIRetriever.getAsString("/item/" + pid + "/context"));
            }
            return jContext;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public JSONObject getFields() {
        try {
            if (json == null) {
                String pid = req.getParameter("pid");
                json = new JSONObject(K5APIRetriever.getAsString("/item/" + pid));
            }
            return json;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public JSONObject getSiblings(){
        try {
                String pid = req.getParameter("pid");
                JSONObject js = new JSONObject(K5APIRetriever.getAsString("/item/" + pid + "/siblings"));
            
            return js;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
        
    }
    
    public JSONObject getChildren(){
        try {
                String pid = req.getParameter("pid");
                JSONObject js = new JSONObject(K5APIRetriever.getAsString("/item/" + pid + "/children"));
            
            return js;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
        
    }
    
    

    public boolean getCheck() {
        return getContext().length() > 0;
    }

}
