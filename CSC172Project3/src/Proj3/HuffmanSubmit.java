/* Ziyi You
Project3
  zyou5
  MW 615 730
  I did not collaborate with anyone on this assignment. */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

// Import any package as required
public class HuffmanSubmit implements Huffman {

	//The class BTNode contains four properties of BTNode, each node has a name, frequency, a left child and a right child
	public static class BTNode implements Comparable<BTNode>{
		int frequency;
		char name;
		BTNode left;
		BTNode right;
		// Add constructor and/or other methods if required

		//constructor of BTNode class
		public BTNode(int item, char charname, BTNode left, BTNode right)
		{
			frequency = item;
			name = charname;
			this.left = left;
			this.right = right;
		}

        //this method lets the priority queue knows what to compare to, which is the frequency of the node
		@Override
		public int compareTo(BTNode o) {
			// TODO Auto-generated method stub
			return frequency - o.frequency;
		}
	}

	//this method counts the number of occurrence of each character in the input file and save the relationship into a map
	public static void character_count(String inputFile, String outputFile, HashMap<Character,Integer> map ) {
		BinaryIn reader = new BinaryIn(inputFile);
		while(!reader.isEmpty()) {
			char st= reader.readChar();
           
			if(map.containsKey(st)) {
				//when the readers reads for example 'a' again, it will simply add the frequency in the hash map
				map.put(st, map.get(st)+1);
			}else{
				//when the readers reads the first occurrence of for example, letter 'a', it will save it in the hash map
				map.put(st, 1);
			}
		}
	}
	
//this method creates a Huffman tree that is used for assigning the Huffman code to the letters
	static BTNode createtree( HashMap<Character, Integer> map) {
//the priority queue is used to store the nodes of the tree
		PriorityQueue<BTNode> q = new PriorityQueue<BTNode>();
		for (char k : map.keySet()) { //goes through all the characters in the hash map and add each of them into the priority queue
			q.offer(new BTNode(map.get(k), k, null, null));
		}

		//keep on polling nodes from the queue until there are only two nodes left
		while(q.size()>1) { 
			//since the priority queue is sorted based on the frequency of the nodes, the first one being polled out has the lowest frequencey, the second one being polled out has the secondest lowest frequency
			BTNode smallest = q.poll();
			BTNode secondsmallest = q.poll();
             //combine the two nodes polled from the queue into a new node with the frequency being the sum of the two being polled and add back into the queue
			BTNode combinednode = new BTNode(smallest.frequency + secondsmallest.frequency, '\0' , smallest, secondsmallest );
			q.offer(combinednode);
		}

		//the root of the tree is returned
		return q.poll();
	}

	//this method recursively assign the Huffman code to the leaf node of the tree
	private static void assignbinaryvaluetonode(BTNode root, HashMap<Character, String> chartobi, String bi){
		//if reaches a leaf which has no left or right children, the character name and the huffman code is stored into the hashmap
		if(root.left == null && root.right ==null){
			chartobi.put(root.name, bi);
			return;
		}
		else{
			//else, recursively assign the huffman code to nodes by adding '0' to each left branch and adding '1' to each right branch
			assignbinaryvaluetonode(root.left, chartobi, bi+"0");
			assignbinaryvaluetonode(root.right, chartobi,bi+"1");
		}
	} 

	//this recursive method changes decimal number into binary numbers 
	public static String decimaltobinary (int n) {
		if (n ==0) {
			return "0";
		}if (n==1) {
			return "1";
		}
		return decimaltobinary(n/2) + n%2;
	}

	//this method assign binary values to characters such as 'a', 'b', 'c', etc.
	public static void assignbinaryvaluetochar (HashMap<Character,Integer> map) {
		for (char c : map.keySet()) {
			//by casting the characters into integers, it can go through the decimaltobinary method because it takes in integer
			decimaltobinary(	(int)c );
		}	
	}

	//this method prints out the tree in a left, root, right order
	private static void printTree(BTNode root){

		if(root.left!=null){
			printTree(root.left);
		}
		if(root.name!= '\0'){
			System.out.println(root.name+ ":" + root.frequency);
		}
		if(root.right!=null){
			printTree(root.right);
		}
	}
	
	//this method prints out the frequency file using buffer writer, it prints out the 8 digits ASCII code and the corresponding frequency
	private static void printFrequencyFile(HashMap<Character, Integer> map, String freqFile) throws Exception{

		BufferedWriter pr = new BufferedWriter(new FileWriter(freqFile));
		for(char c:map.keySet()) {
			pr.write(updatebinary(decimaltobinary((int)c ))+":"+map.get(c)+"\n");
		}
		pr.close();

	}

	//this method prints the encoded file 
	public static void printEncodedFile(String inputFile, String outputFile ,  HashMap<Character,String> map, BTNode root, HashMap<Character, String> chartobi) throws Exception {
		BinaryOut out = new BinaryOut(outputFile);

		assignbinaryvaluetonode( root, chartobi, "");
		BinaryIn reader = new BinaryIn(inputFile);

		while(!reader.isEmpty()) {
			char st= reader.readChar();
			String code = map.get(st);
			for (int i = 0; i<code.length(); i++) {
				//use the write(boolean) method in BinaryOut, if the character is 1, then write(true), if character is 0, then write(false)
				out.write(code.charAt(i)=='1');
			}
		}
		out.flush();
	}

	//when the ASCII code is less than 8, add 0s to the beginning of the string to make it 8 digits
	public static String updatebinary(String b ) {
		while(b.length()<8) {
			b = 0 + b;
		}
		return b;
	}

	public void encode(String inputFile, String outputFile, String freqFile){
		// TODO: Your code here
		HashMap<Character,Integer> map=new HashMap<Character, Integer>(); //this hash map is used to store the character and the frequency it appears in the original file
		HashMap<Character, String> chartobi = new HashMap<Character, String>(); //this hash map is used to store the Huffman code of each character and the character it represents
		//first count the characters and its occurrences in the original file, then create Huffman tree
		character_count(inputFile,outputFile, map);
		BTNode root = createtree(map);
		printTree(root);
		try {
			printFrequencyFile(map, freqFile);
			printEncodedFile(inputFile, outputFile,  chartobi, root, chartobi);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

public static void rewriteOriginal(String inputFile, String outputFile,HashMap<Character,Integer> map ) {
	BTNode root = createtree(map);
	printTree(root);
	BTNode position = root; //this node act like a pointer
	
	BinaryIn in = new BinaryIn(inputFile);
	BinaryOut out = new BinaryOut(outputFile);
	
	while(!in.isEmpty()){ 
		boolean boo = in.readBoolean(); //read the encoded file
		//read the boolean value until reach a leaf, when reach a leaf, then write out the leaf and then reset the pointer back to the root. If did not reach a leaf, keep reading the next boolean value
		if(position.right==null&&position.left==null){
			out.write(position.name);
			out.flush();
			position=root; //reset the pointer back to root
		}
         
		if(boo){
			position=position.right;
		}
		else{
			position=position.left;
		}
	}
	out.flush();
}

	public void decode(String inputFile, String outputFile, String freqFile){
		// TODO: Your code here

		HashMap<Character,Integer> map=new HashMap<Character, Integer>();
		BinaryIn in = new BinaryIn(inputFile);
		BinaryOut out = new BinaryOut(outputFile);
		
		String []s;
		String c;
		try {
			//when decoding, first read the frequency file
			BufferedReader reader = new BufferedReader(new FileReader(freqFile));
			
		while((c=reader.readLine())!=null){ //read the frequency file line by line
			s= c.split(":"); //by the line by the colon
			char x = (char) Integer.parseInt(s[0],2); //change the first part, which is ASKII code, into letter character
			int d = Integer.parseInt(s[1]);
			map.put(x,d); //put character and its frequency into the map
		}
		}catch(IOException e) {
			e.printStackTrace();
		}
	
		//create tree
		BTNode root = createtree(map);
		printTree(root);
		BTNode position = root;
		
		rewriteOriginal(inputFile, outputFile, map);
	}

	public static void main(String[] args) {
		Huffman huffman = new HuffmanSubmit();
		huffman.encode("ur.jpg", "ur.enc", "freq.txt");
		huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");
		// After decoding, both ur.jpg and ur_dec.jpg should be the same. 
		// On linux and mac, you can use `diff' command to check if they are the same.


	}

}
