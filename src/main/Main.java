package main;

import generated.WSMatchingType;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import ontology.MyOntManager;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

public class Main {

    private final String ontologyLocation = "file:///home/alex/workspace/Id2208Matching/data/SUMO.owl";
    private MyOntManager myOntManager;
    private OWLOntologyManager owlOntologyManager;
    private OWLOntology owlOntology;
    private Reasoner reasoner;
    private HashMap<String, OWLClass> owlMap;

    public static void main(String[] args) {
        Matcher matcher = new Matcher();
        SemanticMatcher semMatcher = new SemanticMatcher();

        File folder = new File("sawsdl");
        File[] listOfObjects = folder.listFiles();
        int files = 0;
        ArrayList<File> listOfFiles = new ArrayList<>();

        /*
         for (int i = 0; i < listOfObjects.length; i++) {
         if (listOfObjects[i].isFile()) {
         //System.out.println("FILE " + listOfObjects[i].getAbsolutePath());
         listOfFiles.add(listOfObjects[i]);
         }
         }
         for (File outputFile : listOfFiles) {
         for (File inputFile : listOfFiles) {
         int i = 0;
         if (!outputFile.equals(inputFile)) {

         String fileOutput = outputFile.getAbsolutePath();
         String fileInput = inputFile.getAbsolutePath();

         //String fileOutput = "out.wsdl";
         //String fileInput = "in.wsdl";
         // IF there are Syntactic matches export them to the file
         WSMatchingType wsMatch = matcher.matcher(fileOutput, fileInput, semMatcher);

         if (wsMatch != null) {
         exportToFile(wsMatch, i + "output111.xml");
         } else {
         System.err.println("NO MATCHES FOUND");
         }

         }

         }

         }
         */
        
        
//        String fileOutput = "sawsdl/academic-item-number_publicationauthor_service.wsdl";
//        String fileInput = "sawsdl/AuthorMonographtaxfreepriceService.wsdl";

        String fileOutput = "drink.wsdl";
        String fileInput = "wine.wsdl";
        

        // IF there are Syntactic matches export them to the file
        WSMatchingType wsMatch = matcher.matcher(fileOutput, fileInput,semMatcher);
        if (wsMatch != null) {
            exportToFile(wsMatch, "output111.xml");
        } else {
            System.err.println("NO MATCHES FOUND");
        }
    }

    public Main() {
        myOntManager = new MyOntManager();
        owlOntologyManager = myOntManager.initializeOntologyManager();
        owlOntology = myOntManager.initializeOntology(owlOntologyManager, ontologyLocation);
        reasoner = myOntManager.initializeReasoner(owlOntology, owlOntologyManager);
        owlMap = myOntManager.loadClasses(reasoner);

    }

    public static void exportToFile(WSMatchingType wSMatchingType, String filename) {
        try {
            File outputFile = new File(filename);
            JAXBContext context = JAXBContext.newInstance(
                    wSMatchingType.getClass().getPackage().getName());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            JAXBElement<WSMatchingType> topLevelElement = new JAXBElement<>(
                    new QName("ns2:WSMatching"),
                    WSMatchingType.class, wSMatchingType
            );
            marshaller.marshal(topLevelElement, outputFile);
            System.out.println("CREATED FILE: " + outputFile);
        } catch (JAXBException jexc) {
            jexc.printStackTrace();
        }

    }

}
