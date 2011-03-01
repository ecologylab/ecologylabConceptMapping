package ecologylab.semantics.concept.database.orm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="wiki_concepts")
public class WikiConcept implements Serializable
{

	@Id
	@Column(name="id", nullable=false)
	private int	id;

	@Column(name="title", nullable=false)
	private String	title;

	/**
	 * here text are pure text (after rendering to HTML), not wiki markups.
	 */
	@Column(name="text", nullable=false)
	private String	text;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return text;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof WikiConcept)
		{
			if (id == ((WikiConcept) obj).id)
				return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return id;
	}

}
