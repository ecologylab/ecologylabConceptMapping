package wikxplorer.messages;

import java.util.HashMap;
import java.util.Map;

import ecologylab.collections.Scope;
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

	@Override
	public void processResponse(Scope objectRegistry)
	{
		// just for testing
		System.out.println("RelatednessResponse:");
		for (String targetTitle : targets.keySet())
		{
			Concept concept = targets.get(targetTitle);
			System.out.println(String.format("\t%s: %f", targetTitle, concept.getRelatedness()));
		}
	}

}
