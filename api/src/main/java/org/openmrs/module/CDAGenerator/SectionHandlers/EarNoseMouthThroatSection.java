package org.openmrs.module.CDAGenerator.SectionHandlers;

import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.Section;
import org.openhealthtools.mdht.uml.cda.StrucDocText;
import org.openmrs.module.CDAGenerator.api.CDAHelper;

public class EarNoseMouthThroatSection extends PhysicalExamSection 
{
	public EarNoseMouthThroatSection()
	{
	this.sectionName="Ears, Nose, Mouth and Throat";
	this.templateid="1.3.6.1.4.1.19376.1.5.3.1.1.9.20";
	this.code="11393-6";
	this.sectionDescription="The ears, nose, mouth, and throat section shall contain a description of any type of ears, nose, mouth, or throat exam";
	}
	public static Section buildEarNoseMouthThroatSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		EarNoseMouthThroatSection ccs=new  EarNoseMouthThroatSection();
        section.getTemplateIds().add(CDAHelper.buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(CDAHelper.buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(CDAHelper.buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
}
