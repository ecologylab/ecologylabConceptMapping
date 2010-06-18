package ecologylab.semantics.conceptmapping.model.tuning;

import qmlt.decisiontree.control.DecisionTreeTrainingController;
import qmlt.svm.SVMTrainingController;

public class Configuration
{
	public DecisionTreeTrainingController dtCtrl;
	public SVMTrainingController svmCtrl;
	
	public Configuration(DecisionTreeTrainingController dtCtrl, SVMTrainingController svmCtrl)
	{
		this.dtCtrl = dtCtrl;
		this.svmCtrl = svmCtrl;
	}
}
