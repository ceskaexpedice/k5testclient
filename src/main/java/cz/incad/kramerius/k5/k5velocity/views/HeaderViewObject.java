package cz.incad.kramerius.k5.k5velocity.views;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;

import com.google.inject.Inject;
import com.google.inject.Provider;

//import cz.incad.kramerius.FedoraAccess;
//import cz.incad.kramerius.security.User;
//import cz.incad.kramerius.security.UserManager;
//import cz.incad.kramerius.service.ResourceBundleService;
//import cz.incad.kramerius.users.LoggedUsersSingleton;
//import cz.incad.kramerius.utils.conf.KConfiguration;
//import cz.incad.kramerius.utils.json.JSONUtils;
import org.apache.velocity.tools.generic.ResourceTool;

/**
 * Objekt inicializujici js promenne v hlavicce 
 * @author pavels
 */
public class HeaderViewObject {

    public static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(HeaderViewObject.class.getName());
    
    @Inject
    Provider<HttpServletRequest> requestProvider;
    @Inject
    Provider<Locale> localeProvider;
//    @Inject
//    ResourceBundleService resourceBundleService;
//    @Inject
//    @Named("securedFedoraAccess")
//    FedoraAccess fedoraAccess;
//    @Inject
//    Provider<User> userProvider; 
//    @Inject
//    UserManager userManager;
//    @Inject
//    KConfiguration configuration;
//    @Inject
//    LoggedUsersSingleton loggedUsersSingleton;
    
    public String getDictionary() {
        Map<String, String> resourceBundleMap = new HashMap<String, String>();
        try {
            org.apache.velocity.tools.generic.ResourceTool rt = new ResourceTool();
            Map<String, String> c = new HashMap<String, String>();
            c.put("bundles", "labels");
//            c.put("locale", "cs");
            rt.configure(c);
            List<String> keySet = rt.getKeys();
            for (String key : keySet) {
                String changedValue = rt.get(key).toString();
                if (changedValue.contains("'")) {
                    changedValue = changedValue.replace("'", "\\'");
                }
                changedValue =  changedValue.replace("\n", "\\n");
                resourceBundleMap.put(key,changedValue);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        StringTemplateGroup grp = stGroup();
        StringTemplate inst = grp.getInstanceOf("dictionary");
        inst.setAttribute("resourceBundle", resourceBundleMap);
        return inst.toString();
    }

//    public String getConfig() {
//        StringTemplateGroup grp = stGroup();
//        StringTemplate st = grp.getInstanceOf("config");
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("generatePdfMaxRange", configuration.getConfiguration().getString("generatePdfMaxRange"));
//        st.setAttribute("data", map);
//        
//        return st.toString();
//    }
    
    private static StringTemplateGroup stGroup() {
        InputStream is = HeaderViewObject.class.getResourceAsStream("htmlHeaderJavascript.stg");
        StringTemplateGroup grp = new StringTemplateGroup(new InputStreamReader(is), DefaultTemplateLexer.class);
        return grp;
    }
    
//    public String getInjectedAdminScripts() {
//        User user = this.userProvider.get();
//        if (this.loggedUsersSingleton.isLoggedUser(this.requestProvider)) {
//            StringTemplateGroup grp = stGroup();
//            StringTemplate st = grp.getInstanceOf("injectedAdminScripts");
//            return st.toString();
//        } else {
//            return "";
//        }
//    }
    
    
//    public String getInjectSettings() {
//        return "k4Settings.pdf = {" +
//                    "generatePdfMaxRange:" +KConfiguration.getInstance().getProperty("generatePdfMaxRange")+
//        "}";
//    }
    
    
    
    public String getSearchingRSSChannels() throws MalformedURLException {
        if(true) return "";
        if (isSearchingURL()) {
            
            String urlString = this.requestProvider.get().getRequestURL().toString();
            String rss = urlString.substring(0,urlString.length()-"r.jsp".length())+"r-rss.jsp?"+this.requestProvider.get().getQueryString();

            return "<link rel=\"alternate\" title=\"K4 - vyhledavani\" href=\""+rss+"\" type=\"application/rss+xml\"/>";
        } else return "";
    }

    public boolean isSearchingURL() throws MalformedURLException {
        if(true) return false;
        URL url = new URL(this.requestProvider.get().getRequestURL().toString());
        String file = url.getFile();
        if (file.endsWith("r.jsp")) {
            return true;
        } else {
            return false;
        }
    }
    
    
    public String getLocalizationScripts() {
        if(true) return "";
        Locale locale = this.localeProvider.get();
        return "<script src=\"js/localization/jquery.ui.datepicker-"+locale.getLanguage()+".js\" language=\"javascript\" type=\"text/javascript\"></script>"+
            "<script src=\"js/localization/jquery-ui-timepicker-"+locale.getLanguage()+".js\" language=\"javascript\" type=\"text/javascript\"></script>";
    }

    
    public String getLevelsModelSelectionArray() {
        if(true) return "";
        //StringTemplate template = new StringTemp
        StringTemplateGroup grp = stGroup();
        StringTemplate st = grp.getInstanceOf("levelModelsSelection");
        HashMap<String, String> model = new HashMap<String, String>(); {
            HttpServletRequest request = this.requestProvider.get();
            String pidPath = request.getParameter("pid_path");
            String path = request.getParameter("path");
            if ((pidPath != null) && (path !=null)) {
                String[] uuids = pidPath.split("/");
                String[] models = path.split("/");
                for (int i = 0; i < models.length; i++) {
                    String key = (i+1)+"_"+models[i];
                    model.put(key, uuids[i]);
                }
            }
        }
        st.setAttribute("data", model);
        return st.toString();
    }
}
