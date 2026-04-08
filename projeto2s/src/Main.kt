import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


//constantes globais
const val MSG_CONTINUAR = "(prima enter para continuar)"
const val FICHEIRO_CONFIG = "configuracoes.txt"
const val FICHEIRO_INVESTIMENTOS = "investimentos.txt"
const val FICHEIRO_LIQUIDACOES = "liquidacoes.txt"

fun main() {

    val investimentos = Array<Array<String>?>(99) { null } // array para armazenar ate 99 investimentos
    val liquidacoes = Array(2) { 0.00 }

    // carrega os dados dos ficheiros
    lerInvestimentos(FICHEIRO_INVESTIMENTOS, investimentos)
    lerLiquidacoes(FICHEIRO_LIQUIDACOES, liquidacoes)


    println("\n#####################")
    println("### Investimentos ###")
    println("#####################\n")

    val configuracoes = obterConfiguracoes() //lê ou pede as configuraçoes ao utilizaodr

    println("Ola ${configuracoes[0]}\n")

    var terminar = false
    while (!terminar) {
        println(obterMenu()) //mostra o menu principal
        println ("Indique o comando que pretende:\n")
        val opcao = readln()


        when (opcao) {
            "1" -> {
                val resultado = consultarInvestimentos(investimentos, configuracoes, liquidacoes)
                println(resultado)
                println(MSG_CONTINUAR)
                readln()
            }

            "2" -> {
                println("Adicionar investimento")
                println()

                var nome = ""
                var nomeValido = false
                while (!nomeValido) {
                    println("Nome do investimento:")
                    nome = readln()

                    nomeValido = nome.length >= 3 && nome.all { it.isLetter() }

                    if (!nomeValido) {
                        println()
                        println("Nome invalido, o nome apenas pode conter letras e tem de ter no minimo 3 caracteres.")
                        println(MSG_CONTINUAR)
                        readln()

                        println()
                    }
                }

                println("Valor investido:")
                val valInvestido = readln().toDoubleOrNull()

                println("Valor atual (PU):")
                val valAtual = readln().toDoubleOrNull()

                if (valInvestido == null || valAtual == null) {
                    println("Dados invalidos.")
                } else {
                    val msg = adicionarInvestimento(investimentos, nome, valInvestido, valAtual)
                    println()
                    println(msg)
                    println(MSG_CONTINUAR)
                    readln()
                }

            }

            "3" -> {
                println("Editar investimento")
                println()

                println("Nome do investimento:")
                val nome = readln()

                println("Valor atual (PU):")
                val valAtual = readln().toDoubleOrNull()
                println()

                if (valAtual == null) {
                    println("Valor atual invalido.")
                } else {
                    val msg = editarInvestimento(investimentos, nome, valAtual)
                    println(msg)
                }
                println(MSG_CONTINUAR)
                readln()
            }

            "4" -> {
                println("Liquidar investimento")
                println("Numero do investimento:")
                val num = readln().toIntOrNull()

                if (num == null) {
                    println("Numero invalido.")
                } else {
                    val msg = liquidarInvestimento(liquidacoes, investimentos, num)
                    println()
                    println(msg)

                }
                println(MSG_CONTINUAR)
                readln()
            }

            "5" -> {
                guardarConfiguracoes(FICHEIRO_CONFIG, configuracoes)
                guardarInvestimentos(FICHEIRO_INVESTIMENTOS, investimentos)
                guardarLiquidacoes(FICHEIRO_LIQUIDACOES, liquidacoes)
                println("Investimentos guardados com sucesso.")
                println(MSG_CONTINUAR)
                readln()
            }

            "9" -> {
                println("Configuracoes")
                println()
                val novasConfiguracoes = obterConfiguracoes(forcarPedido = true)
                configuracoes[0] = novasConfiguracoes[0]
                configuracoes[1] = novasConfiguracoes[1]
                println("Configuracoes atualizadas com sucesso!")
                println(MSG_CONTINUAR)
                readln()
            }

            "0" -> {
                println("Adeus e bons investimentos!")
                terminar = true
            }

            else -> {
                println("Opcao invalida!")
                println(MSG_CONTINUAR)
                readln()
            }
        }
    }
}

// FUN OBG
fun obterMenu(): String {
    val menu = "#  Comando\n" +
            "1. Consultar\n" +
            "2. Adicionar\n" +
            "3. Editar\n" +
            "4. Liquidar\n" +
            "5. Guardar\n" +
            "9. Configuracoes\n" +
            "0. Sair\n"
    return menu
}
fun lerConfiguracoes(fileName: String, configuracoes: Array<String>): Boolean {
    val file = File(fileName)

    // Verificar se o arquivo existe
    if (!file.exists()) {
        return false
    }

    // Ler as linhas do arquivo
    val linhas = file.readLines()

    // Verificar se o arquivo contém pelo menos duas linhas
    if (linhas.size >= 2) {
        configuracoes[0] = linhas[0]  // Nome
        configuracoes[1] = linhas[1]  // Moeda
        return true
    }

    return true
}
fun guardarConfiguracoes(fileName: String, configuracoes: Array<String>) {
    val file = File(fileName)
    file.writeText("${configuracoes[0]}\n${configuracoes[1]}")
}
fun lerLiquidacoes(fileName: String, liquidacoes: Array<Double>): Boolean {
    val file = File(fileName)
    if (!file.exists()) return false

    val linhas = file.readLines()
    if (linhas.size >= 2) {
        liquidacoes[0] = linhas[0].toDouble()
        liquidacoes[1] = linhas[1].toDouble()
        return true
    }

    return true
}
fun guardarLiquidacoes(fileName: String, liquidacoes: Array<Double>) {
    val file = File(fileName)
    file.writeText("${liquidacoes[0]}\n${liquidacoes[1]}")
}
fun lerInvestimentos(fileName: String, investimentos: Array<Array<String>?>): Boolean {
    val file = File(fileName)

    // Verifica se o ficheiro existe
    if (!file.exists()) {
        return false
    }

    // lê todas as linhas do ficheiro
    val linhas = file.readLines()

    var indiceInvestimento = 0
    var indiceLinha = 0

    // Enquanto houver linhas para ler e não atingirmos 99 investimentos
    while (indiceLinha < linhas.size && indiceInvestimento < 99) {
        val linha = linhas[indiceLinha].trim()

        if (linha.isNotEmpty()) {
            val partesTemp = linha.split("|")
            val dados = Array(partesTemp.size) { i -> partesTemp[i].trim() }
            investimentos[indiceInvestimento] = dados
            indiceInvestimento++
        }

        indiceLinha++
    }

    return true
}
fun consultarInvestimentos(investimentos: Array<Array<String>?>, configuracoes: Array<String>, liquidacoes: Array<Double>): String {

//    val cabecalho = "# Nome   | Data                | Valor Investido | Valor Atual | Quantidade | Rentabilidade"

    val investimentosExistem = investimentos.any { it != null }

    return if (investimentosExistem) {
        val textoInvestimentos = StringBuilder()
        textoInvestimentos.appendLine("Investimentos:")
        // Calcular o comprimento mÃ¡ximo do nome
        var tamanhoMaxNome = "Nome".length
        for (investimento in investimentos) {
            if (investimento != null && investimento.size >= 1) {
                val nomeLength = investimento[0].length
                if (nomeLength > tamanhoMaxNome) {
                    tamanhoMaxNome = nomeLength
                }
            }
        }

        val cabecalho = String.format(
            "# %-${tamanhoMaxNome}s | %-19s | %-15s | %-11s | %-10s | %-13s",
            "Nome", "Data", "Valor Investido", "Valor Atual", "Quantidade", "Rentabilidade"
        )

        textoInvestimentos.appendLine(cabecalho)

        var numeroLinha = 1
        for (investimento in investimentos) {
            if (investimento != null) {
                val quantidadeDouble = investimento.getOrElse(4) { "0.0" }.toDoubleOrNull() ?: 0.0
                val valorAtualDouble = investimento.getOrElse(3) { "0.0" }.toDoubleOrNull() ?: 0.0
                val valorInvestidoDouble = investimento.getOrElse(2) { "0.0" }.toDoubleOrNull() ?: 0.0
                val linha = String.format(
                    "%-2d%-${tamanhoMaxNome}s | %-19s | %-15s | %-11s | %-10s | %-13s",
                    numeroLinha,
                    investimento.getOrElse(0) { "" },
                    investimento.getOrElse(1) { "" },
                    investimento.getOrElse(2) { "" } + " " + configuracoes[1],
                    investimento.getOrElse(3) { "" } + " " + configuracoes[1],
                    String.format(Locale.US,"%.2f",quantidadeDouble),
                    "${String.format("%.0f", calcularRentabilidade(valorAtualDouble, quantidadeDouble, valorInvestidoDouble))} %"
                )

                textoInvestimentos.appendLine(linha)
                numeroLinha++;
            }
        }

        textoInvestimentos.appendLine()
        textoInvestimentos.appendLine()
        textoInvestimentos.append("Lucro: ${String.format(Locale.US, "%.2f", liquidacoes[1] - liquidacoes[0])} ${configuracoes[1]}")
        textoInvestimentos.toString()
    } else {
        val lucro = String.format(Locale.US, "%.2f", liquidacoes[1] - liquidacoes[0])
        return "Nao existem investimentos em carteira.\n\nLucro: $lucro ${configuracoes[1]}"
    }
}
fun adicionarInvestimento(investimentos: Array<Array<String>?>, nome:String, valorinvestido:Double, valorAtual:Double):String{

    var sucesso = false
    var indice = 0
    while (indice< investimentos.size && !sucesso) {
        if (investimentos[indice] == null) {
            val quantidade = valorinvestido / valorAtual
            val rentabilidade = 0
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dataCriacao = LocalDateTime.now().format(formatter)

            investimentos[indice] = arrayOf(
                nome,
                dataCriacao,
                valorinvestido.toString(),
                valorAtual.toString(),
                quantidade.toString(),
                rentabilidade.toString()
            )

            sucesso = true
        }
        indice++
    }

    return "Investimento adicionado com sucesso!"
}
fun editarInvestimento(investimentos: Array<Array<String>?>, nome: String, novoValorAtualPU: Double): String {
    var atualizado = false

    for (i in investimentos.indices) {
        val investimento = investimentos[i]
        if (investimento != null && investimento[0] == nome) {
            if (investimento.size >= 4) {
                investimento[3] = novoValorAtualPU.toString()
                atualizado = true
            }
        }
    }

    return if (atualizado) {
        "Investimentos atualizados com sucesso!"
    } else {
        "Nao existem investimentos em carteira com esse nome."
    }
}
fun liquidarInvestimento(liquidacoes: Array<Double>, investimentos: Array<Array<String>?>, numero: Int): String {
    val indice = numero - 1

    if (indice < 0 || indice >= investimentos.size || investimentos[indice] == null) {
        return "Nao existem investimentos em carteira com esse numero."
    }

    val investimento = investimentos[indice]!!

    val valorInvestido = investimento.getOrElse(2) { "0.0" }.toDoubleOrNull() ?: 0.0
    val valorAtualPU = investimento.getOrElse(3) { "0.0" }.toDoubleOrNull() ?: 0.0
    val quantidade = investimento.getOrElse(4) { "0.0" }.toDoubleOrNull() ?: 0.0

    liquidacoes[0] += valorInvestido
    liquidacoes[1] += valorAtualPU * quantidade

    investimentos[indice] = null

    return "Investimento liquidado com sucesso!"
}
fun guardarInvestimentos(fileName: String, investimentos: Array<Array<String>?>) {
    val file = File(fileName)
    val writer = file.bufferedWriter()

//    // Escreve o cabeçalho
//    writer.write("# Nome|Data                | Valor Investido | Valor Atual | Quantidade | Rentabilidade")
//    writer.newLine()

    // Escreve cada linha de investimento, se não for null
    for (investimento in investimentos) {
        if (investimento != null) {
            val linha = investimento.joinToString(" | ")
            writer.write(linha)
            writer.newLine()
        }
    }

    writer.close()
}


// FUN AUX
fun validarNome(nome: String): Boolean {
    if (!nome.contains(" ")) {
        return false
    }
    if (nome.length < 4) {
        return false
    }
    return true
}
fun validarMoeda(moeda: String): Boolean {
    if (moeda == "€" || moeda == "$") {
        return true
    }
    return false
}
fun obterConfiguracoes(forcarPedido: Boolean = false): Array<String> {
    val configuracoes = Array(2) { "" }
    if (!forcarPedido) {
        val lido = lerConfiguracoes(FICHEIRO_CONFIG, configuracoes)
        if (lido && validarNome(configuracoes[0]) && validarMoeda(configuracoes[1])) {
            return configuracoes
        }
    }
    // pedir configuracoes (mesmo código de antes)
    while (true) {
        println("Por favor indique o seu nome:")
        val nome = readln()
        println("Por favor indique a moeda da sua conta (€ ou $):")
        val moeda = readln()

        if (validarNome(nome) && validarMoeda(moeda)) {
            println()
            configuracoes[0] = nome
            configuracoes[1] = moeda
            guardarConfiguracoes(FICHEIRO_CONFIG, configuracoes)
            return configuracoes
        } else {
            println("Dados invalidos.")
            println("O nome completo deve ser definido por pelo menos dois nomes e ter pelo menos um espaço vazio e pelo menos 4 caracteres.")
            println("A moeda deverá ser € ou $.")
            println()
        }
    }
}
fun calcularRentabilidade(novoValorAtualPU: Double, quantidade: Double, valorInvestido: Double) : Double {

    val valorAtualTotal = novoValorAtualPU * quantidade
    var percentagem = 0.0

    if (valorInvestido != 0.0) {
        percentagem = ((valorAtualTotal - valorInvestido) * 100) / valorInvestido
    }

    // Corrigir -0.0 para 0.0
    if (percentagem == -0.0) {
        percentagem= 0.0
    }
    return percentagem

}