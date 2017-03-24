package com.icesoft.htmlparser.tags;

import org.htmlparser.nodes.TagNode;

public class VideoTag extends TagNode
{
	private static final long serialVersionUID = 7790259495438442768L;
	private static final String[] mIds = new String[] {"video"};  
    private static final String mEndTagEnders[] = {"video"};   
	@Override
	public String[] getEndTagEnders()
	{
		return mEndTagEnders;
	}

	@Override
	public String[] getIds()
	{
		return mIds;
	}
	
	public String getPoster()
	{
        return getAttribute ("poster");  
	}
    public void setPoster (String url)  
    {  
        setAttribute ("getPoster", url);  
    }
    public void setOption (String url)  
    {  
        setAttribute ("data-crt-options", url);  
    }
	public String getOption()
	{
        return getAttribute ("data-crt-options");  
	}

    public String toString()  
    {  
        return "Video Tag: " + "Poster[" + getPoster()+ "]" + "data-crt-option[" + getOption() + "]" +"; begins at : "+getStartPosition ()+"; ends at : "+getEndPosition ();  
    }
}
