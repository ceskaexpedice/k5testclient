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
import java.util.ArrayList;
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
    String pid;
    JSONObject json;
    JSONArray jContext;
    JSONObject jDisplay;
    JSONObject jStreams;

    public void configure(Map props) {
        req = (HttpServletRequest) props.get("request");
        pid = req.getParameter("pid");
        //jContext = new JSONArray(K5APIRetriever.getAsString("/item/" + pid + "/context"));
    }
    
//    private String getPid(){
//        if(pid==null)
//        pid = req.getParameter("pid");
//        return pid;
//    }

    public JSONArray getContext() {
        try {
            if (jContext == null) {
                //String pid = req.getParameter("pid");
                jContext = new JSONArray(K5APIRetriever.getAsString("/item/" + pid + "/context"));
            }
            return jContext;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public JSONObject getDisplay() {
        try {
            if (jDisplay == null) {
//                String pid = req.getParameter("pid");
                jDisplay = new JSONObject(K5APIRetriever.getAsString("/item/" + pid + "/display"));
            }
            return jDisplay;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public JSONObject getStreams() {
        try {
            if (jStreams == null) {
//                String pid = req.getParameter("pid");
                jStreams = new JSONObject(K5APIRetriever.getAsString("/item/" + pid + "/streams"));
            }
            return jStreams;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String getModelPath() {
        String ret = "";
        try {
            JSONArray ja = getContext().getJSONArray(0);
            for (int i = 0; i < ja.length(); i++) {
                if (i > 0) {
                    ret += "/";
                }
                ret += ja.getJSONObject(i).getString("model");
            }
            return ret;
        } catch (JSONException ex) {
            Logger.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String getPidPath() {
        String ret = "";
        try {
            JSONArray ja = getContext().getJSONArray(0);
            for (int i = 0; i < ja.length(); i++) {
                if (i > 0) {
                    ret += "/";
                }
                ret += ja.getJSONObject(i).getString("pid");
            }
            return ret;
        } catch (JSONException ex) {
            Logger.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public JSONObject getFields() {
        try {
            if (json == null) {
//                String pid = req.getParameter("pid");
                json = new JSONObject(K5APIRetriever.getAsString("/item/" + pid));
            }
            return json;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public JSONObject getSiblings() {
        try {
//            String pid = req.getParameter("pid");
            JSONObject js = new JSONObject(K5APIRetriever.getAsString("/item/" + pid + "/siblings"));

            return js;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public JSONObject getChildren() {
        try {
//            String pid = req.getParameter("pid");
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
    /*    
     {
     pdfMaxRange:20,
     previewStreamGenerated:false,
     deepZoomGenerated:false,
     deepZoomCofigurationEnabled:false,
        
     mimeType:'image/jpeg', 
     hasAlto:false,
     pid:"uuid:6e1b0dda-c1a7-11df-b7b5-001b63bd97ba",
     model:"page",
        
     displayableContent:true,
        
     imgfull:true,
    
     donator:'',
        
        
     pathsOfPids:[    ["uuid:6e1b0dda-c1a7-11df-b7b5-001b63bd97ba" ,"uuid:6b767ab8-c1a7-11df-b7b5-001b63bd97ba" ]],
     imageServerConfigured:'false', 

     rights: 
     { 
     "administrate": { 
     "uuid:6e1b0dda-c1a7-11df-b7b5-001b63bd97ba":false  ,"uuid:6b767ab8-c1a7-11df-b7b5-001b63bd97ba":false  ,"uuid:1":false   
     }  ,
     "read": { 
     "uuid:6e1b0dda-c1a7-11df-b7b5-001b63bd97ba":true  ,"uuid:6b767ab8-c1a7-11df-b7b5-001b63bd97ba":true  ,"uuid:1":true   
     }   
     },                
     isContentPDF:function() {return viewerOptions.mimeType=='application/pdf'},
     isContentDJVU:function() {return viewerOptions.mimeType.indexOf('djvu')> 0 }
     }

     */

    public String getViewerOptions() {
        try {
            getStreams();
            JSONObject jViewer = new JSONObject();
            jViewer.put("pdfMaxRange", 20);
            jViewer.put("previewStreamGenerated", false);
            jViewer.put("deepZoomGenerated", false);
            jViewer.put("deepZoomCofigurationEnabled", false);
            jViewer.put("mimeType", jStreams.getJSONObject("IMG_FULL").getString("mimeType"));
            jViewer.put("hasAlto", jStreams.has("ALTO"));
            jViewer.put("pid", pid);
            jViewer.put("model", false);
            jViewer.put("displayableContent", false);
            jViewer.put("imgfull", jStreams.has("IMG_FULL"));
            jViewer.put("donator", false);
            jViewer.put("pathsOfPids", false);
            jViewer.put("imageServerConfigured", false);
            JSONObject jRights=new JSONObject();
            jRights.put("administrate", new JSONObject());
            jRights.put("read", new JSONObject());
            jViewer.put("rights", jRights);
            jViewer.put("isContentPDF", false);
            jViewer.put("isContentDJVU", false);
            return jViewer.toString();

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return "{}";
        }
    }

}
