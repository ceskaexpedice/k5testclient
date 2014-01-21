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
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cz.ceskaexpedice.k5.k5jaas.basic.K5User;
import cz.incad.kramerius.k5.k5velocity.kapi.AdminUser;
import cz.incad.kramerius.k5.k5velocity.kapi.CallUserController;
import cz.incad.kramerius.k5.k5velocity.kapi.ClientUser;
import cz.incad.kramerius.k5.k5velocity.kapi.ProfileDelegator;
import cz.incad.kramerius.k5.k5velocity.kapi.impl.CallUserControllerImpl;

public class AuthFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) arg0;
		HttpServletResponse httpResp = (HttpServletResponse) arg1;
		if (httpReq.getUserPrincipal() != null) {
			K5User k5user = (K5User) httpReq.getUserPrincipal();
			
			// authenticated by JAAS
			HttpSession session = httpReq.getSession(true);

			CallUserController controller = new CallUserControllerImpl();
			controller.createCaller(k5user.getRemoteName(), k5user.getRemotePass(), AdminUser.class);
			controller.createCaller(k5user.getRemoteName(), k5user.getRemotePass(), ClientUser.class);
			controller.createCaller(k5user.getRemoteName(), k5user.getRemotePass(), ProfileDelegator.class);
			session.setAttribute(CallUserController.KEY, controller);
		}
		arg2.doFilter(arg0, arg1);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	} 

	
}
