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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.tools.config.DefaultKey;
import org.json.JSONObject;

/**
 *
 * @author alberto
 */
@DefaultKey("search")
public class Search {

    public static final Logger LOGGER = Logger.getLogger(Search.class.getName());
    
    HttpServletRequest req;
    public void configure(Map props){
        req = (HttpServletRequest) props.get("request");
    }

    public JSONObject getJSON(String queryString){
        try {
            String jStr = K5APIRetriever.getJSON("/search?"+queryString);
            return new JSONObject(jStr);
        } catch (Exception ex) {
           LOGGER.log(Level.SEVERE, null, ex);
           return null;
        }
    }
    
    private final String homeFacets = "&facet.field=fedora.model&facet.field=keywords&facet.field=collection";
    private final String daFacetsRange = "&facet.range=rok&facet.range.start=1100&facet.range.end=2014&facet.range.gap=1";
    private final String daFacets = "&facet.field=rok&f.rok.facet.limit=-1&f.rok.facet.sort=false";
    private final String groupedParams = "&group.field=root_pid&group.type=normal&group.threshold=1" +
            "&group.facet=true&group=true&group.ngroups=true";
    
    private boolean isFilterByType(){
         String[] fqs = req.getParameterValues("fq");
         if(fqs == null){
             return false;
         }
         for(String fq : fqs){
             if(fq.equals("document_type") || fq.equals("fedora.model"))
             return true;
         }
         return false;
    }
    
    private boolean isFieldedSearch(){
         String[] fqs = req.getParameterValues("fq");
//         for(String fq : fqs){
//             if(fq.equals("document_type") || fq.equals("fedora.model"))
//                 return true;
//         }
         return false;
    }
    
    private String getSort() throws UnsupportedEncodingException{
        String res = "";
        String sort = req.getParameter("sort");
        String asis = req.getParameter("asis");
        String q = req.getParameter("q");
        boolean filterByType = isFilterByType();
        boolean fieldedSearch = isFieldedSearch();
        if(sort != null && !sort.equals("") && asis != null){
            res = sort;
        }else if(sort != null && !sort.equals("") && filterByType){
            res = sort;
        }else if(sort != null && !sort.equals("")){
            res = "level asc, " + sort;
        }else if((q == null || q.equals("")) && filterByType){
            res = "title_sort asc";
        }else if(q != null && !q.equals("") && filterByType){
            res = "score desc, title_sort asc";
        }else if(fieldedSearch){
            res = "level asc, title_sort asc, score desc";
        }else if(q == null || q.equals("")){
            res = "level asc, title_sort asc, score desc";
        }else{
            res = "level asc, score desc";
        }
        
        return "&sort=" + URLEncoder.encode(res, "UTF-8");
    }
    
    private String getFilters(){
        String res = "";
         String[] fqs = req.getParameterValues("fq");
         if(fqs == null){
             return "";
         }
         for(String fq : fqs){
             res += "&fq=" + fq;
         }
        return res;
    }
    
    public String getGrouped(){
        try {
            String q = req.getParameter("q");
            if(q==null || q.equals("")){
                q = "*:*";
            }
            return K5APIRetriever.getAsString("/search?q="+q+"&wt=xml&facet=true" + 
                    homeFacets + 
                    daFacets + 
                    getSort() +
                    getFilters() +
                    groupedParams);
        } catch (Exception ex) {
           LOGGER.log(Level.SEVERE, null, ex);
           return null;
        }
    }
    
    public String getUngrouped(){
        try {
            String q = req.getParameter("q");
            if(q==null || q.equals("")){
                q = "*:*";
            }
            return K5APIRetriever.getAsString("/search?q="+q+"&wt=xml&facet=true" + 
                    homeFacets + 
                    daFacets + 
                    getSort() +
                    getFilters());
        } catch (Exception ex) {
           LOGGER.log(Level.SEVERE, null, ex);
           return null;
        }
    }
    
    public JSONObject getHome(){
        return getJSON("q=*:*&facet=true&rows=0" + homeFacets);
    }
    
    public String getDa(){
        try {
            String q = req.getParameter("q");
            if(q==null || q.equals("")){
                q = "*:*";
            }
            return K5APIRetriever.getJSON("/search?q="+q+"&wt=xml&facet=true&rows=0" + daFacets);
        } catch (Exception ex) {
           LOGGER.log(Level.SEVERE, null, ex);
           return null;
        }
    }
    
    public String getAuthors(String q){
        try {
                    
            return K5APIRetriever.getAsString("/search/terms?wt=xml&terms.fl=browse_autor&terms.lower.incl=true&terms.sort=index&terms.limit=50&terms.lower="+q.toUpperCase());
        } catch (Exception ex) {
           LOGGER.log(Level.SEVERE, null, ex);
           return null;
        }
        
    }
    
    public String getTitles(String q){
        try {
            String s = "q=browse_title:["+q+"*%20TO%20*]&wt=xml&facet.field=browse_title&f.browse_title.facet.sort=false&facet.mincount=1&facet.limit=50&facet=true&rows=0";
            return K5APIRetriever.getAsString("/search?" + s);
        } catch (Exception ex) {
           LOGGER.log(Level.SEVERE, null, ex);
           return null;
        }
        
    }
}
