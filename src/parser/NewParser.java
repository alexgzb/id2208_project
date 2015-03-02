package parser;

import java.util.ArrayList;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.WSDLParser;
import com.predic8.wsdl.Service;
import com.predic8.wsdl.Part;
import com.predic8.wsdl.Types;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewParser {

    private final Definitions definition;
    private final WSDLParser parser;
    private final List<Operation> operations;
    private final List<Service> services;

    public static void main(String[] args) {
        //NewParser p = new NewParser("sawsdl/author_bookmaxprice_service.wsdl");
        NewParser p = new NewParser("sawsdl/academic_address_service.wsdl");
        //NewParser.getOutputElements(p.getOperations().get(0));
        for (Operation o : p.getOperations()) {
            NewParser.getInputElementsType(o);
            NewParser.getOutputElementsType(o);
        }
        
        //NewParser.getOutputElementsType(p.getOperations().get(0));
        //NewParser.getInputElementsType(p.getOperations().get(0));
    }

    public NewParser(String filename) {

        parser = new WSDLParser();
        definition = parser.parse(filename);
        operations = definition.getOperations();
        services = definition.getServices();
        List<Types> types = definition.getTypes();

    }

    /**
     * Simply return the ServiceName
     *
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
     *
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

        try {
            List<Part> parts = operation.getOutput().getMessage().getParts();

            System.out.println("OPERATION NAME " + operation.getQName().getLocalPart());
            System.out.println("MESSAGE NEAME " + operation.getOutput().getMessage().getQName().getLocalPart());
            for (Part part : parts) {
                //If part has Element
                if (part.getElement() != null) {

                    // Check for the regex pattern and match
                    //Pattern p = Pattern.compile("name=('|\")(\\w+)('|\")\\s*type=('|\").*:(string|int|double|date|boolean)");
                    Pattern p = Pattern.compile("name=('|\")(\\w+)('|\")\\s*type=('|\").*:.*");
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
        } catch (NullPointerException e) {
            // Fall through
            // System.out.println("NULL POINTER IN NEW PARSER");
            return elementNames;
        }
    }

    /**
     * Goes through the output message and finds all parts that are of primitive
     * data types and creates a list and returns them
     *
     * @param operation The operation to search through
     * @return The list of Elements
     */
    public static ArrayList<String> getOutputElementsType(Operation operation) {

        ArrayList<String> elementTypes = new ArrayList<>();

        try {
            List<Part> parts = operation.getOutput().getMessage().getParts();

            System.out.println("OPERATION NAME " + operation.getQName().getLocalPart());
            System.out.println("MESSAGE NEAME " + operation.getOutput().getMessage().getQName().getLocalPart());
            for (Part part : parts) {
                //If part has Element
                if (part.getElement() != null) {

                    // Check for the regex pattern and match
                    //Pattern p = Pattern.compile("name=('|\")(\\w+)('|\")\\s*type=('|\").*:(string|int|double|date|boolean)");
                    Pattern p = Pattern.compile("name='(\\w+)'.*type='.*");
                    Matcher m = p.matcher(part.getElement().getAsString());

                    //System.out.println(part.getElement().getAsString());
                    while (m.find()) {

                        int start = m.group().indexOf(":", m.group().indexOf("type")) + 1;
                        int end = m.group().indexOf("'", start);
                        //System.out.println(m.group().substring(start, end));
                        String type = m.group().substring(start, end);
                        //if (!type.equals("string") && !type.equals("int") && !type.equals("double") && !type.equals("date") && !type.equals("boolean")) {
                        System.out.println("TYPE " + type);
                        elementTypes.add(type);
                        //}

                    }
                } else {
                    System.out.println("ELSE TYPE : " + part.getDocumentation());
                    //System.out.println("ELSE TYPE : " + part.getTypePN().getLocalName());
                    elementTypes.add(part.getTypePN().getLocalName());
                }
            }
            return elementTypes;
        } catch (NullPointerException e) {
            // Fall through
            // System.out.println("NULL POINTER IN NEW PARSER");
            return elementTypes;
        }
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

        try {
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
        } catch (NullPointerException n) {
            // Fall through
            // System.out.println("NULL POINTER IN NEW PARSER");
            return elementNames;
        }

        return elementNames;
    }

    /**
     * Goes through the output message and finds all parts that are of primitive
     * data types and creates a list and returns them
     *
     * @param operation The operation to search through
     * @return The list of Elements
     */
    public static ArrayList<String> getInputElementsType(Operation operation) {

        ArrayList<String> elementTypes = new ArrayList<>();

        try {
            List<Part> parts = operation.getInput().getMessage().getParts();

            System.out.println("OPERATION NAME " + operation.getQName().getLocalPart());
            System.out.println("MESSAGE NEAME " + operation.getInput().getMessage().getQName().getLocalPart());
            for (Part part : parts) {
                //If part has Element
                if (part.getElement() != null) {

                    // Check for the regex pattern and match
                    //Pattern p = Pattern.compile("name=('|\")(\\w+)('|\")\\s*type=('|\").*:(string|int|double|date|boolean)");
                    Pattern p = Pattern.compile("name='(\\w+)'.*type='.*");
                    Matcher m = p.matcher(part.getElement().getAsString());

                    //System.out.println(part.getElement().getAsString());
                    while (m.find()) {

                        int start = m.group().indexOf(":", m.group().indexOf("type")) + 1;
                        int end = m.group().indexOf("'", start);
                        //System.out.println("Start index " + start + " End index " + end);
                        //System.out.println(m.group().substring(start, end));
                        String type = m.group().substring(start, end);
                        //if (!type.equals("string") && !type.equals("int") && !type.equals("double") && !type.equals("date") && !type.equals("boolean")) {
                        System.out.println("TYPE " + type);
                        elementTypes.add(type);
                        //}

                    }
                } else {
                    System.out.println("ELSE TYPE : " + part.getTypePN().getLocalName());
                    elementTypes.add(part.getTypePN().getLocalName());
                }
            }
            return elementTypes;
        } catch (NullPointerException e) {
            //Fall through
            //System.out.println("NULL POINTER IN NEW PARSER");
            return elementTypes;
        }
    }
}
