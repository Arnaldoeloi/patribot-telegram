import Constants.Constants;
import model.Bem;
import model.Categoria;
import model.Localizacao;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import repository.BemRepository;
import repository.CategoriaRepository;
import repository.Conexao;
import repository.LocalizacaoRepository;
import service.BemService;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResponseHandler {
    private final MessageSender sender;
    private final Map<Long, ChatStateMachine> chatStates;
    private ArrayList<String> commandsHistory;
    private Localizacao localizacaoTemp;
    private Categoria categoriaTemp;
    private Bem bemTemp;

    public ResponseHandler(MessageSender sender, DBContext db){
        this.sender = sender;
        this.commandsHistory=new ArrayList<String>();
        chatStates = db.getMap(Constants.CHAT_STATE_MACHINE);
    }

    public void aguardandoComando(long chatId) {
        this.commandsHistory.clear();
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
        if(buttonId.contains("findLocalizacao") || buttonId.contains("findCategoria")){
            System.out.println("DISPLAY_DOS_BOTOES");

            if(buttonId.contains("findLocalizacao") && (chatStates.get(chatId) == ChatStateMachine.ESPERANDO_LOCALIZACAO_BEM) ){
                Conexao con = Conexao.getConexao();
                LocalizacaoRepository localizacaoRepository = new LocalizacaoRepository(con);
                localizacaoTemp = localizacaoRepository.findById(Integer.parseInt(buttonId.replaceAll("[\\D]", ""))); //substitui tudo que não for digito com um espaço vazio para fazer o parseInt
                salvarObjBem(chatId);
            }else{
                if(buttonId.contains("findLocalizacao") && (chatStates.get(chatId) == ChatStateMachine.ESPERANDO_LOCALIZACAO_PARA_MOVER_BEM)){
                    Conexao con = Conexao.getConexao();
                    BemService bemService = new BemService(con);
                    bemService.changeLocation(bemTemp.getId(), Integer.parseInt(buttonId.replaceAll("[\\D]", "")) );
                    try {
                        sender.execute(new SendMessage()
                                .setText("O bem foi movido com sucesso para <b>"+bemTemp.getLocalizacao().getNome()+"</b>.")
                                .enableHtml(true)
                                .setChatId(chatId)
                        );
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    replyWithBackButton(chatId);
                }
                if(buttonId.contains("findLocalizacao") && (chatStates.get(chatId) == ChatStateMachine.ESPERANDO_LOCALIZACAO_BUSCA_BEM)){
                    Conexao con = Conexao.getConexao();
                    LocalizacaoRepository localizacaoRepository = new LocalizacaoRepository(con);
                    localizacaoTemp = localizacaoRepository.findById(Integer.parseInt(buttonId.replaceAll("[\\D]", ""))); //substitui tudo que não for digito com um espaço vazio para fazer o parseInt
                    BemRepository bemRepository = new BemRepository(con);
                    try {
                        sender.execute(new SendMessage()
                                .setText("Os bens dessa localização podem ser encontrados abaixo:")
                                .setChatId(chatId)
                        );
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    List<Bem> bens = bemRepository.findByLocal(localizacaoTemp.getNome());
                    if(!bens.isEmpty()){
                        for(Bem bem : bens){
                            try {
                                sender.execute(new SendMessage()
                                        .setText(bem.toString())
                                        .enableHtml(true)
                                        .setChatId(chatId)
                                );
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        try {
                            sender.execute(new SendMessage()
                                    .setText("<b>Não existem bens cadastrados nesta localização.</b>")
                                    .enableHtml(true)
                                    .setChatId(chatId)
                            );
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    replyWithBackButton(chatId);

                }
                if(buttonId.contains("findCategoria") && (chatStates.get(chatId) == ChatStateMachine.ESPERANDO_CATEGORIA_BEM)){
                    Conexao con = Conexao.getConexao();
                    CategoriaRepository categoriaRepository = new CategoriaRepository(con);
                    categoriaTemp = categoriaRepository.findById(Integer.parseInt(buttonId.replaceAll("[\\D]", ""))); //substitui tudo que não for digito com um espaço vazio para fazer o parseInt
                    chatStates.put(chatId, ChatStateMachine.ESPERANDO_LOCALIZACAO_BEM);
                    try {
                        sender.execute(new SendMessage()
                                .setText("Selecione abaixo a localização do bem:")
                                .setReplyMarkup(KeyboardFactory.ReplyKeyboardWithLocalizacoes())
                                .setChatId(chatId)
                        );
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

            }
        }else{

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
                    chatStates.put(chatId, ChatStateMachine.ESPERANDO_NOME_LOCALIZACAO);
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
                case Constants.LISTAR_LOCALIZACOES:
                    chatStates.put(chatId, ChatStateMachine.LISTANDO_LOCALIZACOES);
                    replyListarLocalizacoes(chatId);
                    break;
                case Constants.LISTAR_CATEGORIAS:
                    chatStates.put(chatId, ChatStateMachine.LISTANDO_CATEGORIAS);
                    replyListarCategorias(chatId);
                    break;
                case Constants.BUSCAR_BEM_CODIGO:
                    chatStates.put(chatId, ChatStateMachine.ESPERANDO_CODIGO_BUSCA_BEM);
                    replyEsperandoCodigoBuscaBem(chatId);
                    break;
                case Constants.LISTAR_BENS_POR_LOCALIZACAO:
                    chatStates.put(chatId, ChatStateMachine.ESPERANDO_LOCALIZACAO_BUSCA_BEM);
                    replyEsperandoLocalizacaoParaBuscar(chatId);
                    break;
                case Constants.BUSCAR_BEM_NOME:
                    chatStates.put(chatId, ChatStateMachine.ESPERANDO_NOME_BUSCA_BEM);
                    replyEsperandoNomeParaBuscar(chatId);
                    break;
                case Constants.BUSCAR_BEM_DESCRICAO:
                    chatStates.put(chatId, ChatStateMachine.ESPERANDO_DESCRICAO_BUSCA_BEM);
                    replyEsperandoDescricaoParaBuscar(chatId);
                    break;
                case Constants.MOVIMENTAR_BEM:
                    chatStates.put(chatId, ChatStateMachine.ESPERANDO_CODIGO_PARA_MOVER_BEM);
                    replyEsperandoCodigoParaBuscar(chatId);
                    break;
                case Constants.GERAR_RELATORIO:
                    chatStates.put(chatId, ChatStateMachine.GERANDO_RELATORIO);
                    replyGerarRelatorio(chatId);
                    break;
            }
        }
    }
    private void replyGerarRelatorio(long chatId){
        try {
            sender.execute(new SendMessage()
                    .setText("<i>Gerando relatório...</i>")
                    .enableHtml(true)
                    .setChatId(chatId)
            );
            Conexao con = Conexao.getConexao();
            BemService bemService = new BemService(con);
            for(Bem bem : bemService.getRelatorio()){
                sender.execute(new SendMessage()
                        .setText(bem.toString())
                        .enableHtml(true)
                        .setChatId(chatId)
                );
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        replyWithBackButton(chatId);
    }
    public void replyEsperandoCodigoParaBuscar(long chatId){
        try {
            sender.execute(new SendMessage()
                    .setText("Qual o código do bem que você deseja fazer a movimentação?")
                    .setChatId(chatId)
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void replyEsperandoDescricaoParaBuscar(long chatId){
        try {
            sender.execute(new SendMessage()
                    .setText("Qual a descrição do bem que você deseja encontrar?")
                    .setChatId(chatId)
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void replyEsperandoNomeParaBuscar(long chatId){
        try {
            sender.execute(new SendMessage()
                    .setText("Qual o nome do bem?")
                    .setChatId(chatId)
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void replyEsperandoLocalizacaoParaBuscar(long chatId){
        try {
            sender.execute(new SendMessage()
                    .setText("<b>Selecione a localizacao dos bens abaixo:</b>")
                    .setReplyMarkup(KeyboardFactory.ReplyKeyboardWithLocalizacoes())
                    .enableHtml(true)
                    .setChatId(chatId)
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void replyEsperandoCodigoBuscaBem(long chatId){
        try {
            sender.execute(new SendMessage()
                    .setText("<b>Digite o código de busca do bem:</b>")
                    .enableHtml(true)
                    .setChatId(chatId)
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void replyCadastrarBem(long chatId){
        Conexao con = Conexao.getConexao();
        LocalizacaoRepository localizacaoRepository = new LocalizacaoRepository(con);
        CategoriaRepository categoriaRepository = new CategoriaRepository(con);

        if(categoriaRepository.findall().isEmpty()){
            try {
                sender.execute(new SendMessage()
                        .setText("\n <b>É necessário cadastrar pelo menos uma categoria antes de cadastrar um bem.</b>\n ")
                        .setChatId(chatId)
                        .enableHtml(true)
                );
                aguardandoComando(chatId);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        if(localizacaoRepository.findall().isEmpty()){
            try {
                sender.execute(new SendMessage()
                        .setText("\n <b>É necessário cadastrar pelo menos uma localizacao antes de cadastrar um bem.</b>\n ")
                        .setChatId(chatId)
                        .enableHtml(true)
                );
                aguardandoComando(chatId);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        if(chatStates.get(chatId).equals(ChatStateMachine.ESPERANDO_NOME_BEM)) {
            System.out.println("replyCadastrarBem");
            try {
                sender.execute(new SendMessage()
                        .setText("\n <i>Cadastrando bem</i>\n ")
                        .setChatId(chatId)
                        .enableHtml(true)
                );

                sender.execute(new SendMessage()
                        .setText("Qual o nome do bem?")
                        .setChatId(chatId)
                );
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    private void replyWithBackButton(long chatId) {
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
    private void replyCadastrarLocalizacao(long chatId){
        if(chatStates.get(chatId).equals(ChatStateMachine.ESPERANDO_NOME_LOCALIZACAO)) {
            try {
                sender.execute(new SendMessage()
                        .setText("\n <i>Cadastrando localização...</i>\n ")
                        .setChatId(chatId)
                        .enableHtml(true)
                );

                sender.execute(new SendMessage()
                        .setText("Qual o nome da localização?")
                        .setChatId(chatId)
                );
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            System.out.println("replyCadastrarLocalizacao");
        }
    }
    private void replyCadastrarCategoria(long chatId){
        if(chatStates.get(chatId).equals(ChatStateMachine.ESPERANDO_NOME_CATEGORIA)) {
            System.out.println("replyCadastrarCategorias");
            try {
                sender.execute(new SendMessage()
                        .setText("\n <i>Cadastrando categoria...</i>\n ")
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
    private void replyListarCategorias(long chatId) {
        if (chatStates.get(chatId).equals(ChatStateMachine.LISTANDO_CATEGORIAS)) {
            System.out.println("replyListarCategorias");
            Conexao conexao = Conexao.getConexao();
            CategoriaRepository categoriaRepository = new CategoriaRepository(conexao);
            categoriaRepository.criarTabela();
            try {
                for (Categoria categoria : categoriaRepository.findall()) {
                    sender.execute(new SendMessage()
                            .setText(categoria.toString())
                            .enableHtml(true)
                            .setChatId(chatId));
                }
                replyWithBackButton(chatId);

            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }
    private void replyListarLocalizacoes(long chatId) {
        if (chatStates.get(chatId).equals(ChatStateMachine.LISTANDO_LOCALIZACOES)) {
            Conexao conexao = Conexao.getConexao();
            LocalizacaoRepository localizacaoRepository = new LocalizacaoRepository(conexao);
            localizacaoRepository.criarTabela();
            try {
                for (Localizacao localizacao : localizacaoRepository.findall()) {
                    sender.execute(new SendMessage()
                            .setText(localizacao.toString())
                            .enableHtml(true)
                            .setChatId(chatId));
                }
                replyWithBackButton(chatId);

            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }
    private void replyListarBens(long chatId) {
        if (chatStates.get(chatId).equals(ChatStateMachine.LISTANDO_BENS)) {
            System.out.println("replyListarBens");
            Conexao conexao = Conexao.getConexao();
            BemRepository bemRepository = new BemRepository(conexao);
            bemRepository.criarTabela();
            try {
                for (Bem bem : bemRepository.findall()) {
                    sender.execute(new SendMessage()
                            .setText(bem.toString())
                            .enableHtml(true)
                            .setChatId(chatId));
                }
                replyWithBackButton(chatId);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }
    private void salvarObjCategoria(long chatId){
        Conexao conexao =Conexao.getConexao();
        CategoriaRepository categoriaRepository = new CategoriaRepository(conexao);
        Categoria categoria = new Categoria(commandsHistory.get(0), commandsHistory.get(1));
        commandsHistory.clear();
        categoriaRepository.inserir(categoria);
        replyWithBackButton(chatId);
    }
    private void salvarObjLocalizacao(long chatId){
        Conexao conexao = Conexao.getConexao();
        LocalizacaoRepository localizacaoRepository = new LocalizacaoRepository(conexao);
        Localizacao localizacao = new Localizacao(commandsHistory.get(0), commandsHistory.get(1));
        commandsHistory.clear();
        localizacaoRepository.inserir(localizacao);
        replyWithBackButton(chatId);
    }
    private void salvarObjBem(long chatId){
        Conexao conexao = Conexao.getConexao();
        BemRepository bemRepository = new BemRepository(conexao);
        Bem bem = new Bem(commandsHistory.get(0), commandsHistory.get(1), localizacaoTemp, categoriaTemp);
        commandsHistory.clear();
        bemRepository.inserir(bem);
        replyWithBackButton(chatId);
    }

    public void receiveInput(long chatId, String name){
        switch (chatStates.get(chatId)) {
            case ESPERANDO_NOME_CATEGORIA:
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
                System.out.println("DESC da categoria: " + name);
                this.commandsHistory.add(name);
                salvarObjCategoria(chatId);
                break;

            case ESPERANDO_NOME_LOCALIZACAO:
                this.commandsHistory.add(name);
                try {
                    sender.execute(new SendMessage()
                            .setText("Qual a descricao da localizacao?")
                            .setChatId(chatId)
                    );
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                chatStates.put(chatId, ChatStateMachine.ESPERANDO_DESCRICAO_LOCALIZACAO);
                break;
            case ESPERANDO_DESCRICAO_LOCALIZACAO:
                this.commandsHistory.add(name);
                salvarObjLocalizacao(chatId);
                break;
            case ESPERANDO_CODIGO_BUSCA_BEM:
                findAndSetBemTemp(chatId, name);
                replyWithBackButton(chatId);
                break;
            case ESPERANDO_NOME_BEM:
                this.commandsHistory.add(name);
                chatStates.put(chatId, ChatStateMachine.ESPERANDO_DESCRICAO_BEM);
                try {
                    sender.execute(new SendMessage()
                            .setText("Qual a descrição do bem?")
                            .setChatId(chatId)
                    );
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case ESPERANDO_DESCRICAO_BEM:
                this.commandsHistory.add(name);

                chatStates.put(chatId, ChatStateMachine.ESPERANDO_CATEGORIA_BEM);
                try {
                    sender.execute(new SendMessage()
                            .setText("Selecione abaixo a categoria do bem:")
                            .setReplyMarkup(KeyboardFactory.ReplyKeyboardWithCategorias())
                            .setChatId(chatId)
                    );
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case ESPERANDO_NOME_BUSCA_BEM:
                try {
                    Conexao con = Conexao.getConexao();
                    BemRepository bemRepository = new BemRepository(con);
                    List<Bem> bens = bemRepository.findByName(name);
                    if (!bens.isEmpty()) {
                        for (Bem bem : bens) {
                            sender.execute(new SendMessage()
                                    .setText(bem.toString())
                                    .enableHtml(true)
                                    .setChatId(chatId)
                            );
                        }
                    }else{
                        sender.execute(new SendMessage()
                                .setText("<b>Não foram encontrados bem com esse nome.</b>")
                                .enableHtml(true)
                                .setChatId(chatId)
                        );
                    }
                }catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                replyWithBackButton(chatId);
                break;
            case ESPERANDO_DESCRICAO_BUSCA_BEM:
                try {
                    Conexao con =Conexao.getConexao();
                    BemRepository bemRepository = new BemRepository(con);
                    List<Bem> bens = bemRepository.findByDescription(name);
                    if (!bens.isEmpty()) {
                        for (Bem bem : bens) {
                            sender.execute(new SendMessage()
                                    .setText(bem.toString())
                                    .enableHtml(true)
                                    .setChatId(chatId)
                            );
                        }
                    }else{
                        sender.execute(new SendMessage()
                                .setText("<b>Não foram encontrados bem com essa descrição.</b>")
                                .enableHtml(true)
                                .setChatId(chatId)
                        );
                    }
                }catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                replyWithBackButton(chatId);
                break;
            case ESPERANDO_CODIGO_PARA_MOVER_BEM:
                findAndSetBemTemp(chatId, name);
                try {
                    sender.execute(new SendMessage()
                            .setText("Para onde deseja movimentar o bem <b>"+ bemTemp.getNome() +"</b>?")
                            .setReplyMarkup(KeyboardFactory.ReplyKeyboardWithLocalizacoes())
                            .enableHtml(true)
                            .setChatId(chatId)
                    );
                    chatStates.put(chatId, ChatStateMachine.ESPERANDO_LOCALIZACAO_PARA_MOVER_BEM);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;

        }
        System.out.println(chatStates.get(chatId).toString());
    }
    public void findAndSetBemTemp(long chatId, String unformattedId){
        try {
            Conexao con = Conexao.getConexao();
            BemRepository bemRepository = new BemRepository(con);
            bemTemp = bemRepository.findById(Integer.parseInt(unformattedId.replaceAll("[\\D]", "")));
            if (bemTemp != null) {
                sender.execute(new SendMessage()
                        .setText(bemTemp.toString())
                        .enableHtml(true)
                        .setChatId(chatId)
                );
            } else {
                sender.execute(new SendMessage()
                        .setText("<b>Não há nenhum bem com esse código.</b>")
                        .enableHtml(true)
                        .setChatId(chatId)
                );
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
