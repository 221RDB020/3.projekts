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
    byte[] newData = Arrays.copyOf(data, data.length + 1);
    newData[newData.length - 1] = ' ';
    byte[] compressedData = lz77.compress(newData);
    return compressedData;
  }

  public byte[] decompress(byte[] compressedData) {
    byte[] decompressedData = lz77.decompress(compressedData);
    return decompressedData;
  }
}

class LZ77 {
  private int slideWindow;
  private int searchBuffer;
  private int outputBuffer;

  public LZ77() {
    this.slideWindow = 32768;
    this.searchBuffer = 32768;
    this.outputBuffer = 32767;
  }

  public byte[] compress(byte[] data) {
    List<Triplet> triplets = new ArrayList<>();
    int index = 0;
    int length = data.length;

    while (index < length) {
      int matchIndex = -1;
      int matchLength = -1;

      for (int j = Math.max(0, index - searchBuffer); j < index; j++) {
        int len = 0;
        while (index + len < length && data[j + len] == data[index + len] && len < outputBuffer) {
          len++;
        }
        if (len > matchLength) {
          matchIndex = j;
          matchLength = len;
        }
      }
      if (matchLength > 0) {
        triplets.add(new Triplet(index - matchIndex, matchLength, data[index + matchLength]));
        index += matchLength;
      } else {
        triplets.add(new Triplet(0, 0, data[index]));
        index++;
      }
    }
    return encodeTriplets(triplets);
  }

  public byte[] decompress(byte[] compressedData) {
    List<Triplet> triplets = new ArrayList<>();
    int index = 0;
    int length = compressedData.length;

    while (index < length) {
      if (index + 2 >= length) {
        break;
      }

      Triplet t = readTriplet(compressedData, index, length);
      index += 3;

      if (t.offset == 0 && t.length == 0) {
        if (t.nextSymbol == ' ') {
          break;
        }
      }

      triplets.add(t);
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] bslideWindow = new byte[slideWindow];
    int windowIndex = 0;
    boolean fullyDecompressed = false;

    for (Triplet t : triplets) {
      if (t.offset == 0 && t.length == 0) {
        baos.write(t.nextSymbol);
        bslideWindow[windowIndex] = t.nextSymbol;
        windowIndex = (windowIndex + 1) % bslideWindow.length;
      } else {
        int start = windowIndex - t.offset;
        if (start < 0) {
          start += bslideWindow.length;
        }

        for (int i = 0; i < t.length; i++) {
          byte b = bslideWindow[start];
          baos.write(b);
          bslideWindow[windowIndex] = b;
          windowIndex = (windowIndex + 1) % bslideWindow.length;
          start = (start + 1) % bslideWindow.length;
        }

        byte sb = 0;
        if (t.offset < t.length) {
          if (start < 0) {
            start += bslideWindow.length;
          }
          sb = bslideWindow[start];
        } else {
          if (index < length) {
            sb = compressedData[index++];
          } else {
            fullyDecompressed = true;
          }
        }

        baos.write(sb);
        bslideWindow[windowIndex] = sb;
        windowIndex = (windowIndex + 1) % bslideWindow.length;
      }
      if (fullyDecompressed) {
        break;
      }
    }

    return baos.toByteArray();
  }

  private byte[] encodeTriplets(List<Triplet> triplets) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (Triplet t : triplets) {
      if (t.offset == 0 && t.length == 0) {
        baos.write(0);
        baos.write(t.nextSymbol);
      } else {
        int offsetLength = 0;
        int lenLength = 0;

        int offset = t.offset;
        while (offset > 0) {
          offsetLength++;
          offset >>>= 1;
        }

        int len = t.length;
        while (len > 0) {
          lenLength++;
          len >>>= 1;
        }

        int code = (offsetLength << 4) | lenLength;
        baos.write(code);

        offset = t.offset;
        for (int i = offsetLength - 1; i >= 0; i--) {
          baos.write((offset >> (i * 8)) & 0xff);
        }

        len = t.length;
        for (int i = lenLength - 1; i >= 0; i--) {
          baos.write((len >> (i * 8)) & 0xff);
        }

        baos.write(t.nextSymbol);
      }
    }
    return baos.toByteArray();
  }

  private Triplet readTriplet(byte[] data, int index, int length) {
    byte code = data[index];
    if ((code & 0xf) == 0) {
      return new Triplet(0, 0, data[index + 1]);
    } else {
      int offsetLength = (code >> 4) & 0xf;
      int lenLength = code & 0xf;
      int offset = 0;
      for (int i = 0; i < offsetLength; i++) {
        offset |= ((data[index + 1 + i] & 0xff) << ((offsetLength - i - 1) * 8));
      }

      int len = 0;
      for (int i = 0; i < lenLength; i++) {
        len |= ((data[index + 1 + offsetLength + i] & 0xff) << ((lenLength - i - 1) * 8));
      }

      byte nextSymbol = data[index + 1 + offsetLength + lenLength];
      return new Triplet(offset, len, nextSymbol);
    }
  }

  private static class Triplet {
    public final int offset;
    public final int length;
    public final byte nextSymbol;

    public Triplet(int offset, int length, byte nextSymbol) {
      this.offset = offset;
      this.length = length;
      this.nextSymbol = nextSymbol;
    }
  }
}