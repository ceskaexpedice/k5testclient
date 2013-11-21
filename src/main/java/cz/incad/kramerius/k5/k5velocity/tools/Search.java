/*
 * Copyright (C) 2013 Alberto Hernandez
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.incad.kramerius.k5.k5velocity.tools;

import cz.incad.kramerius.k5.k5velocity.K5APIRetriever;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.velocity.tools.config.DefaultKey;
import org.json.JSONObject;

/**
 *
 * @author alberto
 */
@DefaultKey("search")
public class Search {

    public static final Logger LOGGER = Logger.getLogger(Search.class.getName());

    public JSONObject getJSON(String queryString){
        try {
            String jStr = K5APIRetriever.getJSON("/solr?"+queryString);
            return new JSONObject(jStr);
        } catch (Exception ex) {
           LOGGER.log(Level.SEVERE, null, ex);
           return null;
        }
    }
    
    private final String homeFacets = "&facet.field=fedora.model&facet.field=keywords";
    private final String daFacetsRange = "&facet.range=rok&facet.range.start=1100&facet.range.end=2014&facet.range.gap=1";
    private final String daFacets = "&facet.field=rok&f.rok.facet.limit=-1&f.rok.facet.sort=false";
    
    public JSONObject getHome(){
        return getJSON("q=*:*&facet=true&rows=0" + homeFacets);
    }
    
    public String getDa(){
        try {
            return K5APIRetriever.getJSON("/solr?q=*:*&wt=xml&facet=true&rows=0" + daFacets);
        } catch (Exception ex) {
           LOGGER.log(Level.SEVERE, null, ex);
           return null;
        }
    }
    
    public String getAuthors(String q){
        try {
            return K5APIRetriever.getJSON("/solr?q=*:*&wt=xml&facet=true&rows=0" + daFacets);
        } catch (Exception ex) {
           LOGGER.log(Level.SEVERE, null, ex);
           return null;
        }
        
    }
    
    public String getTitles(String q){
        try {
            String s = "q=browse_title:["+q+"*%20TO%20*]&wt=xml&facet.field=browse_title&f.browse_title.facet.sort=false&facet.mincount=1&facet.limit=50&facet=true&rows=0";
            return K5APIRetriever.getJSON("/solr?" + s);
        } catch (Exception ex) {
           LOGGER.log(Level.SEVERE, null, ex);
           return null;
        }
        
    }
}
