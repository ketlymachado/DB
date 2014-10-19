import java.util.Scanner;
class Interpretador {
	private String lines[];
	private Variavel vars[];
	private int fpov; //Guarda a primeira posição vazia do vetor vars (first position of vars - fpov);
	
	public Interpretador() {
		//Construtor da classe Interpretador;
		this.vars = new Variavel[1000]; //Permite até 1000 variáveis;
		this.fpov = 0;
	}
	
	private void reportError(int i) {
		i++;
		System.out.println("Sintax error at line " + i + ".");
	}
	
	private boolean validName(String s) {
		//Retorna verdadeiro se a string s possui apenas letras ou números e falso em outros casos;
		int i;
		for(i=0;i<s.length();i++) {
			if (!(Character.isDigit(s.charAt(i)) || Character.isLetter(s.charAt(i))))
				return false;
		}
		return true;
	}
	
	private boolean validNumber(String s) {
		//Retorna verdadeiro se a string s possui apenas números e falso em outros casos;
		int i;
		for(i=0;i<s.length();i++) {
			if ((!(Character.isDigit(s.charAt(i)))) && (s.charAt(i)!='.'))
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
		if ((validNumber(a) || validVar(a)!=null) && (validNumber(b) || validVar(b)!=null))
			return true;
		return false;
	}
	
	private String isValidOperation(String s) {
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
		return "$";
	}
	
	private double resolvesOperation(String s, String spl) {
		double a, b, r;
		Variavel v;
		if (spl.equals("+") || spl.equals("*"))
			spl = "\\" + spl;
		String aux[] = s.split(spl);
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
		} else {
			r = a - b;
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
	
	public void interpret(String l[]) {
		int i, j;
		this.lines = l;
		Variavel v, v2;
		
		//Primeiramente, verifica se todas as linhas têm o caracter terminador '$' ou abertura/fechamento de escopo '{' ou '}';
		for(i=0;i<this.lines.length;i++) {
			if (this.lines[i]!=null) {
				j=this.lines[i].length()-1;
				if (!(this.lines[i].charAt(j)=='$' || this.lines[i].charAt(j)=='{' || this.lines[i].charAt(j)=='}')) {
					//Caso uma linha não esteja adequada, retorna erro para o usuário da linguagem e finaliza o interpretador;
					reportError(i);
					return;
				}
			}
		}
		//Após essa verificação, começa a interpretar o código, linha por linha;
		for(i=0;i<this.lines.length;i++) {
			if (this.lines[i]!=null) {
				if (this.lines[i].charAt(0) == '@') {
					//Verifica se é uma declaração de variável(is);
					
					if (this.lines[i].charAt(lines[i].length()-1)!='$') {
						//Analisa se o terminador está correto;
						reportError(i);
						return;
					}
					
					for(j=1;j<this.lines[i].length() && (this.lines[i].charAt(j)==32 || this.lines[i].charAt(j)==9);j++);
					int typeposition = j; //Guarda a posição do tipo da(s) variável(is) declarada(s);
					
					//Verifica se o tipo é válido ou se está declarado;
					switch (this.lines[i].charAt(typeposition)) {
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
										
					String varsdeclaration = this.lines[i].substring(typeposition+1, this.lines[i].length()-1); //Obtém da linha todas as variáveis declaradas;
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
								reportError(i);
								return;
							}
							this.vars[this.fpov] = new Variavel(aux[0], this.lines[i].charAt(typeposition)); //Instancia uma nova variável, com seu nome e tipo;
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
					
					if (this.lines[i].charAt(lines[i].length()-1)!='$') {
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
				String aux[] = this.lines[i].substring(0, this.lines[i].length() - 1).split("=");
				if (aux.length==2) {
					//Caso não seja uma declaração, verifica se é uma atribuição de valor à variável;
					
					if (this.lines[i].charAt(lines[i].length()-1)!='$') {
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
					
					if (this.lines[i].charAt(lines[i].length()-1)!='{') {
						//Analisa se o terminador está correto;
						reportError(i);
						return;
					}
					
					if (!(lines[i].indexOf("(")>=0 && lines[i].indexOf(")")>=0)) {
						reportError(i);
						return;
					}
					
					//Separa a String para obter a expressão entre parenteses;
					String condition = lines[i].substring(2, lines[i].length()-1);
					String c[] = condition.split("\\(");
					c[1] = c[1].trim();
					
					if (!(onlySpaces(c[0]))) {
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
						for (j=i+1;j<lines.length && (lines[j].charAt(0) != '}' || lines[j].charAt(lines[j].length()-1) != '}');j++);
						i = j;
						if (j==lines.length) {
							reportError(i);
							return;
						}
					}
					continue;
				} else if (lines[i].charAt(0)=='g') {
					//Verifica se é um comando de leitura;
					
					if (this.lines[i].charAt(lines[i].length()-1)!='$') {
						//Analisa se o terminador está correto;
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
				}
			}
		}
	}
}
