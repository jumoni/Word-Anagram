import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 * Main class executes some tests.
 *
 * @author liwen feng
 * @version 2.0
 */
class Main {
    public static void main(String[] args) {
        Dict dict = new Dict();

        /* uncomment this part to run in coderpad */
        // String test = "ppeall";
        // System.out.println("input: " + test);
        // System.out.println("output: " + dict.getAnagram(test));
        /* uncomment this part to run in coderpad */

        /* comment this part to run in coderpad */
        System.out.println("************run tests************");
        try {
            BufferedReader br = new BufferedReader(new FileReader("test.txt"));
            String content = new String(Files.readAllBytes(Paths.get("test.txt")));
            String[] inputs = content.split("\n");
            for (String input : inputs) {
                System.out.println("input: " + input);
                System.out.println("output: " + dict.getAnagram(input));
                System.out.println(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("************end tests************");
        /* comment this part to run in coderpad */
    }
}

/**
 * This class creates a dictionary of words and
 * implements searching for the word anagrams.
 */
class Dict {
    /**
     * The link for downloading all the words.
     */
    final String src = "https://raw.githubusercontent.com/lad/words/master/words";
    /**
     * An array of all the words.
     */
    String[] dict;
    /**
     * A map built from the dictionary
     * key: hashcode using the lowercase letter frequencies
     * value: a list of words
     */
    HashMap<String, List<String>> map;

    public Dict() {
        buildDict();
        buildMap();
    }

    /**
     * Builds the hashmap
     */
    private void buildMap() {
        map = new HashMap<>();

        for (String word : dict) {
            int[] cnt = new int[26];
            for (char c : word.toCharArray()) {
                // deal with 'Jean-Christophe'
                if (Character.toLowerCase(c) - 'a' < 0 ||
                        Character.toLowerCase(c) - 'a' > 25)
                    continue;
                cnt[Character.toLowerCase(c) - 'a']++;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 26; i++) {
                sb.append("#");
                sb.append(cnt[i]);
            }
            String key = sb.toString();

            List<String> list = map.getOrDefault(key, new ArrayList<>());
            // skip one letter word
            if (word.length() > 1)
                list.add(word);
            map.put(key, list);
        }
    }

    /**
     * Builds the dict
     */
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
            throw new RuntimeException("Exception while calling URL:" + src, e);
        }

        String all = sb.toString();
        dict = all.split("\n");
    }

    /**
     *
     * Gets all possible word anagrams given the input set.
     * @param target the input characters set
     * @return a list of all possible English words from the dictionary
     *         that could be composed of these characters
     */
    public List<String> getAnagram(String target) {
        Set<String> res = new HashSet<>();

        for (int i = 0; i < target.length(); i++) {
            for (int j = i + 1; j <= target.length(); j++) {
                String tmp = target.substring(i, j);
                int[] cnt = new int[26];
                for (char c : tmp.toCharArray()) {
                    cnt[Character.toLowerCase(c) - 'a']++;
                }
                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < 26; k++) {
                    sb.append("#");
                    sb.append(cnt[k]);
                }
                String key = sb.toString();

                if (map.containsKey(key)) res.addAll(map.get(key));
            }
        }

        return new ArrayList<>(res);
    }

}
