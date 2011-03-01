package ecologylab.semantics.concept.database.orm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "keyphraseness")
public class Keyphraseness implements Serializable
{

	@Id
	@Column(name = "surface", nullable = false)
	private String	surface;

	@Column(name = "keyphraseness", nullable = false)
	private double	keyphraseness;

	public String getSurface()
	{
		return surface;
	}

	public void setSurface(String surface)
	{
		this.surface = surface;
	}

	public double getKeyphraseness()
	{
		return keyphraseness;
	}

	public void setKeyphraseness(double keyphraseness)
	{
		this.keyphraseness = keyphraseness;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof Keyphraseness)
		{
			if (surface.equals(((Keyphraseness) obj).surface))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return surface.hashCode();
	}

}
