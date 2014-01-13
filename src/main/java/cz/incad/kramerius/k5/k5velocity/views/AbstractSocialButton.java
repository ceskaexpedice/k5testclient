/*
 * Copyright (C) 2010 Pavel Stastny
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
package cz.incad.kramerius.k5.k5velocity.views;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;


public abstract class AbstractSocialButton {

    public static final String DEFAULT_LOCALE_STRING="cs_CZ";

    static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(AbstractSocialButton.class.getName());
    
    
    HttpServletRequest req;
    String language;

    public void configure(Map props) {
        req = (HttpServletRequest) props.get("request");
        if(req.getSession().getAttribute("language") != null){
            language = (String) req.getSession().getAttribute("language");
        }else{
            language = req.getLocale().getLanguage();
        }
    }
    
    public String getLocale(){
        return this.language;
    }

    public boolean emptyString(String str) {
        return (str == null) || (str.trim().equals(""));
    }
    
    public boolean isHomePage() {
        String contextPath = req.getContextPath();
        String requestedURL = req.getRequestURL().toString();
        int indexOfContextPath = requestedURL.indexOf(contextPath);
        String queryString = req.getQueryString();
        if (emptyString(queryString)) {
            String stringAfterContext = requestedURL.substring(indexOfContextPath+contextPath.length());
            return stringAfterContext.equals("/") || stringAfterContext.equals("/index.vm");
        } else return false;
    }
    
    public boolean isItemPage() {
        try {
            
            String requestedURL = req.getRequestURL().toString();
            URL url = new URL(requestedURL);
            String furl = requestedURL.replace("?"+url.getQuery(), "");
            return furl.endsWith("doc.vm");
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
        }
        return false;
    }
    
    public boolean isSearchPage() {
        try {
            
            String requestedURL = req.getRequestURL().toString();
            URL url = new URL(requestedURL);
            String furl = requestedURL.replace("?"+url.getQuery(), "");
            return furl.endsWith("search.vm");
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
        }
        return false;
    }

    
    public String getShareURL() {
        try {
            if (isItemPage()) {
                String pidParameter = req.getParameter("pid");
                String encoded = URLEncoder.encode(pidParameter, "UTF-8");
                return req.getContextPath()+"/handle/"+encoded;
            } else {
                String requestedURL = req.getRequestURL().toString();
                String query = req.getQueryString();
                String returnedShareURL = requestedURL;
                if (!emptyString(query)) {
                    returnedShareURL = requestedURL+"?"+query;
                    if ((req.getParameter("language") != null) && (!req.getParameter("language").trim().equals(""))) {
                        returnedShareURL = requestedURL +"&language=" + language;
                    }
                }
                return returnedShareURL;
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE,e.getMessage(), e);
            return "#";
        }
    }

    public String getPidParam(HttpServletRequest request) {
        String pid = request.getParameter("pid");
        return pid;
    }

    public String getMetadataType() {
        if (isItemPage()) {
            return "book";
        } else {
            return "product";
        }
    }


    public String getMetadataImage() {
        String applUrl = req.getContextPath();
        String pid = req.getParameter("pid");
        if (isItemPage()) {
            return applUrl+"/img?uuid="+pid+"&stream=IMG_THUMB";
        } else {
            return applUrl+"/img/logo.png";
        }
    }
    
    public String getPID() {
        String pid = req.getParameter("pid");
        return pid;
    }


    
    public String getTitle() {
        if (this.isItemPage()) {
            return "getRootTitle()";
        } else {
            return "getApplicationTitle()";
        }
    }


}
