package main.java.org.baeldung.spring;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import main.java.org.baeldung.captcha.CaptchaSettings;
import main.java.org.baeldung.persistence.DataSettings;
import main.java.org.baeldung.persistence.dao.PrivilegeRepository;
import main.java.org.baeldung.persistence.dao.RoleRepository;
import main.java.org.baeldung.persistence.dao.UserRepository;
import main.java.org.baeldung.persistence.model.Privilege;
import main.java.org.baeldung.persistence.model.Role;
import main.java.org.baeldung.persistence.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.roslin.mwicks.spring.narf.model.Antibody;
import com.roslin.mwicks.spring.narf.model.AntibodyReference;
import com.roslin.mwicks.spring.narf.model.Avian;
import com.roslin.mwicks.spring.narf.model.LineMhc;
import com.roslin.mwicks.spring.narf.model.LineResistant;
import com.roslin.mwicks.spring.narf.model.LineSusceptible;
import com.roslin.mwicks.spring.narf.model.Line;
import com.roslin.mwicks.spring.narf.model.LineReference;
import com.roslin.mwicks.spring.narf.model.Strain;
import com.roslin.mwicks.spring.narf.model.StrainReference;
import com.roslin.mwicks.spring.narf.model.StrainUse;
import com.roslin.mwicks.spring.narf.model.Organism;
import com.roslin.mwicks.spring.narf.model.BirdOrderLineSex;
import com.roslin.mwicks.spring.narf.model.BirdOrderLineDateFormat;
import com.roslin.mwicks.spring.narf.model.EggOrderLineFertilised;
import com.roslin.mwicks.spring.narf.model.EmbryoOrderLineIncubation;
import com.roslin.mwicks.spring.narf.model.OrderStatus;
import com.roslin.mwicks.spring.narf.model.OrderType;
import com.roslin.mwicks.spring.narf.model.OrderCollection;

import com.roslin.mwicks.spring.narf.routines.ConvertFiletoAntibodyList;
import com.roslin.mwicks.spring.narf.routines.ConvertFiletoAntibodyReferenceList;
import com.roslin.mwicks.spring.narf.routines.ConvertFiletoAvianList;
import com.roslin.mwicks.spring.narf.routines.ConvertFiletoLineList;
import com.roslin.mwicks.spring.narf.routines.ConvertFiletoLineReferenceList;
import com.roslin.mwicks.spring.narf.routines.ConvertFiletoStrainList;
import com.roslin.mwicks.spring.narf.routines.ConvertFiletoStrainReferenceList;
import com.roslin.mwicks.spring.narf.routines.ConvertFiletoStrainUseList;
import com.roslin.mwicks.spring.narf.routines.ConvertFiletoOrganismList;
import com.roslin.mwicks.spring.narf.repository.RepositoryAntibody;
import com.roslin.mwicks.spring.narf.repository.RepositoryAntibodyReference;
import com.roslin.mwicks.spring.narf.repository.RepositoryAvian;
import com.roslin.mwicks.spring.narf.repository.RepositoryLineMhc;
import com.roslin.mwicks.spring.narf.repository.RepositoryLineResistant;
import com.roslin.mwicks.spring.narf.repository.RepositoryLineSusceptible;
import com.roslin.mwicks.spring.narf.repository.RepositoryLine;
import com.roslin.mwicks.spring.narf.repository.RepositoryLineReference;
import com.roslin.mwicks.spring.narf.repository.RepositoryStrain;
import com.roslin.mwicks.spring.narf.repository.RepositoryStrainReference;
import com.roslin.mwicks.spring.narf.repository.RepositoryStrainUse;
import com.roslin.mwicks.spring.narf.repository.RepositoryOrganism;
import com.roslin.mwicks.spring.narf.repository.RepositoryBirdOrderLineSex;
import com.roslin.mwicks.spring.narf.repository.RepositoryBirdOrderLineDateFormat;
import com.roslin.mwicks.spring.narf.repository.RepositoryEggOrderLineFertilised;
import com.roslin.mwicks.spring.narf.repository.RepositoryEmbryoOrderLineIncubation;
import com.roslin.mwicks.spring.narf.repository.RepositoryOrderStatus;
import com.roslin.mwicks.spring.narf.repository.RepositoryOrderType;
import com.roslin.mwicks.spring.narf.repository.RepositoryOrderCollection;

import com.roslin.mwicks.utility.FileUtil;
import com.roslin.mwicks.utility.StringUtility;
import com.roslin.mwicks.utility.Wrapper;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	// Constants ----------------------------------------------------------------------------------
    private static final String MSGLEVEL = "*";
	private static final String STOP = "*";
	
	private static final char PAD_CHAR = ' ';

    @Autowired
    private DataSettings dataSettings;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RepositoryAntibody repositoryAntibody;
    @Autowired
    private RepositoryAntibodyReference repositoryAntibodyReference;
    @Autowired
    private RepositoryAntibodyReference repositoryantibodyreference;
    @Autowired
    private RepositoryAntibody repositoryantibody;
    @Autowired
    private RepositoryLineResistant repositorylineresistant;
    @Autowired
    private RepositoryLineSusceptible repositorylinesusceptible;
    @Autowired
    private RepositoryLineMhc repositorylinemhc;
    @Autowired
    private RepositoryLineResistant repositoryLineResistant;
    @Autowired
    private RepositoryLineSusceptible repositoryLineSusceptible;
    @Autowired
    private RepositoryLine repositoryLine;
    @Autowired
    private RepositoryLineReference repositoryLineReference;
    @Autowired
    private RepositoryLineReference repositorylinereference;
    @Autowired
    private RepositoryLine repositoryline;
    @Autowired
    private RepositoryStrain repositoryStrain;
    @Autowired
    private RepositoryStrainReference repositoryStrainReference;
    @Autowired
    private RepositoryStrainUse repositoryStrainUse;
    @Autowired
    private RepositoryStrainUse repositorystrainuse;
    @Autowired
    private RepositoryStrainReference repositorystrainreference;
    @Autowired
    private RepositoryStrain repositorystrain;
    @Autowired
    private RepositoryBirdOrderLineSex repositorylinesex;
    @Autowired
    private RepositoryBirdOrderLineDateFormat repositorybirdorderlinedateformat;
    @Autowired
    private RepositoryEggOrderLineFertilised repositoryeggorderlinefertilised;
    @Autowired
    private RepositoryEmbryoOrderLineIncubation repositoryeggorderlineincubation;
    @Autowired
    private RepositoryOrderStatus repositoryorderstatus;
    @Autowired
    private RepositoryOrderCollection repositoryordercollection;
    @Autowired
    private RepositoryOrderType repositoryordertype;
    @Autowired
    private RepositoryOrganism repositoryOrganism;
    @Autowired
    private RepositoryAvian repositoryAvian;

    // API

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
    	
        if (dataSettings.isLoadFalse()) {
        	
            return;
        }

        // == create initial privileges
        final Privilege publicPrivilege = createPrivilegeIfNotFound("PUBLIC_PRIVILEGE");
        final Privilege customerPrivilege = createPrivilegeIfNotFound("CUSTOMER_PRIVILEGE");
        final Privilege editorPrivilege = createPrivilegeIfNotFound("EDITOR_PRIVILEGE");
        final Privilege adminPrivilege = createPrivilegeIfNotFound("ADMIN_PRIVILEGE");
        final Privilege superPrivilege = createPrivilegeIfNotFound("SUPER_PRIVILEGE");
        final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");

        // == create initial roles
        final List<Privilege> superPrivileges = Arrays.asList(passwordPrivilege, publicPrivilege, superPrivilege);
        final List<Privilege> adminPrivileges = Arrays.asList(passwordPrivilege, publicPrivilege, adminPrivilege);
        final List<Privilege> editorPrivileges = Arrays.asList(passwordPrivilege, publicPrivilege, editorPrivilege);
        final List<Privilege> customerPrivileges = Arrays.asList(passwordPrivilege, publicPrivilege, customerPrivilege);
        final List<Privilege> publicPrivileges = Arrays.asList(passwordPrivilege, publicPrivilege);
        /*
        final List<Privilege> superPrivileges = Arrays.asList(passwordPrivilege, publicPrivilege, customerPrivilege, editorPrivilege, adminPrivilege, superPrivilege);
        final List<Privilege> adminPrivileges = Arrays.asList(passwordPrivilege, publicPrivilege, customerPrivilege, editorPrivilege, adminPrivilege);
        final List<Privilege> editorPrivileges = Arrays.asList(passwordPrivilege, publicPrivilege, customerPrivilege, editorPrivilege);
        final List<Privilege> customerPrivileges = Arrays.asList(passwordPrivilege, publicPrivilege, customerPrivilege);
        final List<Privilege> publicPrivileges = Arrays.asList(passwordPrivilege, publicPrivilege);
         */
        
        createRoleIfNotFound("ROLE_SUPER", superPrivileges);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_EDITOR", editorPrivileges);
        createRoleIfNotFound("ROLE_CUSTOMER", customerPrivileges);
        createRoleIfNotFound("ROLE_PUBLIC", publicPrivileges);

        final Role superRole = roleRepository.findByName("ROLE_SUPER");
        final Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        final Role editorRole = roleRepository.findByName("ROLE_EDITOR");
        final Role customerRole = roleRepository.findByName("ROLE_CUSTOMER");
        final Role publicRole = roleRepository.findByName("ROLE_PUBLIC");
        
        final User userNull = new User();
        userNull.setFirstName("");
        userNull.setLastName("");
        userNull.setPassword("");
        userNull.setEmail("");
        userNull.setOrganisation("");
        userNull.setRoles(Arrays.asList(publicRole));
        userNull.setEnabled(true);
        userRepository.save(userNull);
        
        final User userPublic = new User();
        userPublic.setFirstName("Joe");
        userPublic.setLastName("Public");
        userPublic.setPassword(passwordEncoder.encode("public"));
        userPublic.setEmail("joe@public.com");
        userPublic.setOrganisation("The Rest of The World");
        userPublic.setRoles(Arrays.asList(publicRole));
        userPublic.setEnabled(true);
        userRepository.save(userPublic);
        
        final User userCustomer = new User();
        userCustomer.setFirstName("The");
        userCustomer.setLastName("Buyer");
        userCustomer.setPassword(passwordEncoder.encode("buyer"));
        userCustomer.setEmail("the@buyer.com");
        userCustomer.setOrganisation("The Rest of The World");
        userCustomer.setRoles(Arrays.asList(customerRole));
        userCustomer.setEnabled(true);
        userRepository.save(userCustomer);
        
        final User userEditor = new User();
        userEditor.setFirstName("The");
        userEditor.setLastName("Editor");
        userEditor.setPassword(passwordEncoder.encode("editor"));
        userEditor.setEmail("the@editor.com");
        userEditor.setOrganisation("Roslin Avian");
        userEditor.setRoles(Arrays.asList(editorRole));
        userEditor.setEnabled(true);
        userRepository.save(userEditor);
        
        final User userAdmin = new User();
        userAdmin.setFirstName("The");
        userAdmin.setLastName("Admin");
        userAdmin.setPassword(passwordEncoder.encode("admin"));
        userAdmin.setEmail("the@admin.com");
        userAdmin.setOrganisation("Roslin Avian");
        userAdmin.setRoles(Arrays.asList(adminRole));
        userAdmin.setEnabled(true);
        userRepository.save(userAdmin);

        final User userSuper = new User();
        userSuper.setFirstName("Super");
        userSuper.setLastName("User");
        userSuper.setPassword(passwordEncoder.encode("super"));
        userSuper.setEmail("super@user.com");
        userSuper.setOrganisation("Roslin Avian");
        userSuper.setRoles(Arrays.asList(superRole));
        userSuper.setEnabled(true);
        userRepository.save(userSuper);

        
        try {

        	/*
        	 * InsertAntibody
        	 */
    		String directory1 = "/Users/mwicks23/Dropbox/Work/Valentine/antibodies";
    		
            File dir1 = new File(directory1);

            if ( dir1.exists() ) {
            	
                File[] filesFile = FileUtil.listAllFilesInDirectory(directory1);
        		
        		int totalRecordCount = 0;
        		int totalFileCount = 0;
        		
                int antibodyTotal = 0;

        		for ( File file : filesFile ) {
        			
        			if ( !file.getName().equals(".DS_Store") ) {
        				
            			totalFileCount++;

                        List<Antibody> antibodyList = ConvertFiletoAntibodyList.run(file, totalRecordCount, MSGLEVEL, MSGLEVEL);

                        totalRecordCount = totalRecordCount +  antibodyList.size();
                        
                        antibodyTotal = antibodyTotal + totalRecordCount;
                        
            			Wrapper.printMessage(StringUtility.pad(totalFileCount, 4, PAD_CHAR) + " : Directory : " + directory1 + " : " + file.getName() + " : records : " + StringUtility.pad(antibodyList.size(), 8, PAD_CHAR), MSGLEVEL, MSGLEVEL);

            	        Iterator<Antibody> iteratorAntibody = antibodyList.iterator();
            	        
            	     	while (iteratorAntibody.hasNext()) {
            	    		
            	     		Antibody antibody = iteratorAntibody.next();

            	    		repositoryAntibody.save(antibody);
            	     	}
        			}
        		}
        		
        		Wrapper.printMessage(StringUtility.pad(antibodyTotal, 8, PAD_CHAR) + " Records inserted into ncs_antibody", MSGLEVEL, MSGLEVEL);

    			Wrapper.printMessage(StringUtility.pad(totalRecordCount, 8, PAD_CHAR) + " Records inserted from " + totalFileCount + " files in Directory " + directory1, MSGLEVEL, MSGLEVEL);
            }
            else {
            	
                Wrapper.printMessage("Directory " + directory1 + " DOES NOT exist!!!", MSGLEVEL, MSGLEVEL);
            }
            
            /*
             * InsertAntibodyReference
             */
    		String directory2 = "/Users/mwicks23/Dropbox/Work/Valentine/antibody_references";
    		
            File dir2 = new File(directory2);

            if ( dir2.exists() ) {
            	
                File[] filesFile = FileUtil.listAllFilesInDirectory(directory2);
        		
        		int totalRecordCount = 0;
        		int totalFileCount = 0;
        		
                int antibodyreferenceTotal = 0;

        		for ( File file : filesFile ) {
        			
        			if ( !file.getName().equals(".DS_Store") ) {
        				
            			totalFileCount++;

                        List<AntibodyReference> antibodyreferenceList = ConvertFiletoAntibodyReferenceList.run(file, totalRecordCount, MSGLEVEL, MSGLEVEL);

                        totalRecordCount = totalRecordCount +  antibodyreferenceList.size();
                        
                        antibodyreferenceTotal = antibodyreferenceTotal + totalRecordCount;
                        
            			Wrapper.printMessage(StringUtility.pad(totalFileCount, 4, PAD_CHAR) + " : Directory : " + directory2 + " : " + file.getName() + " : records : " + StringUtility.pad(antibodyreferenceList.size(), 8, PAD_CHAR), MSGLEVEL, MSGLEVEL);

            	        Iterator<AntibodyReference> iteratorAntibodyReference = antibodyreferenceList.iterator();
            	        
            	     	while (iteratorAntibodyReference.hasNext()) {
            	    		
            	     		AntibodyReference antibodyreference = iteratorAntibodyReference.next();

            	     		repositoryAntibodyReference.save(antibodyreference);
            	     	}
        			}
        		}
        		
        		Wrapper.printMessage(StringUtility.pad(antibodyreferenceTotal, 8, PAD_CHAR) + " Records inserted into ncs_antibody_reference", MSGLEVEL, MSGLEVEL);

    			Wrapper.printMessage(StringUtility.pad(totalRecordCount, 8, PAD_CHAR) + " Records inserted from " + totalFileCount + " files in Directory " + directory2, MSGLEVEL, MSGLEVEL);
            }
            else {
            	
                Wrapper.printMessage("Directory " + directory2 + " DOES NOT exist!!!", MSGLEVEL, MSGLEVEL);
            }

            /*
             * InsertAntibodyRelationships
             */
            AntibodyReference antibodyreference1 = repositoryantibodyreference.findByOid( (long) 41 );
            AntibodyReference antibodyreference2 = repositoryantibodyreference.findByOid( (long) 42 );
            AntibodyReference antibodyreference3 = repositoryantibodyreference.findByOid( (long) 43 );
            AntibodyReference antibodyreference4 = repositoryantibodyreference.findByOid( (long) 44 );

            Antibody antibody5 = repositoryantibody.findByOid( (long) 5 );
            antibody5.addAntibodyReference(antibodyreference4);
        	repositoryantibody.save(antibody5);

            Antibody antibody10 = repositoryantibody.findByOid( (long) 10 );
            antibody10.addAntibodyReference(antibodyreference3);
        	repositoryantibody.save(antibody10);

            Antibody antibody17 = repositoryantibody.findByOid( (long) 17 );
            antibody17.addAntibodyReference(antibodyreference1);
        	repositoryantibody.save(antibody17);

            Antibody antibody18 = repositoryantibody.findByOid( (long) 18 );
            antibody18.addAntibodyReference(antibodyreference1);
        	repositoryantibody.save(antibody18);

            Antibody antibody19 = repositoryantibody.findByOid( (long) 19 );
            antibody19.addAntibodyReference(antibodyreference1);
        	repositoryantibody.save(antibody19);

            Antibody antibody40 = repositoryantibody.findByOid( (long) 40 );
            antibody40.addAntibodyReference(antibodyreference2);
        	repositoryantibody.save(antibody40);

        	/*
            Antibody antibody5 = repositoryantibody.findByOid( (long) 1 );
            antibody5.addAntibodyReference(antibodyreference4);
        	repositoryantibody.save(antibody5);

            Antibody antibody10 = repositoryantibody.findByOid( (long) 2 );
            antibody10.addAntibodyReference(antibodyreference3);
        	repositoryantibody.save(antibody10);

            Antibody antibody17 = repositoryantibody.findByOid( (long) 3 );
            antibody17.addAntibodyReference(antibodyreference1);
        	repositoryantibody.save(antibody17);

            Antibody antibody18 = repositoryantibody.findByOid( (long) 4 );
            antibody18.addAntibodyReference(antibodyreference1);
        	repositoryantibody.save(antibody18);

            Antibody antibody19 = repositoryantibody.findByOid( (long) 5 );
            antibody19.addAntibodyReference(antibodyreference1);
        	repositoryantibody.save(antibody19);

            Antibody antibody40 = repositoryantibody.findByOid( (long) 6 );
            antibody40.addAntibodyReference(antibodyreference2);
        	repositoryantibody.save(antibody40);
        	*/

        	/*
        	 * InsertLineStatic
        	 */
            List<LineMhc> linemhcList = new ArrayList<LineMhc>(); 

            LineMhc linemhc1 = new LineMhc( (long) 1, "" );
            LineMhc linemhc2 = new LineMhc( (long) 2, "B<sup>2</sup>" );
            LineMhc linemhc3 = new LineMhc( (long) 3, "B<sup>4</sup>/B<sup>4</sup>, B<sup>12</sup>/B<sup>12</sup>, B<sup>4</sup>/B<sup>12</sup>" );
            LineMhc linemhc4 = new LineMhc( (long) 4, "B<sup>14</sup>" );
            LineMhc linemhc5 = new LineMhc( (long) 5, "B<sup>15</sup>" );
            LineMhc linemhc6 = new LineMhc( (long) 6, "B<sup>19</sup>" );
            LineMhc linemhc7 = new LineMhc( (long) 7, "B<sup>21</sup>" );
            LineMhc linemhc8 = new LineMhc( (long) 8, "B<sup>132, 133, 134, 135</sup>" );
            LineMhc linemhc9 = new LineMhc( (long) 9, "B<sup>142, 143, 144</sup>" );

            linemhcList.add(linemhc1);
            linemhcList.add(linemhc2);
            linemhcList.add(linemhc3);
            linemhcList.add(linemhc4);
            linemhcList.add(linemhc5);
            linemhcList.add(linemhc6);
            linemhcList.add(linemhc7);
            linemhcList.add(linemhc8);
            linemhcList.add(linemhc9);
            
	        Iterator<LineMhc> iteratorLineMhc = linemhcList.iterator();
	        
	     	while (iteratorLineMhc.hasNext()) {
	    		
	     		LineMhc linemhc = iteratorLineMhc.next();

	     		repositorylinemhc.save(linemhc);
	     	}

	     	
            List<OrderStatus> orderstatusList = new ArrayList<OrderStatus>(); 

            OrderStatus orderstatus1 = new OrderStatus( (long) 1, "New" );
            OrderStatus orderstatus2 = new OrderStatus( (long) 2, "Pending - Greenwood" );
            OrderStatus orderstatus3 = new OrderStatus( (long) 3, "Pending - Bumstead" );
            OrderStatus orderstatus4 = new OrderStatus( (long) 4, "Closed" );
            OrderStatus orderstatus5 = new OrderStatus( (long) 5, "Cancelled" );

            orderstatusList.add(orderstatus1);
            orderstatusList.add(orderstatus2);
            orderstatusList.add(orderstatus3);
            orderstatusList.add(orderstatus4);
            orderstatusList.add(orderstatus5);
            
	        Iterator<OrderStatus> iteratorOrderStatus = orderstatusList.iterator();
	        
	     	while (iteratorOrderStatus.hasNext()) {
	    		
	     		OrderStatus orderstatus = iteratorOrderStatus.next();

	     		repositoryorderstatus.save(orderstatus);
	     	}

            List<OrderCollection> ordercollectionList = new ArrayList<OrderCollection>(); 

            OrderCollection ordercollection1 = new OrderCollection( (long) 1, "Unknown" );
            OrderCollection ordercollection2 = new OrderCollection( (long) 2, "NARF to Courier" );
            OrderCollection ordercollection3 = new OrderCollection( (long) 3, "Customer to Pickup in Person" );
            OrderCollection ordercollection4 = new OrderCollection( (long) 4, "Customer to Courier" );

            ordercollectionList.add(ordercollection1);
            ordercollectionList.add(ordercollection2);
            ordercollectionList.add(ordercollection3);
            ordercollectionList.add(ordercollection4);
            
	        Iterator<OrderCollection> iteratorOrderCollection = ordercollectionList.iterator();
	        
	     	while (iteratorOrderCollection.hasNext()) {
	    		
	     		OrderCollection ordercollection = iteratorOrderCollection.next();

	     		repositoryordercollection.save(ordercollection);
	     	}

            List<OrderType> ordertypeList = new ArrayList<OrderType>(); 

            OrderType ordertype1 = new OrderType( (long) 1, "Bird" );
            OrderType ordertype2 = new OrderType( (long) 2, "Egg" );
            OrderType ordertype3 = new OrderType( (long) 3, "Embryo" );

            ordertypeList.add(ordertype1);
            ordertypeList.add(ordertype2);
            ordertypeList.add(ordertype3);
            
	        Iterator<OrderType> iteratorOrderType = ordertypeList.iterator();
	        
	     	while (iteratorOrderType.hasNext()) {
	    		
	     		OrderType ordertype = iteratorOrderType.next();

	     		repositoryordertype.save(ordertype);
	     	}


	     	List<LineResistant> lineresistantList = new ArrayList<LineResistant>(); 

            LineResistant lineresistant1 = new LineResistant( (long) 1, "" );
            LineResistant lineresistant2 = new LineResistant( (long) 2, "MDV, LLV tumour development" );
            LineResistant lineresistant3 = new LineResistant( (long) 3, "MDV" );
            LineResistant lineresistant4 = new LineResistant( (long) 4, "LLV subgroups B and D (mostly), segregating for A and E. Hatched birds resistant to Eimeria sp." );

            lineresistantList.add(lineresistant1);
            lineresistantList.add(lineresistant2);
            lineresistantList.add(lineresistant3);
            lineresistantList.add(lineresistant4);

	        Iterator<LineResistant> iteratorLineResistant = lineresistantList.iterator();
	        
	     	while (iteratorLineResistant.hasNext()) {
	    		
	     		LineResistant lineresistant = iteratorLineResistant.next();

	     		repositorylineresistant.save(lineresistant);
	     	}

            List<LineSusceptible> linesusceptibleList = new ArrayList<LineSusceptible>(); 
            
            LineSusceptible linesusceptible1 = new LineSusceptible( (long) 1, "" );
            LineSusceptible linesusceptible2 = new LineSusceptible( (long) 2, "LLV subgroups A, B, C, D" );
            LineSusceptible linesusceptible3 = new LineSusceptible( (long) 3, "LLV subgroups A, B, D, E" );
            LineSusceptible linesusceptible4 = new LineSusceptible( (long) 4, "LLV subgroups A, C; segregating for B, D, E. Moderately susceptible to MDV" );
            LineSusceptible linesusceptible5 = new LineSusceptible( (long) 5, "LLV subgroups B, C, D" );
            LineSusceptible linesusceptible6 = new LineSusceptible( (long) 6, "MDV" );
            LineSusceptible linesusceptible7 = new LineSusceptible( (long) 7, "MDV, embryos susceptible to Eimeria sp." );
            LineSusceptible linesusceptible8 = new LineSusceptible( (long) 8, "LLV subgroups B, C, D (not A, E) and to Eimeria sp." );

            linesusceptibleList.add(linesusceptible1);
            linesusceptibleList.add(linesusceptible2);
            linesusceptibleList.add(linesusceptible3);
            linesusceptibleList.add(linesusceptible4);
            linesusceptibleList.add(linesusceptible5);
            linesusceptibleList.add(linesusceptible6);
            linesusceptibleList.add(linesusceptible7);
            linesusceptibleList.add(linesusceptible8);

	        Iterator<LineSusceptible> iteratorLineSusceptible = linesusceptibleList.iterator();
	        
	     	while (iteratorLineSusceptible.hasNext()) {
	    		
	     		LineSusceptible linesusceptible = iteratorLineSusceptible.next();

	     		repositorylinesusceptible.save(linesusceptible);
	     	}


            /*
             * InsertLine
             */
    		String directory3 = "/Users/mwicks23/Dropbox/Work/Valentine/lines";
    		
            File dir3 = new File(directory3);

            if ( dir3.exists() ) {
            	
                File[] filesFile = FileUtil.listAllFilesInDirectory(directory3);
        		
        		int totalRecordCount = 0;
        		int totalFileCount = 0;
        		
                int lineTotal = 0;

        		for ( File file : filesFile ) {
        			
        			if ( !file.getName().equals(".DS_Store") ) {
        				
            			totalFileCount++;

                        List<Line> lineList = ConvertFiletoLineList.run(file, totalRecordCount, MSGLEVEL, MSGLEVEL, repositorylinemhc, repositoryLineResistant, repositoryLineSusceptible);

                        totalRecordCount = totalRecordCount +  lineList.size();
                        
                        lineTotal = lineTotal + totalRecordCount;
                        
            			Wrapper.printMessage(StringUtility.pad(totalFileCount, 4, PAD_CHAR) + " : Directory : " + directory3 + " : " + file.getName() + " : records : " + StringUtility.pad(lineList.size(), 8, PAD_CHAR), MSGLEVEL, MSGLEVEL);

            	        Iterator<Line> iteratorLine = lineList.iterator();
            	        
            	     	while (iteratorLine.hasNext()) {
            	    		
            	    		Line line = iteratorLine.next();

            	    		repositoryLine.save(line);
            	     	}
        			}
        		}
        		
        		Wrapper.printMessage(StringUtility.pad(lineTotal, 8, PAD_CHAR) + " Records inserted into ncs__line", MSGLEVEL, MSGLEVEL);

    			Wrapper.printMessage(StringUtility.pad(totalRecordCount, 8, PAD_CHAR) + " Records inserted from " + totalFileCount + " files in Directory " + directory3, MSGLEVEL, MSGLEVEL);
            }
            else {
            	
                Wrapper.printMessage("Directory " + directory3 + " DOES NOT exist!!!", MSGLEVEL, MSGLEVEL);
            }

            /*
             * InsertLineReference
             */
    		String directory4 = "/Users/mwicks23/Dropbox/Work/Valentine/line_references";
    		
            File dir4 = new File(directory4);

            if ( dir4.exists() ) {
            	
                File[] filesFile = FileUtil.listAllFilesInDirectory(directory4);
        		
        		int totalRecordCount = 0;
        		int totalFileCount = 0;
        		
                int linereferenceTotal = 0;

        		for ( File file : filesFile ) {
        			
        			if ( !file.getName().equals(".DS_Store") ) {
        				
            			totalFileCount++;

                        List<LineReference> linereferenceList = ConvertFiletoLineReferenceList.run(file, totalRecordCount, MSGLEVEL, MSGLEVEL);

                        totalRecordCount = totalRecordCount +  linereferenceList.size();
                        
                        linereferenceTotal = linereferenceTotal + totalRecordCount;
                        
            			Wrapper.printMessage(StringUtility.pad(totalFileCount, 4, PAD_CHAR) + " : Directory : " + directory4 + " : " + file.getName() + " : records : " + StringUtility.pad(linereferenceList.size(), 8, PAD_CHAR), MSGLEVEL, MSGLEVEL);

            	        Iterator<LineReference> iteratorLineReference = linereferenceList.iterator();
            	        
            	     	while (iteratorLineReference.hasNext()) {
            	    		
            	     		LineReference linereference = iteratorLineReference.next();

            	    		repositoryLineReference.save(linereference);
            	     	}
        			}
        		}
        		
        		Wrapper.printMessage(StringUtility.pad(linereferenceTotal, 8, PAD_CHAR) + " Records inserted into ncs__line_reference", MSGLEVEL, MSGLEVEL);

    			Wrapper.printMessage(StringUtility.pad(totalRecordCount, 8, PAD_CHAR) + " Records inserted from " + totalFileCount + " files in Directory " + directory4, MSGLEVEL, MSGLEVEL);
            }
            else {
            	
                Wrapper.printMessage("Directory " + directory4 + " DOES NOT exist!!!", MSGLEVEL, MSGLEVEL);
            }

            /*
             * InsertLineRelationships
             */
            LineReference linereference1 = repositorylinereference.findByOid( (long) 56 );
            LineReference linereference2 = repositorylinereference.findByOid( (long) 57 );
            LineReference linereference3 = repositorylinereference.findByOid( (long) 58 );

            Line line1 = repositoryline.findByOid( (long) 46 );
            Line line2 = repositoryline.findByOid( (long) 47 );
            Line line4 = repositoryline.findByOid( (long) 49 );
            Line line5 = repositoryline.findByOid( (long) 50 );
            Line line6 = repositoryline.findByOid( (long) 51 );
            Line line7 = repositoryline.findByOid( (long) 52 );

            line1.addLineReference(linereference1);
            line1.addLineReference(linereference2);
            line1.addLineReference(linereference3);
            
            line2.addLineReference(linereference1);
            line2.addLineReference(linereference2);
            line2.addLineReference(linereference3);
            
            line4.addLineReference(linereference1);
            line4.addLineReference(linereference2);

            line5.addLineReference(linereference1);
            line5.addLineReference(linereference2);

            line6.addLineReference(linereference1);
            line6.addLineReference(linereference2);

            line7.addLineReference(linereference1);
            line7.addLineReference(linereference2);

        	repositoryline.save(line1);
        	repositoryline.save(line2);
        	repositoryline.save(line4);
        	repositoryline.save(line5);
        	repositoryline.save(line6);
        	repositoryline.save(line7);

        	/*
        	 * InsertStrain
        	 */
    		String directory5 = "/Users/mwicks23/Dropbox/Work/Valentine/strains";
    		
            File dir5 = new File(directory5);

            if ( dir5.exists() ) {
            	
                File[] filesFile = FileUtil.listAllFilesInDirectory(directory5);
        		
        		int totalRecordCount = 0;
        		int totalFileCount = 0;
        		
                int lineTotal = 0;

        		for ( File file : filesFile ) {
        			
        			if ( !file.getName().equals(".DS_Store") ) {
        				
            			totalFileCount++;

                        List<Strain> strainList = ConvertFiletoStrainList.run(file, totalRecordCount, MSGLEVEL, MSGLEVEL);

                        totalRecordCount = totalRecordCount +  strainList.size();
                        
                        lineTotal = lineTotal + totalRecordCount;
                        
            			Wrapper.printMessage(StringUtility.pad(totalFileCount, 4, PAD_CHAR) + " : Directory : " + directory5 + " : " + file.getName() + " : records : " + StringUtility.pad(strainList.size(), 8, PAD_CHAR), MSGLEVEL, MSGLEVEL);

            	        Iterator<Strain> iteratorStrain = strainList.iterator();
            	        
            	     	while (iteratorStrain.hasNext()) {
            	    		
            	     		Strain strain = iteratorStrain.next();

            	     		repositoryStrain.save(strain);
            	     	}
        			}
        		}
        		
        		Wrapper.printMessage(StringUtility.pad(lineTotal, 8, PAD_CHAR) + " Records inserted into ncs__line", MSGLEVEL, MSGLEVEL);

    			Wrapper.printMessage(StringUtility.pad(totalRecordCount, 8, PAD_CHAR) + " Records inserted from " + totalFileCount + " files in Directory " + directory5, MSGLEVEL, MSGLEVEL);
            }
            else {
            	
                Wrapper.printMessage("Directory " + directory5 + " DOES NOT exist!!!", MSGLEVEL, MSGLEVEL);
            }
            
            /*
             * InsertStrainReference
             */
    		String directory6 = "/Users/mwicks23/Dropbox/Work/Valentine/strain_references";
    		
            File dir6 = new File(directory6);

            if ( dir6.exists() ) {
            	
                File[] filesFile = FileUtil.listAllFilesInDirectory(directory6);
        		
        		int totalRecordCount = 0;
        		int totalFileCount = 0;
        		
                int linereferenceTotal = 0;

        		for ( File file : filesFile ) {
        			
        			if ( !file.getName().equals(".DS_Store") ) {
        				
            			totalFileCount++;

                        List<StrainReference> strainreferenceList = ConvertFiletoStrainReferenceList.run(file, totalRecordCount, MSGLEVEL, MSGLEVEL);

                        totalRecordCount = totalRecordCount +  strainreferenceList.size();
                        
                        linereferenceTotal = linereferenceTotal + totalRecordCount;
                        
            			Wrapper.printMessage(StringUtility.pad(totalFileCount, 4, PAD_CHAR) + " : Directory : " + directory6 + " : " + file.getName() + " : records : " + StringUtility.pad(strainreferenceList.size(), 8, PAD_CHAR), MSGLEVEL, MSGLEVEL);

            	        Iterator<StrainReference> iteratorStrainReference = strainreferenceList.iterator();
            	        
            	     	while (iteratorStrainReference.hasNext()) {
            	    		
            	     		StrainReference strainreference = iteratorStrainReference.next();

            	     		repositoryStrainReference.save(strainreference);
            	     	}
        			}
        		}
        		
        		Wrapper.printMessage(StringUtility.pad(linereferenceTotal, 8, PAD_CHAR) + " Records inserted into ncs__line_reference", MSGLEVEL, MSGLEVEL);

    			Wrapper.printMessage(StringUtility.pad(totalRecordCount, 8, PAD_CHAR) + " Records inserted from " + totalFileCount + " files in Directory " + directory6, MSGLEVEL, MSGLEVEL);
            }
            else {
            	
                Wrapper.printMessage("Directory " + directory6 + " DOES NOT exist!!!", MSGLEVEL, MSGLEVEL);
            }

            /*
             * InsertStrainUse
             */
    		String directory7 = "/Users/mwicks23/Dropbox/Work/Valentine/strain_uses";
    		
            File dir7 = new File(directory7);

            if ( dir7.exists() ) {
            	
                File[] filesFile = FileUtil.listAllFilesInDirectory(directory7);
        		
        		int totalRecordCount = 0;
        		int totalFileCount = 0;
        		
                int strainuseTotal = 0;

        		for ( File file : filesFile ) {
        			
        			if ( !file.getName().equals(".DS_Store") ) {
        				
            			totalFileCount++;

                        List<StrainUse> strainuseList = ConvertFiletoStrainUseList.run(file, totalRecordCount, MSGLEVEL, MSGLEVEL);

                        totalRecordCount = totalRecordCount +  strainuseList.size();
                        
                        strainuseTotal = strainuseTotal + totalRecordCount;
                        
            			Wrapper.printMessage(StringUtility.pad(totalFileCount, 4, PAD_CHAR) + " : Directory : " + directory7 + " : " + file.getName() + " : records : " + StringUtility.pad(strainuseList.size(), 8, PAD_CHAR), MSGLEVEL, MSGLEVEL);

            	        Iterator<StrainUse> iteratorStrainUse = strainuseList.iterator();
            	        
            	     	while (iteratorStrainUse.hasNext()) {
            	    		
            	     		StrainUse strainuse = iteratorStrainUse.next();

            	     		repositoryStrainUse.save(strainuse);
            	     	}
        			}
        		}
        		
        		Wrapper.printMessage(StringUtility.pad(strainuseTotal, 8, PAD_CHAR) + " Records inserted into ncs__strain_use", MSGLEVEL, MSGLEVEL);

    			Wrapper.printMessage(StringUtility.pad(totalRecordCount, 8, PAD_CHAR) + " Records inserted from " + totalFileCount + " files in Directory " + directory7, MSGLEVEL, MSGLEVEL);
            }
            else {
            	
                Wrapper.printMessage("Directory " + directory7 + " DOES NOT exist!!!", MSGLEVEL, MSGLEVEL);
            }

            /*
             * Insert Organisms
             */
    		String directory8 = "/Users/mwicks23/Dropbox/Work/Valentine/organisms";
    		
            File dir8 = new File(directory8);

            if ( dir8.exists() ) {
            	
                File[] filesFile = FileUtil.listAllFilesInDirectory(directory8);
        		
        		int totalRecordCount = 0;
        		int totalFileCount = 0;
        		
                int organismTotal = 0;

        		for ( File file : filesFile ) {
        			
        			if ( !file.getName().equals(".DS_Store") ) {
        				
            			totalFileCount++;

                        List<Organism> organismList = ConvertFiletoOrganismList.run(file, totalRecordCount, MSGLEVEL, MSGLEVEL);

                        totalRecordCount = totalRecordCount +  organismList.size();
                        
                        organismTotal = organismTotal + totalRecordCount;
                        
            			Wrapper.printMessage(StringUtility.pad(totalFileCount, 4, PAD_CHAR) + " : Directory : " + directory8 + " : " + file.getName() + " : records : " + StringUtility.pad(organismList.size(), 8, PAD_CHAR), MSGLEVEL, MSGLEVEL);

            	        Iterator<Organism> iteratorOrganism = organismList.iterator();
            	        
            	     	while (iteratorOrganism.hasNext()) {
            	    		
            	     		Organism organism = iteratorOrganism.next();

            	     		repositoryOrganism.save(organism);
            	     	}
        			}
        		}
        		
        		Wrapper.printMessage(StringUtility.pad(organismTotal, 8, PAD_CHAR) + " Records inserted into ncs_organism", MSGLEVEL, MSGLEVEL);

    			Wrapper.printMessage(StringUtility.pad(totalRecordCount, 8, PAD_CHAR) + " Records inserted from " + totalFileCount + " files in Directory " + directory7, MSGLEVEL, MSGLEVEL);
            }
            else {
            	
                Wrapper.printMessage("Directory " + directory8 + " DOES NOT exist!!!", MSGLEVEL, MSGLEVEL);
            }

        	/*
        	 * Insert Other Avian
        	 */
    		String directory9 = "/Users/mwicks23/Dropbox/Work/Valentine/organisms_other";
    		
            File dir9 = new File(directory9);

            if ( dir9.exists() ) {
            	
                File[] filesFile = FileUtil.listAllFilesInDirectory(directory9);
        		
        		int totalRecordCount = 0;
        		int totalFileCount = 0;
        		
                int lineTotal = 0;

        		for ( File file : filesFile ) {
        			
        			if ( !file.getName().equals(".DS_Store") ) {
        				
            			totalFileCount++;

                        List<Avian> avianList = ConvertFiletoAvianList.run(file, totalRecordCount, MSGLEVEL, MSGLEVEL);

                        totalRecordCount = totalRecordCount +  avianList.size();
                        
                        lineTotal = lineTotal + totalRecordCount;
                        
            			Wrapper.printMessage(StringUtility.pad(totalFileCount, 4, PAD_CHAR) + " : Directory : " + directory9 + " : " + file.getName() + " : records : " + StringUtility.pad(avianList.size(), 8, PAD_CHAR), MSGLEVEL, MSGLEVEL);

            	        Iterator<Avian> iteratorAvian = avianList.iterator();
            	        
            	     	while (iteratorAvian.hasNext()) {
            	    		
            	     		Avian avian = iteratorAvian.next();

            	     		repositoryAvian.save(avian);
            	     	}
        			}
        		}
        		
        		Wrapper.printMessage(StringUtility.pad(lineTotal, 8, PAD_CHAR) + " Records inserted into ncs_avian", MSGLEVEL, MSGLEVEL);

    			Wrapper.printMessage(StringUtility.pad(totalRecordCount, 8, PAD_CHAR) + " Records inserted from " + totalFileCount + " files in Directory " + directory9, MSGLEVEL, MSGLEVEL);
            }
            else {
            	
                Wrapper.printMessage("Directory " + directory9 + " DOES NOT exist!!!", MSGLEVEL, MSGLEVEL);
            }

            Line line45 = repositoryline.findByOid( (long) 45 );
            Line line46 = repositoryline.findByOid( (long) 46 );
            Line line47 = repositoryline.findByOid( (long) 47 );
            Line line48 = repositoryline.findByOid( (long) 48 );
            Line line49 = repositoryline.findByOid( (long) 49 );
            Line line50 = repositoryline.findByOid( (long) 50 );
            Line line51 = repositoryline.findByOid( (long) 51 );
            Line line52 = repositoryline.findByOid( (long) 52 );
            Line line53 = repositoryline.findByOid( (long) 53 );
            Line line54 = repositoryline.findByOid( (long) 54 );
            Line line55 = repositoryline.findByOid( (long) 55 );

            Strain strain59 = repositorystrain.findByOid( (long) 59 );
            Strain strain60 = repositorystrain.findByOid( (long) 60 );
            Strain strain61 = repositorystrain.findByOid( (long) 61 );
            Strain strain62 = repositorystrain.findByOid( (long) 62 );

            Avian avian93 = repositoryAvian.findByOid( (long) 93 );

            Organism organism77 = repositoryOrganism.findByOid( (long) 77 );
            Organism organism78 = repositoryOrganism.findByOid( (long) 78 );
            Organism organism79 = repositoryOrganism.findByOid( (long) 79 );
            Organism organism80 = repositoryOrganism.findByOid( (long) 80 );
            Organism organism81 = repositoryOrganism.findByOid( (long) 81 );
            Organism organism82 = repositoryOrganism.findByOid( (long) 82 );
            Organism organism83 = repositoryOrganism.findByOid( (long) 83 );
            Organism organism84 = repositoryOrganism.findByOid( (long) 84 );
            Organism organism85 = repositoryOrganism.findByOid( (long) 85 );
            Organism organism86 = repositoryOrganism.findByOid( (long) 86 );
            Organism organism87 = repositoryOrganism.findByOid( (long) 87 );

            Organism organism88 = repositoryOrganism.findByOid( (long) 88 );
            Organism organism89 = repositoryOrganism.findByOid( (long) 89 );
            Organism organism90 = repositoryOrganism.findByOid( (long) 90 );
            Organism organism91 = repositoryOrganism.findByOid( (long) 91 );

            Organism organism92 = repositoryOrganism.findByOid( (long) 92 );

            organism77.addLine( line45 );
            organism78.addLine( line46 );
            organism79.addLine( line47 );
            organism80.addLine( line48 );
            organism81.addLine( line49 );
            organism82.addLine( line50 );
            organism83.addLine( line51 );
            organism84.addLine( line52 );
            organism85.addLine( line53 );
            organism86.addLine( line54 );
            organism87.addLine( line55 );

            organism88.addStrain( strain59 );
            organism89.addStrain( strain60 );
            organism90.addStrain( strain61 );
            organism91.addStrain( strain62 );

            organism92.addAvian( avian93 );

            repositoryOrganism.save( organism77 );
            repositoryOrganism.save( organism78 );
            repositoryOrganism.save( organism79 );
            repositoryOrganism.save( organism80 );
            repositoryOrganism.save( organism81 );
            repositoryOrganism.save( organism82 );
            repositoryOrganism.save( organism83 );
            repositoryOrganism.save( organism84 );
            repositoryOrganism.save( organism85 );
            repositoryOrganism.save( organism86 );
            repositoryOrganism.save( organism87 );

            repositoryOrganism.save( organism88 );
            repositoryOrganism.save( organism89 );
            repositoryOrganism.save( organism90 );
            repositoryOrganism.save( organism91 );

            repositoryOrganism.save( organism92 );

            /*
             * InsertStrainRelationships
             */
            StrainUse strainuse1 = repositorystrainuse.findByOid( (long) 67 );
            StrainUse strainuse2 = repositorystrainuse.findByOid( (long) 68 );
            StrainUse strainuse4 = repositorystrainuse.findByOid( (long) 70 );
            StrainUse strainuse7 = repositorystrainuse.findByOid( (long) 73 );
            StrainUse strainuse8 = repositorystrainuse.findByOid( (long) 74 );
            StrainUse strainuse10 = repositorystrainuse.findByOid( (long) 76 );

            StrainReference strainreference1 = repositorystrainreference.findByOid( (long) 63 );
            StrainReference strainreference2 = repositorystrainreference.findByOid( (long) 64 );
            StrainReference strainreference3 = repositorystrainreference.findByOid( (long) 65 );
            StrainReference strainreference4 = repositorystrainreference.findByOid( (long) 66 );

            Strain strain1 = repositorystrain.findByOid( (long) 59 );
            Strain strain2 = repositorystrain.findByOid( (long) 60 );
            Strain strain3 = repositorystrain.findByOid( (long) 61 );

            strain1.addStrainReference(strainreference1);
            strain1.addStrainReference(strainreference2);
            strain1.addStrainReference(strainreference3);
            strain1.addStrainUse(strainuse1);
            strain1.addStrainUse(strainuse2);
            strain1.addStrainUse(strainuse10);

            strain2.addStrainUse(strainuse7);
            strain2.addStrainUse(strainuse10);

            strain3.addStrainReference(strainreference4);
            strain3.addStrainUse(strainuse4);
            strain3.addStrainUse(strainuse8);

        	repositorystrain.save(strain1);
        	repositorystrain.save(strain2);
        	repositorystrain.save(strain3);
        	
            List<BirdOrderLineSex> linesexList = new ArrayList<BirdOrderLineSex>(); 

            BirdOrderLineSex linesex1 = new BirdOrderLineSex( (long) 1, "Male" );
            BirdOrderLineSex linesex2 = new BirdOrderLineSex( (long) 2, "Female" );

            linesexList.add(linesex1);
            linesexList.add(linesex2);
            
	        Iterator<BirdOrderLineSex> iteratorBirdOrderLineSex = linesexList.iterator();
	        
	     	while (iteratorBirdOrderLineSex.hasNext()) {
	    		
	     		BirdOrderLineSex linesex = iteratorBirdOrderLineSex.next();

	     		repositorylinesex.save(linesex);
	     	}

            List<BirdOrderLineDateFormat> birdorderlinedateformatList = new ArrayList<BirdOrderLineDateFormat>(); 

            BirdOrderLineDateFormat birdorderlinedateformat1 = new BirdOrderLineDateFormat( (long) 1, "Years" );
            BirdOrderLineDateFormat birdorderlinedateformat2 = new BirdOrderLineDateFormat( (long) 2, "Months" );
            BirdOrderLineDateFormat birdorderlinedateformat3 = new BirdOrderLineDateFormat( (long) 3, "Days" );

            birdorderlinedateformatList.add(birdorderlinedateformat1);
            birdorderlinedateformatList.add(birdorderlinedateformat2);
            birdorderlinedateformatList.add(birdorderlinedateformat3);
            
	        Iterator<BirdOrderLineDateFormat> iteratorBirdOrderLineDateFormat = birdorderlinedateformatList.iterator();
	        
	     	while (iteratorBirdOrderLineDateFormat.hasNext()) {
	    		
	     		BirdOrderLineDateFormat birdorderlinedateformat = iteratorBirdOrderLineDateFormat.next();

	     		repositorybirdorderlinedateformat.save(birdorderlinedateformat);
	     	}
            
            List<EggOrderLineFertilised> eggorderlinefertilisedList = new ArrayList<EggOrderLineFertilised>(); 

            EggOrderLineFertilised eggorderlinefertilised1 = new EggOrderLineFertilised( (long) 1, "Yes" );
            EggOrderLineFertilised eggorderlinefertilised2 = new EggOrderLineFertilised( (long) 2, "No" );

            eggorderlinefertilisedList.add(eggorderlinefertilised1);
            eggorderlinefertilisedList.add(eggorderlinefertilised2);
            
	        Iterator<EggOrderLineFertilised> iteratorEggOrderLineFertilised = eggorderlinefertilisedList.iterator();
	        
	     	while (iteratorEggOrderLineFertilised.hasNext()) {
	    		
	     		EggOrderLineFertilised eggorderlinefertilised = iteratorEggOrderLineFertilised.next();

	     		repositoryeggorderlinefertilised.save(eggorderlinefertilised);
	     	}
            
            List<EmbryoOrderLineIncubation> eggorderlineincubationList = new ArrayList<EmbryoOrderLineIncubation>(); 

            EmbryoOrderLineIncubation eggorderlineincubation1 = new EmbryoOrderLineIncubation( (long) 1, "0.5");
            EmbryoOrderLineIncubation eggorderlineincubation2 = new EmbryoOrderLineIncubation( (long) 2, "1.0");
            EmbryoOrderLineIncubation eggorderlineincubation3 = new EmbryoOrderLineIncubation( (long) 3, "1.5");
            EmbryoOrderLineIncubation eggorderlineincubation4 = new EmbryoOrderLineIncubation( (long) 4, "2.0");
            EmbryoOrderLineIncubation eggorderlineincubation5 = new EmbryoOrderLineIncubation( (long) 5, "2.5");
            EmbryoOrderLineIncubation eggorderlineincubation6 = new EmbryoOrderLineIncubation( (long) 6, "3.0");
            EmbryoOrderLineIncubation eggorderlineincubation7 = new EmbryoOrderLineIncubation( (long) 7, "3.5");
            EmbryoOrderLineIncubation eggorderlineincubation8 = new EmbryoOrderLineIncubation( (long) 8, "4.0");
            EmbryoOrderLineIncubation eggorderlineincubation9 = new EmbryoOrderLineIncubation( (long) 9, "4.5");
            EmbryoOrderLineIncubation eggorderlineincubation10 = new EmbryoOrderLineIncubation( (long) 10, "5.0");
            EmbryoOrderLineIncubation eggorderlineincubation11 = new EmbryoOrderLineIncubation( (long) 11, "5.5");
            EmbryoOrderLineIncubation eggorderlineincubation12 = new EmbryoOrderLineIncubation( (long) 12, "6.0");
            EmbryoOrderLineIncubation eggorderlineincubation13 = new EmbryoOrderLineIncubation( (long) 13, "6.5");
            EmbryoOrderLineIncubation eggorderlineincubation14 = new EmbryoOrderLineIncubation( (long) 14, "7.0");
            EmbryoOrderLineIncubation eggorderlineincubation15 = new EmbryoOrderLineIncubation( (long) 15, "7.5");
            EmbryoOrderLineIncubation eggorderlineincubation16 = new EmbryoOrderLineIncubation( (long) 16, "8.0");
            EmbryoOrderLineIncubation eggorderlineincubation17 = new EmbryoOrderLineIncubation( (long) 17, "8.5");
            EmbryoOrderLineIncubation eggorderlineincubation18 = new EmbryoOrderLineIncubation( (long) 18, "9.0");
            EmbryoOrderLineIncubation eggorderlineincubation19 = new EmbryoOrderLineIncubation( (long) 19, "9.5");
            EmbryoOrderLineIncubation eggorderlineincubation20 = new EmbryoOrderLineIncubation( (long) 20, "10.0");
            EmbryoOrderLineIncubation eggorderlineincubation21 = new EmbryoOrderLineIncubation( (long) 21, "10.5");
            EmbryoOrderLineIncubation eggorderlineincubation22 = new EmbryoOrderLineIncubation( (long) 22, "11.0");
            EmbryoOrderLineIncubation eggorderlineincubation23 = new EmbryoOrderLineIncubation( (long) 23, "11.5");
            EmbryoOrderLineIncubation eggorderlineincubation24 = new EmbryoOrderLineIncubation( (long) 24, "12.0");
            EmbryoOrderLineIncubation eggorderlineincubation25 = new EmbryoOrderLineIncubation( (long) 25, "12.5");
            EmbryoOrderLineIncubation eggorderlineincubation26 = new EmbryoOrderLineIncubation( (long) 26, "13.0");
            EmbryoOrderLineIncubation eggorderlineincubation27 = new EmbryoOrderLineIncubation( (long) 27, "13.5");
            EmbryoOrderLineIncubation eggorderlineincubation28 = new EmbryoOrderLineIncubation( (long) 28, "14.0");
            EmbryoOrderLineIncubation eggorderlineincubation29 = new EmbryoOrderLineIncubation( (long) 29, "14.5");
            EmbryoOrderLineIncubation eggorderlineincubation30 = new EmbryoOrderLineIncubation( (long) 30, "15.0");
            EmbryoOrderLineIncubation eggorderlineincubation31 = new EmbryoOrderLineIncubation( (long) 31, "15.5");
            EmbryoOrderLineIncubation eggorderlineincubation32 = new EmbryoOrderLineIncubation( (long) 32, "16.0");
            EmbryoOrderLineIncubation eggorderlineincubation33 = new EmbryoOrderLineIncubation( (long) 33, "16.5");
            EmbryoOrderLineIncubation eggorderlineincubation34 = new EmbryoOrderLineIncubation( (long) 34, "17.0");
            EmbryoOrderLineIncubation eggorderlineincubation35 = new EmbryoOrderLineIncubation( (long) 35, "17.5");
            EmbryoOrderLineIncubation eggorderlineincubation36 = new EmbryoOrderLineIncubation( (long) 36, "18.0");
            EmbryoOrderLineIncubation eggorderlineincubation37 = new EmbryoOrderLineIncubation( (long) 37, "18.5");
            EmbryoOrderLineIncubation eggorderlineincubation38 = new EmbryoOrderLineIncubation( (long) 38, "19.0");
            EmbryoOrderLineIncubation eggorderlineincubation39 = new EmbryoOrderLineIncubation( (long) 39, "19.5");
            EmbryoOrderLineIncubation eggorderlineincubation40 = new EmbryoOrderLineIncubation( (long) 40, "20.0");
            EmbryoOrderLineIncubation eggorderlineincubation41 = new EmbryoOrderLineIncubation( (long) 41, "20.5");
            EmbryoOrderLineIncubation eggorderlineincubation42 = new EmbryoOrderLineIncubation( (long) 42, "21.0");

            eggorderlineincubationList.add(eggorderlineincubation1);
            eggorderlineincubationList.add(eggorderlineincubation2);
            eggorderlineincubationList.add(eggorderlineincubation3);
            eggorderlineincubationList.add(eggorderlineincubation4);
            eggorderlineincubationList.add(eggorderlineincubation5);
            eggorderlineincubationList.add(eggorderlineincubation6);
            eggorderlineincubationList.add(eggorderlineincubation7);
            eggorderlineincubationList.add(eggorderlineincubation8);
            eggorderlineincubationList.add(eggorderlineincubation9);
            eggorderlineincubationList.add(eggorderlineincubation10);
            eggorderlineincubationList.add(eggorderlineincubation11);
            eggorderlineincubationList.add(eggorderlineincubation12);
            eggorderlineincubationList.add(eggorderlineincubation13);
            eggorderlineincubationList.add(eggorderlineincubation14);
            eggorderlineincubationList.add(eggorderlineincubation15);
            eggorderlineincubationList.add(eggorderlineincubation16);
            eggorderlineincubationList.add(eggorderlineincubation17);
            eggorderlineincubationList.add(eggorderlineincubation18);
            eggorderlineincubationList.add(eggorderlineincubation19);
            eggorderlineincubationList.add(eggorderlineincubation20);
            eggorderlineincubationList.add(eggorderlineincubation21);
            eggorderlineincubationList.add(eggorderlineincubation22);
            eggorderlineincubationList.add(eggorderlineincubation23);
            eggorderlineincubationList.add(eggorderlineincubation24);
            eggorderlineincubationList.add(eggorderlineincubation25);
            eggorderlineincubationList.add(eggorderlineincubation26);
            eggorderlineincubationList.add(eggorderlineincubation27);
            eggorderlineincubationList.add(eggorderlineincubation28);
            eggorderlineincubationList.add(eggorderlineincubation29);
            eggorderlineincubationList.add(eggorderlineincubation30);
            eggorderlineincubationList.add(eggorderlineincubation31);
            eggorderlineincubationList.add(eggorderlineincubation32);
            eggorderlineincubationList.add(eggorderlineincubation33);
            eggorderlineincubationList.add(eggorderlineincubation34);
            eggorderlineincubationList.add(eggorderlineincubation35);
            eggorderlineincubationList.add(eggorderlineincubation36);
            eggorderlineincubationList.add(eggorderlineincubation37);
            eggorderlineincubationList.add(eggorderlineincubation38);
            eggorderlineincubationList.add(eggorderlineincubation39);
            eggorderlineincubationList.add(eggorderlineincubation40);
            eggorderlineincubationList.add(eggorderlineincubation41);
            eggorderlineincubationList.add(eggorderlineincubation42);
            
	        Iterator<EmbryoOrderLineIncubation> iteratorEmbryoOrderLineIncubation = eggorderlineincubationList.iterator();
	        
	     	while (iteratorEmbryoOrderLineIncubation.hasNext()) {
	    		
	     		EmbryoOrderLineIncubation embryoorderlineincubation = iteratorEmbryoOrderLineIncubation.next();

	     		repositoryeggorderlineincubation.save(embryoorderlineincubation);
	     	}
    	}
    	catch (Exception e) {
    		
    		e.printStackTrace();
    	}

    }

    
    @Transactional
    private final Privilege createPrivilegeIfNotFound(final String name) {
    	
        Privilege privilege = privilegeRepository.findByName(name);
        
        if (privilege == null) {
        
        	privilege = new Privilege(name);
            
        	privilegeRepository.save(privilege);
        }
        
        return privilege;
    }

    
    @Transactional
    private final Role createRoleIfNotFound(final String name, final Collection<Privilege> privileges) {
    
    	Role role = roleRepository.findByName(name);
        
    	if (role == null) {
        
    		role = new Role(name);
            role.setPrivileges(privileges);
            
            roleRepository.save(role);
        }
    	
        return role;
    }

}