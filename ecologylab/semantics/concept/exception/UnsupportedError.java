package ecologylab.semantics.concept.exception;

public class UnsupportedError extends Error
{

	public UnsupportedError(String msg, Throwable t)
	{
		super(msg, t);
	}

	public UnsupportedError(String msg)
	{
		super(msg);
	}

	public UnsupportedError(Throwable t)
	{
		super(t);
	}

}
