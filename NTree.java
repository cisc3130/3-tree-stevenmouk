import java.util.*;
import java.io.*;

public class NTree<E> {
	private String code = "";
	private String[] items;
	private int count;

	protected class Node {
		E data;
		Node parent;
		List<Node> children;

		protected Node(E data) {
			this.data = data;
			this.children = new ArrayList<Node>();
		}

		protected void addChild(Node c) {
			children.add(c);
		}

		public boolean equals(Node rhs) {
			return this.data.equals(rhs.data);
		}
	}

	protected Node root;

	public NTree() {
	}

	public NTree(List<E> values, List<Integer> parents) throws Exception {
		if (values.size() != parents.size())
			throw new Exception();
		Map<E, Node> m = new TreeMap<>();
		for (int i = 0; i < values.size(); i++) {
			Node nd = new Node(values.get(i));
			m.put(values.get(i), nd);
			if (parents.get(i) >= 0) { // -1 signals root
				nd.parent = m.get(values.get(parents.get(i)));
				nd.parent.addChild(nd);
			} else
				root = nd;
		}
	}

	public boolean equals(NTree<E> rhs) {
		return equals(root, rhs.root);

	}

	protected boolean equals(Node lhs, Node rhs) {

		if (lhs == null || rhs == null)
			return lhs == rhs;

		if (!lhs.equals(rhs))
			return false;

		for (int i = 0; i < lhs.children.size(); i++) {
			if (!equals(lhs.children.get(i), rhs.children.get(i)))
				return false;

		}
		return true;
	}

	// Encodes a tree and write it to a file. The encoded string is 1 line with the
	// node name followed by a "," and a "StepBack"
	// whenever the end of a node is reached.

	public void serialize(String fname) throws IOException {
		// file and printwriter to write to a file
		FileWriter filewriter = new FileWriter(fname);
		PrintWriter printWriter = new PrintWriter(filewriter);

		// calls serializeHelper which is a recursing method to make the serialized code
		serializeHelper(root);

		// cuts off a trailing comma left at the end of string printed to the file
		code = code.substring(0, code.length() - 1);

		// writes to file.
		printWriter.printf(code);
		printWriter.close();

	}

	public void serializeHelper(Node root) {
		// adds the data of a node to string code. This string is then printed to file
		// when finished.
		code += root.data;

		// adds a comma to seperate the data of each node
		code += ",";

		// loops over the children of root and recurses the method for each child of
		// root
		List<Node> children = root.children;
		if (children.size() > 0) {
			for (Node child : root.children) {
				serializeHelper(child);
			}
		}
		// adds step back everytime it hits the end. This tells deserialize when to
		// stepback and return to the parent node
		code += "StepBack";
		code += ",";

	}

	// deserializes the file
	public void deserialize(String fname) throws FileNotFoundException {
		// reads in file
		File file = new File(fname);
		Scanner scanner = new Scanner(file);

		// splits file by comma and turns it into a string array.
		while (scanner.hasNextLine()) {
			items = scanner.nextLine().split(",");
		}
		scanner.close();
		// calls recursive method to help deserialize the tree
		root = deserializeHelper();

	}

	public Node deserializeHelper() {

		String currentItem = items[count++];
		// returns null whenever the StepBack is read. This allows the method to add
		// children to their proper nodes.
		if (currentItem.equals("StepBack"))
			return null;

		List<Node> children = new LinkedList<>();

		// loops over and adds child from items array to the children array.
		while (count < items.length) {
			Node child = deserializeHelper();
			if (child != null) {
				children.add(child);

			} else {
				break;
			}
		}

		// creates a new node where the parent is assigned and each child in children is
		// added using the addChild method.
		Node root = new Node((E) currentItem);
		for (Node child : children) {
			child.parent = root;
			root.addChild(child);
		}

		return root;

	}

	public static void main(String[] args) {
		try {
			List<String> food = Arrays.asList("Food", "Plant", "Animal", "Roots", "Leaves", "Fruits", "Fish", "Mammals",
					"Birds", "Potatoes", "Carrots", "Lettuce", "Cabbage", "Apples", "Pears", "Plums", "Oranges",
					"Salmon", "Tuna", "Beef", "Lamb", "Chicken", "Duck", "Wild", "Farm", "GrannySmith", "Gala");
			List<Integer> foodparents = Arrays.asList(-1, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 4, 4, 5, 5, 5, 5, 6, 6, 7, 7, 8,
					8, 17, 17, 13, 13);
			NTree<String> foodtree = new NTree(food, foodparents);

			foodtree.serialize("foodtree.out");
			NTree<String> foodtree2 = new NTree<>();
			foodtree2.deserialize("foodtree.out");

			System.out.println(foodtree.equals(foodtree2));

			List<Integer> intvalues = Arrays.asList(9, 6, 5, 4, 2, 10, 7, 1, 3, 8, 11, 12, 13, 14);
			List<Integer> intparents = Arrays.asList(-1, 0, 1, 1, 1, 2, 2, 2, 3, 3, 8, 8, 8, 8);
			NTree<Integer> inttree = new NTree<>(intvalues, intparents);

			NTree<Integer> inttree2 = new NTree<>();

			inttree.serialize("inttree.out");
			inttree2.deserialize("inttree.out");
			System.out.println(inttree.equals(inttree2));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
