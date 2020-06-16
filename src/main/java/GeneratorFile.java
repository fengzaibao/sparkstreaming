import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GeneratorFile {

    public static final String FILE_PATH = "data/input/";//文件指定存放的路径

    public static void main(String[] args) {
        FileOutputStream outFile = null;

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Random r = new Random();
        String txts[] = {"Shares in major European car makers fell on Monday following a threat by US President Trump to tax their vehicles.\n" +
                "Mr Trump said if the EU \"wants to further increase their already massive tariffs and barriers on US companies... we will simply apply a tax on their cars\".\n" +
                "The US is an important market for cars built in the country. US demand for British-built cars rose by 7% in 2017, with exports reaching almost 210,000, and the US is now the UK's second-largest trading partner after the EU, taking 15.7% of car exports.", "old reality is setting in for the GOP: The only thing standing between a serious electoral setback and a truly historic shellacking in November might be Special Counsel Robert Mueller's sprawling investigation into Russia's interference in the 2016 elections.\n" +
                "It's easy to lose sight of this in the day-to-day Choose Your Own Misadventure cacophony of the Trump White House, but the Mueller investigation really is a guillotine blade set to decapitate the administration and congressional Republicans at any moment over the next six months. The White House knows it, which is probably why the president is yelling into the Twitter void about tariffs and trade wars as senior aides continue to disappear back into the private sector with the names of expensive lawyers stenciled on their palms. At this rate, the Trump administration is going to be like some sitcom that replaces every single cast member except the star and dares you not to notice."};
        try {
            for (int i = 0; i < 5; i++) {
                String filename = df.format(new Date()) + ".txt";
                File file = creatFile(FILE_PATH, filename);
                outFile = new FileOutputStream(file);

                String str = txts[r.nextInt(txts.length)];

                outFile.write(str.getBytes());
                outFile.flush();
                outFile.close();
                Thread.sleep(r.nextInt(5000));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File creatFile(String filePath, String fileName) {
        File folder = new File(filePath);
        //文件夹路径不存在
        if (!folder.exists() && !folder.isDirectory()) {
            System.out.println("文件夹路径不存在，创建路径:" + filePath);
            folder.mkdirs();
        } else {
            System.out.println("文件夹路径存在:" + filePath);
        }
        // 如果文件不存在就创建
        File file = new File(filePath + fileName);
        if (!file.exists()) {
            System.out.println("文件不存在，创建文件:" + filePath + fileName);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("文件已存在，文件为:" + filePath + fileName);
        }
        return file;
    }
}
