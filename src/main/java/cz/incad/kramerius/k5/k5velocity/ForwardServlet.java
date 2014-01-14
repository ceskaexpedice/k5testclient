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
import java.io.InputStream;
import java.net.URLConnection;
import java.security.Principal;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.ceskaexpedice.k5.k5jaas.K5User;
import cz.incad.kramerius.k5.k5velocity.kapi.CallUserController;
import cz.incad.kramerius.k5.k5velocity.kapi.User;

public class ForwardServlet extends HttpServlet {

	
	public static final Logger LOGGER = Logger.getLogger(ForwardServlet.class.getName());
	
	public static enum TypeOfCall {
		ADMIN {
			@Override
			public User getUser(CallUserController cus) {
				return cus.getAdminCaller();
			}
		}, 
		USER {
			@Override
			public User getUser(CallUserController cus) {
                            if(cus != null){
				return cus.getClientCaller();
                            }else{
                                return null;
                            }
			}
		};

		public abstract User getUser(CallUserController cus);
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		TypeOfCall tc = disectTypeOfCall(req);
		User user = tc.getUser((CallUserController) req.getSession().getAttribute(CallUserController.KEY));

		String k5addr = getInitParameter("k5prefix");
		
		String queryString = req.getQueryString();
		String requestedURI = req.getRequestURI();
		int index = requestedURI.indexOf("api")+"api".length();
		String rest = requestedURI.substring(index);
		
		String replaceURL =k5addr+rest;
		if (StringUtils.isAnyString(queryString)) {
			replaceURL = replaceURL+"?"+queryString;
		}
		LOGGER.info("requesting url "+replaceURL);
		InputStream inputStream = null;
		if (user != null) {
			inputStream = RESTHelper.inputStream(replaceURL, user.getUserName(), user.getPassword());
		} else {
			inputStream = RESTHelper.inputStream(replaceURL);
		}
		resp.setContentType("application/json; charset=utf-8");
		IOUtils.copyStreams(inputStream, resp.getOutputStream());
	}


	private TypeOfCall disectTypeOfCall(HttpServletRequest req) {
		String requestedURI = req.getRequestURI();
		return requestedURI.contains("admin") ? TypeOfCall.ADMIN : TypeOfCall.USER;
	}
}
