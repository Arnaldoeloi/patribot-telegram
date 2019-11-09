import Constants.Constants;
import org.jetbrains.annotations.NotNull;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
//import org.telegram.telegrambots.Constants;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.abilitybots.api.bot.AbilityBot;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.telegram.abilitybots.api.objects.Flag.MESSAGE;
import static org.telegram.abilitybots.api.objects.Flag.REPLY;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;


public class PatriBot extends AbilityBot{
//    public static String BOT_TOKEN = "691988850:AAGwFAI402F7KFhKnrWYoo8tLd1oI0VAgKA";
//    public static String BOT_USERNAME =  "PatrimonioIMDBot";

    private final ResponseHandler responseHandler;
    public PatriBot(){
        super(Constants.BOT_TOKEN, Constants.BOT_USERNAME);
        responseHandler = new ResponseHandler(sender, db);
    }

    public int creatorId() {
        return Constants.CREATOR_ID;
//        return 732622998; //master ID - Arnaldo Souza
    }

    public Reply replyToButtons(){
        Consumer<Update> action = upd -> responseHandler.replyToButtons(getChatId(upd), upd.getCallbackQuery().getData());
        return Reply.of(action, Flag.CALLBACK_QUERY);
    }

    public Reply receiveInput(){
        Consumer<Update> action = upd -> responseHandler.receiveInput(getChatId(upd), upd.getMessage().getText());
//        Consumer<Update> action = upd -> System.out.println(upd.getMessage().getText());
//        return Reply.of(action, update -> update.getMessage().hasText() && !update.getMessage().getText().contains("/"));
        return Reply.of(action, MESSAGE,update -> update.getMessage().hasText() && !update.getMessage().getText().contains("/"));
    }

    /**
     * A reply that says "yuck" to all images sent to the bot.
     */
//    public Reply sayYuckOnImage() {
//        // getChatId is a public utility function in rg.telegram.abilitybots.api.util.AbilityUtils
//        Consumer<Update> action = upd -> silent.send("Yuck", getChatId(upd));
//
//        return Reply.of(action, Flag.PHOTO);
//    }

//    public Ability playWithMe() {
//        String playMessage = "Play with me!";
//
//        return Ability.builder()
//                .name("play")
//                .info("Do you want to play with me?")
//                .privacy(PUBLIC)
//                .locality(ALL)
//                .input(0)
//                .action(ctx -> silent.forceReply(playMessage, ctx.chatId()))
//                // The signature of a reply is -> (Consumer<Update> action, Predicate<Update>... conditions)
//                // So, we  first declare the action that takes an update (NOT A MESSAGECONTEXT) like the action above
//                // The reason of that is that a reply can be so versatile depending on the message, context becomes an inefficient wrapping
//                .reply(upd -> {
//                            // Prints to console
//                            System.out.println("I'm in a reply!");
//                            // Sends message
//                            silent.send("It's been nice playing with you!", upd.getMessage().getChatId());
//
//                        },
//                        // Now we start declaring conditions, MESSAGE is a member of the enum Flag class
//                        // That class contains out-of-the-box predicates for your replies!
//                        // MESSAGE means that the update must have a message
//                        // This is imported statically, Flag.MESSAGE
//                        MESSAGE,
//                        // REPLY means that the update must be a reply, Flag.REPLY
//                        REPLY,
//                        // A new predicate user-defined
//                        // The reply must be to the bot
//                        isReplyToBot(),
//                        // If we process similar logic in other abilities, then we have to make this reply specific to this message
//                        // The reply is to the playMessage
//                        isReplyToMessage(playMessage)
//                )
//                // You can add more replies by calling .reply(...)
//                .build();
//    }

    private Predicate<Update> isReplyToMessage(String message) {
        return upd -> {
            Message reply = upd.getMessage().getReplyToMessage();
            return reply.hasText() && reply.getText().equalsIgnoreCase(message);
        };
    }

    private Predicate<Update> isReplyToBot() {
        return upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername());
    }

    public Ability replyToStart(){
        return Ability
                .builder()
                .name("start")
                .info(Constants.START_DESCRIPTION)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> responseHandler.aguardandoComando(ctx.chatId()))
                .build();
    }


}
