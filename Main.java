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
    // counts frequency of each character
    Map<Character, Integer> frequencyMap = new HashMap<>();

    try {
      FileInputStream inputFile = new FileInputStream(sourceFile);
      int c;

      while ((c = inputFile.read()) != -1) {
        char character = (char) c;
        frequencyMap.put(character, frequencyMap.getOrDefault(character,0) + 1);
      }

      System.out.println(frequencyMap);

      inputFile.close();
    } catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
  }

  public static void decomp(String sourceFile, String resultFile) {
    
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
    // TODO insert information about authors
    System.out.println("221RDB020 Jānis Žogots 12.grupa");
    System.out.println("111RDB111 Ilze Programmētāja");
  }
}

