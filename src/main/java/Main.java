import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        //Bem bem = new Bem("PS4", "Um videogame em ótimo estado");
        Bem bem = Bem.get("bem1");
        bem.setCodigo("bem1");
        System.out.println(bem.getNome());
        System.out.println(bem.getDescricao());


        /*
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new PatriBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

         */
    }
}