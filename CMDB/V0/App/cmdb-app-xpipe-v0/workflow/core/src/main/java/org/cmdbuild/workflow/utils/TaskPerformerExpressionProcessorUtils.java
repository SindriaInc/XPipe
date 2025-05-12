/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.utils;

import java.util.Map;
import java.util.Set;
import org.cmdbuild.easytemplate.EasytemplateProcessor;

public class TaskPerformerExpressionProcessorUtils {

	public static Set<String> getPerformersFromExpression(EasytemplateProcessor templateResolver, Map<String, Object> rawWorkflowVars, String expression) {
		String resolvedExpression = templateResolver.processExpression(expression);

		TaskPerformerExpressionProcessor evaluator = new BshTaskPerformerExpressionProcessor(resolvedExpression);

		evaluator.setVariables(rawWorkflowVars);

		return evaluator.getNames();
	}

}
