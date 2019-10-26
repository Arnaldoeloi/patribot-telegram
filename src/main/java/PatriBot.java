import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class PatriBot extends TelegramLongPollingBot{

    public void onUpdateReceived(Update update) {
//        System.out.println(update.getMessage().getText());
//        System.out.println(update.getMessage().getFrom().getFirstName());
        String command = update.getMessage().getText();
        SendMessage message = new SendMessage();

        if(command.equals("/myname")){
            message.setText(update.getMessage().getFrom().getFirstName());
        }

        message.setChatId(update.getMessage().getChatId());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public String getBotUsername() {
        return "PatrimonioIMDBot";
    }

    public String getBotToken() {
        return "691988850:AAGwFAI402F7KFhKnrWYoo8tLd1oI0VAgKA";
    }
}
