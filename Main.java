import java.io.*;
import java.util.*;

public class Main {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String choiseStr;
    String sourceFile, resultFile, firstFile, secondFile;
    
    loop: while (true) {
      
      choiseStr = sc.next();
                
      switch (choiseStr) {
      case "comp":
        System.out.print("source file name: ");
        sourceFile = sc.next();
        System.out.print("archive name: ");
        resultFile = sc.next();
        comp(sourceFile, resultFile);
        break;
      case "decomp":
        System.out.print("archive name: ");
        sourceFile = sc.next();
        System.out.print("file name: ");
        resultFile = sc.next();
        decomp(sourceFile, resultFile);
        break;
      case "size":
        System.out.print("file name: ");
        sourceFile = sc.next();
        size(sourceFile);
        break;
      case "equal":
        System.out.print("first file name: ");
        firstFile = sc.next();
        System.out.print("second file name: ");
        secondFile = sc.next();
        System.out.println(equal(firstFile, secondFile));
        break;
      case "about":
        about();
        break;
      case "exit":
        break loop;
      }
    }

    sc.close();
  }

  public static void comp(String sourceFile, String resultFile) {
    try {
      FileInputStream in = new FileInputStream(sourceFile);
      FileOutputStream out = new FileOutputStream(resultFile);

      byte[] inputBuffer = new byte[1024];
      int bytesRead;

      ByteArrayOutputStream compressed = new ByteArrayOutputStream();

      while ((bytesRead = in.read(inputBuffer)) != -1) {
          byte[] encoded = new DeflateAlgorithm().compress(inputBuffer);
          compressed.write(encoded);
      }

      byte[] compressedBytes = compressed.toByteArray();

      out.write(compressedBytes);
      in.close();
      out.close();
    } catch (IOException e) {
        System.out.println(e.getMessage());
    }
  }

  public static void decomp(String sourceFile, String resultFile) {
    try {
      FileInputStream in = new FileInputStream(sourceFile);
      FileOutputStream out = new FileOutputStream(resultFile);

      byte[] inputBuffer = new byte[1024];
      int bytesRead;

      ByteArrayOutputStream decompressed = new ByteArrayOutputStream();

      while ((bytesRead = in.read(inputBuffer)) != -1) {
          byte[] decoded = new DeflateAlgorithm().decompress(inputBuffer);
          decompressed.write(decoded);
      }

      byte[] decompressedBytes = decompressed.toByteArray();

      out.write(decompressedBytes);
      in.close();
      out.close();
    } catch (IOException e) {
        System.out.println(e.getMessage());
    }
  }

  public static void size(String sourceFile) {
    try {
      FileInputStream f = new FileInputStream(sourceFile);
      System.out.println("size: " + f.available());
      f.close();
    }
    catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
    
  }
  
  public static boolean equal(String firstFile, String secondFile) {
    try {
      FileInputStream f1 = new FileInputStream(firstFile);
      FileInputStream f2 = new FileInputStream(secondFile);
      int k1, k2;
      byte[] buf1 = new byte[1000];
      byte[] buf2 = new byte[1000];
      do {
        k1 = f1.read(buf1);
        k2 = f2.read(buf2);
        if (k1 != k2) {
          f1.close();
          f2.close();
          return false;
        }
        for (int i=0; i<k1; i++) {
          if (buf1[i] != buf2[i]) {
            f1.close();
            f2.close();
            return false;
          }
            
        }
      } while (k1 == 0 && k2 == 0);
      f1.close();
      f2.close();
      return true;
    }
    catch (IOException ex) {
      System.out.println(ex.getMessage());
      return false;
    }
  }
  
  public static void about() {
    System.out.println("221RDB020 Jānis Žogots 12.grupa");
    System.out.println("221RDB228 Lauris Senkāns 4.grupa");
    System.out.println("221RDB063 Renārs Dambis 13.grupa");
    System.out.println("221RDB334 Ronalds Jierkis 17.grupa");
    System.out.println("221RDB136 Dainis Kudrjavcevs 1.grupa");
    System.out.println("221RDB353 Pāvels Kudrjavcevs 2.grupa");
  }
  
  public static class DeflateAlgorithm {
    private LZ77 lz77;
    private HuffmanCoding huffman;

    public DeflateAlgorithm() {
        this.lz77 = new LZ77();
        this.huffman = new HuffmanCoding();
    }

    public byte[] compress(byte[] data) {
        byte[] compressedData = lz77.compress(data);
        compressedData = huffman.encode(compressedData);
        return compressedData;
    }

    public byte[] decompress(byte[] compressedData) {
        byte[] decompressedData = huffman.decode(compressedData);
        decompressedData = lz77.decompress(compressedData);
        return decompressedData;
    }
  }

  public static class LZ77 {
    private int slideWindow;
    private int searchBuffer;
    private int outputBuffer;

    public LZ77() {
        //TODO: Initialize slide window, search buffer, and output buffer
    }

    public byte[] compress(byte[] data) {
        //TODO: Compress data using LZ77 algorithm
        return null; //compressedData;
    }

    public byte[] decompress(byte[] compressedData) {
        //TODO: Decompress compressedData using LZ77 algorithm
        return null; //decompressedData;
    }
  }

  public static class HuffmanCoding {
    private Map<Byte, Integer> frequencyTable;
    private Map<Byte, String> codeTable;

    public HuffmanCoding() {
        //TODO: Initialize frequency table and code table
    }

    public void buildFrequencyTable(byte[] data) {
        //TODO: Build frequency table for data
    }

    public void buildCodeTable() {
        //TODO: Build code table using frequency table
    }

    public byte[] encode(byte[] data) {
        //TODO: Encode data using Huffman coding
        return null; //encodedData;
    }

    public byte[] decode(byte[] encodedData) {
        //TODO: Decode encodedData using Huffman coding
        return null; //decodedData;
    }
  }
}