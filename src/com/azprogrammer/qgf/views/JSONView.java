package com.azprogrammer.qgf.views;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.springframework.web.servlet.View;




public class JSONView implements View {

    public static String JSON_CONTENT_TYPE = "text/javascript";
    protected boolean m_useCaching = false;
    
    protected String m_jsonpCallback = null;
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.View#getContentType()
     */
    public String getContentType() {
	return JSON_CONTENT_TYPE;
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.View#render(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void render(Map model, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

		response.setContentType(JSON_CONTENT_TYPE);
		
		if(!isUseCaching()){
			//TODO set appropriate headers
		}
		
		PrintWriter writer = response.getWriter();
		
		
		writer.print(JSONObject.fromObject(model).toString());

    }

	public boolean isUseCaching() {
		return m_useCaching;
	}

	public void setUseCaching(boolean useCaching) {
		m_useCaching = useCaching;
	}
	
    public String getJsonpCallback ()
    {
        return m_jsonpCallback;
    }

    public void setJsonpCallback (String jsonpCallback)
    {
        m_jsonpCallback = jsonpCallback;
    }

}