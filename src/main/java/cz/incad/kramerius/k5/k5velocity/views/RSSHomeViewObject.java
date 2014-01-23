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
package cz.incad.kramerius.k5.k5velocity.views;

import cz.incad.kramerius.k5.k5velocity.K5APIRetriever;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.velocity.tools.config.DefaultKey;
import org.json.JSONObject;

/**
 *
 * @author alberto
 */
@DefaultKey("rss")
public class RSSHomeViewObject {

    JSONObject json;

    public void getDesirables() {
        try {
            String jStr = K5APIRetriever.getAsString("/feed/mostdesirable");
            this.json = new JSONObject(jStr);
        } catch (Exception ex) {
            Logger.getLogger(RSSHomeViewObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getNewest() {
        try {
            String jStr = K5APIRetriever.getAsString("/feed/newest");
            this.json = new JSONObject(jStr);
        } catch (Exception ex) {
            Logger.getLogger(RSSHomeViewObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<String> getData() {
        ArrayList<String> data = new ArrayList<String>();
        return data;
    }

    public JSONObject getJSON() {
        return json;
    }

    public String getISSNType(String issn) {
        if (issn.matches("[0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9X]")) {
            return "ISSN";
        } else {
            return "ISBN";
        }
    }
    
    public String getApplicationURL(){
        return "";
    }
}
