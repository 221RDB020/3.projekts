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
        new Compressor().comp(sourceFile, resultFile);
        break;
      case "decomp":
        System.out.print("archive name: ");
        sourceFile = sc.next();
        System.out.print("file name: ");
        resultFile = sc.next();
        new Compressor().decomp(sourceFile, resultFile);
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
  
  static class Compressor {
    public void comp(String sourceFile, String resultFile) {
      try {
          FileInputStream in = new FileInputStream(sourceFile);
          FileOutputStream out = new FileOutputStream(resultFile);

          byte[] inputBuffer = new byte[1024];
          byte[] encodedBuffer = new byte[1024];

          int bytesRead;
          
          ByteArrayOutputStream compressed = new ByteArrayOutputStream();

          while ((bytesRead = in.read(inputBuffer)) != -1) {
              byte[] encoded = new LZ77().encode(inputBuffer);
              compressed.write(encoded);
          }

          HuffmanCoding huffman = new HuffmanCoding();

          byte[] compressedBytes = compressed.toByteArray();
          byte[] encodedHuffman = huffman.encode(compressedBytes);
          
          out.write(encodedHuffman);
          in.close();
          out.close();
      } catch (IOException e) {
          System.out.println(e.getMessage());
      }
    }

    public void decomp(String sourceFile, String resultFile) {
      //TODO: implement decomp algorythm
    }
  }

  static class LZ77 {
    public byte[] encode(byte[] data) {
      return null;
    }

    public byte[] decode(byte[] data) {
      return null;
    }
  }
  
  static class LZ77Token {
    public LZ77Token(int matchOffset, int matchLength, byte b) {
    }

    public int getOffset() {
      return 0;
    }

    public Byte getLiteral() {
      return null;
    }

    public int getLength() {
      return 0;
    }
  }

  static class SuffixArray {
    public static int byteArrayToInt(byte[] copyOfRange) {
      return 0;
    }

    public static Collection<? extends Byte> intToByteArray(int offset) {
      return null;
    }
  }

  static class HuffmanCoding {
    public byte[] encode(byte[] compressedBytes) {
      return null;
    }

    public byte[] decode(byte[] compressedBytes) {
      return null;
    }
    //TODO: implement HuffmanCoding algorythm
  }

  static class HuffmanTree {
    //TODO: implement HuffmanTree algorythm
  }
}