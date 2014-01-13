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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.tools.config.DefaultKey;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author alberto
 */
@DefaultKey("search")
public class Search {

    public static final Logger LOGGER = Logger.getLogger(Search.class.getName());

    HttpServletRequest req;

    public void configure(Map props) {
        req = (HttpServletRequest) props.get("request");
    }

    private JSONObject getJSON(String queryString) {
        try {
            String jStr = K5APIRetriever.getJSON("/search?" + queryString);
            return new JSONObject(jStr);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        } catch (JSONException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private final String homeFacets = "&facet.field=fedora.model&facet.field=keywords&facet.field=collection";
    private final String daFacetsRange = "&facet.range=rok&facet.range.start=1100&facet.range.end=2014&facet.range.gap=1";
    private final String daFacets = "&facet.mincount=1&facet.field=rok&f.rok.facet.limit=-1&f.rok.facet.sort=false";
    private final String groupedParams = "&group.field=root_pid&group.type=normal&group.threshold=1"
            + "&group.facet=false&group=true&group.ngroups=true";

    private boolean isFilterByType() {
        String[] fqs = req.getParameterValues("fq");
        if (fqs == null) {
            return false;
        }
        for (String fq : fqs) {
            if (fq.equals("document_type") || fq.equals("fedora.model")) {
                return true;
            }
        }
        return false;
    }

    private boolean isFieldedSearch() {
        String[] fqs = req.getParameterValues("fq");
//         for(String fq : fqs){
//             if(fq.equals("document_type") || fq.equals("fedora.model"))
//                 return true;
//         }
        return false;
    }

    private String advFilter(String param) {
        return advFilter(param, param);
    }

    private String advFilter(String param, String field) {
        String p = req.getParameter(param);
        if (p != null && !p.equals("")) {
            return "&fq=" + field + ":" + p;
        }
        return "";
    }

    private String getAdvSearch() {
        StringBuilder res = new StringBuilder();
        res.append(advFilter("title", "dc.title"));
        res.append(advFilter("author", "dc.creator"));
        res.append(advFilter("udc", "mdt"));
        res.append(advFilter("ddc", "ddt"));
        res.append(advFilter("rok"));
        res.append(advFilter("keywords"));
        res.append(advFilter("onlyPublic", "dostupnost"));
        String p = req.getParameter("issn");
        if (p != null && !p.equals("")) {
            res.append("&fq=issn:" + p + "%20OR%20dc.identifier:" + p);
        }
        return res.toString();

    }
    
    private String getCollectionFilter(){
        String col   = req.getParameter("collection");
        if (col != null && !col.equals("")) {
            return "&fq=collection:\""+ col + "\"";
        }
        return "";
    }
    
     private String getStart() throws UnsupportedEncodingException {
        String res = "";
        String offset = req.getParameter("offset");
        if (offset == null || offset.equals("")) {
            offset = "0";
        }
        String rows = req.getParameter("rows");
        if (rows == null || rows.equals("")) {
            rows = "20";
        }
        return "&start=" + offset + "&rows=" + rows;
     }

    private String getSort() throws UnsupportedEncodingException {
        String res = "";
        String sort = req.getParameter("sort");
        String asis = req.getParameter("asis");
        String q = req.getParameter("q");
        boolean filterByType = isFilterByType();
        boolean fieldedSearch = isFieldedSearch();
        if (sort != null && !sort.equals("") && asis != null) {
            res = sort;
        } else if (sort != null && !sort.equals("") && filterByType) {
            res = sort;
        } else if (sort != null && !sort.equals("")) {
            res = "level asc, " + sort;
        } else if ((q == null || q.equals("")) && filterByType) {
            res = "title_sort asc";
        } else if (q != null && !q.equals("") && filterByType) {
            res = "score desc, title_sort asc";
        } else if (fieldedSearch) {
            res = "level asc, title_sort asc, score desc";
        } else if (q == null || q.equals("")) {
            res = "level asc, title_sort asc, score desc";
        } else {
            res = "level asc, score desc";
        }

        return "&sort=" + URLEncoder.encode(res, "UTF-8");
    }

    private String getFilters() {
        String res = getAdvSearch();
        String[] fqs = req.getParameterValues("fq");
        if (fqs != null) {
            for (String fq : fqs) {
                res += "&fq=" + fq;
            }
        }
        String browse_title = req.getParameter("browse_title");
        if (browse_title != null && !browse_title.equals("")) {
            res = "fq=search_title:"+browse_title;
        }
        return res;
        
        /*
        
        <c:set var="fieldedSearch" value="false" scope="request" />
    <%-- advanced params --%>
    <c:if test="${!empty param.issn}">
        <c:param name="fq" value="issn:${searchParams.escapedIssn} OR dc.identifier:${searchParams.escapedIssn}" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
    <c:if test="${!empty param.title}">
        <c:param name="fq" value="dc.title:${searchParams.escapedTitle}" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
    <c:if test="${!empty param.author}">
        <c:param name="fq" value="dc.creator:${searchParams.escapedAuthor}" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
    <c:if test="${!empty param.rok}">
        <c:param name="fq" value="rok:${searchParams.escapedRok}" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
    <c:if test="${!empty param.udc}">
        <c:param name="fq" value="mdt:\"${param.udc}\"" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
    <c:if test="${!empty param.ddc}">
        <c:param name="fq" value="ddt:\"${param.ddc}\"" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
    <c:if test="${!empty param.keywords}">
        <c:param name="fq" value="keywords:\"${param.keywords}\"" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
    <c:if test="${!empty param.onlyPublic}">
        <c:param name="fq" value="dostupnost:${param.onlyPublic}" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
        
        */
    }
    
    public String getQuery(){
        String ret = "";
        boolean hasExtField = false;
        String browse_title = req.getParameter("browse_title");
        if (browse_title != null && !browse_title.equals("")) {
            ret = "fq=search_title:"+browse_title;
            hasExtField = true;
        }
        return ret;
    }

    public String getGrouped() {
        try {
            
            String q = req.getParameter("q");
            if (q == null || q.equals("")) {
                q += "*:*";
            }else{
                q = URLEncoder.encode(q, "UTF-8");
            }
            return K5APIRetriever.getAsString("/search?q=" + q + "&wt=xml&facet=true"
                    + getStart()
                    + getCollectionFilter()
                    + homeFacets
                    + daFacets
                    + getSort()
                    + getFilters()
                    + groupedParams);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String getPolicy() {
        try {
            
            String q = "root_pid:\""+req.getParameter("root")+"\"";
            
            return K5APIRetriever.getAsString("/search?q=" + q + 
                    "&wt=xml&facet=true&facet.field=dostupnost&facet.mincount=1&rows=0");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String getUngrouped() {
        try {
            String q = req.getParameter("q");
            if (q == null || q.equals("")) {
                q = "*:*";
            }else{
                q = URLEncoder.encode(q, "UTF-8");
            }
            return K5APIRetriever.getAsString("/search?q=" + q + "&wt=xml&facet=true"
                    + getStart()
                    + getCollectionFilter()
                    + homeFacets
                    + daFacets
                    + getSort()
                    + getFilters());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public JSONObject getHome() {
        return getJSON("q=*:*&facet=true&rows=0" + homeFacets + getCollectionFilter());
    }

    public String getDa() {
        try {
            String q = req.getParameter("q");
            if (q == null || q.equals("")) {
                q = "*:*";
            }
            return K5APIRetriever.getJSON("/search?q=" + q + "&wt=xml&facet=true&rows=0" + daFacets + getCollectionFilter());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String getAuthors(String q) {
        try {

            return K5APIRetriever.getAsString("/search/terms?wt=xml&terms.fl=browse_autor&terms.lower.incl=true&terms.sort=index&terms.limit=50&terms.lower=" + q.toUpperCase());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public String getTitles(String q) {
        try {
            String s = "q=browse_title:[" + q + "*%20TO%20*]&wt=xml&facet.field=browse_title&f.browse_title.facet.sort=false&facet.mincount=1&facet.limit=50&facet=true&rows=0";
            return K5APIRetriever.getAsString("/search?" + s);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }

    }
}
