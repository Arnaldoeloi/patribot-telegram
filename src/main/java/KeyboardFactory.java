import Constants.Constants;
import model.Categoria;
import model.Localizacao;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import repository.CategoriaRepository;
import repository.Conexao;
import repository.LocalizacaoRepository;

import java.util.ArrayList;
import java.util.List;

public class KeyboardFactory {
    public static InlineKeyboardMarkup ReplyKeyboardWithCommandButtons(){
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline4 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline5 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline6 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline7 = new ArrayList<>();


        rowInline1.add(new InlineKeyboardButton().setText(Constants.CADASTRAR_BEM).setCallbackData(Constants.CADASTRAR_BEM));
        rowInline1.add(new InlineKeyboardButton().setText(Constants.CADASTRAR_LOCALIZACAO).setCallbackData(Constants.CADASTRAR_LOCALIZACAO));

        rowInline2.add(new InlineKeyboardButton().setText(Constants.CADASTRAR_CATEGORIA).setCallbackData(Constants.CADASTRAR_CATEGORIA));

        rowInline3.add(new InlineKeyboardButton().setText(Constants.LISTAR_BENS).setCallbackData(Constants.LISTAR_BENS));
        rowInline3.add(new InlineKeyboardButton().setText(Constants.LISTAR_LOCALIZACOES).setCallbackData(Constants.LISTAR_LOCALIZACOES));

        rowInline4.add(new InlineKeyboardButton().setText(Constants.LISTAR_CATEGORIAS).setCallbackData(Constants.LISTAR_CATEGORIAS));

        rowInline5.add(new InlineKeyboardButton().setText(Constants.BUSCAR_BEM_CODIGO).setCallbackData(Constants.BUSCAR_BEM_CODIGO));
        rowInline5.add(new InlineKeyboardButton().setText(Constants.BUSCAR_BEM_NOME).setCallbackData(Constants.BUSCAR_BEM_NOME));

        rowInline6.add(new InlineKeyboardButton().setText(Constants.BUSCAR_BEM_DESCRICAO).setCallbackData(Constants.BUSCAR_BEM_DESCRICAO));
        rowInline7.add(new InlineKeyboardButton().setText(Constants.MOVIMENTAR_BEM).setCallbackData(Constants.MOVIMENTAR_BEM));
        rowInline7.add(new InlineKeyboardButton().setText(Constants.GERAR_RELATORIO).setCallbackData(Constants.GERAR_RELATORIO));


//        rowInline.add(new InlineKeyboardButton().setText(Constants.TRAINING_TODAY).setCallbackData(Constants.TRAINING_TODAY));
//        rowInline.add(new InlineKeyboardButton().setText(Constants.TRAINING_TOMORROW).setCallbackData(Constants.TRAINING_TOMORROW));
        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);
        rowsInline.add(rowInline4);
        rowsInline.add(rowInline5);
        rowsInline.add(rowInline6);
        rowsInline.add(rowInline7);
        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }

    public static InlineKeyboardMarkup ReplyKeyboardWithBackButton(){
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        rowInline.add(new InlineKeyboardButton().setText(Constants.VOLTAR_AO_MENU).setCallbackData(Constants.VOLTAR_AO_MENU));
        rowsInline.add(rowInline);
        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }

    public static InlineKeyboardMarkup ReplyKeyboardWithCategorias(){
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        Conexao con = new Conexao();
        CategoriaRepository categoriaRepository = new CategoriaRepository(con);

        for(Categoria categoria : categoriaRepository.findall()){
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            rowInLine.add(new InlineKeyboardButton().setText(categoria.getNome()).setCallbackData("categoria"+categoria.getId()));
            rowsInline.add(rowInLine);
        }
        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }

    public static InlineKeyboardMarkup ReplyKeyboardWithLocalizacoes(){
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        Conexao con = new Conexao();
        LocalizacaoRepository localizacaoRepository = new LocalizacaoRepository(con);

        for(Localizacao localizacao : localizacaoRepository.findall()){
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            rowInLine.add(new InlineKeyboardButton().setText(localizacao.getNome()).setCallbackData("localizacao"+localizacao.getId()));
            rowsInline.add(rowInLine);
        }
        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }
}

