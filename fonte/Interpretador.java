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
		if (spl.equals("+") || spl.equals("*"))
			spl = "\\" + spl;
		String aux[] = s.split(spl);
		if (validNumber(aux[0].trim())) {
			a = Double.parseDouble(aux[0].trim());
		} else {
			Variavel v;
			v = validVar(aux[0].trim());
			a = v.getVarNum();
		}
		if (validNumber(aux[1].trim())) {
			b = Double.parseDouble(aux[1].trim());
		} else {
			Variavel v;
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
	
	public void interpret(String l[]) {
		int i, j;
		this.lines = l;
		
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
				}
				String aux[] = this.lines[i].substring(0, this.lines[i].length() - 1).split("=");
				if (aux.length==2) {
					//Caso não seja uma declaração, verifica se é uma atribuição de valor à variável;
					aux[0] = aux[0].trim();
					Variavel v = validVar(aux[0]);
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
							Variavel v2 = validVar(aux[1]);
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
							Variavel v2 = validVar(aux[1]);
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
				}
			}
		}
	}
}
