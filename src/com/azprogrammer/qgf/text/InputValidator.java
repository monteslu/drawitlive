package com.azprogrammer.qgf.text;

import org.apache.commons.lang.StringUtils;




/**
 * @author lmontes
 *
 */
public class InputValidator
{
    
    protected int m_maxSubjectLength = 100;
    int m_maxStringLength = 80;
    protected int m_maxCommentTextLength = 2000;
   
    /* (non-Javadoc)
     * @see com.azprogrammer.gb.forum.helper.ForumInputValidator#cleanCommentText(java.lang.String)
     */
    public String cleanCommentText (String rawText)
    {
        if(rawText == null){
            return ""; 
        }
        rawText = rawText.trim ();
        rawText = rawText.replaceAll ("\\<", "{");
        rawText = rawText.replaceAll ("\\>", "}");
        rawText = shortenLongWords (rawText);
        return resizeString (rawText, getMaxCommentTextLength ());
    }

    /* (non-Javadoc)
     * @see com.azprogrammer.gb.forum.helper.ForumInputValidator#cleanSubject(java.lang.String)
     */
    public String cleanSubject (String rawText)
    {
        if(rawText == null){
            return ""; 
        }
        rawText = rawText.trim ();
        rawText = rawText.replaceAll ("\"", "'");
        rawText = rawText.replaceAll ("\\>", "}");
        rawText = rawText.replaceAll ("\\<", "{");
        return resizeString (rawText, getMaxSubjectLength ());
    }


    public int getMaxSubjectLength ()
    {
        return m_maxSubjectLength;
    }

    public void setMaxSubjectLength (int maxSubjectLength)
    {
        m_maxSubjectLength = maxSubjectLength;
    }

    public int getMaxCommentTextLength ()
    {
        return m_maxCommentTextLength;
    }

    public void setMaxCommentTextLength (int maxCommentTextLength)
    {
        m_maxCommentTextLength = maxCommentTextLength;
    }
    
    public String resizeString(String rawInput, int size){
        if((rawInput == null) || (size < 1)){
            return "";
        }else{
            if(rawInput.length () > size){
                return rawInput.substring (0, size);
            }else{
                return rawInput;
            }
        }
    }
    
    
  //shorten long strings in case some joker tries a page-widening post
    public String shortenLongWords(String rawText){
        if( (rawText == null) || ("".equals (rawText.trim())) ){
            return "";
        }else{
            String retVal = rawText;
            String[] words = StringUtils.split (rawText);
            for (int i = 0; i < words.length; i++)
            {
                if(words[i].length () > getMaxStringLength ()){
                    retVal = StringUtils.replaceOnce (retVal, words[i], words[i].substring (0,getMaxStringLength () - 4) + "...");
                }
            }
            return retVal;
        }
    }
    
    public int getMaxStringLength ()
    {
        return m_maxStringLength;
    }

    public void setMaxStringLength (int maxStringLength)
    {
        m_maxStringLength = maxStringLength;
    }
}
