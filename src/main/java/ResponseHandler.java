import Constants.Constants;
import model.Bem;
import model.Categoria;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import repository.BemRepository;
import repository.CategoriaRepository;
import repository.Conexao;


import java.util.ArrayList;
import java.util.Map;

public class ResponseHandler {
    private final MessageSender sender;
    private final Map<Long, ChatStateMachine> chatStates;
    private ArrayList<String> commandsHistory;

    public ResponseHandler(MessageSender sender, DBContext db){
        this.sender = sender;
        this.commandsHistory=new ArrayList<String>();
        chatStates = db.getMap(Constants.CHAT_STATE_MACHINE);
    }

    public void aguardandoComando(long chatId) {

        try {
            chatStates.put(chatId, ChatStateMachine.AGUARDANDO_COMANDO);
            sender.execute(new SendMessage()
                    .setText(Constants.START_REPLY)
                    .setChatId(chatId));

            sender.execute(new SendMessage()
                    .setText(Constants.ASK_FOR_COMMAND)
                    .setChatId(chatId)
                    .setReplyMarkup(KeyboardFactory.ReplyKeyboardWithCommandButtons())
            );

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void replyToButtons(long chatId, String buttonId){
        switch (buttonId){
            case Constants.VOLTAR_AO_MENU:
                chatStates.put(chatId, ChatStateMachine.AGUARDANDO_COMANDO);
                aguardandoComando(chatId);
                break;
            case Constants.CADASTRAR_BEM:
                chatStates.put(chatId, ChatStateMachine.ESPERANDO_NOME_BEM);
                replyCadastrarBem(chatId);
                break;
            case Constants.CADASTRAR_LOCALIZACAO:
                chatStates.put(chatId, ChatStateMachine.CADASTRANDO_LOCALIZACAO);
                replyCadastrarLocalizacao(chatId);
                break;
            case Constants.CADASTRAR_CATEGORIA:
                chatStates.put(chatId, ChatStateMachine.ESPERANDO_NOME_CATEGORIA);
                replyCadastrarCategoria(chatId);
                break;
            case Constants.LISTAR_BENS:
                chatStates.put(chatId, ChatStateMachine.LISTANDO_BENS);
                replyListarBens(chatId);
                break;
            case Constants.LISTAR_CATEGORIAS:
                chatStates.put(chatId, ChatStateMachine.LISTANDO_CATEGORIAS);
                replyListarCategorias(chatId);
                break;
        }
    }

    public void replyCadastrarBem(long chatId){
        if(chatStates.get(chatId).equals(ChatStateMachine.CADASTRANDO_BEM)) {
            System.out.println("replyCadastrarBem");
            try {
                sender.execute(new SendMessage()
                        .setText("\n <b>Cadastrando bem</b>\n ")
                        .setChatId(chatId)
                        .enableHtml(true)
                );
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void replyWithBackButton(long chatId) {
        if (!chatStates.get(chatId).equals(ChatStateMachine.AGUARDANDO_COMANDO)) {
            System.out.println("back button");
            try {
                sender.execute(new SendMessage()
                        .setText("\n Voltar ao menu.")
                        .setChatId(chatId)
                        .setReplyMarkup(KeyboardFactory.ReplyKeyboardWithBackButton())
                );
                chatStates.put(chatId, ChatStateMachine.AGUARDANDO_COMANDO);
                replyWithBackButton(chatId);

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void replyCadastrarLocalizacao(long chatId){
        if(chatStates.get(chatId).equals(ChatStateMachine.CADASTRANDO_LOCALIZACAO)) {
            System.out.println("replyCadastrarLocalizacao");
            replyWithBackButton(chatId);

        }
    }

    public void replyCadastrarCategoria(long chatId){
        if(chatStates.get(chatId).equals(ChatStateMachine.ESPERANDO_NOME_CATEGORIA)) {
            System.out.println("replyCadastrarCategorias");
            try {
                sender.execute(new SendMessage()
                        .setText("\n <b>Cadastrando categoria...</b>\n ")
                        .setChatId(chatId)
                        .enableHtml(true)
                );

                sender.execute(new SendMessage()
                        .setText("Qual o nome da categoria?")
                        .setChatId(chatId)
                );
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void replyListarCategorias(long chatId) {
        if (chatStates.get(chatId).equals(ChatStateMachine.LISTANDO_CATEGORIAS)) {
            System.out.println("replyListarCategorias");
            Conexao conexao = new Conexao();
            CategoriaRepository categoriaRepository = new CategoriaRepository(conexao);
            categoriaRepository.criarTabela();
            try {
                for (Categoria categoria : categoriaRepository.findall()) {
                    sender.execute(new SendMessage()
                            .setText(categoria.toString())
                            .setChatId(chatId));
                }
                replyWithBackButton(chatId);

            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    public void replyListarBens(long chatId) {
        if (chatStates.get(chatId).equals(ChatStateMachine.LISTANDO_BENS)) {
            System.out.println("replyListarBens");
            Conexao conexao = new Conexao();
            BemRepository bemRepository = new BemRepository(conexao);
            bemRepository.criarTabela();
            try {
                for (Bem bem : bemRepository.findall()) {
                    sender.execute(new SendMessage()
                            .setText(bem.toString())
                            .setChatId(chatId));
                }
                replyWithBackButton(chatId);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    public void receiveInput(long chatId, String name){
        switch (chatStates.get(chatId)){
            case ESPERANDO_NOME_CATEGORIA:
                System.out.println("Nome da categoria: "+name);
                this.commandsHistory.add(name);
                try {
                    sender.execute(new SendMessage()
                            .setText("Qual a descricao da categoria?")
                            .setChatId(chatId)
                    );
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                chatStates.put(chatId, ChatStateMachine.ESPERANDO_DESCRICAO_CATEGORIA);
                break;
            case ESPERANDO_DESCRICAO_CATEGORIA:

                System.out.println("DESC da categoria: "+name);
                this.commandsHistory.add(name);
                salvarObjCategoria(chatId);
                break;

        }
        System.out.println(chatStates.get(chatId).toString());
    }
    private void salvarObjCategoria(long chatId){
        Conexao conexao = new Conexao();
        CategoriaRepository categoriaRepository = new CategoriaRepository(conexao);
        Categoria categoria = new Categoria(commandsHistory.get(0), commandsHistory.get(1));
        commandsHistory.clear();
        categoriaRepository.inserir(categoria);
        replyWithBackButton(chatId);
    }
}