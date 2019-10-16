import java.io.*;
import java.util.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.*; 

class Main {
  public static void main(String[] args) {
    Dict dict = new Dict();
    
    System.out.println("************run tests************");
    try(BufferedReader br = new BufferedReader(new FileReader("test.txt"))) {
        String content = new String(Files.readAllBytes(Paths.get("test.txt"))); 
        String[] inputs = content.split("\n");
        for (String input : inputs) {
          System.out.println("input: "+input);
          System.out.println("output: "+dict.getAnagram(input));
          System.out.println(" ");
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    System.out.println("************end tests************");
  }
}

class Dict {
  final String src = "https://raw.githubusercontent.com/lad/words/master/words";
  String[] dict;
  HashMap<String, List<String>> map;
  
  public Dict() {
    buildDict();
    buildMap();
  }
  
  private void buildMap() {
    map = new HashMap<>();
    
    for (String word : dict) {
      int[] cnt = new int[26];
      for (char c : word.toCharArray()) {
        // 'Jean-Christophe'
        if (Character.toLowerCase(c) - 'a' < 0 ||
            Character.toLowerCase(c) - 'a' > 25)
          continue;
        cnt[Character.toLowerCase(c) - 'a']++;
      }
      
      StringBuilder sb = new StringBuilder();
      for (int i = 0;i < 26;i++) {
        sb.append("#");
        sb.append(cnt[i]);
      }
      String key = sb.toString();
      
      List<String> list = map.getOrDefault(key, new ArrayList<>());
      list.add(word);
      map.put(key, list);
    }
  }
  
  public List<String> getAnagram(String target) {
    int[] cnt = new int[26];
    for (char c : target.toCharArray()) {
      cnt[Character.toLowerCase(c) - 'a']++;
    }
    
    StringBuilder sb = new StringBuilder();
    for (int i = 0;i < 26;i++) {
      sb.append("#");
      sb.append(cnt[i]);
    }
    String key = sb.toString();
    
    if (map.containsKey(key)) return map.get(key);
    else return new ArrayList<>();
  }
  
  private void buildDict() {
    StringBuilder sb = new StringBuilder();
    URLConnection urlConn = null;
    InputStreamReader in = null;
    try {
      URL url = new URL(src);
      urlConn = url.openConnection();
      if (urlConn != null)
        urlConn.setReadTimeout(60 * 1000);
      if (urlConn != null && urlConn.getInputStream() != null) {
        in = new InputStreamReader(urlConn.getInputStream(),
            Charset.defaultCharset());
        BufferedReader bufferedReader = new BufferedReader(in);
        if (bufferedReader != null) {
          int cp;
          while ((cp = bufferedReader.read()) != -1) {
            sb.append((char) cp);
          }
          bufferedReader.close();
        }
      }
    in.close();
    } catch (Exception e) {
      throw new RuntimeException("Exception while calling URL:"+ src, e);
    } 
 
    String all = sb.toString();
    dict = all.split("\n");
  }
  
}
