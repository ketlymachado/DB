/*
 * Interpretador
 *
 * Esse código foi desenvolvido para a disciplina de Programação I da Universidade Federal da Fronteira Sul.
 * Por meio da linguagem Java, ele trabalha como um interpretador para a linguagem DB. Seu objetivo é, 
 * dado um código escrito na linguagem DB, interpretar todos os comandos presentes no mesmo, executando-os
 * ou relatando erros de sintaxe.
 * 
 * Para informações sobre o uso da linguagem DB, consulte o manual.
 * 
 * Por Kétly Gonçalves Machado <ketly.machado@gmail.com>
 */

import java.util.Scanner;

class Interpretador {
	private Variavel vars[];
	private int fpov; //Guarda a primeira posição vazia do vetor vars (first position of vars - fpov);
	private int flagloop;
	
	public Interpretador() {
		//Construtor da classe Interpretador;
		this.vars = new Variavel[1000]; //Permite até 1000 variáveis;
		this.fpov = 0;
		this.flagloop = 0;
	}
	
	private void reportError(int i) {
		i++;
		this.flagloop = 1;
		System.out.println("Syntax error at line " + i + ".");
	}
	
	private boolean validName(String s) {
		//Retorna verdadeiro se a string s possui apenas letras ou números e falso em outros casos;
		int i;
		for(i=0;i<s.length();i++) {
			if (i==0 && Character.isDigit(s.charAt(i))) return false;
			if (!(Character.isDigit(s.charAt(i)) || Character.isLetter(s.charAt(i))))
				return false;
		}
		return true;
	}
	
	private boolean validNumber(String s) {
		//Retorna verdadeiro se a string s possui apenas números e falso em outros casos;
		int i;
		for(i=0;i<s.length();i++) {
			if ((!(Character.isDigit(s.charAt(i)))) && (s.charAt(i)!='.') && (s.charAt(i)!='-'))
				return false;
		}
		return true;
	}
	
	private boolean onlySpaces(String s) {
		//Retorna verdadeiro se a string s possui apenas espaços e falso em outros casos;
		int i;
		for(i=0;i<s.length();i++) {
			if (s.charAt(i)!=32 && s.charAt(i)!=9)
				return false;
		}
		return true;
	}
	
	private boolean haveQuotes(String s) {
		if (s.charAt(0)==34 && s.charAt(s.length()-1)==34)
			return true;
		return false;
	}
	
	private Variavel validVar(String s) {
		//Retorna o objeto com o nome igual a string s, se a string s é o nome de uma váriavel válida e null em outros casos;
		int i;
		for (i=0;i<this.fpov;i++) {
			if (s.equals(this.vars[i].getName()))
				return this.vars[i];
		}
		return null;
	}
	
	private boolean verify(String a, String b) {
		//Verifica se as duas strings recebidas são números ou variáveis válidas;
		if ((!a.isEmpty()) && (!b.isEmpty()) && (!onlySpaces(a)) && (!onlySpaces(b))) {
			if ((validNumber(a) || validVar(a)!=null) && (validNumber(b) || validVar(b)!=null))
				return true;
		}
		return false;
	}
	
	private String isValidOperation(String s) {
		//Verifica se a string s representa uma operação válida, conforme seus operandos e operadores;
		String aux[];
		aux = s.split("\\*");
		if (aux.length==2) {
			if (verify(aux[0].trim(), aux[1].trim())) {
				return "*";
			} else return "$";
		}
		aux = s.split("/");
		if (aux.length==2) {
			if (verify(aux[0].trim(), aux[1].trim())) {
				return "/";
			} else return "$";
		}
		aux = s.split("\\+");
		if (aux.length==2) {
			if (verify(aux[0].trim(), aux[1].trim())) {
				return "+";
			} else return "$";
		}
		aux = s.split("-");
		if (aux.length==2) {
			if (verify(aux[0].trim(), aux[1].trim())) {
				return "-";
			} else return "$";
		}
		aux = s.split("%");
		if (aux.length==2) {
			if (verify(aux[0].trim(), aux[1].trim())) {
				Variavel v1 = validVar(aux[0].trim());
				Variavel v2 = validVar(aux[1].trim());
				if ((v1!=null && v1.getType()=='i') || aux[0].indexOf(".")<0) {
					if ((v2!=null && v2.getType()=='i') || aux[1].indexOf(".")<0) {
						return "%";
					}
				}
			} else return "$";
		}
		return "$";
	}
	
	private double resolvesOperation(String s, String spl) {
		//Verifica se os operandos são variáveis ou números, obtém seus valores, realiza a opeção conforme o operador e retorna o valor obtido;
		double a, b, r=0;
		int c, d;
		Variavel v;
		String aux[];
	
		if (spl.equals("%")) {
			aux = s.split(spl);
			if (validNumber(aux[0].trim())) {
				c = Integer.parseInt(aux[0].trim());
			} else {
				v = validVar(aux[0].trim());
				c = (int)v.getVarNum();
			}
			if (validNumber(aux[1].trim())) {
				d = Integer.parseInt(aux[1].trim());
			} else {
				v = validVar(aux[1].trim());
				d = (int)v.getVarNum();
			}
			r = c%d;
		} else {
			if (spl.equals("+") || spl.equals("*"))
				spl = "\\" + spl;
			aux = s.split(spl);
			if (validNumber(aux[0].trim())) {
				a = Double.parseDouble(aux[0].trim());
			} else {
				v = validVar(aux[0].trim());
				a = v.getVarNum();
			}
			if (validNumber(aux[1].trim())) {
				b = Double.parseDouble(aux[1].trim());
			} else {
				v = validVar(aux[1].trim());
				b = v.getVarNum();
			}
			if (spl.equals("\\+")) {
				r = a + b;
			} else if (spl.equals("\\*")) {
				r = a * b;
			} else if (spl.equals("/")) {
				r = a / b;
			} else if (spl.equals("-")){
				r = a - b;
			} 
		}
		return r;
	}
	
	private int validCondition(String s) {
		//Retorna 1 se a expressão dentro de um controlador de fluxo é válida e verdadeira, 0 se ela é válida e falsa e 2 se ela é inválida;
		Variavel v = validVar(s), v2;
		String aux[];
		int c;
		//Verifica se a expressão é apenas uma váriavel ou um número válidos;
		if (v!=null) {
			if (v.getType()=='s')
				return 2;
			if (v.getVarNum()!=0)
				return 1;
			return 0;
		}
		if (validNumber(s)) {
			if (Double.parseDouble(s)!=0)
				return 1;
			return 0;
		}
		//Caso não seja, verifica qual o operador booleano da expressão;
		aux = s.split("<==");
		c = 1;
		if (aux.length!=2) {
			aux = s.split(">==");
			c = 2;
			if (aux.length!=2) {
				aux = s.split("==");
				c = 3;
				if (aux.length!=2) {
					aux = s.split("<");
					c = 4;
					if (aux.length!=2) {
						aux = s.split(">");
						c = 5;
						if (aux.length!=2) {
							aux = s.split("#");
							c = 6;
						}
					}
				}
			}
		}
		aux[0] = aux[0].trim();
		aux[1] = aux[1].trim();
		v = validVar(aux[0]);
		if (v!=null || validNumber(aux[0]) || haveQuotes(aux[0])) {
			//Verifica se o lado esquerdo da expressão é uma variável, um número ou uma string;
			v2 = validVar(aux[1]);
			if (v2!=null || validNumber(aux[1]) || haveQuotes(aux[0])) {
				//Verifica se o lado direito da expressão é uma variável, um número ou uma string;
				if (v!=null) {
					if (v2!=null) {
						if (v.getType()=='s') {
							if (v2.getType()=='s') {
								//Se os dois lados forem variáveis do tipo string, a única comparação válida é o igual;
								if (c==3) {
									if (v.getVarStr().equals(v2.getVarStr())) {
										return 1;
									}
									return 0;
								}
								return 2;
							}
							//Se um lado for do tipo string e o outro não, a comparação é inválida;
							return 2;
						}
						//Se um lado for do tipo string e o outro não, a comparação é inválida;
						if (v2.getType()=='s') return 2;
						//Aqui os dois lados são variáveis numéricas, então faz a comparação conforme o operador e retorna o valor resultante;
						switch (c) {
							case 1:
								if (v.getVarNum() <= v2.getVarNum()) {
									return 1;
								}
								return 0;
							case 2:
								if (v.getVarNum() >= v2.getVarNum()) {
									return 1;
								}
								return 0;
							case 3:
								if (v.getVarNum() == v2.getVarNum()) {
									return 1;
								}
								return 0;
							case 4:
								if (v.getVarNum() < v2.getVarNum()) {
									return 1;
								}
								return 0;
							case 5:
								if (v.getVarNum() > v2.getVarNum()) {
									return 1;
								}
								return 0;
							case 6:
								if (v.getVarNum() != v2.getVarNum()) {
									return 1;
								}
								return 0;
						}
					}
					//Caso o lado esquerdo seja uma váriavel do tipo String, o lado direito, ao não ser uma variável, pode ser apenas uma string;
					if (v.getType()=='s') {
						if (haveQuotes(aux[1])) {
							if (c==3) {
								if (v.getVarStr().equals(aux[1].substring(1, aux[1].length() - 1))) {
									return 1;
								}
								return 0;
							}
							return 2;
						}
					}
					//Caso o lado esquerdo não seja do tipo String, o lado direito, ao não ser uma variável, pode ser apenas um número;
					if (validNumber(aux[1])) {
						//Caso seja, faz a comparação conforme o operador e retorna o valor resultante;
						switch (c) {
							case 1:
								if (v.getVarNum() <= Double.parseDouble(aux[1])) {
									return 1;
								}
								return 0;
							case 2:
								if (v.getVarNum() >= Double.parseDouble(aux[1])) {
									return 1;
								}
								return 0;
							case 3:
								if (v.getVarNum() == Double.parseDouble(aux[1])) {
									return 1;
								}
								return 0;
							case 4:
								if (v.getVarNum() < Double.parseDouble(aux[1])) {
									return 1;
								}
								return 0;
							case 5:
								if (v.getVarNum() > Double.parseDouble(aux[1])) {
									return 1;
								}
								return 0;
							case 6:
								if (v.getVarNum() != Double.parseDouble(aux[1])) {
									return 1;
								}
								return 0;
						}
					}
					return 2;
				}
				//Se o lado esquerdo não for uma variável;
				if (haveQuotes(aux[0])) {
					//Caso seja uma string, verifica se o lado direito é uma variável do tipo string ou outra string;
					if (v2!=null) {
						if (v2.getType()=='s') {
							if (c==3) {
								if (aux[0].substring(1, aux[0].length()-1).equals(v2.getVarStr())) {
									return 1;
								}
								return 0;
							}
							return 2;
						}
						return 2;
					}
					if (haveQuotes(aux[1])) {
						if (c==3) {
							if (aux[0].substring(1, aux[0].length()-1).equals(aux[1].substring(1, aux[1].length()-1))) {
								return 1;
							}
							return 0;
						}
						return 2;
					}
					return 2;
				}
				//Se não for uma string, pode ser apenas um número;
				if (v2!=null) {
					//Se o lado direito for uma variável do tipo string, a comparação é inválida;
					if (v2.getType()=='s') return 2;
					//Caso contrário, faz a comparação conforme o operador e retorna o valor resultante;
					switch (c) {
						case 1:
							if (Double.parseDouble(aux[0]) <= v2.getVarNum()) {
								return 1;
							}
							return 0;
						case 2:
							if (Double.parseDouble(aux[0]) >= v2.getVarNum()) {
								return 1;
							}
							return 0;
						case 3:
							if (Double.parseDouble(aux[0]) == v2.getVarNum()) {
								return 1;
							}
							return 0;
						case 4:
							if (Double.parseDouble(aux[0]) < v2.getVarNum()) {
								return 1;
							}
							return 0;
						case 5:
							if (Double.parseDouble(aux[0]) > v2.getVarNum()) {
								return 1;
							}
							return 0;
						case 6:
							if (Double.parseDouble(aux[0]) != v2.getVarNum()) {
								return 1;
							}
							return 0;
					}
				}
				//Se o lado direito for uma string, a comparação é inválida;
				if (haveQuotes(aux[1])) return 2;
				//Por fim, o lado direito pode apenas ser um número, então faz a comparação conforme o operador e retorna o valor resultante;
				switch (c) {
					case 1:
						if (Double.parseDouble(aux[0]) <= Double.parseDouble(aux[1])) {
							return 1;
						}
						return 0;
					case 2:
						if (Double.parseDouble(aux[0]) >= Double.parseDouble(aux[1])) {
							return 1;
						}
						return 0;
					case 3:
						if (Double.parseDouble(aux[0]) == Double.parseDouble(aux[1])) {
							return 1;
						}
						return 0;
					case 4:
						if (Double.parseDouble(aux[0]) < Double.parseDouble(aux[1])) {
							return 1;
						}
						return 0;
					case 5:
						if (Double.parseDouble(aux[0]) > Double.parseDouble(aux[1])) {
							return 1;
						}
						return 0;
					case 6:
						if (Double.parseDouble(aux[0]) != Double.parseDouble(aux[1])) {
							return 1;
						}
						return 0;
				}
			}
			return 2;
		}
		return 2;
	}
	
	public void interpret(String lines[]) {
		int i, j;
		Variavel v, v2;
		
		//Primeiramente, verifica se todas as linhas têm o caracter terminador '$' ou abertura/fechamento de escopo '{' ou '}';
		for(i=0;i<lines.length;i++) {
			if (lines[i]!=null && (!lines[i].isEmpty()) && (!onlySpaces(lines[i]))) {
				j=lines[i].length()-1;
				if (!(lines[i].charAt(j)=='$' || lines[i].charAt(j)=='{' || lines[i].charAt(j)=='}' || lines[i].charAt(j)=='[' || lines[i].charAt(j)==']')) {
					//Caso uma linha não esteja adequada, retorna erro para o usuário da linguagem e finaliza o interpretador;
					reportError(i);
					return;
				}
			}
		}
		//Após essa verificação, começa a interpretar o código, linha por linha;
		for(i=0;i<lines.length;i++) {
			if (lines[i]!=null && (!lines[i].isEmpty()) && (!onlySpaces(lines[i]))) {
				if (lines[i].charAt(0) == '@') {
					//Verifica se é uma declaração de variável(is);
					
					if (lines[i].charAt(lines[i].length()-1)!='$') {
						//Analisa se o terminador está correto;
						reportError(i);
						return;
					}
					
					for(j=1;j<lines[i].length() && (lines[i].charAt(j)==32 || lines[i].charAt(j)==9);j++);
					int typeposition = j; //Guarda a posição do tipo da(s) variável(is) declarada(s);
					
					//Verifica se o tipo é válido ou se está declarado;
					switch (lines[i].charAt(typeposition)) {
						case 'i':
							break;
						case 'f':
							break;
						case 's':
							break;
						default:
							reportError(i);
							return;
					}
					if (lines[i].charAt(typeposition+1)!=32) {
						reportError(i);
						return;
					}
					///////////////////////////////////////////////////////////
										
					String varsdeclaration = lines[i].substring(typeposition+1, lines[i].length()-1); //Obtém da linha todas as variáveis declaradas;
					String auxvars[] = varsdeclaration.split(","); //Faz a separação das variáveis;
					String aux[];
					
					for(j=0;j<auxvars.length;j++) {
						if (auxvars[j].equals("") || onlySpaces(auxvars[j])) {
							//Caso haja duas vírgulas seguidas ou apenas espaços e tabulações entre vírgulas;
							reportError(i);
							return;
						} else {
							aux = auxvars[j].split("="); //Verifica se há atribuição;
							aux[0] = aux[0].trim();
							
							if (!(validName(aux[0]))) {
								//Verifica se o nome dado à variável é válido;
								reportError(i);
								return;
							}
							
							if (validVar(aux[0])!=null) {
								//Verifica duplicação de variáveis;
								i++;
								this.flagloop = 1;
								System.out.println("Duplicate variable declaration on line " + i);
								return;
							}
							
							this.vars[this.fpov] = new Variavel(aux[0], lines[i].charAt(typeposition)); //Instancia uma nova variável, com seu nome e tipo;
							if (aux.length==2) {
								//Se há uma atribuição;
								aux[1] = aux[1].trim();
								if (aux[1].equals("") || ((this.vars[this.fpov].getType()!='s') && (!(validNumber(aux[1]))))
									|| (this.vars[this.fpov].getType()=='s' && (!(haveQuotes(aux[1]))))) {
									//Caso não haja valores após o igual, ou eles estejam incorretos;
									reportError(i);
									return;
								}
								//Fazem a atribuição do valor a variável:
								if (this.vars[this.fpov].getType()=='f' || this.vars[this.fpov].getType()=='i') {
									this.vars[this.fpov].setVarNum(Double.parseDouble(aux[1]));
								} else {
									this.vars[this.fpov].setVarStr(aux[1].substring(1, aux[1].length()-1));
								}
							}
							this.fpov++;
						}
					}
					continue;
				} else if (lines[i].charAt(0)=='!') {
					//Verifica se é um comando de impressão na tela;
					
					if (lines[i].charAt(lines[i].length()-1)!='$') {
						//Analisa se o terminador está correto;
						reportError(i);
						return;
					}
					
					for (j=1;lines[i].charAt(j)==32;j++);
					
					if (lines[i].charAt(j)=='v') {
						//Impressão de uma variável;
						String auxlines = lines[i];
						String aux2[] = auxlines.split("\\(");
						//Separa a string para obter o nome da variável;
						if (j+1<=aux2[0].length()-1) {
							aux2[0] = aux2[0].substring(j+1, aux2[0].length()-1);
							if (!(onlySpaces(aux2[0]))) {
								reportError(i);
								return;
							}
						}
						if (aux2.length==2) {
							aux2 = aux2[1].split("\\)");
							if (0<=aux2[1].length()-2) {
								aux2[1] = aux2[1].substring(0, aux2[1].length()-2);
								if (!(onlySpaces(aux2[1]))) {
									reportError(i);
									return;
								}
							}
							if (aux2.length==2) {
								aux2[0] = aux2[0].trim();
								v = validVar(aux2[0]);
								//Verifica se a variável é válida;
								if (v!=null) {
									//Se for, verifica o seu tipo para fazer a impressão adequada;
									if (v.getType()=='s') {
										System.out.print(v.getVarStr());
									} else {
										double x = v.getVarNum();
										if (v.getType()=='i') {
											System.out.printf("%.0f", x);
										} else System.out.print(x);
									}
								} else {
									reportError(i);
									return;
								}
							} else {
								reportError(i);
								return;
							}
						} else {
							reportError(i);
							return;
						}
					} else if (lines[i].charAt(j)=='l') {
						//Impressão de uma quebra de linha;
						if (lines[i].charAt(j+1)=='$' || onlySpaces(lines[i].substring(j+1, lines[i].length()-1))) {
							System.out.println();
						} else {
							reportError(i);
							return;
						}
					} else if (lines[i].charAt(j)=='t') {
						//Impressão de um texto;
						String auxlines = lines[i];
						String aux2[] = auxlines.split("\\(");
						//Separa a String para obter o texto;
						if (j+1<=aux2[0].length()-1) {
							aux2[0] = aux2[0].substring(j+1, aux2[0].length()-1);
							if (!(onlySpaces(aux2[0]))) {
								reportError(i);
								return;
							}
						}
						if (aux2.length==2) {
							aux2 = aux2[1].split("\\)");
							if (0<=aux2[1].length()-2) {
								aux2[1] = aux2[1].substring(0, aux2[1].length()-1);
								if (!(onlySpaces(aux2[1]))) {
									reportError(i);
									return;
								}
							}
							if (aux2.length==2) {
								System.out.print(aux2[0]);
							} else {
								reportError(i);
								return;
							}
						} else {
							reportError(i);
							return;
						}
					}
					continue;
				}
				String aux[] = lines[i].substring(0, lines[i].length() - 1).split("=");
				if (aux.length==2) {
					//Caso não seja uma declaração, verifica se é uma atribuição de valor à variável;
					
					if (lines[i].charAt(lines[i].length()-1)!='$') {
						//Analisa se o terminador está correto;
						reportError(i);
						return;
					}
					
					aux[0] = aux[0].trim();
					v = validVar(aux[0]);
					if (v==null) {
						reportError(i);
						return;
					}
					aux[1] = aux[1].trim();
					if (v.getType()=='s') {
						//Verifica se a variável cujo valor será atribuido é do tipo string e nesta situação, caso o valor seja válido, faz a atribuição;
						if (haveQuotes(aux[1])) {
							v.setVarStr(aux[1].substring(1, aux[1].length()-1));
						} else {
							v2 = validVar(aux[1]);
							if (v2!=null) {
								v.setVarStr(v2.getVarStr());
							} else {
								reportError(i);
								return;
							}
						}
					} else {
						//Verifica se o lado direito da atribuição é uma operação;
						String op = isValidOperation(aux[1]);
						if (!(op.equals("$"))) {
							//Realiza a operação e atribui o resultado à variável;
							v.setVarNum(resolvesOperation(aux[1], op));
						} else {
							//Caso não seja uma operação, pode ser outra variável, um número ou  uma atribuição inválida;
							v2 = validVar(aux[1]);
							if (v2!=null) {
								v.setVarNum(v2.getVarNum());
							} else if (validNumber(aux[1])) {
								v.setVarNum(Double.parseDouble(aux[1]));
							} else {
								reportError(i);
								return;
							}
						}
					}
					continue;
				}
				if (lines[i].charAt(0)=='i') {
					//Verifica se é um controlador de fluxo (if);
					
					if (lines[i].charAt(lines[i].length()-1)!='{') {
						//Analisa se o terminador está correto;
						reportError(i);
						return;
					}
					
					if (lines[i].charAt(1)!='f') {
						//Verifica se a sintaxe do if está correta;
						reportError(i);
						return;
					}
					
					if (!(lines[i].indexOf("(")>=0 && lines[i].indexOf(")")>=0)) {
						reportError(i);
						return;
					}
					
					for (j=i+1;j<lines.length;j++) {
						//Verifica se existe um fecha escopo de if;
						if (lines[j]!=null && (!lines[j].isEmpty()) && (!onlySpaces(lines[j]))) {
							if (lines[j].charAt(0)=='}') {
								break;
							}
						}
					}
					
					if (j==lines.length) {
						reportError(i);
						return;
					}
					
					//Separa a String para obter a expressão entre parenteses;
					String condition = lines[i].substring(2, lines[i].length()-1);
					String c[] = condition.split("\\(");
					c[1] = c[1].trim();
					
					if ((!(c[0].isEmpty())) && (!(onlySpaces(c[0])))) {
						reportError(i);
						return;
					}
					
					c = c[1].split("\\)");
					c[0] = c[0].trim();
					
					if (c.length!=1) {
						reportError(i);
						return;
					}
					
					//Verifica se a condição é válida/inválida e verdadeira/falsa;
					int x = validCondition(c[0]);
					if (x==2) {
						reportError(i);
						return;
					}
					if (x==0) {
						//Caso seja falsa, ignora o código dentro do controlador de fluxo;
						i = j;
					}
					continue;
				} else if (lines[i].charAt(0)=='g') {
					//Verifica se é um comando de leitura;
					
					if (lines[i].charAt(lines[i].length()-1)!='$') {
						//Analisa se o terminador está correto;
						reportError(i);
						return;
					}
					
					if (lines[i].charAt(1)!='e' || lines[i].charAt(2)!='t') {
						//Verifica se a sintaxe do get está correta;
						reportError(i);
						return;
					}
					
					//Separa a string para obter o nome da variável;
					String read = lines[i].substring(3, lines[i].length()-1);
					read = read.trim();
					String r[] = read.split("\\(");
					
					if (r.length!=2 || (!(r[0].isEmpty()))) {
						reportError(i);
						return;
					}
					
					r[1] = r[1].trim();
					r = r[1].split("\\)");
					
					if (r.length!=1) {
						reportError(i);
						return;
					}
					
					//Verifica se é uma variável válida;
					r[0] = r[0].trim();
					v = validVar(r[0]);
					
					if (v==null) {
						reportError(i);
						return;
					}
					
					Scanner sc = new Scanner(System.in);
					
					//Faz a leitura;
					if (v.getType()=='s') {
						v.setVarStr(sc.nextLine());
					} else {
						double x = sc.nextDouble();
						v.setVarNum(x);
					}
				} else if (lines[i].charAt(0)=='r') {
					//Verifica se é um laço;
					
					if (lines[i].charAt(lines[i].length()-1)!='[') {
						//Analisa se o terminador está correto;
						reportError(i);
						return;
					}
					
					if (!(lines[i].substring(0, 8).equals("repeatif"))) {
						//Verifica se a sintaxe do comando está correta;
						reportError(i);
						return;
					}
					
					if (!(lines[i].indexOf("(")>=0 && lines[i].indexOf(")")>=0)) {
						reportError(i);
						return;
					}
					
					//Separa a String para obter a expressão entre parenteses;
					String loop = lines[i].substring(8, lines[i].length()-1);
					String lo[] = loop.split("\\(");
					
					lo[1] = lo[1].trim();
					
					if ((!lo[0].isEmpty()) && (!(onlySpaces(lo[0])))) {
						reportError(i);
						return;
					}
					
					lo = lo[1].split("\\)");
					lo[0] = lo[0].trim();
					
					if (lo.length!=1) {
						reportError(i);
						return;
					}
					
					//Encontra a linha do fechamento do laço;
					for (j=i+1;j<lines.length;j++) {
						if (lines[j]!=null && (!lines[j].isEmpty()) && (!onlySpaces(lines[j]))) {
							if (lines[j].charAt(0)==']') {
								break;
							}
						}
					}
					
					if (j==lines.length) {
						i++;
						System.out.println("Missing closes brackets of the loop in the line " + i + ".");
						return;
					}
					
					String lines2[] = new String[j - i+1];
					int ii = 0, i2 = i+1;
					
					//Copia o código que será repetido para uma variável auxiliar;
					while (i2<j) {
						lines2[ii] = lines[i2];
						ii++;
						i2++;
					}
					
					if (validCondition(lo[0])==2) {
						//Se a condição é inválida;
						reportError(i);
						return;
					}
					
					//Executa o laço;
					while (validCondition(lo[0])==1) {
						interpret(lines2);
						if (this.flagloop==1) {
							i++;
							System.out.println("--- Of the loop at line " + i + ".");
							return;
						}
					}
					i = j;
					continue;
				} else {
					//Se uma linha possui apenas um caracter de fechamento de controlador de fluxo ou de laço;
					if (lines[i].charAt(0)!='}' && lines[i].charAt(0)!=']') {
						reportError(i);
						return;
					}
				}
			}
		}
	}
}	
