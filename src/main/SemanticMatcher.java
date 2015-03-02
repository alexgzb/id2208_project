/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.HashMap;
import java.util.Vector;
import ontology.MyOntManager;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 *
 * @author alex
 */
public class SemanticMatcher {

    final double EXACT = 1.0;
    final double SUBSUMPTION = 0.8;
    final double PLUGIN = 0.6;
    final double STRUCTURAL = 0.5;
    final double NOTMATCHED = 0.0;
    public final double THRESHOLD = 0.5;
    static MyOntManager om;
    static OWLOntologyManager manager;
    static OWLOntology ontology;
    static Reasoner reasoner;
    static HashMap<String, OWLClass> mapName_OWLClass;

    public static void main(String[] args) {
        new SemanticMatcher();
    }
    
    public SemanticMatcher() {
        
        String ontologyLocation = "file:///home/alex/workspace/Id2208Matching/data/SUMO.owl";
        om = new MyOntManager();
        manager = om.initializeOntologyManager();
        ontology = om.initializeOntology(manager, ontologyLocation);
        reasoner = om.initializeReasoner(ontology, manager);
        if (reasoner == null ) {
            System.out.println("Reasoner is null ");
        }
        mapName_OWLClass = om.loadClasses(reasoner);
        
        //System.out.println("/n/nMap size = " + mapName_OWLClass.size());
        
    }
    
    
        public double findMatching(String output, String input) {
        if (isSameAs(output, input)) {
            return EXACT;
        } else if (isSubClassOf(input, output)) {
            return SUBSUMPTION;
        } else if (isSubClassOf(output, input)) {
            return PLUGIN;
        } else if (hasRelationWith(output, input)) {
            return STRUCTURAL;
        } else {
            return NOTMATCHED;
        }
    }

    static private boolean isSameAs(String output, String input) {
        return output.equals(input);
    }

    static private boolean isSubClassOf(String child, String parent) {
        OWLClass cls1 = mapName_OWLClass.get(child.toLowerCase());
        OWLClass cls2 = mapName_OWLClass.get(parent.toLowerCase());

        if (cls1 == null || cls2 == null) {
            return false;
        }
        return reasoner.isSubClassOf(cls1, cls2);
    }

    static private boolean hasRelationWith(String output, String input) {

        OWLClass cls1 = mapName_OWLClass.get(output.toLowerCase());

        OWLClass cls2 = mapName_OWLClass.get(input.toLowerCase());
        if (cls1 == null || cls2 == null) {
            return false;
        }

        Vector<OWLObjectProperty> objprops = om.findRelationship(cls1, cls2, reasoner);
        //System.out.println("Vector size " + objprops.size());

        for (OWLObjectProperty p : objprops) {
            System.out.println("  : " + p.getURI().getFragment() + "  " + p.getNamedProperty().toString());
        }
        return objprops.size() > 0;
    }

    
    
}
