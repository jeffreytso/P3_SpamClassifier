import java.util.*;
import java.io.*;

public class ClassificationTree extends Classifier {
    private ClassificationNode overallRoot;

    public ClassificationTree(Scanner sc) {
        overallRoot = ClassTreeMaker(sc, overallRoot);
    }

    private ClassificationNode ClassTreeMaker(Scanner sc, ClassificationNode currNode) {
        if (sc.hasNextLine()) {
            String line1 = sc.nextLine();
            if (line1.contains("Feature")) {
                String feature = line1.split(" ")[1];
                double threshold = Double.parseDouble(sc.nextLine().split(" ")[1]);
                currNode = new ClassificationNode(feature, threshold, null, null);
                currNode.left = ClassTreeMaker(sc, currNode.left);
                currNode.right = ClassTreeMaker(sc, currNode.right);
            } else {
                currNode = new ClassificationNode(line1, null);
            }
        }
        return currNode;
    }

    public ClassificationTree(List<Classifiable> data, List<String> results) {

    }

    public boolean canClassify(Classifiable input) {
        return true;
    }

    public String classify(Classifiable input) {
        return "";
    }

    public void save(PrintStream ps) {
        save(ps, overallRoot);
    }

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
    
    private static class ClassificationNode {
        public String label;
        public Split split;
        public Classifiable oldData;
        public ClassificationNode left;
        public ClassificationNode right;

        public ClassificationNode(String label, Classifiable oldData) {
            this.label = label;
            this.oldData = oldData;
        }

        public ClassificationNode(String feature, double threshold, 
                    ClassificationNode left, ClassificationNode right) {
            this.split = new Split(feature, threshold);
            this.left = left;
            this.right = right;
        }
    }
}
