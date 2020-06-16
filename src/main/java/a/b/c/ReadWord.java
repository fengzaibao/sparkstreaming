package a.b.c;

import java.io.*;
import java.util.Random;

public class ReadWord {

    public String readword() {
        FileReader fileReader = null;
        BufferedReader bufferedReader=null;
        Random r = new Random();
        String words = "";
        try {
            fileReader = new FileReader("data2/hanzi.txt");
            bufferedReader = new BufferedReader(fileReader);
            String txt = null;
            while ((txt = bufferedReader.readLine()) != null) {
                words = txt.substring(r.nextInt(2000), (txt.length() - 1)-500);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return words;
    }

}
