/*
 * Copyright (C) 2013 Pavel Stastny
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
package cz.incad.kramerius.k5.k5velocity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;

import cz.incad.kramerius.k5.k5velocity.ForwardServlet.TypeOfCall;
import cz.incad.kramerius.k5.k5velocity.kapi.CallUserController;
import cz.incad.kramerius.k5.k5velocity.kapi.User;
import cz.incad.kramerius.k5.k5velocity.socialauth.BasicAuthenticationFilter;

public class LRForwardServlet extends HttpServlet {

	public static Logger LOGGER = Logger.getLogger(LRForwardServlet.class.getName());
	
    public static String get(String url, String userName, String pswd) throws JSONException {
    	Client c = Client.create();

    	c.getProperties().put(
    	        ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);

        WebResource r = c.resource(url);
        if (userName != null && pswd != null) {
            r.addFilter(new BasicAuthenticationFilter("krameriusAdmin", "krameriusAdmin"));
        }
    	Builder builder = r.accept(MediaType.APPLICATION_JSON);
		return builder.get(String.class);
    }


	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			HttpSession session = req.getSession();
			User user = null;
			if (session != null) {
				CallUserController cus =  (CallUserController) req.getSession().getAttribute(CallUserController.KEY);
				user = cus.getClientCaller();
			}
			
			String k5addr = getInitParameter("k5prefix");
			
			String queryString = req.getQueryString();
			String uri = k5addr + "?"+URLEncoder.encode(queryString, "UTF-8");
			
			String content = get(uri, user!= null ? user.getUserName():null, user!= null ? user.getPassword():null);
			resp.getWriter().write(content);
			
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		 //http://localhost:8080/search/lr?action=form_get&def=delete_processes&paramsMapping={1;4}
		String encoded = URLEncoder.encode("http://localhost:8080/search/lr?action=form_get&def=delete_processes&paramsMapping={1;4}", "UTF-8");
		
		URI created = URI.create(encoded);
		System.out.println(created);
		
	}

	
}
