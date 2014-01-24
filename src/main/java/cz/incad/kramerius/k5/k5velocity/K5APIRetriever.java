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
package cz.incad.kramerius.k5.k5velocity;

import static cz.incad.kramerius.k5.k5velocity.ForwardServlet.LOGGER;
import cz.incad.kramerius.k5.k5velocity.kapi.CallUserController;
import cz.incad.kramerius.k5.k5velocity.kapi.User;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.tools.generic.ResourceTool;

/**
 *
 * @author alberto
 */
public class K5APIRetriever {

    public static final Logger LOGGER = Logger.getLogger(K5APIRetriever.class.getName());

//    public static String getJSON(String queryString) throws IOException {
//
//        org.apache.velocity.tools.generic.ResourceTool rt = new ResourceTool();
//            Map<String, String> c = new HashMap<String, String>();
//            c.put("bundles", "res.configuration");
//            rt.configure(c);
//        String k5addr = rt.get("k5addr").toString() + queryString;
//        
//        LOGGER.info("requesting url " + k5addr);
//        InputStream inputStream = RESTHelper.inputStream(k5addr);
//        StringWriter sw = new StringWriter();
//        org.apache.commons.io.IOUtils.copy(inputStream, sw, "UTF-8");
//        return sw.toString();
//    }
    public static String get(String url) throws IOException {

        LOGGER.info("requesting url " + url);
        InputStream inputStream = RESTHelper.inputStream(url);
        StringWriter sw = new StringWriter();
        org.apache.commons.io.IOUtils.copy(inputStream, sw, "UTF-8");
        return sw.toString();
    }

    public static String getAsString(String queryString) throws IOException {

        org.apache.velocity.tools.generic.ResourceTool rt = new ResourceTool();
        Map<String, String> c = new HashMap<String, String>();
        c.put("bundles", "res.configuration");
        rt.configure(c);
        String k5addr = rt.get("k5addr").toString() + queryString;

        LOGGER.info("requesting url " + k5addr);
        InputStream inputStream = RESTHelper.inputStream(k5addr);
        StringWriter sw = new StringWriter();
        org.apache.commons.io.IOUtils.copy(inputStream, sw, "UTF-8");
        return sw.toString();
    }

    public static String getAsString(String queryString, HttpServletRequest req) throws IOException {

        org.apache.velocity.tools.generic.ResourceTool rt = new ResourceTool();
        Map<String, String> c = new HashMap<String, String>();
        c.put("bundles", "res.configuration");
        rt.configure(c);
        String k5addr = rt.get("k5addr").toString() + queryString;

        ForwardServlet.TypeOfCall tc = disectTypeOfCall(queryString);
        User user = tc.getUser((CallUserController) req.getSession().getAttribute(CallUserController.KEY));

        LOGGER.info("requesting url " + k5addr);

        StringWriter sw = new StringWriter();
        InputStream inputStream = null;
        if (user != null) {
            inputStream = RESTHelper.inputStream(k5addr, user.getUserName(), user.getPassword());
        } else {
            inputStream = RESTHelper.inputStream(k5addr);
        }
        org.apache.commons.io.IOUtils.copy(inputStream, sw, "UTF-8");
        return sw.toString();
    }

    private static ForwardServlet.TypeOfCall disectTypeOfCall(String queryString) {
        return queryString.contains("admin") ? ForwardServlet.TypeOfCall.ADMIN : ForwardServlet.TypeOfCall.USER;
    }
}
