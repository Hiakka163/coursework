import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LinksSuggester {
public static final int TYPE_KEYWORD = 0;
public static final int TYPE_TITLE = 1;
public static final int TYPE_URL = 2;
List<Suggest> suggestList = new ArrayList<>();
    public LinksSuggester(File file) throws IOException, WrongLinksFormatException {
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Scanner sc;
        int i = 0;
        while ((line = reader.readLine())!= null) {
            sc = new Scanner(line);
            sc.useDelimiter("\t");
            Suggest suggest = new Suggest();
     while (sc.hasNext()) {
         String s = sc.next();
         switch (i) {
             case TYPE_KEYWORD:{
                 suggest.setKeyWord(s);
                 break;
             }
             case TYPE_TITLE: {
                 suggest.setTitle(s);
                 break;
             }
             case TYPE_URL: {
                 suggest.setUrl(s);
                 break;
             }
             default:
                throw new WrongLinksFormatException("Не правильно введены данные, необходимо что бы было введено 3 данные!");
         }
         i++;
     }
     i = 0;
     suggestList.add(suggest);
                }
        reader.close();
    }
    public List<Suggest> suggest(String text) {
        List<Suggest> list = new ArrayList<>();
        for (Suggest suggest : suggestList) {
            if (text.toLowerCase().contains(suggest.getKeyWord())) {
                list.add(suggest);
            }
        }

        return list;
    }

    public List<Suggest> getSuggestList() {
        return suggestList;
    }
}