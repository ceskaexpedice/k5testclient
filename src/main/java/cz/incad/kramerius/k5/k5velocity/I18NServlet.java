package cz.incad.kramerius.k5.k5velocity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.antlr.stringtemplate.StringTemplate;

import com.google.inject.Provider;
import cz.incad.utils.ResourceBundleService;
import javax.servlet.http.HttpServlet;



/**
 * This servlet produces bundles as properties or as xml via http protocol. <br>
 * This is useful for xslt transformations
 *
 * @author pavels
 */
public class I18NServlet extends HttpServlet {

    public static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(I18NServlet.class.getName());

    

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = Actions.bundle.name();
        }
        Actions selectedAction = Actions.valueOf(action);
        selectedAction.doAction(getServletContext(), req, resp);
    }

//    public static String i18nServlet(HttpServletRequest request) {
//        return ApplicationURL.urlOfPath(request, InternalConfiguration.get().getProperties().getProperty("servlets.mapping.i18n"));
//    }

    static enum Formats {

        xml,
        json;

        public static Formats find(String val) {
            Formats[] vals = values();
            for (Formats f : vals) {
                if (f.name().equals(val)) {
                    return f;
                }
            }
            return xml;
        }
    }

    static enum Actions {

        
        bundle {

                    @Override
                    public void doAction(ServletContext context, HttpServletRequest req, HttpServletResponse resp) {
                        try {
                            String parameter = req.getParameter("name");
                            String format = req.getParameter("format");
                            String language = req.getParameter("language");

                            ResourceBundle resourceBundle = ResourceBundleService.getBundle(parameter, language);
                            Formats foundFormat = Formats.find(format);
                            String renderedBundle = null;
                            if (foundFormat == Formats.xml) {
                                renderedBundle = formatBundleToXML(resourceBundle, parameter).toString();
                                resp.setContentType("application/xhtml+xml");
                                resp.setCharacterEncoding("UTF-8");
                                
                            } else {
                                resp.setContentType("application/json");
                                resp.setCharacterEncoding("UTF-8");
                                renderedBundle = formatBundleToJSON(resourceBundle, parameter);
                            }
                            //resp.setCharacterEncoding("UTF-8");
                            resp.getWriter().write(renderedBundle);
                        } catch (IOException e) {
                            LOGGER.log(Level.SEVERE, e.getMessage(), e);
                            try {
                                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            } catch (IOException e1) {
                            }
                        }

                    }

                };

        abstract void doAction(ServletContext context, HttpServletRequest req, HttpServletResponse resp);

        static Locale locale(HttpServletRequest req, Provider<Locale> provider) {
            String lang = req.getParameter("lang");
            String country = req.getParameter("country");
            if ((lang != null) && (country != null)) {
                Locale locale = new Locale(lang, country);
                return locale;
            } else {
                return provider.get();
            }
        }

        static String formatBundleToJSON(ResourceBundle bundle, String bundleName) {
            Map<String, String> map = new HashMap<String, String>();
            Set<String> keySet = bundle.keySet();
            for (String key : keySet) {
                String changedValue = bundle.getString(key);
                if (changedValue.contains("\"")) {
                    changedValue = changedValue.replace("\"", "\\\"");
                }
                changedValue = changedValue.replace("\n", "\\n");
                map.put(key, changedValue);
            }

            StringTemplate template = new StringTemplate(
                    "{\"bundle\":{\n"
                    + "   $bundle.keys:{k| \"$k$\":\"$bundle.(k)$\" };separator=\",\\n\"$"
                    + "\n}}");
            template.setAttribute("bundle", map);
            return template.toString();
        }

        static StringBuffer formatBundleToXML(ResourceBundle bundle, String bundleName) {
            StringBuffer buffer = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            buffer.append("<bundle name='").append(bundleName).append("'>\n");
            Set<String> keySet = bundle.keySet();
            for (String key : keySet) {
                buffer.append("<value key='" + key + "'>").append(bundle.getString(key)).append("</value>");
            }
            buffer.append("\n</bundle>");
            return buffer;
        }

        static StringBuffer formatTextToXML(String text, String textName) {
            StringBuffer buffer = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            buffer.append("<text name='").append(textName).append("'>\n");
            buffer.append(text);
            buffer.append("\n</text>");
            return buffer;
        }

        static String formatTextToJSON(String text, String textName) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", textName);
            map.put("value", text.trim().replace("\n", "\\n"));
            StringTemplate template = new StringTemplate(
                    "{\"text\":{\n"
                    + "  \"name\":\"$data.name$\","
                    + "  \"value\":\"$data.value$\""
                    + "\n}}");
            template.setAttribute("data", map);
            return template.toString();

        }
    }
}
