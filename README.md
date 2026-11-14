# Pagaê

App Android (Kotlin) para anotar fiado: você cadastra pessoas e comidas, registra
quem comprou o quê e acompanha quanto cada um acumulou em dívidas.
Os dados ficam salvos localmente no celular (SQLite), sem internet.

## Telas (navegação inferior ou arrastando para o lado)

- **Vender** (tela inicial): escolha a comida e a pessoa (com busca) e salve a venda.
- **Perfis**: adicionar e editar pessoas e produtos, configurar o formato de exibição
  e abrir a tela **Sobre**.
- **Totais**: saldo de cada pessoa, gráfico de vendas por mês, histórico de compras
  e limpeza de saldos (subtrair um valor ou zerar).

## Como abrir

1. Instale o [Android Studio](https://developer.android.com/studio).
2. Clone este repositório (ou baixe o ZIP) e, no Android Studio, use **File > Open**
   e selecione a pasta `Pagae`.
3. Aguarde o Sync do Gradle e clique em **Run** com um celular (depuração USB) ou emulador.

## Versões

Gradle 8.7 · AGP 8.5.2 · Kotlin 1.9.24 · compileSdk 34 · minSdk 24

## Estrutura

- `app/src/main/java/com/pagae/app/` — código Kotlin (`MainActivity`, `db/`, `ui/`)
- `app/src/main/res/` — layouts, ícones, cores, temas e a fonte Matcha Home

## Créditos e licença

- Fonte **Matcha Home**, por Khurasan ([khurasanstudio.com](https://www.khurasanstudio.com)),
  de uso gratuito pessoal e comercial.
- Direitos autorais: veja o arquivo [LICENSE](LICENSE).
