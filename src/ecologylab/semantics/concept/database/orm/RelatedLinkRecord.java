package ecologylab.semantics.concept.database.orm;

class RelatedLinkRecord implements Comparable<RelatedLinkRecord>
{

	private WikiConcept	relatedLink;

	private double			relatedness;

	public RelatedLinkRecord(WikiConcept relatedLink, double relatedness)
	{
		this.relatedLink = relatedLink;
		this.relatedness = relatedness;
	}

	public WikiConcept getRelatedLink()
	{
		return relatedLink;
	}

	public double getRelatedness()
	{
		return relatedness;
	}

	public int compareTo(RelatedLinkRecord o)
	{
		return Double.compare(o.relatedness, this.relatedness);
	}

}
