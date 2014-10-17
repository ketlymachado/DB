class Variavel {
	private double var_num;
	private String var_str;
	private char type;
	private String name;
	
	//Para fazer a declaração de uma variável usa-se o construtor;
	public Variavel(String n, char t) {
		this.var_num = 0;
		this.var_str = ("");
		this.name = n;
		this.type = t;
	}
	
	//Como não se pode alterar o tipo ou o nome de uma variável após declarada, não são necessários setter's para esses atributos;
	public String getName() {
		return this.name;
	}
	public char getType() {
		return this.type;
	}
	public void setVarNum(double d) {
		this.var_num = d;
	}
	public double getVarNum() {
		return this.var_num;
	}
	public void setVarStr(String s) {
		this.var_str = s;
	}
	public String getVarStr() {
		return this.var_str;
	}
}
