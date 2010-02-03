package ecologylab.semantics.conceptmapping;

/**
 * 
 * @author quyin
 * 
 */
public class Token
{
	private String	originalForm;

	private String	form;

	private String	posTag;

	private String	context;

	public void setOriginalForm(String originalForm)
	{
		this.originalForm = originalForm;
	}

	public String getOriginalForm()
	{
		return originalForm;
	}

	public void setForm(String form)
	{
		this.form = form;
	}

	public String getForm()
	{
		return form;
	}

	public void setPosTag(String posTag)
	{
		this.posTag = posTag;
	}

	public String getPosTag()
	{
		return posTag;
	}

	public void setContext(String context)
	{
		this.context = context;
	}

	public String getContext()
	{
		return context;
	}
}
