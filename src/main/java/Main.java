import model.Localizacao;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import repository.CategoriaRepository;
import repository.Conexao;
import repository.LocalizacaoRepository;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Conexao conexao = new Conexao();
        CategoriaRepository  categoriaRepository= new CategoriaRepository(conexao);
        LocalizacaoRepository localizacaoRepository = new LocalizacaoRepository(conexao);
//        localizacaoRepository.criarTabela();
        Localizacao local =localizacaoRepository.findById(3);
        System.out.println(local.getId() +  "\t" +
                local.getNome() + "\t" +
                local.getDescricao());
//        localizacaoRepository.inserir(new Localizacao("quarto3","local para procastinar"));
//        List<Localizacao> locais = localizacaoRepository.findall();
//        System.out.println(locais.get(0).getNome());
//        categoriaRepository.criarTabela();
//        Bem bem = new Bem("PS4", "Um videogame em ótimo estado");
//        //bem.setCodigo("bem1");
//        bem.save();
//
//
//
//
//        bem = Bem.get(bem.getCodigo());
//        System.out.println(bem.getNome());
//        System.out.println(bem.getDescricao());
//
//
//        //Date d = new Date();
//        //System.out.println(d.getTime());
//
//        /*
//        ApiContextInitializer.init();
//        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
//        try {
//            telegramBotsApi.registerBot(new PatriBot());
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//
//         */
    }
}