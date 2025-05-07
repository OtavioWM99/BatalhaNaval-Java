package BatalhaNaval;
import java.util.Random;
import java.util.Scanner;

public class BatalhaNaval {

    public static final int AGUA = 0;
    public static final int BARCO = 1;
    public static final int AGUA_ATINGIDA = 2;
    public static final int BARCO_ATINGIDO = 3;

    public static final int TAMANHO_MAPA = 10;

    public static final String SIMBOLO_AGUA = "~ ";
    public static final String SIMBOLO_BARCO = "B ";
    public static final String SIMBOLO_AGUA_ATINGIDA = "O ";
    public static final String SIMBOLO_BARCO_ATINGIDO = "X ";

    public static final int MODO_PVE = 1;
    public static final int MODO_PVP = 2;

    public static void main(String[] args) {
        Scanner ler = new Scanner(System.in);
        Random aleatorio = new Random();

        // Inicializar configurações do jogo
        String[] nomeBarcos = {"Navio", "Navio", "Navio", "Navio"};
        int[] tamanhoBarcos = {4, 3, 2, 1};
        int[] quantidadeBarcos = {1, 2, 3, 4};

        exibirTelaInicial();

        // Obter configurações iniciais do jogo
        String nomeJogador1 = obterNomeJogador(ler, 1);
        int modoJogo = selecionarModoJogo(ler);
        String nomeJogador2 = obterNomeJogador2(ler, modoJogo);

        // Inicializar mapas dos jogadores
        int[][] mapaJogador1 = new int[TAMANHO_MAPA][TAMANHO_MAPA];
        int[][] mapaJogador2 = new int[TAMANHO_MAPA][TAMANHO_MAPA];
        int[][] mapaVisaoJogador1 = new int[TAMANHO_MAPA][TAMANHO_MAPA];
        int[][] mapaVisaoJogador2 = new int[TAMANHO_MAPA][TAMANHO_MAPA];

        posicionarBarcosParaJogador(mapaJogador1, nomeBarcos, tamanhoBarcos, quantidadeBarcos, ler, aleatorio, nomeJogador1, modoJogo);
        posicionarBarcosParaJogador(mapaJogador2, nomeBarcos, tamanhoBarcos, quantidadeBarcos, ler, aleatorio, nomeJogador2, modoJogo);

        iniciarJogo(mapaJogador1, mapaJogador2, mapaVisaoJogador1, mapaVisaoJogador2, nomeJogador1, nomeJogador2, modoJogo, ler, aleatorio);

        ler.close();
    }

    public static void exibirTelaInicial() {
        System.out.println("-----------------------");
        System.out.println("|    BATALHA NAVAL    |");
        System.out.println("-----------------------");
    }

    public static String obterNomeJogador(Scanner ler, int numeroJogador) {
        System.out.print("\nDigite o nome do jogador " + numeroJogador + ": ");
        return ler.nextLine();
    }

    public static int selecionarModoJogo(Scanner ler) {
        int modoJogo;
        do {
            System.out.println("\nEscolha o modo de jogo:");
            System.out.println("1 - Para jogar contra o computador");
            System.out.println("2 - Para jogar contra outro jogador");
            System.out.print("Opção: ");
            modoJogo = ler.nextInt();
            ler.nextLine();

            if (modoJogo != MODO_PVE && modoJogo != MODO_PVP) {
                System.out.println("Opção inválida! Tente novamente.");
            }
        } while (modoJogo != MODO_PVE && modoJogo != MODO_PVP);

        return modoJogo;
    }

    //Obtém o nome do segundo jogador com base no modo de jogo
    public static String obterNomeJogador2(Scanner ler, int modoJogo) {
        if (modoJogo == MODO_PVE) {
            System.out.println("\nVocê jogará contra o computador!");
            return "Computador";
        } else {
            return obterNomeJogador(ler, 2);
        }
    }

    public static void posicionarBarcosParaJogador(int[][] mapa, String[] nomeBarcos, int[] tamanhoBarcos,
                                                    int[] quantidadeBarcos, Scanner ler, Random aleatorio,
                                                    String nomeJogador, int modoJogo) {
        // Se for computador no modo PvE, posiciona automaticamente
        if (modoJogo == MODO_PVE && nomeJogador.equals("Computador")) {
            System.out.println();
            alocarBarcosAutomatico(mapa, tamanhoBarcos, quantidadeBarcos, aleatorio);
            System.out.println("Computador posicionou seus barcos!");
        } else {
            // Se for jogador humano, pergunta como quer posicionar
            System.out.println("\n" + nomeJogador + ", vamos posicionar seus barcos");
            alocarBarcos(mapa, nomeBarcos, tamanhoBarcos, quantidadeBarcos, ler, aleatorio, nomeJogador);
        }
    }


    //Método principal de exibição de mapas, com os símbolos adequados
    public static void mostrarMapa(int[][] mapa, boolean mostrarBarcos) {
        System.out.println("    A B C D E F G H I J");
        System.out.println("   ---------------------");

        for (int i = 0; i < mapa.length; i++) {
            System.out.print(" " + i + "| ");

            for (int j = 0; j < mapa[i].length; j++) {
                exibirCelula(mapa[i][j], mostrarBarcos);
            }
            System.out.println();
        }
    }

    public static void exibirCelula(int valorCelula, boolean mostrarBarcos) {
        switch (valorCelula) {
            case AGUA:
                System.out.print(SIMBOLO_AGUA); // Água
                break;
            case BARCO:
                // Se pode mostrar barcos, exibe-os, senão mostra como água
                System.out.print(mostrarBarcos ? SIMBOLO_BARCO : SIMBOLO_AGUA);
                break;
            case AGUA_ATINGIDA:
                System.out.print(SIMBOLO_AGUA_ATINGIDA);
                break;
            case BARCO_ATINGIDO:
                System.out.print(SIMBOLO_BARCO_ATINGIDO);
                break;
        }
    }


    //Mostra o mapa com todos os elementos (incluindo barcos não atingidos)
    public static void mostrarMapaCompleto(int[][] mapa) {
        mostrarMapa(mapa, true);
    }

    //Mostra o mapa de visão (escondendo barcos não atingidos)
    public static void mostrarMapaVisao(int[][] mapaVisao) {
        mostrarMapa(mapaVisao, false);
    }

    public static void alocarBarcos(int[][] mapa, String[] nomesBarcos, int[] tamanhosBarcos,
                                     int[] quantidadesBarcos, Scanner ler, Random aleatorio, String nomeJogador) {
        System.out.println("\nPosicionar barcos: ");
        System.out.print("Escolha (A(Automaticamente)/M(Manualmente): ");

        String escolha = ler.next().toLowerCase();
        while (!escolha.equals("a") && !escolha.equals("m")) {
            System.out.println("Opção inválida! Tente novamente:");
            System.out.print("Escolha (A/M): ");
            escolha = ler.next().toLowerCase();
        }

        if (escolha.equals("a")) {
            System.out.println("\nOs barcos serão posicionados automaticamente");
            alocarBarcosAutomatico(mapa, tamanhosBarcos, quantidadesBarcos, aleatorio);
            System.out.println("\nMapa de " + nomeJogador + ":");
            mostrarMapaCompleto(mapa);
        } else {
            alocarBarcosManual(mapa, nomesBarcos, tamanhosBarcos, quantidadesBarcos, ler, nomeJogador);
        }
    }

    public static void alocarBarcosManual(int[][] mapa, String[] nomesBarcos, int[] tamanhosBarcos,
                                           int[] quantidadesBarcos, Scanner ler, String nomeJogador) {
        // Exibir mapa inicial vazio
        System.out.println("\nMapa inicial de " + nomeJogador + ":");
        mostrarMapaCompleto(mapa);

        for (int tipo = 0; tipo < nomesBarcos.length; tipo++) {
            for (int quantidade = 0; quantidade < quantidadesBarcos[tipo]; quantidade++) {
                System.out.println("\nPosicione o Navio de Tamanho: " + tamanhosBarcos[tipo]);

                int[] posicao = obterPosicaoValidaParaBarco(mapa, tamanhosBarcos[tipo], ler);

                colocarBarco(mapa, posicao[0], posicao[1], tamanhosBarcos[tipo], posicao[2] == 1);

                System.out.println("\nMapa atualizado:");
                mostrarMapaCompleto(mapa);
            }
        }
    }

    public static int[] obterPosicaoValidaParaBarco(int[][] mapa, int tamanhoBarco, Scanner ler) {
        int linha, coluna, orientacao;
        boolean posicaoValida;

        do {
            System.out.println("Digite 1 para posicionar o barco horizontalmente");
            System.out.println("Digite 2 para posicionar o barco verticalmente");
            System.out.print("Orientação: ");
            orientacao = ler.nextInt();

            while (orientacao != 1 && orientacao != 2) {
                System.out.println("Orientação inválida! Tente novamente:");
                System.out.print("Orientação: ");
                orientacao = ler.nextInt();
            }

            System.out.print("Linha (0-9): ");
            linha = ler.nextInt();

            System.out.print("Coluna (A-J): ");
            String colunaLetra = ler.next().toUpperCase();
            coluna = colunaLetra.charAt(0) - 'A'; // Converter letra para índice

            posicaoValida = verificarPosicao(mapa, linha, coluna, tamanhoBarco, orientacao == 1);

            if (!posicaoValida) {
                System.out.println("Posição inválida! O barco não cabe nessa posição ou colide com outro barco.");
            }
        } while (!posicaoValida);

        return new int[] {linha, coluna, orientacao};
    }

    public static void alocarBarcosAutomatico(int[][] mapa, int[] tamanhosBarcos,
                                               int[] quantidadesBarcos, Random aleatorio) {

        for (int tipo = 0; tipo < tamanhosBarcos.length; tipo++) {

            for (int quantidade = 0; quantidade < quantidadesBarcos[tipo]; quantidade++) {
                boolean posicionado = false;

                // Tentar posições aleatórias até conseguir posicionar
                while (!posicionado) {
                    // Gerar posição e orientação aleatórias
                    int linha = aleatorio.nextInt(TAMANHO_MAPA);
                    int coluna = aleatorio.nextInt(TAMANHO_MAPA);
                    boolean horizontal = aleatorio.nextBoolean();

                    if (verificarPosicao(mapa, linha, coluna, tamanhosBarcos[tipo], horizontal)) {

                        colocarBarco(mapa, linha, coluna, tamanhosBarcos[tipo], horizontal);
                        posicionado = true;
                    }
                }
            }
        }
    }

    public static boolean verificarPosicao(int[][] mapa, int linha, int coluna,
                                            int tamanhoBarco, boolean horizontal) {
        // Verificar se a posição inicial está dentro do mapa
        if (linha < 0 || linha >= mapa.length || coluna < 0 || coluna >= mapa[0].length) {
            return false;
        }

        if (horizontal) {
            // Verificar se o barco cabe horizontalmente
            if (coluna + tamanhoBarco > mapa[0].length) {
                return false;
            }

            // Verificar se não há sobreposição com outros barcos
            for (int i = 0; i < tamanhoBarco; i++) {
                if (mapa[linha][coluna + i] != AGUA) {
                    return false;
                }
            }
        }
        // Verificar espaço e sobreposição na vertical
        else {
            // Verificar se o barco cabe verticalmente
            if (linha + tamanhoBarco > mapa.length) {
                return false;
            }

            for (int i = 0; i < tamanhoBarco; i++) {
                if (mapa[linha + i][coluna] != AGUA) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void colocarBarco(int[][] mapa, int linha, int coluna,
                                     int tamanhoBarco, boolean horizontal) {
        if (horizontal) {
            for (int i = 0; i < tamanhoBarco; i++) {
                mapa[linha][coluna + i] = BARCO;
            }
        } else {
            for (int i = 0; i < tamanhoBarco; i++) {
                mapa[linha + i][coluna] = BARCO;
            }
        }
    }

    public static void iniciarJogo(int[][] mapaJogador1, int[][] mapaJogador2,
                                    int[][] mapaVisaoJogador1, int[][] mapaVisaoJogador2,
                                    String nomeJogador1, String nomeJogador2,
                                    int modoJogo, Scanner ler, Random aleatorio) {
        // Contar barcos iniciais
        int barcosRestantesJogador1 = contarBarcos(mapaJogador1);
        int barcosRestantesJogador2 = contarBarcos(mapaJogador2);

        // Definir quem começa jogando
        boolean jogador1Ativo = true;

        // Loop principal do jogo
        while (barcosRestantesJogador1 > 0 && barcosRestantesJogador2 > 0) {
            if (jogador1Ativo) {

                System.out.println("\n-- Vez de " + nomeJogador1 + " --");
                System.out.println("Mapa do adversário (" + nomeJogador2 + "):");
                mostrarMapaVisao(mapaVisaoJogador1);

                // Realizar jogada e verificar resultado
                boolean acertouBarco = realizarJogada(mapaJogador2, mapaVisaoJogador1, ler, nomeJogador1);
                barcosRestantesJogador2 = contarBarcos(mapaJogador2);

                // Informar resultado e verificar se continua jogando
                if (acertouBarco) {
                    System.out.println("Acertou um barco! Jogue novamente.");
                } else {
                    jogador1Ativo = false;
                    System.out.println("Acertou na água! Vez do seu adversário jogar.");
                }
            } else {

                System.out.println("\n-- Vez de " + nomeJogador2 + " --");

                boolean acertouBarco;

                if (modoJogo == MODO_PVE) {
                    System.out.println("Jogada do computador");
                    acertouBarco = realizarJogadaComputador(mapaJogador1, mapaVisaoJogador2, aleatorio);
                    barcosRestantesJogador1 = contarBarcos(mapaJogador1);

                    if (acertouBarco) {
                        System.out.println("O Computador acertou seu barco! Ele vai jogar de novo.");
                    } else {
                        jogador1Ativo = true;
                        System.out.println("O Computador acertou na água! Agora é sua vez.");
                    }
                } else {
                    System.out.println("Mapa do seu adversário (" + nomeJogador1 + "):");
                    mostrarMapaVisao(mapaVisaoJogador2);

                    acertouBarco = realizarJogada(mapaJogador1, mapaVisaoJogador2, ler, nomeJogador2);
                    barcosRestantesJogador1 = contarBarcos(mapaJogador1);

                    if (acertouBarco) {
                        System.out.println("Acertou um barco! Jogue novamente.");
                    } else {
                        jogador1Ativo = true;
                        System.out.println("Acertou na água! Vez do seu adversário jogar.");
                    }
                }
            }
        }

        exibirResultadoFinal(barcosRestantesJogador1, nomeJogador1, nomeJogador2);
    }

    public static boolean realizarJogada(int[][] mapaOponente, int[][] mapaVisao, Scanner ler, String nomeJogador) {
        // Coordenadas do tiro
        int[] coordenadas = obterCoordenadas(mapaVisao, ler, nomeJogador);
        int linha = coordenadas[0];
        int coluna = coordenadas[1];

        // Realizar o ataque e verificar resultado
        if (mapaOponente[linha][coluna] == BARCO) {
            // Acertou um barco
            mapaOponente[linha][coluna] = BARCO_ATINGIDO;
            mapaVisao[linha][coluna] = BARCO_ATINGIDO;
            return true;
        } else {
            // Acertou na água
            mapaOponente[linha][coluna] = AGUA_ATINGIDA;
            mapaVisao[linha][coluna] = AGUA_ATINGIDA;
            return false;
        }
    }

    public static int[] obterCoordenadas(int[][] mapaVisao, Scanner ler, String nomeJogador) {
        int linha, coluna;
        boolean jogadaValida = false;

        do {
            System.out.print("\n" + nomeJogador + ", escolha a linha para atacar (0-9): ");
            linha = ler.nextInt();

            System.out.print("Escolha a coluna para atacar (A-J): ");
            String colunaLetra = ler.next().toUpperCase();
            coluna = colunaLetra.charAt(0) - 'A'; // Converter letra para índice

            // Validar coordenadas
            if (linha < 0 || linha >= mapaVisao.length || coluna < 0 || coluna >= mapaVisao[0].length) {
                System.out.println("Posição inválida! Escolha linha entre 0 e 9 e coluna entre A e J.");
            }
            // Verificar se a posição já foi atacada
            else if (mapaVisao[linha][coluna] == AGUA_ATINGIDA || mapaVisao[linha][coluna] == BARCO_ATINGIDO) {
                System.out.println("Você já atacou essa posição! Escolha outra.");
            } else {
                jogadaValida = true;
            }
        } while (!jogadaValida);

        return new int[] {linha, coluna};
    }

    public static boolean realizarJogadaComputador(int[][] mapaOponente, int[][] mapaVisao, Random aleatorio) {
        int linha, coluna;
        boolean jogadaValida = false;

        // Escolher uma posição válida (que não tenha sido atingida antes)
        do {
            linha = aleatorio.nextInt(TAMANHO_MAPA);
            coluna = aleatorio.nextInt(TAMANHO_MAPA);

            if (mapaVisao[linha][coluna] != AGUA_ATINGIDA && mapaVisao[linha][coluna] != BARCO_ATINGIDO) {
                jogadaValida = true;
            }
        } while (!jogadaValida);

        char colunaLetra = (char)('A' + coluna);
        System.out.println("Computador atacou em: Linha " + linha + ", Coluna " + colunaLetra);

        if (mapaOponente[linha][coluna] == BARCO) {
            mapaOponente[linha][coluna] = BARCO_ATINGIDO;
            mapaVisao[linha][coluna] = BARCO_ATINGIDO;
            return true;
        } else {
            mapaOponente[linha][coluna] = AGUA_ATINGIDA;
            mapaVisao[linha][coluna] = AGUA_ATINGIDA;
            return false;
        }
    }

    public static int contarBarcos(int[][] mapa) {
        int contador = 0;

        // Percorrer todo o mapa contando células com barcos não atingidos
        for (int i = 0; i < mapa.length; i++) {
            for (int j = 0; j < mapa[i].length; j++) {
                if (mapa[i][j] == BARCO) {
                    contador++;
                }
            }
        }

        return contador;
    }

    public static void exibirResultadoFinal(int barcosRestantesJogador1, String nomeJogador1, String nomeJogador2) {
        System.out.println("\n --ACABOU O JOGO--");

        if (barcosRestantesJogador1 == 0) {
            System.out.println("PARABÉNS PELA VITÓRIA " + nomeJogador2.toUpperCase());
        } else {
            System.out.println("PARABÉNS PELA VITÓRIA " + nomeJogador1.toUpperCase());
        }
    }
}