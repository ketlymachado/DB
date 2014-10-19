/*
 * Classe Principal
 *
 * Esse código foi desenvolvido para a disciplina de Programação I da Universidade Federal da Fronteira Sul.
 * Seu objetivo é instanciar o interpretador da linguagem DB, lendo o código fonte a partir do arquivo, para
 * então repassá-lo para ser interpretado.
 * 
 * Para informações sobre o uso da linguagem DB, consulte o manual.
 * Para informações sobre o interpretador, veja o arquivo 'Interpretador.java'.
 * 
 * Por Kétly Gonçalves Machado <ketly.machado@gmail.com>
 */

import java.util.Scanner;
import java.io.*;

class DB {
	public static void main(String args[]) throws Exception {
		File sourcecode;
		Scanner reader;
		Interpretador ITP;
		String code[] = new String[3500]; //Podem haver no máximo 3500 linhas de código;
		
		try {
			sourcecode = new File(args[0]); //Referencia para a variável sourcecode o arquivo passado por parâmetro;
			reader = new Scanner(sourcecode); //Determina a leitura a partir do arquivo;
			ITP = new Interpretador(); //Instancia o interpretador;
			
			int i = 0;
			while(reader.hasNext()) {
				//Faz a leitura de todo o código fonte do arquivo, tirando espaços excedentes no começo e no fim;
				code[i] = reader.nextLine().trim();
				i++;
			}
			
			//Inicia o interpretador com o código lido. 
			//A partir daqui o objeto ITP é quem realiza todo o trabalho de interpretação da linguagem;
			ITP.interpret(code);
			
        } catch(IOException e) {
			System.out.println("Nao consegui ler o arquivo: " + (args.length > 0 ? args[0] : "(desconhecido)"));
			System.out.println("Uso:");
			System.out.println("    java DB /caminho/para/arquivo.DB");
		}
	}
}
