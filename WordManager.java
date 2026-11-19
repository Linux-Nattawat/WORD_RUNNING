import java.util.ArrayList;
import java.util.Collections;

public class WordManager {

    public static ArrayList<Word> getRandomGameWords() {
        ArrayList<Word> WordList = new ArrayList<>();

        WordList.add(new Word("bug"));
        WordList.add(new Word("byte"));
        WordList.add(new Word("node"));
        WordList.add(new Word("path"));
        WordList.add(new Word("host"));
        WordList.add(new Word("json"));
        WordList.add(new Word("ping"));
        WordList.add(new Word("java"));
        WordList.add(new Word("code"));
        WordList.add(new Word("game"));
        WordList.add(new Word("loop"));
        WordList.add(new Word("main"));
        WordList.add(new Word("data"));
        WordList.add(new Word("port"));
        WordList.add(new Word("root"));
        WordList.add(new Word("null"));
        WordList.add(new Word("true"));
        WordList.add(new Word("void"));
        WordList.add(new Word("file"));
        WordList.add(new Word("user"));
        WordList.add(new Word("html"));
        WordList.add(new Word("func"));
        WordList.add(new Word("char"));
        WordList.add(new Word("push"));
        WordList.add(new Word("socket"));
        WordList.add(new Word("server"));
        WordList.add(new Word("client"));
        WordList.add(new Word("thread"));
        WordList.add(new Word("action"));
        WordList.add(new Word("method"));
        WordList.add(new Word("script"));
        WordList.add(new Word("commit"));
        WordList.add(new Word("branch"));
        WordList.add(new Word("docker"));
        WordList.add(new Word("python"));
        WordList.add(new Word("binary"));
        WordList.add(new Word("domain"));
        WordList.add(new Word("string"));
        WordList.add(new Word("object"));
        WordList.add(new Word("cookie"));
        WordList.add(new Word("return"));
        WordList.add(new Word("network"));
        WordList.add(new Word("running"));
        WordList.add(new Word("program"));
        WordList.add(new Word("display"));
        WordList.add(new Word("winner"));
        WordList.add(new Word("protocol"));


        Collections.shuffle(WordList);

        ArrayList<Word> selectedWords = new ArrayList<>();

        for (int i = 0; i < 10; i++) selectedWords.add(WordList.get(i));

        return selectedWords; 
    }
}