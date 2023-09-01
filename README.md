<img src="./assets/images/dbest-logo.png" alt="Logo do DBest" width="256">

# Database Basics for Engaging Students and Teachers (DBest)

## Índice

1. [Resumo](#resumo)
2. [Objetivo inicial](#objetivo-inicial)
3. [Objetivo atual](#objetivo-atual)
4. [Ferramentas](#ferramentas)
5. [Utilização](#utilização)
6. [Dependências externas](#dependências-externas)
7. [Contribuições](#contribuições)

## Resumo

### Objetivo inicial

O DBest surge como um projeto que pretende ser um [Sistema Gerenciador de Banco de Dados][sgbd] (SGBD) que demonstra as operações da [álgebra relacional][algebra-relacional] de uma forma visual, de modo que fosse implementado de maneira extensível, assim facilitando a adição de novas operações.

### Objetivo atual

O escopo atual visa construir um plano de execução de consulta em um nível mais baixo.

### Ferramentas

O software é construído utilizando Java.

### Utilização

Este software existe com a intenção de facilitar o aprendizado das operações envolvendo tabelas relacionais.

## Dependências externas

| Nome                                                         | Descrição                                                                        |
| :----------------------------------------------------------- | :------------------------------------------------------------------------------- |
| [For Your Information Database (FYI Database)][fyi-database] | Utilizado como o banco de dados principal                                        |
| [ANTLR4][antlr4]                                             | Utilizado para validar as entradas possíveis da DSL desenvolvida para o software |
| [JGraphX][jgraphx]                                           | Utilizado para construir os nós visuais e suas conexões                          |
| [Data Faker][data-faker]                                     | Utilizado para gerar dados e popular as tabelas criadas na ferramenta            |

## Contribuições

Caso você queira contribuir com o projeto, abaixo estão os contatos das pessoas que participam dele.

| Nome   | Contato                                                                                           |
| :----- | :------------------------------------------------------------------------------------------------ |
| Sérgio | [sergio@inf.ufsm.br][email-inf-sergio]                                                            |
| Rhuan  | [rmoreiramaciel@gmail.com][email-pessoal-rhuan] <br> [rmmaciel@inf.ufsm.br][email-inf-rhuan]      |
| Luiz   | [lhlago@inf.ufsm.br][email-inf-luiz]                                                              |
| Marcos | [marcosvisentini@gmail.com][email-pessoal-marcos] <br> [mvisentini@inf.ufsm.br][email-inf-marcos] |

<!-- Links -->

[sgbd]:                 <https://pt.wikipedia.org/wiki/Sistema_de_gerenciamento_de_banco_de_dados> "Sistema Gerenciador de Banco de Dados"
[algebra-relacional]:   <https://pt.wikipedia.org/wiki/%C3%81lgebra_relacional>                    "Álgebra relacional"
[fyi-database]:         <https://github.com/crazynds/FyiDatabase-Java>                             "For Your Information Database (FYI Database)"
[antlr4]:               <https://github.com/antlr/antlr4>                                          "ANTLR4"
[jgraphx]:              <https://github.com/vlsi/jgraphx-publish>                                  "JGraphX"
[data-faker]:           <https://github.com/datafaker-net/datafaker>                               "Data Faker"
[email-inf-sergio]:     <mailto:sergio@inf.ufsm.br>                                                "E-mail da informática do Sérgio"
[email-pessoal-rhuan]:  <mailto:rmoreiramaciel@gmail.com>                                          "E-mail pessoal do Rhuan"
[email-inf-rhuan]:      <mailto:rmmaciel@inf.ufsm.br>                                              "E-mail da informática do Rhuan"
[email-inf-luiz]:       <mailto:lhlago@inf.ufsm.br>                                                "E-mail da informática do Luiz"
[email-pessoal-marcos]: <mailto:marcosvisentini@gmail.com>                                         "E-mail pessoal do Marcos"
[email-inf-marcos]:     <mailto:mvisentini@inf.ufsm.br>                                            "E-mail da informática do Marcos"
