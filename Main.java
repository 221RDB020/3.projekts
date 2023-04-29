// 221RDB020 Jānis Žogots 12.grupa
// 221RDB228 Lauris Senkāns 4.grupa
// 221RDB063 Renārs Dambis 13.grupa
// 221RDB334 Ronalds Jierkis 17.grupa
// 221RDB136 Dainis Kudrjavcevs 1.grupa
// 221RDB353 Pāvels Kudrjavcevs 2.grupa

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

      byte[] inputBuffer = new byte[8192];
      int bytesRead;

      ByteArrayOutputStream compressed = new ByteArrayOutputStream();

      while ((bytesRead = in.read(inputBuffer)) != -1) {
        byte[] encoded = new DeflateAlgorithm().compress(Arrays.copyOf(inputBuffer, bytesRead));
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

      byte[] inputBuffer = new byte[8192];
      int bytesRead;

      ByteArrayOutputStream decompressed = new ByteArrayOutputStream();

      while ((bytesRead = in.read(inputBuffer)) != -1) {
        byte[] decoded = new DeflateAlgorithm().decompress(Arrays.copyOf(inputBuffer, bytesRead));
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
    } catch (IOException ex) {
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
        for (int i = 0; i < k1; i++) {
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
    } catch (IOException ex) {
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
}

class DeflateAlgorithm {
  private LZ77 lz77;

  public DeflateAlgorithm() {
    this.lz77 = new LZ77();
  }

  public byte[] compress(byte[] data) {
    byte[] compressedData = lz77.compress(data);
    return compressedData;
  }

  public byte[] decompress(byte[] compressedData) {
    byte[] decompressedData = null;
    try {
      decompressedData = lz77.decompress(compressedData);
    } catch (IOException e) {
      System.out.println(e);
    }
    return decompressedData;
  }
}

class LZ77 {
  public static final int DEFAULT_BUFF_SIZE = 1024;
  protected int mBufferSize;
  protected StringBuffer mSearchBuffer;

  public LZ77() {
    this(DEFAULT_BUFF_SIZE);
  }

  public LZ77(int buffSize) {
    mBufferSize = buffSize;
    mSearchBuffer = new StringBuffer(mBufferSize);
  }

  private void trimSearchBuffer() {
    if (mSearchBuffer.length() > mBufferSize) {
      mSearchBuffer = mSearchBuffer.delete(0, mSearchBuffer.length() - mBufferSize);
    }
  }

  public byte[] compress(byte[] data) {
    StringBuffer compressedData = new StringBuffer();
    int nextChar;
    String currentMatch = "";
    int matchIndex = 0, tempIndex = 0;
    for (int i = 0; i < data.length; i++) {
      nextChar = data[i] & 0xFF;
      tempIndex = mSearchBuffer.indexOf(currentMatch + (char) nextChar);
      if (tempIndex != -1) {
        currentMatch += (char) nextChar;
        matchIndex = tempIndex;
      } else {
        String codedString = "\0" + matchIndex + "\0" + currentMatch.length() + "\0" + (char) nextChar;
        String concat = currentMatch + (char) nextChar;
        if (codedString.length() <= concat.length()) {
          compressedData.append(codedString);
          mSearchBuffer.append(concat);
          currentMatch = "";
          matchIndex = 0;
        } else {
          currentMatch = concat;
          matchIndex = -1;
          while (currentMatch.length() > 1 && matchIndex == -1) {
            compressedData.append(currentMatch.charAt(0));
            mSearchBuffer.append(currentMatch.charAt(0));
            currentMatch = currentMatch.substring(1, currentMatch.length());
            matchIndex = mSearchBuffer.indexOf(currentMatch);
          }
        }
        trimSearchBuffer();
      }
    }
    if (matchIndex != -1) {
      String codedString = "\0" + matchIndex + "\0" + currentMatch.length() + "\0";
      if (codedString.length() <= currentMatch.length()) {
        compressedData.append(codedString);
      } else {
        compressedData.append(currentMatch);
      }
    }
    return compressedData.toString().getBytes();
  }

  public byte[] decompress(byte[] compressedData) throws IOException {
    StringBuffer decompressedData = new StringBuffer();
    mSearchBuffer = new StringBuffer(mBufferSize);
    ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
    BufferedReader br = new BufferedReader(new InputStreamReader(bais));

    int offset, length, b;
    char nextChar;
    String temp = "";
    while ((b = br.read()) != -1) {
      nextChar = (char) b;
      if (b != '\0') {
        mSearchBuffer.append(nextChar);
        decompressedData.append(nextChar);
        trimSearchBuffer();
      } else {
        String[] off_len = br.readLine().strip().split("\0");
        offset = Integer.parseInt(off_len[0]);
        length = Integer.parseInt(off_len[1]);
        if (offset == 0 && length == 0) {
          decompressedData.append(" ");
        } else if (offset > 0) {
          if (offset + length > mSearchBuffer.length()) {
            length = mSearchBuffer.length() - offset;
          }
          temp = mSearchBuffer.substring(offset, offset + length);
          decompressedData.append(temp);
        } else {
          temp = "";
          for (int i = 0; i < length; i++) {
            int c = br.read();
            temp += (char) c;
            decompressedData.append((char) c);
          }
        }
        mSearchBuffer.append(temp);
        trimSearchBuffer();
      }
    }
    return decompressedData.toString().getBytes();
  }

}
