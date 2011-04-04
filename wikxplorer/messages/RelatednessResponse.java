package wikxplorer.messages;

import java.util.HashMap;
import java.util.Map;

import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.serialization.simpl_inherit;

/**
 * Relatedness values.
 * 
 * @author quyin
 * 
 */
@simpl_inherit
public class RelatednessResponse extends ResponseMessage
{

	/**
	 * Map from target concept title to an object holding returned relatedness values.
	 */
	@simpl_map
	private Map<String, Concept>	targets	= new HashMap<String, Concept>();

	private boolean								ok			= false;

	public Map<String, Concept> getTargets()
	{
		return targets;
	}

	public void setOk(boolean ok)
	{
		this.ok = ok;
	}

	@Override
	public boolean isOK()
	{
		return ok;
	}

}
