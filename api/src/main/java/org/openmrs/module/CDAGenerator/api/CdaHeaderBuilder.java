package org.openmrs.module.CDAGenerator.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.openhealthtools.mdht.uml.cda.AssignedAuthor;
import org.openhealthtools.mdht.uml.cda.AssignedCustodian;
import org.openhealthtools.mdht.uml.cda.AssignedEntity;
import org.openhealthtools.mdht.uml.cda.AssociatedEntity;
import org.openhealthtools.mdht.uml.cda.Author;
import org.openhealthtools.mdht.uml.cda.AuthoringDevice;
import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.Custodian;
import org.openhealthtools.mdht.uml.cda.CustodianOrganization;
import org.openhealthtools.mdht.uml.cda.DocumentationOf;
import org.openhealthtools.mdht.uml.cda.Entry;
import org.openhealthtools.mdht.uml.cda.InfrastructureRootTypeId;
import org.openhealthtools.mdht.uml.cda.Organization;
import org.openhealthtools.mdht.uml.cda.Participant1;
import org.openhealthtools.mdht.uml.cda.PatientRole;
import org.openhealthtools.mdht.uml.cda.Performer1;
import org.openhealthtools.mdht.uml.cda.Person;
import org.openhealthtools.mdht.uml.cda.Section;
import org.openhealthtools.mdht.uml.cda.ServiceEvent;
import org.openhealthtools.mdht.uml.cda.StrucDocText;
import org.openhealthtools.mdht.uml.hl7.datatypes.AD;
import org.openhealthtools.mdht.uml.hl7.datatypes.CE;
import org.openhealthtools.mdht.uml.hl7.datatypes.CS;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.II;
import org.openhealthtools.mdht.uml.hl7.datatypes.IVL_TS;
import org.openhealthtools.mdht.uml.hl7.datatypes.IVXB_TS;
import org.openhealthtools.mdht.uml.hl7.datatypes.ON;
import org.openhealthtools.mdht.uml.hl7.datatypes.PN;
import org.openhealthtools.mdht.uml.hl7.datatypes.SC;
import org.openhealthtools.mdht.uml.hl7.datatypes.ST;
import org.openhealthtools.mdht.uml.hl7.datatypes.TEL;
import org.openhealthtools.mdht.uml.hl7.datatypes.TS;
import org.openhealthtools.mdht.uml.hl7.vocab.ActClassRoot;
import org.openhealthtools.mdht.uml.hl7.vocab.NullFlavor;
import org.openhealthtools.mdht.uml.hl7.vocab.ParticipationType;
import org.openhealthtools.mdht.uml.hl7.vocab.RoleClassAssociative;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ServiceEventPerformer;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
import org.openmrs.module.CDAGenerator.CDAHandlers.BaseCdaTypeHandler;
import org.openmrs.module.CDAGenerator.SectionHandlers.AbdomenSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.BreastSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.ChestWallSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.ChiefComplaintSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.EarNoseMouthThroatSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.EarsSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.EndocrineSystemSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.GeneralAppearanceSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.GenitaliaSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.HeadSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.HeartSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.HistoryOfInfectionSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.HistoryOfPresentIllnessSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.IntegumentarySystemSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.LymphaticSystemSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.MouthThroatTeethSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.MusculoskeletalSystemSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.NeckSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.NeurologicSystemSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.NoseSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.OptionalEyesSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.PhysicalExamSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.RectumSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.RespiratorySystemSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.ReviewOfSystemsSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.SocialHistorySection;
import org.openmrs.module.CDAGenerator.SectionHandlers.ThoraxLungsSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.VesselsSection;
import org.openmrs.module.CDAGenerator.SectionHandlers.VisibleImplantedMedicalDevicesSection;

public class CdaHeaderBuilder 
{
	
	public ClinicalDocument buildHeader(ClinicalDocument doc,BaseCdaTypeHandler bh,Patient p)
	{
				
		InfrastructureRootTypeId typeId = CDAFactory.eINSTANCE.createInfrastructureRootTypeId();
		typeId.setExtension("POCD_HD000040");/*fixed*/
		typeId.setRoot("2.16.840.1.113883.1.3");
		doc.setTypeId(typeId);
		
		doc.getTemplateIds().clear();
		doc.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10","IMPL_CDAR2_LEVEL1",null));
		doc.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.3",null,null));
		doc.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.1.1",null,null));//Medical Documents 
		doc.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.1.2",null,null));//Medical Summary
		doc.getTemplateIds().add(buildTemplateID(bh.templateid,null,null));//
		
		
		doc.setId(buildID(Context.getAdministrationService().getImplementationId().getImplementationId(),bh.documentShortName));//need to generate dynamically
		
		doc.setCode(buildCodeCE("34117-2","2.16.840.1.113883.6.1",null,"LOINC"));//need to generate dynamically template id according loinc code system if we choose snomed then it would be diffrent
		
		doc.setTitle(buildTitle(bh.documentFullName));
		
		Date d = new Date();
		doc.setEffectiveTime(buildEffectiveTime(d));
		
		CE confidentialityCode = DatatypesFactory.eINSTANCE.createCE();
		confidentialityCode.setCode("N");//this can change N,M,L,R,V
		confidentialityCode.setCodeSystem("2.16.840.1.113883.5.25");/*fixed*/
		doc.setConfidentialityCode(confidentialityCode);
		
		CS languageCode = DatatypesFactory.eINSTANCE.createCS();
		languageCode.setCode("en-US");/*fixed*/
		doc.setLanguageCode(languageCode);
     
        CS realmcode=DatatypesFactory.eINSTANCE.createCS("US");
        doc.getRealmCodes().add(realmcode);
		
		
		
		PatientRole patientRole = CDAFactory.eINSTANCE.createPatientRole();
		patientRole.getIds().add(buildID(Context.getAdministrationService().getImplementationId().getImplementationId(),
				p.getPatientIdentifier().getIdentifier()));//get dynamically from patient service
		
		Set<PersonAddress> addresses = p.getAddresses();
		
		AD patientAddress = DatatypesFactory.eINSTANCE.createAD();
		
		patientAddress=buildAddresses(patientAddress, addresses);
		patientRole.getAddrs().add(patientAddress);
		
		TEL patientTelecom = DatatypesFactory.eINSTANCE.createTEL();
		patientTelecom.setNullFlavor(NullFlavor.UNK);
		patientRole.getTelecoms().add(patientTelecom);
		org.openhealthtools.mdht.uml.cda.Patient cdapatient = CDAFactory.eINSTANCE.createPatient();
		
		patientRole.setPatient(cdapatient);
		PN name = DatatypesFactory.eINSTANCE.createPN();
		if(p.getPersonName().getFamilyNamePrefix()!=null)
		{
			name.addPrefix(p.getPersonName().getFamilyNamePrefix());
		}
		name.addGiven(p.getPersonName().getGivenName());/* dynamically get patient name*/
		name.addFamily(p.getPersonName().getFamilyName());
		if(p.getPersonName().getFamilyNameSuffix()!=null)
		{
		name.addSuffix(p.getPersonName().getFamilyNameSuffix());
		}
		cdapatient.getNames().add(name);

		
		CE gender = DatatypesFactory.eINSTANCE.createCE();
		gender.setCode(p.getGender());//dynamic
		gender.setCodeSystem("2.16.840.1.113883.5.1");//fixed
		cdapatient.setAdministrativeGenderCode(gender);
		
		
		
		TS dateOfBirth = DatatypesFactory.eINSTANCE.createTS();
		SimpleDateFormat s1 = new SimpleDateFormat("yyyyMMdd");
		Date dobs=p.getBirthdate();
		String dob = s1.format(dobs);
		dateOfBirth.setValue(dob);
		cdapatient.setBirthTime(dateOfBirth); 
		
		
		CE codes = DatatypesFactory.eINSTANCE.createCE();
		codes.setCode("S");
		cdapatient.setMaritalStatusCode(codes);
		
		CE codes1 = DatatypesFactory.eINSTANCE.createCE();
		codes1.setCode("AAA");				
		cdapatient.setEthnicGroupCode(codes1);
		
		Organization providerOrganization = CDAFactory.eINSTANCE.createOrganization();
		AD providerOrganizationAddress = DatatypesFactory.eINSTANCE.createAD();
		providerOrganizationAddress.addCounty(" ");
		providerOrganization.getIds().add(buildID(Context.getAdministrationService().getImplementationId().getImplementationId(),null));
		providerOrganization.getAddrs().add(providerOrganizationAddress);

		ON organizationName = DatatypesFactory.eINSTANCE.createON();
		organizationName.addText(Context.getAdministrationService().getImplementationId().getName());
		providerOrganization.getNames().add(organizationName);

		TEL providerOrganizationTelecon = DatatypesFactory.eINSTANCE.createTEL();
		providerOrganizationTelecon.setNullFlavor(NullFlavor.UNK);
		providerOrganization.getTelecoms().add(providerOrganizationTelecon);

		patientRole.setProviderOrganization(providerOrganization);

			
		doc.addPatientRole(patientRole);

		
		
		Author author = CDAFactory.eINSTANCE.createAuthor();
		author.setTime(buildEffectiveTime(new Date()));
		//in this case we consider the assigned author is the one generating the document i.e the logged in user exporting the document
		AssignedAuthor assignedAuthor = CDAFactory.eINSTANCE.createAssignedAuthor();
		II authorId = DatatypesFactory.eINSTANCE.createII();
		authorId.setRoot(Context.getAdministrationService().getImplementationId().getImplementationId());
		assignedAuthor.getIds().add(authorId);
		
		AD assignedAuthorAddress=DatatypesFactory.eINSTANCE.createAD();
		assignedAuthorAddress.addCountry(" ");
		TEL assignedAuthorTelecon = DatatypesFactory.eINSTANCE.createTEL();
		assignedAuthorTelecon.setNullFlavor(NullFlavor.UNK);
		
		assignedAuthor.getAddrs().add(assignedAuthorAddress);
		assignedAuthor.getTelecoms().add(assignedAuthorTelecon);
		
			

		Person assignedPerson = CDAFactory.eINSTANCE.createPerson(); //assigned person must be system
		PN assignedPersonName = DatatypesFactory.eINSTANCE.createPN();
		assignedPersonName.addPrefix("Dr.");  
		assignedPersonName.addGiven("Robert");
		assignedPersonName.addFamily("Dolin");
		assignedPerson.getNames().add(assignedPersonName);
		assignedAuthor.setAssignedPerson(assignedPerson);
		Organization representedOrganization = CDAFactory.eINSTANCE.createOrganization();
		representedOrganization.getIds().add(buildID("2.16.840.1.113883.19.5",null));
		ON representedOrganizationName=DatatypesFactory.eINSTANCE.createON();
		representedOrganizationName.addText(Context.getAdministrationService().getImplementationId().getName());
		representedOrganization.getNames().add(representedOrganizationName);
		AD representedOrganizationAddress = DatatypesFactory.eINSTANCE.createAD();
		representedOrganizationAddress.addCounty(" ");
		representedOrganization.getAddrs().add(representedOrganizationAddress);
		TEL representedOrganizationTelecon = DatatypesFactory.eINSTANCE.createTEL();
		representedOrganizationTelecon.setNullFlavor(NullFlavor.UNK);
		representedOrganization.getTelecoms().add(representedOrganizationTelecon);
		assignedAuthor.setRepresentedOrganization(representedOrganization);

		
		
		
		
		
		AuthoringDevice authoringDevice = CDAFactory.eINSTANCE.createAuthoringDevice();
		SC authoringDeviceName = DatatypesFactory.eINSTANCE.createSC();
		authoringDeviceName.addText(Context.getAdministrationService().getGlobalProperty("application.name"));
		authoringDevice.setSoftwareName(authoringDeviceName);
		assignedAuthor.setAssignedAuthoringDevice(authoringDevice);
		
		author.setAssignedAuthor(assignedAuthor);
		doc.getAuthors().add(author);
		

		Custodian custodian = CDAFactory.eINSTANCE.createCustodian();
		AssignedCustodian assignedCustodian = CDAFactory.eINSTANCE.createAssignedCustodian();
		CustodianOrganization custodianOrganization = CDAFactory.eINSTANCE.createCustodianOrganization();
		AD custodianOrganizationAddress=DatatypesFactory.eINSTANCE.createAD();
		II custodianId = DatatypesFactory.eINSTANCE.createII();
		custodianId.setRoot("2.16.840.1.113883.19.5");
		custodianOrganization.getIds().add(custodianId);
		custodianOrganizationAddress.addCountry(" ");
		custodianOrganization.setAddr(custodianOrganizationAddress);
		ON custodianOrganizationName=DatatypesFactory.eINSTANCE.createON();
		custodianOrganizationName.addText(Context.getAdministrationService().getImplementationId().getName());
		custodianOrganization.setName(custodianOrganizationName);
		TEL custodianOrganizationTelecon=DatatypesFactory.eINSTANCE.createTEL();
		custodianOrganizationTelecon.setNullFlavor(NullFlavor.UNK);
		custodianOrganization.setTelecom(custodianOrganizationTelecon);
		assignedCustodian.setRepresentedCustodianOrganization(custodianOrganization);
		custodian.setAssignedCustodian(assignedCustodian);
		doc.setCustodian(custodian);

		
		List<Relationship> relationShips= Context.getPersonService().getRelationshipsByPerson(p);
		System.out.println(relationShips);
		List<Participant1> participantList = new ArrayList<Participant1>(relationShips.size());
		System.out.print(participantList);
		for (int i = 0; i< relationShips.size();i++) {
			Participant1 e = CDAFactory.eINSTANCE.createParticipant1();

			e.setTypeCode(ParticipationType.IND);
			II pid1 = DatatypesFactory.eINSTANCE.createII();
			pid1.setRoot("1.3.6.1.4.1.19376.1.5.3.1.2.4");

			II pid2 = DatatypesFactory.eINSTANCE.createII();
			pid2.setRoot("1.3.6.1.4.1.19376.1.5.3.1.2.4.1");

			e.getTemplateIds().add(pid1);
			e.getTemplateIds().add(pid2);
			
			IVL_TS time = DatatypesFactory.eINSTANCE.createIVL_TS();
			time.setHigh(time.getHigh());
			time.setLow(time.getLow());
			//time.setNullFlavor(NullFlavor.UNK);
			e.setTime(time);
			Relationship relationship = relationShips.get(i);
			AssociatedEntity patientRelationShip = CDAFactory.eINSTANCE.createAssociatedEntity();
			patientRelationShip.setClassCode(RoleClassAssociative.PRS);
			CE relationShipCode = DatatypesFactory.eINSTANCE.createCE();
			relationShipCode.setCodeSystemName("Loinc");
			relationShipCode.setCodeSystem("2.16.840.1.113883.6.1");
			Person associatedPerson = CDAFactory.eINSTANCE.createPerson();
			PN associatedPersonName = DatatypesFactory.eINSTANCE.createPN();
			Iterator<PersonAddress> patientAddressIterator = null;
            TEL associatedPersonTelecon=DatatypesFactory.eINSTANCE.createTEL();
            associatedPersonTelecon.setNullFlavor(NullFlavor.UNK);
			switch (relationship.getRelationshipType().getId()) {
			case 1:
				
				relationShipCode.setDisplayName("Doctor");
				associatedPersonName.addFamily(relationship.getPersonA().getFamilyName());
				associatedPersonName.addGiven(relationship.getPersonA().getGivenName());
				patientAddressIterator = relationship.getPersonB().getAddresses().iterator();
				break;
			case 2:

				relationShipCode.setDisplayName("Sibling");
				associatedPersonName.addFamily(relationship.getPersonA().getFamilyName());
				associatedPersonName.addGiven(relationship.getPersonA().getGivenName());
				patientAddressIterator = relationship.getPersonA().getAddresses().iterator();
				break;
			case 3:
				if(p.getId() == relationship.getPersonA().getId())
				{
					relationShipCode.setDisplayName("Child");
					associatedPersonName.addFamily(relationship.getPersonB().getFamilyName());
					associatedPersonName.addGiven(relationship.getPersonB().getGivenName());
					patientAddressIterator = relationship.getPersonB().getAddresses().iterator();
				}else
				{
					relationShipCode.setDisplayName("Parent");
					associatedPersonName.addFamily(relationship.getPersonA().getFamilyName());
					associatedPersonName.addGiven(relationship.getPersonA().getGivenName());
					patientAddressIterator = relationship.getPersonA().getAddresses().iterator();

				}
				break;
			case 4:
				if(p.getId() == relationship.getPersonA().getId())
				{
					if(relationship.getPersonB().getGender().equalsIgnoreCase("M"))
						relationShipCode.setDisplayName("Nephew");
						else
					relationShipCode.setDisplayName("Neice");
					associatedPersonName.addFamily(relationship.getPersonB().getFamilyName());
					associatedPersonName.addGiven(relationship.getPersonB().getGivenName());
					patientAddressIterator = relationship.getPersonB().getAddresses().iterator();
				}else
				{
					if(relationship.getPersonA().getGender().equalsIgnoreCase("M"))
						relationShipCode.setDisplayName("Uncle");
					else
					relationShipCode.setDisplayName("Aunt");
					associatedPersonName.addFamily(relationship.getPersonA().getFamilyName());
					associatedPersonName.addGiven(relationship.getPersonA().getGivenName());
					patientAddressIterator = relationship.getPersonB().getAddresses().iterator();

				}	

				break;

			}

			patientRelationShip.setCode(relationShipCode);
			AD associatedPersonAddress = DatatypesFactory.eINSTANCE.createAD();

			if(patientAddressIterator.hasNext())
			{
				PersonAddress padd = patientAddressIterator.next();
				associatedPersonAddress.addStreetAddressLine(padd.getAddress1()+ padd.getAddress2())	;
			}

			patientRelationShip.getAddrs().add(associatedPersonAddress);
			patientRelationShip.getTelecoms().add(associatedPersonTelecon);
			associatedPerson.getNames().add(associatedPersonName );
			patientRelationShip.setAssociatedPerson(associatedPerson );
			e.setAssociatedEntity(patientRelationShip);
			participantList.add(e);


		}
		doc.getParticipants().addAll(participantList);
		
	DocumentationOf dof=CDAFactory.eINSTANCE.createDocumentationOf();
	ServiceEvent serviceEvent=CDAFactory.eINSTANCE.createServiceEvent();
	serviceEvent.setClassCode(ActClassRoot.PCPR);
	
	serviceEvent.setEffectiveTime(buildEffectiveTimeinIVL(new Date(),new Date()));
	Performer1 performer=CDAFactory.eINSTANCE.createPerformer1();
	
	performer.setTypeCode(x_ServiceEventPerformer.PPRF);
	performer.setFunctionCode(buildCodeCE("PCP","2.16.840.1.113883.5.88",null,null));
	performer.setTime(buildEffectiveTimeinIVL(new Date(),new Date()));
	
	AssignedEntity assignedEntity=CDAFactory.eINSTANCE.createAssignedEntity();
	II  assignedEntityId= DatatypesFactory.eINSTANCE.createII();
	assignedEntityId.setRoot(Context.getAdministrationService().getImplementationId().getImplementationId());
	assignedEntity.getIds().add(assignedEntityId);
	AD assignedPersonAddress=DatatypesFactory.eINSTANCE.createAD();
	assignedPersonAddress.addCountry(" ");
    TEL assignedPersonTelecon=DatatypesFactory.eINSTANCE.createTEL();
    assignedPersonTelecon.setNullFlavor(NullFlavor.UNK);
	assignedEntity.setAssignedPerson(assignedPerson);
	assignedEntity.getAddrs().add(assignedPersonAddress);
	assignedEntity.getTelecoms().add(assignedPersonTelecon);
	assignedEntity.getRepresentedOrganizations().add(representedOrganization);
	
	performer.setAssignedEntity(assignedEntity);
	
	
	serviceEvent.getPerformers().add(performer);
	dof.setServiceEvent(serviceEvent);
	doc.getDocumentationOfs().add(dof);
	
	
	
	doc=buildHistoryOfPresentIllnessSection(doc);
	
	doc=buildChiefComplaintSection(doc);
	
	doc=buildSocialHistorySection(doc);
	
	doc=buildHistoryOfInfectionSection(doc);
	
	doc=buildReviewOfSystemsSection(doc);
	
	doc=buildPhysicalExamSection(doc);
	
	
	return doc;
	}
	public IVL_TS  buildEffectiveTimeinIVL(Date d , Date d1)
	{
		IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
		String creationDate = s.format(d);
		IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
		low.setValue(creationDate);
		effectiveTime.setLow(low);
		IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
		if(d1 != null)
			high.setValue(s.format(d1));
		effectiveTime.setHigh(high);
		return effectiveTime;
	}
	public AD buildAddresses(AD documentAddress,Set<PersonAddress> addresses)
	{
		for(PersonAddress address : addresses)
		{
 
			if(address.getAddress1()!=null &&address.getAddress2()!=null)
			{
				documentAddress.addStreetAddressLine(address.getAddress1().concat(address.getAddress2()));
			}
			else
			{
				documentAddress.addStreetAddressLine(" ");
			}
			if(address.getCityVillage()!=null)
			{
				documentAddress.addCity(address.getCityVillage());
			}
			else
			{
				documentAddress.addCity(" ");	
			}
			if(address.getStateProvince()!=null)
			{
				documentAddress.addState(address.getStateProvince());
			}
			else
			{
				documentAddress.addState(" ");
			}
			if(address.getCountry()!=null)
			{
				documentAddress.addCountry(address.getCountry());
			}
			else
			{
				documentAddress.addCountry(" ");
			}
			if(address.getPostalCode()!=null)
			{
				documentAddress.addPostalCode(address.getPostalCode());
			}
			else
			{
				documentAddress.addPostalCode(" ");
			}
			}
		
		

		return documentAddress;
	}
	public   II buildTemplateID(String root , String extension ,String assigningAuthorityName)
	{

			II templateID = DatatypesFactory.eINSTANCE.createII();
			if(root!=null)
			{
			templateID.setRoot(root);
			}
			if(extension!=null)
			{
			templateID.setExtension(extension);
			}
			if(assigningAuthorityName!=null)
			{
			templateID.setAssigningAuthorityName(assigningAuthorityName);
			}
			
			return templateID;

	}
	public ST buildTitle(String title)
	{
		ST displayTitle = DatatypesFactory.eINSTANCE.createST();
		displayTitle.addText(title);
		return displayTitle;
	}

	public II buildID(String root , String extension)
	{
		II id = DatatypesFactory.eINSTANCE.createII();
		if(root!=null)
		{
		id.setRoot(root);
		}
		if(extension!=null)
		{
		id.setExtension(extension);
		}
		return id;
		
	}
	
	public CE buildCodeCE(String code , String codeSystem, String displayString, String codeSystemName)
	{
		CE e = DatatypesFactory.eINSTANCE.createCE();
		if(code!=null)
		{
		e.setCode(code);
		}
		if(codeSystem!=null)
		{
		e.setCodeSystem(codeSystem);
		}
		if(displayString!=null)
		{
		e.setDisplayName(displayString);
		}
		if(displayString!=null)
		{
		e.setCodeSystemName(codeSystemName);
		}
		return e;

	}
	public  TS buildEffectiveTime(Date d)
	{
		TS effectiveTime = DatatypesFactory.eINSTANCE.createTS();
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
		
		String creationDate = s.format(d);
	
		effectiveTime.setValue(creationDate);
		
		return effectiveTime;
	}
	
	public ClinicalDocument buildChiefComplaintSection(ClinicalDocument cd)
	{
		Section section=CDAFactory.eINSTANCE.createSection();
      //  section.setSectionId(UUID.randomUUID().toString());
        ChiefComplaintSection ccs=new ChiefComplaintSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);        
		cd.addSection(section);
		return cd;
		
	}
	
	public ClinicalDocument buildHistoryOfPresentIllnessSection(ClinicalDocument cd)
	{
		Section section=CDAFactory.eINSTANCE.createSection();
     //   section.setSectionId(UUID.randomUUID().toString());
        HistoryOfPresentIllnessSection ccs=new HistoryOfPresentIllnessSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);        
		cd.addSection(section);
		return cd;
	}
	public ClinicalDocument buildHistoryOfInfectionSection(ClinicalDocument cd)
	{
		Section section=CDAFactory.eINSTANCE.createSection();
   //     section.setSectionId(UUID.randomUUID().toString());
        HistoryOfInfectionSection ccs=new HistoryOfInfectionSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);        
		cd.addSection(section);
		return cd;
	}
	public ClinicalDocument buildSocialHistorySection(ClinicalDocument cd)
	{
		Section section=CDAFactory.eINSTANCE.createSection();
   //     section.setSectionId(UUID.randomUUID().toString());
        SocialHistorySection ccs=new SocialHistorySection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getParentTemplateId(),null ,null ));
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);        
		cd.addSection(section);
		return cd;
	}
	
	public ClinicalDocument buildReviewOfSystemsSection(ClinicalDocument cd)
	{
		Section section=CDAFactory.eINSTANCE.createSection();
   //     section.setSectionId(UUID.randomUUID().toString());
        ReviewOfSystemsSection ccs=new ReviewOfSystemsSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);        
		cd.addSection(section);
		return cd;
	}
	public  ClinicalDocument buildPhysicalExamSection(ClinicalDocument cd)
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		
		
       // section.setSectionId(UUID.randomUUID().toString());
        PhysicalExamSection ccs=new PhysicalExamSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getParentTemplateId(),null ,null ));
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
        
        Section OptionalSecs=CDAFactory.eINSTANCE.createSection();
        
        OptionalSecs=buildGeneralAppearanceSection();
        section.addSection(OptionalSecs);
        
        OptionalSecs=buildVisibleImplantedMedicalDevicesSection();
        section.addSection(OptionalSecs);
        
        OptionalSecs=buildIntegumentarySystemSection();
        section.addSection(OptionalSecs);
        
        OptionalSecs=buildHeadSection();
        section.addSection(OptionalSecs);
        
        OptionalSecs=buildOptionalEyesSection();
        section.addSection(OptionalSecs);
        
        OptionalSecs=buildEarNoseMouthThroatSection();
        section.addSection(OptionalSecs);
        
        OptionalSecs=buildEarsSection();
        section.addSection(OptionalSecs);

        OptionalSecs=buildNoseSection();
        section.addSection(OptionalSecs);

        OptionalSecs=buildMouthThroatTeethSection();
        section.addSection(OptionalSecs);
        
        OptionalSecs=buildNeckSection();
        section.addSection(OptionalSecs);
        
        OptionalSecs=buildEndocrineSystemSection();
        section.addSection(OptionalSecs);
        
        OptionalSecs=buildThoraxLungsSection();
        section.addSection(OptionalSecs);
        
        OptionalSecs=buildThoraxLungsSection();
        section.addSection(OptionalSecs);
        
        OptionalSecs=buildBreastSection();
        section.addSection(OptionalSecs);
        
        OptionalSecs=buildHeartSection();
        section.addSection(OptionalSecs);
        
        OptionalSecs=buildRespiratorySystemSection();
        section.addSection(OptionalSecs);
        
       	OptionalSecs=buildAbdomenSection();
       	section.addSection(OptionalSecs);
       	
       	OptionalSecs=buildLymphaticSystemSection();
       	section.addSection(OptionalSecs);
       	
       	OptionalSecs=buildVesselsSection();
       	section.addSection(OptionalSecs);
       	
       	OptionalSecs= buildMusculoskeletalSystemSection();
       	section.addSection(OptionalSecs);
       	
       	OptionalSecs=buildNeurologicSystemSection();
       	section.addSection(OptionalSecs);
       	
       	OptionalSecs=buildGenitaliaSection();
       	section.addSection(OptionalSecs);
       	
       	OptionalSecs=buildRectumSection();
       	section.addSection(OptionalSecs);
       	
		cd.addSection(section);
		
			
		
		return cd;
	}
	public  Section buildGeneralAppearanceSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		GeneralAppearanceSection ccs=new  GeneralAppearanceSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildVisibleImplantedMedicalDevicesSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		VisibleImplantedMedicalDevicesSection ccs=new  VisibleImplantedMedicalDevicesSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildIntegumentarySystemSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		IntegumentarySystemSection ccs=new  IntegumentarySystemSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildHeadSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		HeadSection ccs=new  HeadSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildOptionalEyesSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		OptionalEyesSection ccs=new  OptionalEyesSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildEarNoseMouthThroatSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		EarNoseMouthThroatSection ccs=new  EarNoseMouthThroatSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildEarsSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		EarsSection ccs=new  EarsSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildNoseSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		NoseSection ccs=new  NoseSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildMouthThroatTeethSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		MouthThroatTeethSection ccs=new  MouthThroatTeethSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}	
	public  Section buildNeckSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		NeckSection ccs=new  NeckSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}	
	public  Section buildEndocrineSystemSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		EndocrineSystemSection ccs=new EndocrineSystemSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}	
	public  Section buildThoraxLungsSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		ThoraxLungsSection ccs=new ThoraxLungsSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}	
	public  Section buildChestWallSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		ChestWallSection ccs=new ChestWallSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}	
	public  Section buildBreastSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		BreastSection ccs=new BreastSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildHeartSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		HeartSection ccs=new HeartSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}	
	public  Section buildRespiratorySystemSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		RespiratorySystemSection ccs=new RespiratorySystemSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildAbdomenSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		AbdomenSection ccs=new AbdomenSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildLymphaticSystemSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		LymphaticSystemSection ccs=new LymphaticSystemSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildVesselsSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		VesselsSection ccs=new VesselsSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildMusculoskeletalSystemSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		MusculoskeletalSystemSection ccs=new MusculoskeletalSystemSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildNeurologicSystemSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		NeurologicSystemSection ccs=new NeurologicSystemSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildGenitaliaSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		GenitaliaSection ccs=new GenitaliaSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
	public  Section buildRectumSection()
	{
		Section section=CDAFactory.eINSTANCE.createSection();
		RectumSection ccs=new RectumSection();//this is bad approach though,just to test
        section.getTemplateIds().add(buildTemplateID(ccs.getTemplateid(),null ,null ));
        section.setCode(buildCodeCE(ccs.getCode(),ccs.getCodeSystem(),ccs.getSectionName(),ccs.getCodeSystemName()));
        section.setTitle(buildTitle(ccs.getSectionDescription()));
        StrucDocText text=CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("Text as described above");
        section.setText(text);  
		return section;
	}
}