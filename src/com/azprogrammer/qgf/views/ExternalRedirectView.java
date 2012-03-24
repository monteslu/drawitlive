package com.azprogrammer.qgf.views;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.RedirectView;

/** variant of RedirectView, will not add a session id to the url
 */
public class ExternalRedirectView extends RedirectView {
    public ExternalRedirectView(String url, boolean contextRelative) {
        super(url, contextRelative);
    }
    
    public ExternalRedirectView(String url) {
        super(url);
    }

    /** copied from @link{RedirectView#sendRedirect} and removed calls to
     * reponse.encodeRedirectURL()
     */
    @Override
    protected void sendRedirect( HttpServletRequest request,
            HttpServletResponse response, String targetUrl,
            boolean http10Compatible ) throws IOException {
        if (http10Compatible) {
            // Always send status code 302.
            response.sendRedirect(targetUrl);
        }
        else {
            // Correct HTTP status code is 303, in particular for POST requests.
            response.setStatus(303);
            response.setHeader("Location", targetUrl);
        }
    }
}