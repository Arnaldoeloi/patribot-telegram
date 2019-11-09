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
import repository.BemRepository;
import repository.CategoriaRepository;
import repository.Conexao;
import repository.LocalizacaoRepository;

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
        /**
        * Cria o banco e suas tabelas caso não existam.
        *
        * */
        Conexao conexao = new Conexao();
        LocalizacaoRepository localizacaoRepository = new LocalizacaoRepository(conexao);
        localizacaoRepository.criarTabela();
        CategoriaRepository categoriaRepository= new CategoriaRepository(conexao);
        categoriaRepository.criarTabela();
        BemRepository bemRepository = new BemRepository(conexao);
        bemRepository.criarTabela();

        /**
         * Cria o responseHandler, uma classe externa para tratar as entradas e saídas.
         */
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
