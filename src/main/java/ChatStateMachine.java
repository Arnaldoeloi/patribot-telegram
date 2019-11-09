public enum ChatStateMachine {
    AGUARDANDO_COMANDO,

    LISTANDO_BENS,
    LISTANDO_CATEGORIAS,
    LISTANDO_LOCALIZACOES,

    CADASTRANDO_BEM,

    ESPERANDO_NOME_BEM,

    ESPERANDO_DESCRICAO_BEM,
    ESPERANDO_CATEGORIA_BEM,
    ESPERANDO_LOCALIZACAO_BEM,

    ESPERANDO_NOME_CATEGORIA,
    ESPERANDO_DESCRICAO_CATEGORIA,

    ESPERANDO_NOME_LOCALIZACAO,
    ESPERANDO_DESCRICAO_LOCALIZACAO,

    ESPERANDO_CODIGO_BUSCA_BEM,
    ESPERANDO_NOME_BUSCA_BEM,
    ESPERANDO_DESCRICAO_BUSCA_BEM,
    CADASTRANDO_CATEGORIA,
    CADASTRANDO_LOCALIZACAO,
    MOVENDO_BEM,
    GERANDO_RELATORIO,
}
