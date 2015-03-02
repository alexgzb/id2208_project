package main;

import com.predic8.wsdl.Operation;
import generated.MatchedElementType;
import generated.MatchedOperationType;
import generated.MatchedWebServiceType;
import generated.WSMatchingType;
import java.util.ArrayList;
import parser.NewParser;
//import com.ibm.wsdl.OperationImpl;
import java.util.List;
//import java.util.List;
//import javax.xml.namespace.QName;
//import parser.WsdlParser;

/**
 *
 *
 */
public class Matcher {

    private final double threshold = 0.8;

    public static void main(String[] args) {
        Matcher matcher = new Matcher();
        SemanticMatcher semMatcher = new SemanticMatcher();

        String fileOutput = "sawsdl/author_bookmaxprice_service.wsdl";
        String fileInput = "sawsdl/author_booktaxedprice_service.wsdl";

        matcher.matcher(fileOutput, fileInput, semMatcher);

    }

    public WSMatchingType matcher(String fileOutput, String fileInput, SemanticMatcher semMatcher) {

//        // ADD THE FILENAMES
//        String fileOutput = "drink.wsdl";
//        String fileInput = "wine.wsdl";
        // CREATE THE PARSERS
        NewParser parserOutput = new NewParser(fileOutput);
        NewParser parserInput = new NewParser(fileInput);

        // GET SERVICENAMES
        String outputService = parserOutput.getServiceName();
        String inputService = parserInput.getServiceName();

        // GET OPERATIONS
        List<Operation> outputOperations = parserOutput.getOperations();
        List<Operation> inputOperations = parserInput.getOperations();

        // CONTAINER FOR ELEMENTS
        ArrayList<String> outputElements;
        ArrayList<String> inputElements;

        // MEASURE THE NUMBER OF ELEMENTS IN A OPERATION
        int outSize;
        int inSize;

        // CONTAINER FOR ALL MATCHED OPERATIONS
        ArrayList<MatchedOperationType> matchedOperations = new ArrayList<>();

        // USED TO CALCULATE AVERAGE OPERATION SCORE
        double totalOperationsScore = 0;
        int totalOperationsMatches = 0;

        // OUTER LOOP THROUGH OUTPUT OPERATIONS
        for (Operation out : outputOperations) {

            // CREATE A CONTAINER FOR A MATCHED OPERATION
            MatchedOperationType matchedOperation = null;

            // NAME OF THE OUTPUT OPERATION
            String outoutOperationName = out.getQName().getLocalPart();

            // GET ALL ELEMENTS FOR THE OPERATION
            outputElements = NewParser.getOutputElements(out);

            // Get all elementTypes of non primary type
            ArrayList<String> outputElementTypes = NewParser.getOutputElementsType(out);
            System.err.println("Size of out types " + outputElementTypes.size());

            // NUMBER OF ELEMENTS IN THE OPERATION
            outSize = outputElements.size();

            // INNER LOOP THROUGH THE INPUT OPERATIONS
            for (Operation in : inputOperations) {

                // NAME OF THE INPUT OPERATION
                String inputOperationName = in.getQName().getLocalPart();

                // GET ALL THE ELEMENTS OF THE OPERATION
                inputElements = NewParser.getInputElements(in);

                // GET ALL THE Types
                ArrayList<String> inputElementTypes = NewParser.getInputElementsType(in);
                System.err.println("Size of out types " + inputElementTypes.size());

                // NUMBER OF ELEMENTS IN THE OPERATION
                inSize = inputElements.size();

                // IF OUTPUT OPERATIONS NUMBER OF ELEMENTS MATCHES INPUT
                if (/*outSize == inSize*/true) {
                    System.out.println("MATCHED NUMBER OF ELEMENTS ");

                    // TO CALCULATE THE AVERAGE ELEMENTS SCORE
                    double totalElementsScore = 0;
                    int totalElementsMatches = 0;

                    ArrayList<MatchedElementType> matchedElements;
                    matchedElements = new ArrayList<>();

                    // OUTER LOOP ELEMENTS
                    for (String outputElement : outputElements) {

                        // INNER LOOP ELEMENTS
                        for (String inputElement : inputElements) {

                            double scoreSynt;

                            scoreSynt = EditDistance.getSimilarity(outputElement,
                                    inputElement);
                            System.out.println("TESTING: OUTPUTELEMENT="
                                    + outputElement + " INPUTELEMENT="
                                    + inputElement + " SCORE=" + scoreSynt);

                            if (scoreSynt > threshold) {

                                totalElementsScore += scoreSynt;
                                totalElementsMatches++;

                                MatchedElementType matchElement;
                                matchElement = new MatchedElementType();
                                matchElement.setOutputElement(outputElement);
                                matchElement.setInputElement(inputElement);
                                matchElement.setScore(scoreSynt);
                                matchedElements.add(matchElement);

                                System.out.println("FOUND SYNTACTIC MATCH: "
                                        + "OUTPUTELEMENT=" + outputElement
                                        + " INPUTELEMENT=" + inputElement
                                        + " SCORE=" + scoreSynt);

                            }

                        }

                    }

                    /* REMOVE COMMENT FOR SEMANTIC MATCHING 
                    for (String outputElementType : outputElementTypes) {

                        for (String inputElementType : inputElementTypes) {
                            double scoreSem;
                            scoreSem = semMatcher.findMatching(outputElementType, inputElementType);

                            System.out.println("TESTING SEMANTIC MATCH: "
                                    + "OUTPUTELEMENT=" + outputElementType
                                    + " INPUTELEMENT=" + inputElementType
                                    + " SCORE=" + scoreSem);

                            if (scoreSem > 0.5) {

                                totalElementsScore += scoreSem;
                                totalElementsMatches++;

                                MatchedElementType matchElement;
                                matchElement = new MatchedElementType();
                                matchElement.setOutputElement(outputElementType);
                                matchElement.setInputElement(inputElementType);
                                matchElement.setScore(scoreSem);
                                matchedElements.add(matchElement);

                                System.out.println("FOUND SEMANTIC MATCH: "
                                        + "OUTPUTELEMENT=" + outputElementType
                                        + " INPUTELEMENT=" + inputElementType
                                        + " SCORE=" + scoreSem);

                            }
                            //System.out.println("!!!!!!!!!!!!!!!+++++ SEMANTIC SCORE " + scoreSem);

                        }

                    }
                    
                    */

                    // STORE THE MATCHED ELEMENTS FOR THE OPERATIONS
                    if (matchedElements.size() > 0) {
                        System.out.println("ADDING MATCHED OPERATION");
                        totalOperationsMatches++;
                        totalOperationsScore
                                += (totalElementsScore / totalElementsMatches);

                        matchedOperation = new MatchedOperationType();
                        matchedOperation.setOpScore(totalElementsScore / totalElementsMatches);
                        matchedOperation.setInputOperationName(inputOperationName);
                        matchedOperation.setOutputOperationName(outoutOperationName);
                        for (MatchedElementType match : matchedElements) {
                            matchedOperation.getMacthedElement().add(match);
                        }
                        matchedOperations.add(matchedOperation);
                    }
                } else {
                    System.out.println("NUMBER OF ELEMENTS DID NOT MATCH "
                            + "outpuElements= "
                            + outSize + " inputElements= " + inSize);
                }
            }//END OF INNER FOR LOOP

        }//END OF OUTER FOR LOOP

        // IF THERE ARE MATHCED OPERATIONS THEN CREATE AND ADD EVERYTHINGd
        if (matchedOperations.size() > 0) {
            System.out.println("ADDING MATCHED WEB SERVICE");
            MatchedWebServiceType matchedWebService = new MatchedWebServiceType();
            matchedWebService.setOutputServiceName(outputService);
            matchedWebService.setInputServiceName(inputService);
            matchedWebService.setWsScore(totalOperationsScore / totalOperationsMatches);
            for (MatchedOperationType matchedOperation : matchedOperations) {
                matchedWebService.getMacthedOperation().add(matchedOperation);
            }

            WSMatchingType wsMatching = new WSMatchingType();
            wsMatching.getMacthing().add(matchedWebService);
            return wsMatching;
        }
        return null;

    }//END OF MAIN

}
