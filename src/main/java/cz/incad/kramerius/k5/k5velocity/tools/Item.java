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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    }
    
    public JSONArray getContext() {
        try {
//            if (jContext == null) {
//                jContext = new JSONArray(K5APIRetriever.getAsString("/item/" + pid + "/context"));
//            }
//            return jContext;
            JSONArray ja = getFields().getJSONArray("context");
            for(int i = 0; i<ja.length(); i++){
                JSONArray jc = ja.getJSONArray(i);
                for(int j = 0; j<jc.length(); j++){
                    JSONObject jcpid = jc.getJSONObject(j);
                    String cpid = jcpid.getString("pid");
                    JSONObject jpid = new JSONObject(K5APIRetriever.getAsString("/item/" + cpid));
                    jcpid.put("title", jpid.get("title"));
                    jcpid.put("root_title", jpid.get("root_title"));
                    jcpid.put("details", jpid.get("details"));
                }
            }
             return getFields().getJSONArray("context");
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
                jStreams = new JSONObject(K5APIRetriever.getAsString("/item/" + pid + "/streams"));
            }
            return jStreams;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String getMods() {
        try {
            return getStream("BIBLIO_MODS");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String getAlto() {
        try {
            return getStream("ALTO");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String getStream(String ds) {
        try {
            return K5APIRetriever.getAsString("/item/" + pid + "/streams/" + ds);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    

    public String getModelPath() {
        String ret = "";
        try {
            JSONArray ja = getFields().getJSONArray("context").getJSONArray(0);
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
            JSONObject js = new JSONObject(K5APIRetriever.getAsString("/item/" + pid + "/siblings"));

            return js;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public JSONArray getChildren() {
        try {
            JSONArray js = new JSONArray(K5APIRetriever.getAsString("/item/" + pid + "/children"));

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
    
    public String getCustomContent(){
        StringBuilder stringBuilder = new StringBuilder();
        String tab = req.getParameter("tab");
        String ds = tab;
        String xsl = tab;
        if (tab.indexOf('.') >= 0) {
            ds = tab.split("\\.")[0];
            xsl = tab.split("\\.")[1] + ".xsl";
        }
        String pid_path = req.getParameter("pid_path");
        List<String> pids =  Arrays.asList(pid_path.split("/"));
        if(ds.startsWith("-")){ 
            Collections.reverse(pids);
            ds = ds.substring(1);
        }
        for (String pid : pids) {
            //TODO: proces vsechny. Zatim jen posledni
        }
        
        pid = pids.get(pids.size()-1);
        
            
            if (getStreams().has(ds)) {
            try {                
                String mime = getStreams().getJSONObject(ds).getString("mimeType");
                if (mime.equals("text/plain")) {
                    
                    stringBuilder.append("<textarea style=\"width:98%; height:98%; border:0; \">" + getStream(ds)+ "</textarea>");
                    
                } else if (mime.equals("text/xml") || mime.equals("application/rdf+xml")) {
                            stringBuilder.append(getStream(ds));
                            // TODO: apply transform
//                    try {
//                        org.w3c.dom.Document xml = XMLUtils.parseDocument(fedoraAccess.getDataStream(pid, ds), true);
//                        if (xslService.isAvailable(xsl)) {
//                            String text = xslService.transform(xml, xsl, this.localesProvider.get());
//                            stringBuilder.append(text);
//                        }
//                    } catch (cz.incad.kramerius.security.SecurityException e) {
//                        securityError(stringBuilder, pid,ds);
//                    } catch (Exception e) {
//                        LOGGER.log(Level.SEVERE,e.getMessage(), e);
//                    }
                }
            } catch (JSONException ex) {
                Logger.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        
        return stringBuilder.toString();
    }

    public String getViewerOptions() {
        try {
            getStreams();
            JSONObject jViewer = new JSONObject();
            jViewer.put("pdfMaxRange", 20);
            jViewer.put("previewStreamGenerated", false);
            jViewer.put("deepZoomGenerated", false);
            jViewer.put("deepZoomCofigurationEnabled", false);
            jViewer.put("imgfull", jStreams.has("IMG_FULL"));
            if(jStreams.has("IMG_FULL")){
                jViewer.put("mimeType", jStreams.getJSONObject("IMG_FULL").getString("mimeType"));
            }
            jViewer.put("hasAlto", jStreams.has("ALTO"));
            jViewer.put("pid", pid);
            jViewer.put("model", getFields().getString("model"));
            jViewer.put("displayableContent", true);
            jViewer.put("donator", "");
            JSONArray pop = new JSONArray();
            JSONArray cxt =  getFields().getJSONArray("context");
            for(int i = 0; i<cxt.length(); i++){
                JSONArray na = new JSONArray();
                JSONArray p = cxt.getJSONArray(i);
                for(int j = 0; j<p.length(); j++){
                    na.put(p.getJSONObject(j).getString("pid"));
                }
                pop.put(na);
            }
            jViewer.put("pathsOfPids", pop);
            jViewer.put("imageServerConfigured", false);
            JSONObject jRights = new JSONObject();
            JSONObject jrAdmin = new JSONObject();
            jrAdmin.put(pid, false);
            //jrAdmin.put(pid, false);
            jRights.put("administrate", jrAdmin);
            JSONObject jrRead = new JSONObject();
            jrRead.put(pid, true);
            jRights.put("read", jrRead);
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
