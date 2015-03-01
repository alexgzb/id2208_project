package parser;

import java.util.ArrayList;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.WSDLParser;
import com.predic8.wsdl.Service;
import com.predic8.wsdl.Part;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewParser {

    private final Definitions definition;
    private final WSDLParser parser;
    private final List<Operation> operations;
    private final List<Service> services;


    //public static void main(String[] args) {
    //    NewParser p = new NewParser("wsdl/TripAuthorityAPIProfile.wsdl");
    //    NewParser.getOutputElements(p.getOperations().get(0));
    //}

    public NewParser(String filename) {

        parser = new WSDLParser();
        definition = parser.parse(filename);
        operations = definition.getOperations();
        services = definition.getServices();

    }

    /**
     * Simply return the ServiceName
     * @return 
     */
    public String getServiceName() {
        String serviceName = "";
        for (Service s : services) {
            System.out.println("SERVICENAME: " + s.getQName().getLocalPart());
            serviceName = s.getQName().getLocalPart();
        }
        return serviceName;
    }

    /**
     * Get Operations
     * @return 
     */
    public List<Operation> getOperations() {
        return operations;
    }

    /**
     * Goes through the output message and finds all parts that are of primitive
     * data types and creates a list and returns them
     * 
     * @param operation The operation to search through
     * @return The list of Elements
     */
    public static ArrayList<String> getOutputElements(Operation operation) {

        ArrayList<String> elementNames = new ArrayList<>();

        List<Part> parts = operation.getOutput().getMessage().getParts();
        System.out.println("OPERATION NAME " + operation.getQName().getLocalPart());
        System.out.println("MESSAGE NEAME " + operation.getOutput().getMessage().getQName().getLocalPart());
        for (Part part : parts) {
            //If part has Element
            if (part.getElement() != null) {
                
                // Check for the regex pattern and match
                Pattern p = Pattern.compile("name=('|\")(\\w+)('|\")\\s*type=('|\").*:(string|int|double|date|boolean)");
                Matcher m = p.matcher(part.getElement().getAsString());

                // System.out.println(part.getElement().getAsString());
                while (m.find()) {
                    // System.out.println(m.group());
                    String name = m.group().substring(6, m.group().indexOf("'", 6));
                    elementNames.add(name);
                }
                //If part does not have an Element the part name is retreived
            } else {
                System.out.println("Else adding " + part.getQName().getLocalPart());
                elementNames.add(part.getQName().getLocalPart());
            }
        }
        return elementNames;
    }

        /**
     * Goes through the input message and finds all parts that are of primitive
     * data types and creates a list and returns them
     * 
     * @param operation The operation to search through
     * @return The list of Elements
     */
    public static ArrayList<String> getInputElements(Operation operation) {

        ArrayList<String> elementNames = new ArrayList<>();

        String prefixName = operation.getInput().getMessagePrefixedName().getLocalName();
        if ((prefixName.equals("string") || prefixName.equals("int") || prefixName.equals("double") || prefixName.equals("date"))) {
            elementNames.add(operation.getInput().getQName().getLocalPart());
        } else {

            List<Part> parts = operation.getInput().getMessage().getParts();

            for (Part part : parts) {
                if (part.getElement() != null) {
                    Pattern p = Pattern.compile("name='(\\w+)'\\s*type='.*:(string|int|double|date)");
                    Matcher m = p.matcher(part.getElement().getAsString());
                    
                    while (m.find()) {
                        String name = m.group().substring(6, m.group().indexOf("'", 6));
                        elementNames.add(name);
                    }
                } else {
                    System.out.println("Else adding " + part.getQName().getLocalPart());
                    elementNames.add(part.getQName().getLocalPart());
                }

            }
        }

        return elementNames;
    }

}
