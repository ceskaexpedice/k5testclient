/*
 * Copyright (C) 2014 alberto
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
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.ConversionUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.generic.ResourceTool;
import org.apache.velocity.tools.view.context.ChainedContext;

/**
 *
 * @author alberto
 */
@DefaultKey("language")
public class LanguageTool extends ResourceTool {
    
    public static final Logger LOGGER = Logger.getLogger(LanguageTool.class.getName());

    HttpServletRequest req;
    String language;
    String bundleUrl;
    private final String LANGUAGE_PARAM = "language";

    @Override
    public void configure(Map props) {
        //this.configure(props);
        req = (HttpServletRequest) props.get("request");
        language = req.getParameter(LANGUAGE_PARAM);
        if(language!=null && language != ""){
            req.getSession().setAttribute(LANGUAGE_PARAM, language);
            setLocale(ConversionUtils.toLocale(language));
        }else{
            if(req.getSession().getAttribute("language") != null){
                language = (String) req.getSession().getAttribute("language");
                setLocale(ConversionUtils.toLocale(language));
            }else{
                language = req.getLocale().getLanguage();
                setLocale(ConversionUtils.toLocale(language));
            }
        }
        ChainedContext vc = (ChainedContext) props.get("velocityContext");
        ResourceTool rt = (ResourceTool) vc.getVelocityContext().get("text");
        Key k = rt.locale(language);
        vc.put("text", k);
        ResourceTool conf = (ResourceTool) vc.getVelocityContext().get("conf");
        String app = conf.get("applicationUrl").toString();
        bundleUrl =  app + "/i18n?action=bundle&format=xml&name=labels&language=" + language;
        //vc.put("language", language);
        
    }
    
    public String getBundleUrl(){
        return bundleUrl;
    }
    
    public String getLanguage(){
        return language;
    }
}
