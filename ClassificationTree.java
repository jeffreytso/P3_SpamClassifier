import java.util.*;
import java.io.*;

// Jeffrey Tso
// 12/3/2024
// CSE 123
// Programming Assignment 3: Spam Classifier
// Sean Eglip

// This class extends the abstract class classifier to predict some label for some input data.
public class ClassificationTree extends Classifier {
    private ClassificationNode overallRoot;

    // Behavior: 
    //      - This constructor creates a classification tree based off a given scanner.
    // Parameter:
    //      - Takes in a scanner that reads off a file containing nodes formatted in a pre-order
    //      - traversal of the tree. The scanner should be non-null.
    public ClassificationTree(Scanner sc) {
        overallRoot = scannerConstrHelper(sc);
    }

    // Behavior: 
    //      - Private helper method to assist the scanner constructor in the formation of the tree.
    // Parameters:
    //      - Takes in a scanner that reads off a file containing nodes formatted in a pre-order
    //      - traversal of the tree. The scanner should be non-null.
    private ClassificationNode scannerConstrHelper(Scanner sc) {
        ClassificationNode currNode = null;
        if (sc.hasNextLine()) {
            String line1 = sc.nextLine();
            if (line1.contains("Feature")) {
                String feature = line1.split(" ")[1];
                double threshold = Double.parseDouble(sc.nextLine().split(" ")[1]);
                currNode = new ClassificationNode(feature, threshold, scannerConstrHelper(sc), scannerConstrHelper(sc));
            } else {
                currNode = new ClassificationNode(line1, null);
            }
        }
        return currNode;
    }

    // Behavior:
    //      - Creates a classification tree from a list of input data and corresponding labels.
    // Exceptions:
    //      - Throws an IllegalArgumentException if the 
    //      - lists aren't the same size or the lists are empty.
    // Parameters:
    //      - Takes in a list of data (List<Classifiable>) and 
    //      - a list of corresponding labels (List<String>) that should both be non-null.
    public ClassificationTree(List<Classifiable> data, List<String> results) {
        if (data.size() == 0 || results.size() == 0) {
            throw new IllegalArgumentException("Lists should not be empty.");
        }
        if (data.size() != results.size()) {
            throw new IllegalArgumentException("Lists should be the same size.");
        }
        overallRoot = new ClassificationNode(results.get(0), data.get(0));
        for (int i = 1; i < data.size(); i++) {
            overallRoot = twoListConstrHelper(data.get(i), results.get(i), overallRoot);
        }
    }

    // Behavior:
    //      - Private helper method to assist the constructor that takes in two lists in
    //      - creating the classification tree.
    // Parameters:
    //      - Takes in a datapoint (Classifiable), a corresponding label (String),
    //      - and a node (ClassificationNode) that should all be non-null.
    private ClassificationNode twoListConstrHelper(Classifiable data, String result, ClassificationNode currNode) {
        if (currNode.split == null) {
            if (!result.equals(currNode.label)) {
                currNode.split = data.partition(currNode.oldData);
                if (currNode.split.evaluate(data)) {
                    currNode.left = new ClassificationNode(result, data);
                    currNode.right = new ClassificationNode(currNode.label, currNode.oldData);
                } else {
                    currNode.right = new ClassificationNode(result, data);
                    currNode.left = new ClassificationNode(currNode.label, currNode.oldData);
                }
            }
            return currNode;
        } else {
            if (currNode.split.evaluate(data)) {
                currNode.left = twoListConstrHelper(data, result, currNode.left);
            } else {
                currNode.right = twoListConstrHelper(data, result, currNode.right);
            }
        }
        return currNode;
    }

    // Behavior: 
    //      - Returns whether or not the classifier is able to classify 
    //      - datapoints that match that of the provided 'input'
    // Returns: 
    //      - a boolean representing if the classifier can classify the input
    // Parameters: 
    //      - input - the classifiable object, which should be non-null
    public boolean canClassify(Classifiable input) {
        return canClassify(input, overallRoot);
    }

    // Behavior:
    //      - Private helper method to help determine whether an input is classifiable.
    // Returns: 
    //      - A boolean representing if the classifier can classify the input
    // Parameters: 
    //      - Takes in an input, the classifiable object, which should be non-null.
    //      - Takes in a singular node (ClassificationNode).
    private boolean canClassify(Classifiable input, ClassificationNode currNode) {
        if (currNode != null) {
            if (!input.getFeatures().contains(currNode.split.getFeature())) {
                return false;
            }
            if (currNode.left != null && currNode.left.split != null) {
                return canClassify(input, currNode.left);
            }
            if (currNode.right != null && currNode.right.split != null) {
                return canClassify(input, currNode.right);
            }
            return true;
        } else {
            return false;
        }
        
        
    }
 
    // Behavior: 
    //      - Classifies the provided 'input', returning the associated learned label.
    // Exceptions: 
    //      - IllegalArgumentException if the provided input can't be classified.
    // Returns: 
    //      - a String representing the learned label.
    // Parameters: 
    //      - input - the classifiable object, which should be non-null.
    public String classify(Classifiable input) {
        if (!canClassify(input)) {
            throw new IllegalArgumentException("Input cannot be classified.");
        }
        return classify(input, overallRoot);
    }

    // Behavior:
    //      - Private helper method to help classify a given input with its associated label.
    // Returns:
    //      - a String representing the learned label.
    // Parameters:
    //      - Takes in an input, the classifiable object, which should be non-null.
    //      - Takes in a singular node (ClassificationNode) that should be non-null.
    private String classify(Classifiable input, ClassificationNode currNode) {
        if (currNode.split == null) {
            return currNode.label;
        }
        if (currNode.split.evaluate(input)) {
            return classify(input, currNode.left);
        } else {
            return classify(input, currNode.right);
        }
    }

    // Behavior: 
    //      - Saves this classifier to the provided PrintStream 
    //      - 'ps' in a pre-order traversal order.
    // Parameters: ps - the PrintStream to save the classifier to, which should be non-null
    public void save(PrintStream ps) {
        save(ps, overallRoot);
    }

    // Behavior: 
    //      - Private helper method to help save this classifier to the 
    //      - provided PrintStream in a pre-order traversal order.
    // Parameters: 
    //      - Takes in a PrintStream to save the classifier to, which should be non-null
    //      - as well as a node (ClassificationNode).
    private void save(PrintStream ps, ClassificationNode currNode) {
        if (currNode != null) {
            if (currNode.left == null && currNode.right == null) {
                ps.println(currNode.label);
            } else {
                ps.println(currNode.split.toString());
            }
            save(ps, currNode.left);
            save(ps, currNode.right);
        }
    }
    
    // This class represents the nodes of a classification tree.
    private static class ClassificationNode {
        public String label;
        public Split split;
        public Classifiable oldData;
        public ClassificationNode left;
        public ClassificationNode right;

        // This constructor creates a leaf node. It takes in a label (String) and old data
        // (Classifiable).
        public ClassificationNode(String label, Classifiable oldData) {
            this.label = label;
            this.oldData = oldData;
        }

        // This constructor creates an intermediary node. It takes in a feature (String),
        // a threshold (double), and a right and left node (ClassificationNode).
        public ClassificationNode(String feature, double threshold, 
                    ClassificationNode left, ClassificationNode right) {
            this.split = new Split(feature, threshold);
            this.left = left;
            this.right = right;
        }
    }
}
