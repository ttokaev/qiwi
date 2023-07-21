package qiwi.currency.quotes;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        if (args.length != 3 || !args[1].startsWith("--code=") || !args[2].startsWith("--date=")) {
            System.out.println("Неверные аргументы командной строки.");
            System.out.println("Использование: currency_rates --code=??? --date=yyyy-mm-dd");
            return;
        }

        String currencyCode = args[1].substring(7);
        String date = args[2].substring(7);


        try {
            URL url = new URL(String.format("https://www.cbr.ru/scripts/XML_daily.asp?date_req=%s", DataConverter.dash2Slash(date)));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "Windows-1251"));
                StringBuilder xmlContent = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    xmlContent.append(line);
                }

                reader.close();

                JAXBContext jaxbContext = JAXBContext.newInstance(ValCurs.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                ValCurs valCurs = (ValCurs) jaxbUnmarshaller.unmarshal(new StringReader(xmlContent.toString()));

                Arrays.stream(valCurs.getValutes())
                        .filter(valute -> valute.getCharCode().equals(currencyCode))
                        .findFirst()
                        .ifPresent(valute -> System.out.printf("%s (%s): %s", valute.getCharCode(), valute.getName(), valute.getValue())
                );

            } else {
                System.out.println("Ошибка получения содержимого. Response Code: " + responseCode);
            }
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }
}
