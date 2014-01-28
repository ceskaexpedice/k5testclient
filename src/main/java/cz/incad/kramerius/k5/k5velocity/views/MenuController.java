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
package cz.incad.kramerius.k5.k5velocity.views;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.brickred.socialauth.Profile;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import cz.incad.kramerius.k5.k5velocity.socialauth.BasicAuthenticationFilter;
import cz.incad.kramerius.k5.k5velocity.views.itms.MenuItem;


public class MenuController {

	//{"read":true,"import":true,"convert":true,"replicationrights":true,"enumerator":true,"reindex":true,"replikator_periodicals":true,"replikator_monographs":true,"replikator_k3":true,"delete":true,"export":true,"setprivate":true,"setpublic":true,"administrate":true,"editor":true,"manage_lr_process":true,"export_k4_replications":true,"import_k4_replications":true,"export_cdk_replications":true,"edit_info_text":true,"rightsadmin":true,"rightssubadmin":false,"virtualcollection_manage":true,"criteria_rights_manage":true,"ndk_mets_import":true,"display_admin_menu":true,"aggregate":true,"show_statictics":true,"sort":true,"show_print_menu":true}

	public static Logger LOGGER = Logger.getLogger(MenuController.class.getName());
	
	// create user
    public static String rights() throws JSONException {
    	Client c = Client.create();
        WebResource r = c.resource("http://localhost:8080/search/api/v5.0/rights");
        r.addFilter(new BasicAuthenticationFilter("krameriusAdmin", "krameriusAdmin"));
        String t = r.accept(MediaType.APPLICATION_JSON).get(String.class);
        return t;
    }

    public static String showAdminMenu() throws JSONException {
    	Client c = Client.create();
        WebResource r = c.resource("http://localhost:8080/search/api/v5.0/rights?actions=display_admin_menu");
        r.addFilter(new BasicAuthenticationFilter("krameriusAdmin", "krameriusAdmin"));
        String t = r.accept(MediaType.APPLICATION_JSON).get(String.class);
        return t;
    }

    
    public MenuItem history() {
    	StringBuilder builder=new StringBuilder();
    	builder.append("javascript:showSearchHistory.showHistory();javascript:adminMenu.hide();");
    	MenuItem itm = new MenuItem(builder.toString(), "Historie");
    	return itm;
    }
    
    public MenuItem changePswd() {
    	StringBuilder builder=new StringBuilder();
    	builder.append("javascript:(new ChangePswd()).changePassword(); javascript:adminMenu.hide();");
    	MenuItem itm = new MenuItem(builder.toString(), "Zmena hesla");
    	return itm;
    }
    
    
    public MenuItem saveProfile() {
    	StringBuilder builder=new StringBuilder();
    	builder.append("javascript:saveProfile.saveProfile(); javascript:adminMenu.hide();");
    	MenuItem itm = new MenuItem(builder.toString(), "Ulozeni profilu");
    	return itm;
    }

    
    
    public MenuItem adminProcesses() {
		StringBuilder builder=new StringBuilder();
		builder.append("javascript:processes.processes(); javascript:hideAdminMenu();");
		MenuItem itm = new MenuItem(builder.toString(), "Sprava admin. procesu");
		return itm;
    }

    public MenuItem indexDocuments() {
    	//javascript:showIndexerAdmin(); javascript:hideAdminMenu();
    	StringBuilder builder=new StringBuilder();
		builder.append("javascript:showIndexerAdmin(); javascript:hideAdminMenu();");
		MenuItem itm = new MenuItem(builder.toString(), "Indexace dokumentu");
		return itm;
    }

    public MenuItem globalActions() {
    	StringBuilder builder=new StringBuilder();
		builder.append("javascript:globalActions.globalActions(); javascript:hideAdminMenu();");
		MenuItem itm = new MenuItem(builder.toString(), "Nastaveni prav globalnich akci");
		return itm;
    }

    public MenuItem criteriaEditor() {
    	StringBuilder builder=new StringBuilder();
		builder.append("javascript:criteriumsSearcher.showCriteriums(); javascript:hideAdminMenu();");
		MenuItem itm = new MenuItem(builder.toString(), "Editor dodatecnych podminek");
		return itm;
    }

    public MenuItem virtualCollectionsEditor() {
    	//javascript:showVirtualCollectionsAdmin(); javascript:hideAdminMenu();
    	StringBuilder builder=new StringBuilder();
		builder.append("javascript:showVirtualCollectionsAdmin(); javascript:hideAdminMenu();");
		MenuItem itm = new MenuItem(builder.toString(), "Sprava virutalnich sbirek");
		return itm;
    }


    public MenuItem statistics() {
    	//javascript:showVirtualCollectionsAdmin(); javascript:hideAdminMenu();
    	StringBuilder builder=new StringBuilder();
		builder.append("javascript:statistics.showDialog(); javascript:hideAdminMenu();");
		MenuItem itm = new MenuItem(builder.toString(), "Statistiky");
		return itm;
    }

	public List<MenuItem> getPublicItems() {
		List<MenuItem> l = new ArrayList<MenuItem>();
		l.add(history());
		l.add(saveProfile());
		l.add(changePswd());
		return l;
	}
    
	
	public boolean getAdminMenuVisible() {
		try {
			String json = showAdminMenu();
			JSONObject jsonObj = new JSONObject(json);
			boolean flag = jsonObj.getBoolean("display_admin_menu");
			return flag;
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE,e.getMessage(),e);
		}
		return false;
	}

	private MenuItem deleteProcesses() {
    	StringBuilder builder=new StringBuilder();
    	builder.append("javascript:parametrizedProcess.open('delete_processes'); javascript:hideAdminMenu();");
    	MenuItem itm = new MenuItem(builder.toString(), "Smazat stare procesy");
    	return itm;
	}

	
	public List<MenuItem> getAdminItems() {
		List<MenuItem> l = new ArrayList<MenuItem>();
		try {
			String jsonR = rights();
			JSONObject jsonObj = new JSONObject(jsonR);
			if (jsonObj.getBoolean("manage_lr_process")){
				l.add(adminProcesses());
			}
			if (jsonObj.getBoolean("reindex")){
				l.add(indexDocuments());
			}			
			if (jsonObj.getBoolean("reindex")){
				l.add(globalActions());
			}
			if (jsonObj.getBoolean("criteria_rights_manage")){
				l.add(criteriaEditor());
			}
			if (jsonObj.getBoolean("show_statictics")){
				l.add(statistics());
			}			
			if (jsonObj.getBoolean("manage_lr_process")){
				l.add(deleteProcesses());
			}			
			
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(),e);
		}
		return l;
	}


}
