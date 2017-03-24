package com.icesoft.htmlparser.tags;

import org.htmlparser.nodes.TagNode;

public class SourceTag extends TagNode
{
	private static final long serialVersionUID = 7790259495438442768L;
	private static final String[] mIds = new String[] {"source"};  
    private static final String mEndTagEnders[] = {"source"};   
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
	
	public String getSrc()
	{
        return getAttribute ("src");
	}
    public void setSrc (String url)  
    {  
        setAttribute ("src", url);  
    }
	public String getType()
	{
        return getAttribute ("type");
	}
    public void setType (String url)  
    {  
        setAttribute ("type", url);  
    }
    public String toString()  
    {  
        return "SourceTag: " + this.getSrc() + "Type:" + this.getType() + "; begins at : "+getStartPosition ()+"; ends at : "+getEndPosition ();  
    }
}
