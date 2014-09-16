package com.vmware.horizontoolset.cli;

import java.util.Calendar;

import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.IContextExecutionInfo;
import org.thymeleaf.context.WebContextExecutionInfo;

public class ThymeleafContext extends AbstractContext{


	public ThymeleafContext(){
	}
	
	
	@Override
	protected IContextExecutionInfo buildContextExecutionInfo(
			String templateName) {
		final Calendar now = Calendar.getInstance();
        return new WebContextExecutionInfo(templateName, now);
	}


}
