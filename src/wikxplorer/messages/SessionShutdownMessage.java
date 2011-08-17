package wikxplorer.messages;

import org.hibernate.Session;

import wikxplorer.ScopeKeys;

import ecologylab.collections.Scope;
import ecologylab.oodss.messages.OkResponse;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.oodss.messages.ResponseMessage;

/**
 * Use this message to tell the server to close a session. Needed for gracefully shutdown of the
 * database connection.
 * 
 * @author quyin
 * 
 */
public class SessionShutdownMessage extends RequestMessage
{

	@Override
	public ResponseMessage performService(Scope clientSessionScope)
	{
		Session session = (Session) clientSessionScope.get(ScopeKeys.SESSION);
		session.close();
		return new OkResponse();
	}

}
