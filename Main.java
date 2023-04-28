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
        System.out.println("ir");
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

class LZ77 {
  private int slideWindow;
  private int searchBuffer;
  private int outputBuffer;

  public LZ77() {
    this.slideWindow = 32768;
    this.searchBuffer = 32768;
    this.outputBuffer = 32768;
  }

  public byte[] compress(byte[] data) {
    List<Triplet> triplets = new ArrayList<>();
    int index = 0;
    int length = data.length - 1;

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
        index += matchLength + 1;
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
    System.out.println(length);

    while (index < length) {
      Triplet t = readTriplet(compressedData, index);
      index += 3;

      triplets.add(t);
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] bslideWindow = new byte[slideWindow];
    int windowIndex = 0;

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

        baos.write(t.nextSymbol);
        bslideWindow[windowIndex] = t.nextSymbol;
        windowIndex = (windowIndex + 1) % bslideWindow.length;
      }
    }

    return baos.toByteArray();
  }

  private byte[] encodeTriplets(List<Triplet> triplets) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (Triplet t : triplets) {
      if (t.offset == 0 && t.length == 0) {
        baos.write(0);
        baos.write(0);
        baos.write(t.nextSymbol);
      } else {
        int offsetCode = t.offset << 4;
        int lengthCode = t.length;
        if (t.offset < t.length) {
          lengthCode--;
        }
        baos.write(offsetCode >> 8);
        baos.write(offsetCode & 0xFF);
        baos.write(lengthCode >> 4);
        baos.write((lengthCode << 4) | ((int) t.nextSymbol & 0x0F));
      }
    }
    return baos.toByteArray();
  }

  private Triplet readTriplet(byte[] data, int index) {
    int offset = ((data[index] & 0xFF) << 4) | ((data[index + 1] & 0xFF) >> 4);
    int length = ((data[index + 1] & 0x0F) << 8) | (data[index + 2] & 0xFF);
    byte nextSymbol = (byte) ((offset < length) ? (data[index + 3] & 0xFF) : data[index + 2]);
    System.out.printf("%d %d %d \n", offset, length, nextSymbol);

    return new Triplet(offset, length, nextSymbol);
  }

  private class Triplet {
    public int offset;
    public int length;
    public byte nextSymbol;

    public Triplet(int offset, int length, byte nextSymbol) {
      this.offset = offset;
      this.length = length;
      this.nextSymbol = nextSymbol;
    }
  }
}

class HuffmanCoding {
  private Map<Byte, Integer> frequencyTable;
  private Map<Byte, String> codeTable;

  public HuffmanCoding() {
    frequencyTable = new HashMap<>();
    codeTable = new HashMap<>();
  }

  public void buildFrequencyTable(byte[] data) {
    frequencyTable.clear();
    for (byte b : data) {
      frequencyTable.put(b, frequencyTable.getOrDefault(b, 0) + 1);
    }
  }

  public void buildCodeTable() {
    PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();

    for (byte b : frequencyTable.keySet()) {
      int frequency = frequencyTable.get(b);
      HuffmanNode node = new HuffmanNode(frequency, b);
      pq.add(node);
    }

    while (pq.size() > 1) {
      HuffmanNode node1 = pq.poll();
      HuffmanNode node2 = pq.poll();
      HuffmanNode mergedNode = new HuffmanNode(node1, node2);
      pq.add(mergedNode);
    }

    HuffmanNode root = pq.poll();
    generateCodeTable(root, "");
  }

  private void generateCodeTable(HuffmanNode node, String code) {
    if (node.isLeaf()) {
      codeTable.put(node.getByte(), code);
      return;
    }
    generateCodeTable(node.getLeft(), code + "0");
    generateCodeTable(node.getRight(), code + "1");
  }

  public byte[] encode(byte[] data) {
    StringBuilder encodedData = new StringBuilder();
    for (byte b : data) {
      encodedData.append(codeTable.get(b));
    }

    int len = (encodedData.length() + 7) / 8;
    byte[] result = new byte[len];

    for (int i = 0; i < len; i++) {
      int start = i * 8;
      int end = Math.min(start + 8, encodedData.length());
      String byteStr = encodedData.substring(start, end);
      result[i] = (byte) Integer.parseInt(byteStr, 2);
    }

    return result;
  }

  public byte[] decode(byte[] encodedData) {
    StringBuilder binaryStr = new StringBuilder();
    PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();

    for (byte b : encodedData) {
      String byteStr = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
      binaryStr.append(byteStr);
    }

    HuffmanNode node = pq.peek();
    StringBuilder decodedData = new StringBuilder();
    for (int i = 0; i < binaryStr.length(); i++) {
      if (node.isLeaf()) {
        decodedData.append(node.getByte());
        node = pq.peek();
      }
      if (binaryStr.charAt(i) == '0') {
        node = node.getLeft();
      } else {
        node = node.getRight();
      }
    }
    decodedData.append(node.getByte());

    byte[] result = new byte[decodedData.length()];
    for (int i = 0; i < decodedData.length(); i++) {
      result[i] = (byte) decodedData.charAt(i);
    }

    return result;
  }
}

class HuffmanNode implements Comparable<HuffmanNode> {
  private int frequency;
  private byte b;
  private HuffmanNode left;
  private HuffmanNode right;

  public HuffmanNode(int frequency, byte b) {
    this.frequency = frequency;
    this.b = b;
  }

  public HuffmanNode(HuffmanNode left, HuffmanNode right) {
    this.frequency = left.frequency + right.frequency;
    this.left = left;
    this.right = right;
  }

  public boolean isLeaf() {
    return left == null && right == null;
  }

  public int compareTo(HuffmanNode other) {
    return this.frequency - other.frequency;
  }

  public byte getByte() {
    return b;
  }

  public HuffmanNode getLeft() {
    return left;
  }

  public HuffmanNode getRight() {
    return right;
  }
}