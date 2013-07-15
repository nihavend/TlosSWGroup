package com.likya.tlossw.web.definitions.helpers;

import java.math.BigInteger;
import java.util.Collection;

import javax.faces.model.SelectItem;

import com.likya.tlos.model.xmlbeans.common.EventTypeDefDocument.EventTypeDef;
import com.likya.tlos.model.xmlbeans.common.InParamDocument.InParam;
import com.likya.tlos.model.xmlbeans.common.JobBaseTypeDocument.JobBaseType;
import com.likya.tlos.model.xmlbeans.common.JobTypeDefDocument.JobTypeDef;
import com.likya.tlos.model.xmlbeans.common.SpecialParametersDocument.SpecialParameters;
import com.likya.tlos.model.xmlbeans.data.BaseJobInfosDocument.BaseJobInfos;
import com.likya.tlos.model.xmlbeans.data.JobInfosDocument.JobInfos;
import com.likya.tlos.model.xmlbeans.data.JobPriorityDocument.JobPriority;
import com.likya.tlos.model.xmlbeans.data.JsIsActiveDocument.JsIsActive;
import com.likya.tlos.model.xmlbeans.data.OSystemDocument.OSystem;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlossw.web.definitions.JobBasePanelBean;
import com.likya.tlossw.web.utils.DefinitionUtils;
import com.likya.tlossw.web.utils.WebInputUtils;

public class BaseJobInfosTabBean {

	private String jobPriority;

	/* periodic job */
	private String periodTime;

	private String oSystem;
	private Collection<SelectItem> oSystemList = null;

	private Collection<SelectItem> jobBaseTypeList = null;
	private String jobBaseType = JobBaseType.NON_PERIODIC.toString();

	private Collection<SelectItem> jobTypeDefList = null;
	private String jobTypeDef = JobTypeDef.TIME_BASED.toString();

	private Collection<SelectItem> eventTypeDefList = null;
	private String eventTypeDef = EventTypeDef.FILE.toString();

	JobBasePanelBean jobBasePanelBean;

	public BaseJobInfosTabBean(JobBasePanelBean jobBasePanelBean) {
		super();
		this.jobBasePanelBean = jobBasePanelBean;
	}
	
	public void resetTab() {
		oSystem = OSystem.WINDOWS.toString();
		jobPriority = "1";
		jobBaseType = JobBaseType.NON_PERIODIC.toString();
		periodTime = "";
		jobTypeDef = JobTypeDef.TIME_BASED.toString();
		eventTypeDef = EventTypeDef.FILE.toString();
	}

	public void fillTab() {
		fillJobBaseTypeList();
		fillEventTypeDefList();
		fillJobTypeDefList();
		fillOSystemList();
	}
	
	public void fillBaseInfosTab() {

		if (jobBasePanelBean.getJobProperties() != null) {

			BaseJobInfos baseJobInfos = jobBasePanelBean.getJobProperties().getBaseJobInfos();
			jobBasePanelBean.setJsCalendar(baseJobInfos.getCalendarId() + "");
			oSystem = baseJobInfos.getOSystem().toString();
			jobPriority = baseJobInfos.getJobPriority().toString();
			jobTypeDef = baseJobInfos.getJobInfos().getJobTypeDef().toString();
			jobBaseType = baseJobInfos.getJobInfos().getJobBaseType().toString();

			if (jobBaseType.equals(JobBaseType.PERIODIC.toString())) {
				for (Parameter param : baseJobInfos.getJobInfos().getJobTypeDetails().getSpecialParameters().getInParam().getParameterArray()) {
					if (param.getName().equals(JobBasePanelBean.PERIOD_TIME_PARAM)) {
						// periodTime = DefinitionUtils.calendarToStringTimeFormat(param.getValueTime());
						String timeOutputFormat = new String("HH:mm:ss");
						periodTime = DefinitionUtils.calendarToStringTimeFormat(param.getValueTime(), jobBasePanelBean.getTimeManagementTabBean().getSelectedTZone(), timeOutputFormat);
					}
				}
			}

			if (jobTypeDef.equals(JobTypeDef.EVENT_BASED.toString())) {
				eventTypeDef = baseJobInfos.getJobInfos().getJobTypeDetails().getEventTypeDef().toString();
			}

			if (baseJobInfos.getJsIsActive().equals(JsIsActive.YES)) {
				jobBasePanelBean.setJsActive(true);
			} else {
				jobBasePanelBean.setJsActive(false);
			}

		} else {
			System.out.println("jobProperties is NULL in fillBaseInfosTab !!");
		}
	}

	public void fillBaseJobInfos() {
		
		BaseJobInfos baseJobInfos = jobBasePanelBean.getJobProperties().getBaseJobInfos();

		baseJobInfos.setCalendarId(Integer.parseInt(jobBasePanelBean.getJsCalendar()));
		baseJobInfos.setOSystem(OSystem.Enum.forString(oSystem));
		if (jobPriority.isEmpty())
			jobPriority = "1"; // default değer
		baseJobInfos.setJobPriority(JobPriority.Enum.forString(jobPriority));

		if (jobBasePanelBean.isJsActive()) {
			baseJobInfos.setJsIsActive(JsIsActive.YES);
		} else {
			baseJobInfos.setJsIsActive(JsIsActive.NO);
		}

		JobInfos jobInfos = baseJobInfos.getJobInfos();
		jobInfos.setJobBaseType(JobBaseType.Enum.forString(jobBaseType));

		// periyodik is ise onunla ilgili alanlari dolduruyor
		if (jobBaseType.equals(JobBaseType.PERIODIC.toString())) {

			SpecialParameters specialParameters;
			if (baseJobInfos.getJobInfos().getJobTypeDetails().getSpecialParameters() == null) {
				specialParameters = SpecialParameters.Factory.newInstance();
			} else {
				specialParameters = baseJobInfos.getJobInfos().getJobTypeDetails().getSpecialParameters();
			}

			InParam inParam = InParam.Factory.newInstance();

			Parameter parameter = Parameter.Factory.newInstance();
			parameter.setName(JobBasePanelBean.PERIOD_TIME_PARAM);
			parameter.setValueTime(DefinitionUtils.dateToXmlTime(periodTime, jobBasePanelBean.getTimeManagementTabBean().getSelectedTZone()));
			parameter.setId(new BigInteger("1"));

			inParam.addNewParameter();
			inParam.setParameterArray(0, parameter);

			specialParameters.setInParam(inParam);

			jobInfos.getJobTypeDetails().setSpecialParameters(specialParameters);
		}

		jobInfos.setJobTypeDef(JobTypeDef.Enum.forString(jobTypeDef));

		// event tabanli bir is ise event turunu set ediyor
		if (jobTypeDef.equals(JobTypeDef.EVENT_BASED.toString())) {
			jobInfos.getJobTypeDetails().setEventTypeDef(EventTypeDef.Enum.forString(eventTypeDef));
		}

		baseJobInfos.setUserId(jobBasePanelBean.getWebAppUser().getId());
	}
	
	public void fillJobTypeDefList() {
		if (getJobTypeDefList() == null) {
			setJobTypeDefList(WebInputUtils.fillJobTypeDefList());
		}
	}

	public void fillJobBaseTypeList() {
		if (getJobBaseTypeList() == null) {
			setJobBaseTypeList(WebInputUtils.fillJobBaseTypeList());
		}
	}

	public void fillEventTypeDefList() {
		if (getEventTypeDefList() == null) {
			setEventTypeDefList(WebInputUtils.fillEventTypeDefList());
		}
	}
	
	public void fillOSystemList() {
		if (getoSystemList() == null) {
			setoSystemList(WebInputUtils.fillOSystemList());
		}
	}
	
	public String getJobPriority() {
		return jobPriority;
	}

	public void setJobPriority(String jobPriority) {
		this.jobPriority = jobPriority;
	}

	public String getPeriodTime() {
		return periodTime;
	}

	public void setPeriodTime(String periodTime) {
		this.periodTime = periodTime;
	}

	public String getoSystem() {
		return oSystem;
	}

	public void setoSystem(String oSystem) {
		this.oSystem = oSystem;
	}

	public Collection<SelectItem> getoSystemList() {
		return oSystemList;
	}

	public void setoSystemList(Collection<SelectItem> oSystemList) {
		this.oSystemList = oSystemList;
	}

	public Collection<SelectItem> getJobBaseTypeList() {
		return jobBaseTypeList;
	}

	public void setJobBaseTypeList(Collection<SelectItem> jobBaseTypeList) {
		this.jobBaseTypeList = jobBaseTypeList;
	}

	public String getJobBaseType() {
		return jobBaseType;
	}

	public void setJobBaseType(String jobBaseType) {
		this.jobBaseType = jobBaseType;
	}

	public Collection<SelectItem> getJobTypeDefList() {
		return jobTypeDefList;
	}

	public void setJobTypeDefList(Collection<SelectItem> jobTypeDefList) {
		this.jobTypeDefList = jobTypeDefList;
	}

	public String getJobTypeDef() {
		return jobTypeDef;
	}

	public void setJobTypeDef(String jobTypeDef) {
		this.jobTypeDef = jobTypeDef;
	}

	public Collection<SelectItem> getEventTypeDefList() {
		return eventTypeDefList;
	}

	public void setEventTypeDefList(Collection<SelectItem> eventTypeDefList) {
		this.eventTypeDefList = eventTypeDefList;
	}

	public String getEventTypeDef() {
		return eventTypeDef;
	}

	public void setEventTypeDef(String eventTypeDef) {
		this.eventTypeDef = eventTypeDef;
	}

}