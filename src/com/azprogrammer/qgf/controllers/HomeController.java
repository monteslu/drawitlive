/*
 * $Id$
 *
 * Copyright ( c ) 2010 Carefx Corporation. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Carefx
 * Corporation ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with Carefx Corporation or a Carefx
 * authorized reseller (the "License Agreement"). Carefx may make changes to the
 * Confidential Information from time to time. Such Confidential Information may
 * contain errors.
 *
 * EXCEPT AS EXPLICITLY SET FORTH IN THE LICENSE AGREEMENT, CAREFX DISCLAIMS ALL
 * WARRANTIES, COVENANTS, REPRESENTATIONS, INDEMNITIES, AND GUARANTEES WITH
 * RESPECT TO SOFTWARE AND DOCUMENTATION, WHETHER EXPRESS OR IMPLIED, WRITTEN OR
 * ORAL, STATUTORY OR OTHERWISE INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, TITLE, NON-INFRINGEMENT AND FITNESS FOR A
 * PARTICULAR PURPOSE. CAREFX DOES NOT WARRANT THAT END USER'S USE OF THE
 * SOFTWARE WILL BE UNINTERRUPTED, ERROR FREE OR SECURE.
 *
 * CAREFX SHALL NOT BE LIABLE TO END USER, OR ANY OTHER PERSON, CORPORATION OR
 * ENTITY FOR INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY OR CONSEQUENTIAL
 * DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR USE, WHETHER IN AN
 * ACTION IN CONTRACT, TORT OR OTHERWISE, EVEN IF CAREFX HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. CAREFX' TOTAL LIABILITY TO END USER SHALL NOT
 * EXCEED THE AMOUNTS PAID FOR THE CAREFX SOFTWARE BY END USER DURING THE PRIOR
 * TWELVE (12) MONTHS FROM THE DATE IN WHICH THE CLAIM AROSE.  BECAUSE SOME
 * STATES OR JURISDICTIONS DO NOT ALLOW LIMITATION OR EXCLUSION OF CONSEQUENTIAL
 * OR INCIDENTAL DAMAGES, THE ABOVE LIMITATION MAY NOT APPLY TO END USER.
 *
 * Copyright version 2.0
 */
package com.azprogrammer.qgf.controllers;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.azprogrammer.qgf.model.PMF;
import com.azprogrammer.qgf.model.WBChannel;
import com.azprogrammer.qgf.model.WBEmail;
import com.azprogrammer.qgf.model.WBGeometry;
import com.azprogrammer.qgf.model.WBMessage;
import com.azprogrammer.qgf.model.WhiteBoard;
import com.azprogrammer.qgf.text.InputValidator;
import com.azprogrammer.qgf.text.TextFormatter;
import com.azprogrammer.qgf.util.WebUtil;
import com.azprogrammer.qgf.views.ExternalRedirectView;
import com.azprogrammer.qgf.views.JSONView;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


/**
 * @author lmontes
 *
 */

@Controller
public class HomeController
{

    static final char[] validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray ();
    protected InputValidator inputValidator;
    protected TextFormatter textFormatter;

    //how long ago does a user have to have pinged before not being in room anymore?
    protected long lastPingMillis = 1000 * 60 * 7;
    
    public HomeController(){
        
    }
    
    @RequestMapping(value="/*")
    public String doHome(ModelMap model, HttpServletRequest req){
        return "home";
    }
    
    @RequestMapping(value="/whiteboard/{wbId}")
    public ModelAndView doWhiteboardREST(ModelMap model, HttpServletRequest req,@PathVariable String wbId){
        return doWhiteboard (model, req,wbId);
    }
  
    
    
    @RequestMapping(value="/embed/{wbId}")
    public ModelAndView doEmbedRest(ModelMap model, HttpServletRequest req,@PathVariable String wbId){
        return doEmbed(model, req,wbId);
    }
    
    @RequestMapping(value="/boards")
    public String doBoards(ModelMap model, HttpServletRequest req,String wbId){
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser(); // req.getUserPrincipal()?!?
        if(user == null){
            String loginURL = userService.createLoginURL (req.getRequestURI());
            model.put ("loginURL", loginURL);
            return "login";
        }else{
            if(isAdmin ()){
                
                long start = 0;
                try{
                    start = Long.parseLong (req.getParameter ("start"));                    
                }catch(Exception numE){}
                
                model.put ("user", user);
                PersistenceManager pm = getPM();
                Query query = pm.newQuery(WhiteBoard.class);
                query.setOrdering("creationDate desc");
                query.setRange (start, start + 101);
                 //Collection result = (Collection)query.execute(date);
                        
                List<WhiteBoard> boards = (List<WhiteBoard>) query.execute();// pm.newQuery(query).execute();
                System.out.println ("boards: " + boards.size ());
                model.put ("boards", boards);
                return "boards";
            }else{
                String logoutURL = userService.createLogoutURL (req.getRequestURI());
                model.put ("logoutURL", logoutURL);
                return "logout";                
            }
        }
        
    }
    
    
    @RequestMapping(value="/embed")
    public ModelAndView doEmbed(ModelMap model, HttpServletRequest req,String wbId){
        
        ModelAndView mav = new ModelAndView ();
        mav.addAllObjects (model);
        
        String hangoutUrl = req.getParameter ("hangoutUrl");
        if(hangoutUrl != null){
            req.getSession ().setAttribute ("hangoutUrl", hangoutUrl);
        }else{
            try{
                hangoutUrl = req.getSession ().getAttribute ("hangoutUrl").toString ();
            }catch(Exception e){
                
            }
        }
        
        
        
        
        
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser(); // req.getUserPrincipal()?!?
        if(user != null){
            String loginURL = userService.createLoginURL (req.getRequestURI());
            model.put ("loginURL", loginURL);
        }
        else{
           
        }
        
        /*
        if(user == null){
            
            model.put ("loginURL", loginURL);
            mav.setViewName ("login");
            return mav;
        }else{
            model.put ("userName", user.getUserId ());
            
        }
        */
        
        
        PersistenceManager pm = getPM();
        HttpSession session = req.getSession ();
        WhiteBoard wb = null;
        List<WBMessage> messages = null;
        
        if((wbId == null) || ("".equals (wbId))){
            wbId = req.getParameter ("wb");
        }
        
        if((wbId != null) && (!"".equals (wbId.trim()))){
            wbId = cleanupWbId (wbId);
            try{
                req.getSession ().setAttribute ("userName", Math.random () + "abasd");
                
                Key key = KeyFactory.stringToKey (wbId);
                wb = pm.getObjectById (WhiteBoard.class, key);
                //String query = "select from " + WBMessage.class.getName() + " where wbKey == KEY('" + wbId + "')";
                
                Query query = pm.newQuery(WBMessage.class, "this.wbKey == key");
                query.declareParameters("com.google.appengine.api.datastore.Key key");
                query.setOrdering("creationTime asc");
                //Collection result = (Collection)query.execute(date);
                
                
                
                messages = (List<WBMessage>) query.execute(key);// pm.newQuery(query).execute();
                if(messages != null){
                   for (WBMessage wbMessage : messages)
                    {
                        if((wbMessage.getGeometry () != null) && wbMessage.getGeometry ().getData () != null){
                            wbMessage.getGeometry ().setDataStr (wbMessage.getGeometry ().getData ().getValue ());
                            wbMessage.getGeometry ().setData (null);
                        }
                    }
                }
                
            }catch(Exception e)
            {
               model.put ("errorMsg", e.getMessage ()); 
            }
        }else{
            
            if(hangoutUrl != null){
                Query query = pm.newQuery(WhiteBoard.class, "this.hangoutURL == hangoutUrl");
                query.declareParameters("String hangoutUrl");
                
                
                List<WhiteBoard> boards   = (List<WhiteBoard>) query.execute(hangoutUrl);
                if((boards != null) && boards.size () > 0){
                    wb = boards.get (0);
                }
            }
            
            
            
            if(wb == null){
                wb = new WhiteBoard ();
                wb.setCreatedBySessionId (req.getSession ().getId ());
                wb.setCreationDate (new Date ());
                wb.setReferer (req.getHeader ("referer"));
                wb.setUserAgent (req.getHeader ("user-agent"));
                wb.setHangoutURL (hangoutUrl);
                pm.makePersistent (wb);
            }
            
            
            
            ExternalRedirectView rv = new ExternalRedirectView ("/embed/" + KeyFactory.keyToString (wb.getKey ()) );
            mav.setView (rv);
            return mav;
            
        }
        
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        
        if(wb == null){
            mav.setViewName ("badWbId");
            return mav;
        }
        
        if(messages == null){
            messages = new ArrayList <WBMessage> ();
        }
        
        
        //don't need key data in JSON
        JsonConfig jsconfig = new JsonConfig ();
        String [] exlcudes = new String[]{"key","wbKey","userList","shapeId"};
        jsconfig.setExcludes (exlcudes);
        HashMap <String, Object> messagesMap = new HashMap <String, Object> ();
        messagesMap.put ("messages", messages);
        model.put ("messageMapJSON", JSONObject.fromObject (messagesMap,jsconfig).toString ());
        
        String wbKeyStr = KeyFactory.keyToString (wb.getKey ());
        
        Object userNameObj = req.getSession ().getAttribute ("userName");
        String userName = null;
        if(userNameObj != null){
            userName = userNameObj.toString ();
            WBChannel wbc = null;
            try
            {
                //String query = "select from " + WBChannel.class.getName() + " where sessionId == '" + session.getId () + "' && wbKey == '" + wbKeyStr + "'";
                //List<WBChannel> channels = (List<WBChannel>) pm.newQuery(query).execute();
                
                
                Query query = pm.newQuery(WBChannel.class, "this.wbKey == key && this.sessionId == sessId");
                query.declareParameters("com.google.appengine.api.datastore.Key key, String sessId");
                
                List<WBChannel> channels = (List<WBChannel>) query.execute(wb.getKey (), session.getId ());// pm.newQuery(query).execute();
                
                if((channels != null) && (channels.size () > 0)){                    
                    wbc = channels.get (0);
                    Transaction tx = pm.currentTransaction ();
                    tx.begin ();
                    wbc.setTime (System.currentTimeMillis ());
                    tx.commit ();
                }
            }catch(Exception e)
            {
                
            }
            
            if(wbc == null){
                wbc = new WBChannel ();
                wbc.setSessionId (session.getId ());
                wbc.setWbKey (wb.getKey ());
                wbc.setUserName (userName);
                wbc.setTime (System.currentTimeMillis ());
                wbc.setUserAgent (req.getHeader ("user-agent"));
                pm.makePersistent (wbc);
            }

            pushNewUserList (wbId);
            
            model.put ("userName", userName);
            String token = channelService.createChannel(session.getId ());
            model.put ("token", token);
        }
        
        
        
        
        
        model.put ("wbId", wbKeyStr);
       
         mav.setViewName ("embed");
        return mav;
    }
    
    @RequestMapping(value="/whiteboard")
    public ModelAndView doWhiteboard(ModelMap model, HttpServletRequest req,String wbId){
        
        ModelAndView mav = new ModelAndView ();
        mav.addAllObjects (model);
        PersistenceManager pm = getPM();
        HttpSession session = req.getSession ();
        WhiteBoard wb = null;
        List<WBMessage> messages = null;
        
        if((wbId == null) || ("".equals (wbId))){
            wbId = req.getParameter ("wb");
        }
        
        if((wbId != null) && (!"".equals (wbId.trim()))){
            wbId = cleanupWbId (wbId);
            try{
                Key key = KeyFactory.stringToKey (wbId);
                wb = pm.getObjectById (WhiteBoard.class, key);
                //String query = "select from " + WBMessage.class.getName() + " where wbKey == KEY('" + wbId + "')";
                
                Query query = pm.newQuery(WBMessage.class, "this.wbKey == key");
                query.declareParameters("com.google.appengine.api.datastore.Key key");
                query.setOrdering("creationTime asc");
                //Collection result = (Collection)query.execute(date);
                
                messages = (List<WBMessage>) query.execute(key);// pm.newQuery(query).execute();
                
            }catch(Exception e)
            {
               model.put ("errorMsg", e.getMessage ()); 
            }
        }else{
            wb = new WhiteBoard ();
            wb.setCreatedBySessionId (req.getSession ().getId ());
            wb.setCreationDate (new Date ());
            wb.setReferer (req.getHeader ("referer"));
            wb.setUserAgent (req.getHeader ("user-agent"));
            pm.makePersistent (wb);
            
            ExternalRedirectView rv = new ExternalRedirectView ("/whiteboard/" + KeyFactory.keyToString (wb.getKey ()) );
            mav.setView (rv);
            return mav;
            
        }
        
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        
        if(wb == null){
            mav.setViewName ("badWbId");
            return mav;
        }
        
        if(messages == null){
            messages = new ArrayList <WBMessage> ();
        }
        
        
        //don't need key data in JSON
        JsonConfig jsconfig = new JsonConfig ();
        String [] exlcudes = new String[]{"key","wbKey","userList","shapeId"};
        jsconfig.setExcludes (exlcudes);
        HashMap <String, Object> messagesMap = new HashMap <String, Object> ();
        messagesMap.put ("messages", messages);
        model.put ("messageMapJSON", JSONObject.fromObject (messagesMap,jsconfig).toString ());
        
        String wbKeyStr = KeyFactory.keyToString (wb.getKey ());
        
        Object userNameObj = req.getSession ().getAttribute ("userName");
        String userName = null;
        if(userNameObj != null){
            userName = userNameObj.toString ();
            WBChannel wbc = null;
            try
            {
                //String query = "select from " + WBChannel.class.getName() + " where sessionId == '" + session.getId () + "' && wbKey == '" + wbKeyStr + "'";
                //List<WBChannel> channels = (List<WBChannel>) pm.newQuery(query).execute();
                
                
                Query query = pm.newQuery(WBChannel.class, "this.wbKey == key && this.sessionId == sessId");
                query.declareParameters("com.google.appengine.api.datastore.Key key, String sessId");
                
                List<WBChannel> channels = (List<WBChannel>) query.execute(wb.getKey (), session.getId ());// pm.newQuery(query).execute();
                
                if((channels != null) && (channels.size () > 0)){                    
                    wbc = channels.get (0);
                    Transaction tx = pm.currentTransaction ();
                    tx.begin ();
                    wbc.setTime (System.currentTimeMillis ());
                    tx.commit ();
                }
            }catch(Exception e)
            {
                
            }
            
            if(wbc == null){
                wbc = new WBChannel ();
                wbc.setSessionId (session.getId ());
                wbc.setWbKey (wb.getKey ());
                wbc.setUserName (userName);
                wbc.setTime (System.currentTimeMillis ());
                wbc.setUserAgent (req.getHeader ("user-agent"));
                pm.makePersistent (wbc);
            }

            pushNewUserList (wbId);
            
            model.put ("userName", userName);
            String token = channelService.createChannel(session.getId ());
            model.put ("token", token);
        }
        
        
        
        
        
        model.put ("wbId", wbKeyStr);
        if(WebUtil.isMobile (req)){
            model.put ("mobileTheme", WebUtil.getMobileTheme (req));
            mav.setViewName ("mobileboard");
        }else{
            mav.setViewName ("whiteboard");
        }
        return mav;
    }
    
    

    
    
    @RequestMapping(value="/wbping")
    public ModelAndView doWhiteboardPing(ModelMap model, HttpServletRequest req){
        model.clear ();
        HttpSession session = req.getSession ();
        try{
            PersistenceManager pm = getPM();
            WBChannel wbc = null;
            //String query = "select from " + WBChannel.class.getName() + " where sessionId == '" + session.getId () + "' && wbKey == '" + cleanupWbId (req.getParameter ("wbId")) + "'";
            //List<WBChannel> channels = (List<WBChannel>) pm.newQuery(query).execute();
            
            Query query = pm.newQuery(WBChannel.class, "this.wbKey == key && this.sessionId == sessId");
            query.declareParameters("com.google.appengine.api.datastore.Key key, String sessId");
            
            List<WBChannel> channels = (List<WBChannel>) query.execute(KeyFactory.stringToKey (cleanupWbId (req.getParameter ("wbId"))), session.getId ());// pm.newQuery(query).execute();
           
            
            if((channels != null) && (channels.size () > 0)){                    
                wbc = channels.get (0);
                Transaction tx = pm.currentTransaction ();
                tx.begin ();
                wbc.setTime (System.currentTimeMillis ());
                tx.commit ();
                model.put ("status", "ok");
            }else{
                model.put("error","Invalid channel.");
                
            }
        }catch(Exception e)
        {
            model.put("error",e.getMessage ());
        }
        
        return doJSON (model);
    }
    
 
    @RequestMapping(value="/wbchannels/{wbId}")
    public ModelAndView doWhiteboardChannels(ModelMap model, HttpServletRequest req,@PathVariable String wbId){
        model.clear ();
        try{
            model.put("wbId",wbId);
            if(isAdmin ()){
                PersistenceManager pm = getPM();
                
                Query query = pm.newQuery(WBChannel.class, "this.wbKey == key");
                query.declareParameters("com.google.appengine.api.datastore.Key key");
                
                
                List<WBChannel> channels = (List<WBChannel>) query.execute(KeyFactory.stringToKey (wbId));
               
                
                if((channels != null) && (channels.size () > 0)){                    
                    model.put ("channels", channels);
                    model.put ("status", "ok");
                }else{
                    model.put("error","no channels.");
                    
                }
            }else{
                throw new Exception("Unauthorized");
            }
        }catch(Exception e)
        {
            model.put("error",e.getMessage ());
        }
        
        return doJSON (model);
    }
    
    @RequestMapping(value="/wbmessages/{wbId}")
    public ModelAndView doWhiteboardMessages(ModelMap model, HttpServletRequest req,@PathVariable String wbId){
        model.clear ();
        try{
            model.put("wbId",wbId);
            if(isAdmin ()){
                PersistenceManager pm = getPM();
                
                Query query = pm.newQuery(WBMessage.class, "this.wbKey == key");
                query.declareParameters("com.google.appengine.api.datastore.Key key");
                
                
                List<WBMessage> messages = (List<WBMessage>) query.execute(KeyFactory.stringToKey (wbId));
               
                
                if((messages != null) && (messages.size () > 0)){                    
                    model.put ("messages", messages);
                    model.put ("status", "ok");
                }else{
                    model.put("error","no messages.");
                    
                }
            }else{
                throw new Exception("Unauthorized");
            }
        }catch(Exception e)
        {
            model.put("error",e.getMessage ());
        }
        
        return doJSON (model);
    }
    
    
    
    @RequestMapping(value="/backup/{numBoards}")
    public ModelAndView doBackup(ModelMap model, HttpServletRequest req,@PathVariable int numBoards){
        model.clear ();
        try{
        	
        	if(isAdmin ()){
        		List<Map<String,Object>> bMaps = new ArrayList<Map<String,Object>>();
        		int found = 0;
            	PersistenceManager pm = getPM();
                Query query = pm.newQuery(WhiteBoard.class);
                query.setOrdering("creationDate asc");
                query.setRange (0, numBoards);
                 //Collection result = (Collection)query.execute(date);
                        
                List<WhiteBoard> boards = (List<WhiteBoard>) query.execute();
               
                 
                if((boards != null) && (boards.size () > 0)){                    
                    for (WhiteBoard wb : boards) {
                    	Map<String,Object> bMap = new HashMap<String, Object>();
                    	bMap.put("board", wb);
                    	
                    	
                    	//messages
                    	Query queryM = pm.newQuery(WBMessage.class, "this.wbKey == key");
                    	queryM.declareParameters("com.google.appengine.api.datastore.Key key");
                        
                        List<WBMessage> messages = (List<WBMessage>) queryM.execute(wb.getKey());
                        
                        if((messages != null) && (messages.size () > 0)){                    
                        	bMap.put ("messages", messages);  
                        	for (WBMessage wbMessage : messages) {
							}
                        }
                        
                        //users
                        Query queryU = pm.newQuery(WBChannel.class, "this.wbKey == key");
                        queryU.declareParameters("com.google.appengine.api.datastore.Key key");
                        
                       
                        List<WBChannel> channels = (List<WBChannel>) queryU.execute(wb.getKey());
                                               
                        if((channels != null) && (channels.size () > 0)){                    
                            bMap.put ("channels", channels);
                            for (WBChannel wbChannel : channels) {
							}
                        }
                        
                        bMaps.add(bMap);
                        found++;
                        
					}
                }else{
                    model.put("error","no boards.");
                    
                }
                
              model.put("boards",bMaps);
              model.put("countRequested",numBoards);
              model.put("records",found);
            }else{
                throw new Exception("Unauthorized");
            }
        }catch(Exception e)
        {
            model.put("error",e.getMessage ());
        }
        
        return doJSON (model);
    }
    
    @RequestMapping(value="/cleanup/{numBoards}")
    public ModelAndView doCleanup(ModelMap model, HttpServletRequest req,@PathVariable int numBoards){
        model.clear ();
        try{
        	
        	if(isAdmin ()){
        		List<Map<String,Object>> bMaps = new ArrayList<Map<String,Object>>();
        		int deleted = 0;
        		
            	PersistenceManager pm = getPM();
                Query query = pm.newQuery(WhiteBoard.class);
                query.setOrdering("creationDate asc");
                query.setRange (0, numBoards);
                 //Collection result = (Collection)query.execute(date);
                        
                List<WhiteBoard> boards = (List<WhiteBoard>) query.execute();
               
                 
                if((boards != null) && (boards.size () > 0)){                    
                    for (WhiteBoard wb : boards) {
                    	Map<String,Object> bMap = new HashMap<String, Object>();
                    	bMap.put("board", wb);
                    	
                    	
                    	//messages
                    	Query queryM = pm.newQuery(WBMessage.class, "this.wbKey == key");
                    	queryM.declareParameters("com.google.appengine.api.datastore.Key key");
                        
                        List<WBMessage> messages = (List<WBMessage>) queryM.execute(wb.getKey());
                        
                        if((messages != null) && (messages.size () > 0)){                    
                        	bMap.put ("messages", messages);  
                        	for (WBMessage wbMessage : messages) {
								pm.deletePersistent(wbMessage);
							}
                        }
                        
                        //users
                        Query queryU = pm.newQuery(WBChannel.class, "this.wbKey == key");
                        queryU.declareParameters("com.google.appengine.api.datastore.Key key");
                        
                       
                        List<WBChannel> channels = (List<WBChannel>) queryU.execute(wb.getKey());
                                               
                        if((channels != null) && (channels.size () > 0)){                    
                            bMap.put ("channels", channels);
                            for (WBChannel wbChannel : channels) {
								pm.deletePersistent(wbChannel);
							}
                        }
                        
                        bMaps.add(bMap);
                        pm.deletePersistent(wb);
                        deleted++;
                    	
					}
                }else{
                    model.put("error","no boards.");
                    
                }
                
              model.put("countRequested",numBoards);
              model.put("deleted",deleted);
            }else{
                throw new Exception("Unauthorized");
            }
        }catch(Exception e)
        {
            model.put("error",e.getMessage ());
        }
        
        return doJSON (model);
    }
    
    @RequestMapping(value="/wbgetUsers")
    public ModelAndView doWhiteboardGetUsers(ModelMap model, HttpServletRequest req){
        model.clear ();
        try{
            List<WBChannel> channels = getLiveChannels (cleanupWbId (req.getParameter ("wbId")));
            
            if((channels != null) && (channels.size () > 0)){                    
                Set <String> userNames = new HashSet <String> ();
                for (WBChannel wbChannel : channels)
                {
                    userNames.add (wbChannel.getUserName ());
                }
                
                model.put ("userList", userNames);
                model.put ("status", "ok");
                
            }else{
                model.put("error","Invalid channel.");
                
            }
        }catch(Exception e)
        {
            model.put("error",e.getMessage ());
        }
        
        return doJSON (model);
    }
    
    public ModelAndView doJSON(Map <String, Object> model){
        ModelAndView mav = new ModelAndView (new JSONView ());
        
        mav.addAllObjects (model);
        return mav;
    }
    
    protected List <WBChannel> getLiveChannels(String wbId)
    {
        List <WBChannel> channels = new ArrayList <WBChannel> ();
        try{
            PersistenceManager pm = getPM();
            long time = System.currentTimeMillis () - lastPingMillis;
            
            //String query = "select from " + WBChannel.class.getName() + " where time > " + time + " && wbKey == '" + wbId + "'";
            //List <WBChannel> result = (List<WBChannel>) pm.newQuery(query).execute();
            
            Query query = pm.newQuery(WBChannel.class, "this.wbKey == key && this.time > lastPing");
            query.declareParameters("com.google.appengine.api.datastore.Key key, Long lastPing");
            
            List<WBChannel> result = (List<WBChannel>) query.execute(KeyFactory.stringToKey (wbId), time);// pm.newQuery(query).execute();
           
            
            for (WBChannel wbChannel : result)
            {
                channels.add (wbChannel);
            }
            
        }catch(Exception e)
        {
            
        }

        if(channels == null){
            channels = new ArrayList <WBChannel> ();
        }
        
        return channels;
        
        
    }
    
    @RequestMapping(value="/wbmail")
    public ModelAndView doWhiteboardMail(ModelMap model, HttpServletRequest req)
    {
        model.clear ();
        
        
        try{
            WBEmail email = new WBEmail ();
            HttpSession session = req.getSession ();
            String userName = session.getAttribute ("userName").toString ();
            String toAddress = req.getParameter ("email");
            
            if(!WebUtil.isValidEmail (toAddress)){
                throw new Exception("invalid email");
                
            }
            
            //TODO validate message contents
            email.setFromUser (userName);
            email.setToAddress (toAddress);
            
            
            WhiteBoard wb = getWhiteBoard (req);
            if(wb == null){
                throw new Exception("Invalid White Board");
            }
            
            email.setWbKey (wb.getKey ());
            email.setCreationTime (System.currentTimeMillis ());
            
            
            Properties props = new Properties();
            Session mailsession = Session.getDefaultInstance(props, null);

            
            
            String emailError = null;
            try {
                MimeMessage msg = new MimeMessage(mailsession);
                msg.setFrom(new InternetAddress("no_reply@drawitlive.com", "Draw it Live"));
                msg.addRecipient(Message.RecipientType.TO,
                                 new InternetAddress(toAddress));
                msg.setSubject("Please join the whiteboard session on " + req.getServerName());
                String msgBody = "To join " + userName + " on a colloborative whiteboard follow this link: <a href=\"http://" + req.getServerName() + "/whiteboard/" + req.getParameter ("wbId") + "\"> http://" + req.getServerName() + "/whiteboard/" + req.getParameter ("wbId") + "</a>";
                msg.setText(msgBody,"UTF-8","html");
                Transport.send(msg);

            } catch (AddressException e) {
                emailError = e.getMessage ();
            } catch (MessagingException e) {
                emailError = e.getMessage ();
            } 
            
            if(emailError == null){
                email.setStatus (WBEmail.STATUS_SENT);
            }else{
                email.setStatus (WBEmail.STATUS_ERROR);
            }
            
            getPM ().makePersistent (email);
            
            if(email.getStatus () == WBEmail.STATUS_ERROR){
                throw new Exception (emailError);
            }
            

            model.put ("status", "ok");
            
        }catch(Exception e)
        {
            model.put ("error",e.getMessage ());
        }
        
        return doJSON (model);
    }
    
    @RequestMapping(value="/wbpost")
    public ModelAndView doWhiteboardPost(ModelMap model, HttpServletRequest req){
        model.clear ();
        
        
        try{
            String jsonData = req.getParameter("data");
            JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonData);
            WBMessage message = (WBMessage) JSONObject.toBean (json,WBMessage.class);
            HttpSession session = req.getSession ();
            String userName = session.getAttribute ("userName").toString ();
            
            //TODO validate message contents
            message.setFromUser (userName);
            
            if(message.getChatMessage () != null)
            {
                message.setChatMessage (getInputValidator ().cleanCommentText (message.getChatMessage ()));
                message.setChatMessage (getTextFormatter ().formatCommentText (message.getChatMessage ()));
            }
            
            WhiteBoard wb = getWhiteBoard (req);
            if(wb == null){
                throw new Exception("Invalid White Board");
            }
            
            message.setWbKey (wb.getKey ());
            message.setFromUser (userName);
            message.setCreationTime (System.currentTimeMillis ());
            
            //trim any text geom > 100 chars
            if(message.getGeometry () != null){
                WBGeometry geom = message.getGeometry ();
                if((geom.getText () != null) && (geom.getText ().length () > 100)  )
                {
                        geom.setText (geom.getText ().substring (0, 99));   
                }
                
                if("image".equals (geom.getShapeType ()) && geom.getDataStr () != null){
                    //validate image data
                    geom.setData (new Text (geom.getDataStr ()));
                    
                }
                
            }
            
            getPM ().makePersistent (message);
            
            //don't need data twice on messages sent to channels
            if((message.getGeometry () != null) && (message.getGeometry ().getData () != null) && (message.getGeometry ().getDataStr () != null)){
                message.getGeometry ().setData (null);
            }
            
            List<WBChannel> channels = getLiveChannels (KeyFactory.keyToString (wb.getKey ()));
            ChannelService channelService = ChannelServiceFactory.getChannelService();
            String channelError = null;
            for (WBChannel wbChannel : channels)
            {
                
                // don't need to send drawing data back to originating user
                if((message.getChatMessage () != null) || (!session.getId ().equals (wbChannel.getSessionId ()))){
                    try{
                        channelService.sendMessage(new ChannelMessage(wbChannel.getSessionId (),JSONObject.fromObject (message).toString () ));
                        
                    }catch(Exception e)
                    {
                        channelError = e.getMessage ();
                    }
                }
            }
            
            //if there was at least one channelError, we'll log it to the browser console
            if(channelError != null)
            {
                model.put ("channelError","error delivering to at least one channel: " + channelError + "(user may have left)");
            }
            model.put ("status", "ok");
            model.put("message",JSONObject.fromObject (message));
            
        }catch(Exception e)
        {
            model.put ("error",e.getMessage ());
        }
        
        return doJSON (model);
    }
    
    protected PersistenceManager getPM(){        
        return PMF.get().getPersistenceManager();
    }
    
    @RequestMapping(value="/wbSetName")
    public ModelAndView doWBSetName(ModelMap model, HttpServletRequest req){
        model.clear ();
        
        HttpSession session = req.getSession ();
        PersistenceManager pm = getPM();
        
        WhiteBoard wb = null;
        try{
            
            wb = getWhiteBoard (req);
            if(wb == null){
                throw new Exception("Invalid Whiteboard ID");
            }
        }catch(Exception e){
            model.put ("error", e.getMessage ());
        }
        
        try{
            if(wb != null){
                String userName = cleanUpName (req);
                String wbId = KeyFactory.keyToString (wb.getKey ());
                if("".equals (userName))
                {
                    model.put ("error", "Invalid username");
                }
                else{
                    req.getSession ().setAttribute ("userName", userName);
                    
                    WBChannel wbc = new WBChannel ();
                    wbc.setSessionId (session.getId ());
                    wbc.setWbKey (wb.getKey ());
                    wbc.setUserName (userName);
                    wbc.setTime (System.currentTimeMillis ());
                    wbc.setUserAgent (req.getHeader ("user-agent"));
                    pm.makePersistent (wbc);
                    pushNewUserList (wbId);
                    
                    ChannelService channelService = ChannelServiceFactory.getChannelService();
                    String token = channelService.createChannel(session.getId ());
                    model.put ("token", token);
                    model.put ("userName", userName);
                    model.put ("status", "ok");
                    
                }
            }
        }catch(Exception e)
        {
            model.put ("error", e.getMessage ());
        }
        return doJSON (model);
    }
    
    protected void pushNewUserList(String wbId){
        List <WBChannel> channels = getLiveChannels (wbId);
        HashSet <String> userNames = new HashSet <String> ();
        for (WBChannel wbChannel : channels)
        {
            userNames.add (wbChannel.getUserName ());
        }
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        for (WBChannel wbChannel : channels)
        {
            try{
                WBMessage message = new WBMessage ();
                message.setUserList (userNames);
                channelService.sendMessage(new ChannelMessage(wbChannel.getSessionId (),JSONObject.fromObject (message).toString () ));
                
            }catch(Exception e)
            {
                
            }
        }
        
    }
    
    
    protected WhiteBoard getWhiteBoard(HttpServletRequest req) throws Exception
    {
        PersistenceManager pm = getPM();
        return pm.getObjectById (WhiteBoard.class, KeyFactory.stringToKey (cleanupWbId (req.getParameter ("wbId"))));
        
    }
    
    protected String cleanUpName(HttpServletRequest req)
    {
        String userName = req.getParameter ("userName");
        if(userName == null)
        {
            userName = "";
        }
        
        userName = userName.trim ();
        
        char[] userChars = userName.toCharArray ();
        for (int i = 0; i < userChars.length; i++)
        {
            if(!validChar (userChars[i])){
                userChars[i] = '_';
            }
        }
        
        return new String (userChars);
    }
    
    protected boolean validChar(char c)
    {
        for (int i = 0; i < validChars.length; i++)
        {
         if(c == validChars[i])
         {
             return true;
         }
        }
        return false;
    }
    
    protected String cleanupWbId(String wbId)
    {
        if(wbId != null)
        {
            if(wbId.indexOf ("'") > -1){
                return null;
            }
            if(wbId.indexOf ("\"") > -1){
                return null;
            }
        }
        return wbId;
    }

    /**
     * @return the inputValidator
     */
    public InputValidator getInputValidator ()
    {
        return inputValidator;
    }

    /**
     * @param inputValidator the inputValidator to set
     */
    @Autowired
    public void setInputValidator (InputValidator inputValidator)
    {
        this.inputValidator = inputValidator;
    }

    /**
     * @return the textFormatter
     */
    public TextFormatter getTextFormatter ()
    {
        return textFormatter;
    }

    /**
     * @param textFormatter the textFormatter to set
     */
    @Autowired
    public void setTextFormatter (TextFormatter textFormatter)
    {
        this.textFormatter = textFormatter;
    }
    
    
    public boolean isAdmin(){
        
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser(); // req.getUserPrincipal()?!?
        userService.isUserAdmin()
        
        if(user != null){
            String nick = user.getNickname ().toUpperCase ();
            if(nick.startsWith ("MONTESLU@")){
                return true;
            }
            return false;
        }else{
            return false;
        }
    }
    
    
    
}
