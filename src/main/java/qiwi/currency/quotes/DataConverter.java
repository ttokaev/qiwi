package qiwi.currency.quotes;

public class DataConverter {

    public static String dash2Slash(String name) {
        return name.substring(8) + "/" + name.substring(5, 7) + "/" + name.substring(0, 4);
    }
}
