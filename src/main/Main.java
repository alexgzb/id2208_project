package main;

import generated.WSMatchingType;
import java.io.File;
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
        String fileOutput = "drink.wsdl";
        String fileInput = "wine.wsdl";
        WSMatchingType wsMatch = matcher.matcher(fileOutput, fileInput);
        if (wsMatch != null) {
            exportToFile(wsMatch);
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

    public static void exportToFile(WSMatchingType wSMatchingType) {
        try {
            File outputFile = new File("output111.xml");
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
