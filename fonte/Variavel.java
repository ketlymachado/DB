class Variavel {
	private double var_num;
	private String var_str;
	private int type;
	private String name;
	
	//Para fazer a declaração de uma variável usa-se os construtores;
	public Variavel(String n, int t, double d) {
		this.var_num = d;
		this.type = t;
		this.name = n;
	}
	public Variavel(String n, int t, String s) {
		this(n, t, 0);
		this.var_num = s;
	}
	//Como não se pode alterar o tipo ou o nome de uma variável após declarada, não são necessários setter's para esses atributos;
	public String getName() {
		return this.name;
	}
	public int getType() {
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
