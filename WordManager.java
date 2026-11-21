import java.util.ArrayList;
import java.util.Collections;

public class WordManager {

    public static ArrayList<Word> getRandomGameWords() {
        ArrayList<Word> WordList = new ArrayList<>();

        WordList.add(new Word("bug"));
        WordList.add(new Word("apple"));
        WordList.add(new Word("lion"));
        WordList.add(new Word("banana"));
        WordList.add(new Word("host"));
        WordList.add(new Word("json"));
        WordList.add(new Word("ping"));
        WordList.add(new Word("java"));
        WordList.add(new Word("code"));
        WordList.add(new Word("game"));
        WordList.add(new Word("loop"));
        WordList.add(new Word("hello"));
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
        WordList.add(new Word("zoo"));
        WordList.add(new Word("super"));
        WordList.add(new Word("clone"));
        WordList.add(new Word("cpu"));
        WordList.add(new Word("open"));
        WordList.add(new Word("milk"));
        WordList.add(new Word("slow"));
        WordList.add(new Word("com"));
        WordList.add(new Word("git"));
        WordList.add(new Word("ant"));
        WordList.add(new Word("bean"));
        WordList.add(new Word("brid"));
        WordList.add(new Word("cat"));
        WordList.add(new Word("dog"));
        WordList.add(new Word("cow"));
        WordList.add(new Word("cookie"));
        WordList.add(new Word("turn"));
        WordList.add(new Word("net"));
        WordList.add(new Word("run"));
        WordList.add(new Word("what"));
        WordList.add(new Word("free"));
        WordList.add(new Word("winner"));
        WordList.add(new Word("tree"));


        Collections.shuffle(WordList);

        ArrayList<Word> selectedWords = new ArrayList<>();

        for (int i = 0; i < 10; i++) selectedWords.add(WordList.get(i));

        return selectedWords; 
    }
}