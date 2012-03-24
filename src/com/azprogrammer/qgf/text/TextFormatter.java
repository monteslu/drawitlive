
package com.azprogrammer.qgf.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * @author lmontes
 *
 */
public class TextFormatter 
{

    Map <String, String> m_simpleMarkup = new HashMap <String, String>();
    
    
    
    public TextFormatter(){
        m_simpleMarkup.put ("b", "b");
        m_simpleMarkup.put ("i", "i");
        m_simpleMarkup.put ("c", "code");
        m_simpleMarkup.put ("s", "del");
        m_simpleMarkup.put ("u", "u");
        m_simpleMarkup.put ("u", "u");
        m_simpleMarkup.put ("sup", "sup");
        m_simpleMarkup.put ("sub", "sub");
        m_simpleMarkup.put ("quote", "blockquote");
    }

    /* (non-Javadoc)
     * @see com.azprogrammer.gb.forum.helper.ForumTextFormatter#formatCommentText(java.lang.String)
     */
    
    public String formatCommentText (String rawText)
    {
        if(rawText == null ){
            return "";
        }
        else{
            String retVal = rawText;
            
            
            
            
           // StringUtils.countMatches (str, sub)
            for (Map.Entry<String,String> entry : m_simpleMarkup.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                String startTag = '[' + key + ']';
                int startMatches = StringUtils.countMatches (retVal, startTag);
                if(startMatches > 0){
                    String endTag = "[/" + key + ']';
                    int endMatches = StringUtils.countMatches (retVal, endTag );
                    //same number of start and end tags
                    if(endMatches == startMatches){
                        //make sure that the end tags come after the start tags
                        Map <Integer, Integer> locations = new HashMap <Integer, Integer> ();
                        for (int i = 1; i < startMatches + 1; i++)
                        {
                           locations.put (StringUtils.ordinalIndexOf (retVal, '[' + key + ']', i), 1);
                           locations.put (StringUtils.ordinalIndexOf (retVal, "[/" + key + ']', i), -1);
                        }
                        
                        List <Integer> keys = new ArrayList <Integer> (locations.keySet ()) ;
                        Collections.sort (keys);
                        int order = 0;
                        boolean orderOk = true;
                        for (Integer orderKey : keys)
                        {
                            order += locations.get (orderKey);
                            if(order < 0){
                                orderOk = false;
                            }
                        }
                        
                        if(orderOk){
                            retVal = retVal.replaceAll ("\\[" + key + "\\]", "<" + value + ">");
                            retVal = retVal.replaceAll ("\\[\\/" + key + "\\]", "</" + value + ">");
                        }
                        
                    }
                }
            }
            return retVal;
            
        }
        
    }


}
