package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     *
     * Implement this method to read the file referenced by filename character by character, and store a sorted ArrayList of CharFreq objects, sorted by frequency, in sortedCharFreqList. Characters that do not appear in the input file will not appear in your ArrayList.
     * Notice that your provided code begins by setting the file with StdIn. You can now use methods like StdIn.hasNextChar() and StdIn.readChar() which will operate on the file as if it was standard input.
     * Also notice that there are only 128 ASCII values. This means that you can keep track of the number of occurrences of each character in an array of size 128. You can use a char as an array index, and it will automatically convert to the corresponding ASCII int value. You can convert an ASCII int value “num” back into its corresponding char with (char) num.
     * The Huffman Coding algorithm does not work when there is only 1 distinct character. For this specific case, you must add a different character with probOcc 0 to your ArrayList, so you can build a valid tree and encode properly later. For this assignment, simply add the character with ASCII value one more than the distinct character. If you are already at ASCII value 127, wrap around to ASCII 0. DO NOT add more than one of these, and also DO NOT add any characters with frequency 0 in any normal input case.
     * Because the CharFreq object has been implemented to compare based on probOcc primarily, you can simply use Collections.sort(list) before returning your final ArrayList. You do not need to implement your own sorting method.
     * Methods provided to you:
     *
     * writeBitString
     * This method takes in a file name and a string consisting of the characters ‘1’ and ‘0’, and writes the string to the file.
     * You must use this provided method to write your encoding to your output file, and must not try to write your string to a file.
     * Note that it does not actually write the characters ‘1’ and ‘0’, and actually writes in bits.
     * The file name given does not need to exist yet, and if it doesn’t the method will create a new file. If the file exists, the method will overwrite it.
     * Do not edit this method.
     * readBitString
     * This method takes in a file name containing an encoded message, and returns a string consisting of the characters ‘1’ and ‘0’.
     * You must use this provided method to recover your encoded string from your input file, and must not try to read the encoded file yourself.
     * Note that it reads the file byte by byte and converts the bits back into characters.
     * The given file name must exist, and it must have been written to by writeBitString already.
     * Do not edit this method.
     *
     */
    public void makeSortedList() {
        sortedCharFreqList = new ArrayList<CharFreq>();
        int[] freq = new int[128];
        StdIn.setFile(fileName);

        int size = 0;
        while (StdIn.hasNextChar()) {
            char c = StdIn.readChar();
            size++;
            freq[c]++;
        }
        for (int i = 0; i < freq.length; i++) {
            if (freq[i] > 0) {
                sortedCharFreqList.add(new CharFreq((char) i, freq[i] / (double) size));
                if( (double) freq[i] / size == 1.0){
                    int ind = i+1;
                    if(ind == 128) {
                        ind = 0;
                    }




                    sortedCharFreqList.add(new CharFreq((char) (ind), 0));
                }

            }
        }

        Collections.sort(sortedCharFreqList);
    }





	/* Your code goes here */



    public void makeTree() {
        /**
         * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
         * in huffmanRoot
         * Start two empty queues: Source and Target
         * Create a node for each character present in the input file, each node contains the character and its occurrence probability.
         * Enqueue the nodes in the Source queue in increasing order of occurrence probability.
         * repeat until the Source queue is empty and the Target queue has only one node.
         * Dequeue from either queue or both the two nodes with the smallest occurrence probability. If the front node of Source and Target have the same occurrence probability, dequeue from Source first.
         * Create a new node whose character is null and occurrence probability is the sum of the occurrence probabilities of the two dequeued nodes. Add the two dequeued nodes as children: the first dequeued node as the left child and the second dequeued node as the right child.
         * Enqueue the new node into Target
         */

        Queue<TreeNode> source = new Queue();

        for (int i = 0; i < sortedCharFreqList.size(); i++) {
            CharFreq charFreqs = sortedCharFreqList.get(i);
            TreeNode node = new TreeNode(charFreqs, null, null);
            source.enqueue(node);
        }

        Queue<TreeNode> target = new Queue();

        while (!source.isEmpty() || target.size() != 1) {
            TreeNode left, right;
            if (target.isEmpty() || (!source.isEmpty() && source.peek().getData().getProbOcc() <= target.peek().getData().getProbOcc())) {
                left = source.dequeue();
            } else {
                left = target.dequeue();
            }
            if (target.isEmpty() || (!source.isEmpty() && source.peek().getData().getProbOcc() <= target.peek().getData().getProbOcc())) {
                right = source.dequeue();
            } else {
                right = target.dequeue();
            }
            TreeNode pnode = new TreeNode(new CharFreq(null, left.getData().getProbOcc() + right.getData().getProbOcc()), left, right);
            target.enqueue(pnode);
        }
        huffmanRoot = target.dequeue();
    }

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    public void makeEncodings() {
        encodings = new String[128];
        recurse(huffmanRoot, "");

	/* Your code goes here */
    }

    private void recurse(TreeNode root, String code)
    {
        if(root == null)
        {
            return;
        }
        if(root.getLeft() == null && root.getRight() == null)
        {
            encodings[root.getData().getCharacter()] = code;
        }
        else
        {
            recurse(root.getLeft(), code + "0");
            recurse(root.getRight(), code + "1");
        }



    }

    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);
        String bitString = "";
        while (StdIn.hasNextChar()) {
            char c = StdIn.readChar();
            bitString += encodings[c];

        }
        writeBitString(encodedFile, bitString);

    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        //Implement this method to take in your encoded file and use huffmanRoot, and print out the decoding. If done correctly, the decoding will be the same as the contents of the text file used for encoding.
        //Notice that your provided code begins by setting the output file with StdOut. You can now use methods like StdOut.println() and StdOut.print() which will operate on the decodings file as if it was standard output.
        //You must start your method using the provided readBitString method in order to get the string of ones and zeros from the encoded file. DO NOT try to read the encoded file manually.
        //You must then use your tree and the procedure outlined above to decode the bit string into characters, according to the tree’s encoding scheme.
        StdOut.setFile(decodedFile);
        String bitString = readBitString(encodedFile);
        TreeNode root = huffmanRoot;
        String ans = "";
        for(int i = 0; i < bitString.length(); i++)
        {
            if(bitString.charAt(i) == '0')
            {
                root = root.getLeft();
            }
            else
            {
                root = root.getRight();
            }
            if(root.getLeft() == null && root.getRight() == null)
            {
                ans += (char)root.getData().getCharacter();
                root = huffmanRoot;

            }
        }
        decodedFile=ans;
        StdOut.print(decodedFile);




    }

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";

        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}
