import Constants.Constants;
import model.Bem;
import model.Categoria;
import model.Localizacao;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
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
    private Conexao conexao;
    private BemRepository bemRepository;
    private CategoriaRepository categoriaRepository;
    private LocalizacaoRepository localizacaoRepository;
    private BemService bemService;
    /**
     * @param sender
     * @param db
     */
    public ResponseHandler(MessageSender sender, DBContext db){
        this.sender = sender;
        this.commandsHistory=new ArrayList<String>();
        this.conexao = Conexao.getConexao();
        this.bemRepository = new BemRepository(conexao);
        bemRepository.criarTabela();
        this.localizacaoRepository = new LocalizacaoRepository(conexao);
        localizacaoRepository.criarTabela();
        this.categoriaRepository = new CategoriaRepository(conexao);
        categoriaRepository.criarTabela();
        this.bemRepository = new BemRepository(conexao);
        bemRepository.criarTabela();
        this.bemService=new BemService(conexao);
        chatStates = db.getMap(Constants.CHAT_STATE_MACHINE);
    }

    /**
     * @param chatId
     */
    public void aguardandoComando(long chatId) {
        this.commandsHistory.clear();
        chatStates.put(chatId, ChatStateMachine.AGUARDANDO_COMANDO);
        replyWithHtmlMarkup(chatId,Constants.START_REPLY);
        replyWithInlineKeyboard(chatId, Constants.ASK_FOR_COMMAND, KeyboardFactory.ReplyKeyboardWithCommandButtons());
    }

    /**
     * @param chatId
     * @param buttonId
     */
    public void replyToButtons(long chatId, String buttonId){
        if(buttonId.contains("findLocalizacao") || buttonId.contains("findCategoria")){
            if(buttonId.contains("findLocalizacao") && (chatStates.get(chatId) == ChatStateMachine.ESPERANDO_LOCALIZACAO_BEM) ){
                localizacaoTemp = localizacaoRepository.findById(Integer.parseInt(buttonId.replaceAll("[\\D]", ""))); //substitui tudo que não for digito com um espaço vazio para fazer o parseInt
                replyWithHtmlMarkup(chatId,"<b>Bem cadastrado com sucesso!</b>");
                salvarObjBem(chatId);
            }else{
                if(buttonId.contains("findLocalizacao") && (chatStates.get(chatId) == ChatStateMachine.ESPERANDO_LOCALIZACAO_PARA_MOVER_BEM)){
                    bemService.changeLocation(bemTemp.getId(), Integer.parseInt(buttonId.replaceAll("[\\D]", "")) );
                    replyWithHtmlMarkup(chatId, "O bem foi movido com sucesso para <b>"+bemTemp.getLocalizacao().getNome()+"</b>.");
                    replyWithBackButton(chatId);
                }
                if(buttonId.contains("findLocalizacao") && (chatStates.get(chatId) == ChatStateMachine.ESPERANDO_LOCALIZACAO_BUSCA_BEM)){
                    localizacaoTemp = localizacaoRepository.findById(Integer.parseInt(buttonId.replaceAll("[\\D]", ""))); //substitui tudo que não for digito com um espaço vazio para fazer o parseInt
                    replyWithHtmlMarkup(chatId, "Os bens dessa localização podem ser encontrados abaixo:");
                    List<Bem> bens = bemRepository.findByLocal(localizacaoTemp.getNome());
                    if(!bens.isEmpty()){
                        for(Bem bem : bens){
                            replyWithHtmlMarkup(chatId, bem.toString());
                        }
                    }else{
                        replyWithHtmlMarkup(chatId,"<b>Não existem bens cadastrados nesta localização.</b>");
                    }
                    replyWithBackButton(chatId);
                }
                if(buttonId.contains("findCategoria") && (chatStates.get(chatId) == ChatStateMachine.ESPERANDO_CATEGORIA_BEM)){
                    categoriaTemp = categoriaRepository.findById(Integer.parseInt(buttonId.replaceAll("[\\D]", ""))); //substitui tudo que não for digito com um espaço vazio para fazer o parseInt
                    chatStates.put(chatId, ChatStateMachine.ESPERANDO_LOCALIZACAO_BEM);
                    replyWithInlineKeyboard(chatId, "Selecione abaixo a localização do bem:", KeyboardFactory.ReplyKeyboardWithLocalizacoes());
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

    /**
     * @param chatId
     */
    private void replyGerarRelatorio(long chatId){
        replyWithHtmlMarkup(chatId,"<i>Gerando relatório...</i>");
        for(Bem bem : bemService.getRelatorio()){
            replyWithHtmlMarkup(chatId,bem.toString());
        }
        replyWithBackButton(chatId);
    }

    /**
     * @param chatId
     */
    public void replyEsperandoCodigoParaBuscar(long chatId){
        replyWithHtmlMarkup(chatId,"Qual o código do bem que você deseja fazer a movimentação?");
    }

    /**
     * @param chatId
     */
    public void replyEsperandoDescricaoParaBuscar(long chatId){
        replyWithHtmlMarkup(chatId,"Qual a descrição do bem que você deseja encontrar?");
    }

    /**
     * @param chatId
     */
    public void replyEsperandoNomeParaBuscar(long chatId){
        replyWithHtmlMarkup(chatId,"Qual o nome do bem?");
    }

    /**
     * @param chatId
     */
    public void replyEsperandoLocalizacaoParaBuscar(long chatId){
        replyWithInlineKeyboard(chatId, "<b>Selecione a localizacao dos bens abaixo:</b>", KeyboardFactory.ReplyKeyboardWithLocalizacoes());
    }

    /**
     * @param chatId
     */
    private void replyEsperandoCodigoBuscaBem(long chatId){
        replyWithHtmlMarkup(chatId, "<b>Digite o código de busca do bem:</b>");
    }

    /**
     * @param chatId
     */
    private void replyCadastrarBem(long chatId){
        if(categoriaRepository.findall().isEmpty()){
            replyWithHtmlMarkup(chatId, "\n <b>É necessário cadastrar pelo menos uma categoria antes de cadastrar um bem.</b>\n ");
            aguardandoComando(chatId);
        }
        if(localizacaoRepository.findall().isEmpty()){
            replyWithHtmlMarkup(chatId, "\n <b>É necessário cadastrar pelo menos uma localizacao antes de cadastrar um bem.</b>\n ");
            aguardandoComando(chatId);
        }
        if(chatStates.get(chatId).equals(ChatStateMachine.ESPERANDO_NOME_BEM)) {
            replyWithHtmlMarkup(chatId,"\n <i>Cadastrando bem</i>\n ");
            replyWithHtmlMarkup(chatId, "Qual o nome do bem?");
        }
    }

    /**
     * @param chatId
     */
    private void replyWithBackButton(long chatId) {
        if (!chatStates.get(chatId).equals(ChatStateMachine.AGUARDANDO_COMANDO)) {
            replyWithInlineKeyboard(chatId, "\n Voltar ao menu." ,KeyboardFactory.ReplyKeyboardWithBackButton());
            chatStates.put(chatId, ChatStateMachine.AGUARDANDO_COMANDO);
            replyWithBackButton(chatId);
        }
    }

    /**
     * @param chatId
     */
    private void replyCadastrarLocalizacao(long chatId){
        if(chatStates.get(chatId).equals(ChatStateMachine.ESPERANDO_NOME_LOCALIZACAO)) {
            replyWithHtmlMarkup(chatId, "\n <i>Cadastrando localização...</i>\n ");
            replyWithHtmlMarkup(chatId, "Qual o nome da localização?");
        }
    }

    /**
     * @param chatId
     */
    private void replyCadastrarCategoria(long chatId){
        if(chatStates.get(chatId).equals(ChatStateMachine.ESPERANDO_NOME_CATEGORIA)) {
            replyWithHtmlMarkup(chatId, "\n <i>Cadastrando categoria...</i>\n ");
            replyWithHtmlMarkup(chatId, "Qual o nome da categoria?");
        }
    }

    /**
     * @param chatId
     */
    private void replyListarCategorias(long chatId) {
        if (chatStates.get(chatId).equals(ChatStateMachine.LISTANDO_CATEGORIAS)) {
            for (Categoria categoria : categoriaRepository.findall()) {
                replyWithHtmlMarkup(chatId, categoria.toString());
            }
            replyWithBackButton(chatId);
        }
    }

    /**
     * @param chatId
     */
    private void replyListarLocalizacoes(long chatId) {
        if (chatStates.get(chatId).equals(ChatStateMachine.LISTANDO_LOCALIZACOES)) {
            for (Localizacao localizacao : localizacaoRepository.findall()) {
                replyWithHtmlMarkup(chatId, localizacao.toString());
            }
            replyWithBackButton(chatId);
        }
    }

    /**
     * @param chatId
     */
    private void replyListarBens(long chatId) {
        if (chatStates.get(chatId).equals(ChatStateMachine.LISTANDO_BENS)) {
            for (Bem bem : bemRepository.findall()) {
                replyWithHtmlMarkup(chatId, bem.toString());
            }
            replyWithBackButton(chatId);
        }
    }

    /**
     * @param chatId
     */
    private void salvarObjCategoria(long chatId){
        Categoria categoria = new Categoria(commandsHistory.get(0), commandsHistory.get(1));
        commandsHistory.clear();
        categoriaRepository.inserir(categoria);
        replyWithBackButton(chatId);
    }

    /**
     * @param chatId
     */
    private void salvarObjLocalizacao(long chatId){
        Localizacao localizacao = new Localizacao(commandsHistory.get(0), commandsHistory.get(1));
        commandsHistory.clear();
        localizacaoRepository.inserir(localizacao);
        replyWithBackButton(chatId);
    }

    /**
     * @param chatId
     */
    private void salvarObjBem(long chatId){
        Bem bem = new Bem(commandsHistory.get(0), commandsHistory.get(1), localizacaoTemp, categoriaTemp);
        commandsHistory.clear();
        bemRepository.inserir(bem);
        replyWithBackButton(chatId);
    }

    /**
     * @param chatId
     * @param name
     */
    public void receiveInput(long chatId, String name){
        switch (chatStates.get(chatId)) {
            case ESPERANDO_NOME_CATEGORIA:
                this.commandsHistory.add(name);
                replyWithHtmlMarkup(chatId, "Qual a descricao da categoria?");
                chatStates.put(chatId, ChatStateMachine.ESPERANDO_DESCRICAO_CATEGORIA);
                break;
            case ESPERANDO_DESCRICAO_CATEGORIA:
                this.commandsHistory.add(name);
                salvarObjCategoria(chatId);
                break;
            case ESPERANDO_NOME_LOCALIZACAO:
                this.commandsHistory.add(name);
                replyWithHtmlMarkup(chatId, "Qual a descricao da localizacao?" );
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
                replyWithHtmlMarkup(chatId,"Qual a descrição do bem?");
                break;
            case ESPERANDO_DESCRICAO_BEM:
                this.commandsHistory.add(name);
                chatStates.put(chatId, ChatStateMachine.ESPERANDO_CATEGORIA_BEM);
                replyWithInlineKeyboard(chatId, "Selecione abaixo a categoria do bem:", KeyboardFactory.ReplyKeyboardWithCategorias());
                break;
            case ESPERANDO_NOME_BUSCA_BEM:
                List<Bem> bens = bemRepository.findByName(name);
                if (!bens.isEmpty()) {
                    for (Bem bem : bens) {
                        replyWithHtmlMarkup(chatId, bem.toString());
                    }
                }else{
                    replyWithHtmlMarkup(chatId, "<b>Não foram encontrados bem(ns) com esse nome.</b>");
                }
                replyWithBackButton(chatId);
                break;
            case ESPERANDO_DESCRICAO_BUSCA_BEM:
                List<Bem> bensTemp = bemRepository.findByDescription(name);
                if (!bensTemp.isEmpty()) {
                    for (Bem bem : bensTemp) {
                        replyWithHtmlMarkup(chatId, bem.toString());
                    }
                }else{
                    replyWithHtmlMarkup(chatId,"<b>Não foram encontrados bem com essa descrição.</b>");
                }
                replyWithBackButton(chatId);
                break;
            case ESPERANDO_CODIGO_PARA_MOVER_BEM:
                findAndSetBemTemp(chatId, name);
                replyWithInlineKeyboard(chatId, "Para onde deseja movimentar o bem <b>"+ bemTemp.getNome() +"</b>?", KeyboardFactory.ReplyKeyboardWithLocalizacoes());
                chatStates.put(chatId, ChatStateMachine.ESPERANDO_LOCALIZACAO_PARA_MOVER_BEM);
                break;
        }
    }

    /**
     * @param chatId
     * @param unformattedId
     */
    public void findAndSetBemTemp(long chatId, String unformattedId){
        try {
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

    private void replyWithHtmlMarkup(long chatId, String text){
        try {
            sender.execute(new SendMessage()
                    .setText(text)
                    .enableHtml(true)
                    .setChatId(chatId)
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void replyWithInlineKeyboard(long chatId, String text, InlineKeyboardMarkup keyboard){
        try {
            sender.execute(new SendMessage()
                    .setText(text)
                    .enableHtml(true)
                    .setChatId(chatId)
                    .setReplyMarkup(keyboard)
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
