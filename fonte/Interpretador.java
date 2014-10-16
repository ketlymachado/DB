class Interpretador {
    private String lines[];
    
    public void interpret(String l[]) {
    
        this.lines = l;
        
        //Primeiramente, veririca se todas as linhas têm o caracter terminador '$' ou abertura/fechamento de escopo '{' ou '}';
        for(int i=0;i<this.lines.length;i++) {
        	if (this.lines[i]!=null) {
            	for (int j=this.lines[i].length()-1;j>=0;j--) {
		        	if (this.lines[i].charAt(j)!=32 && this.lines[i].charAt(j)!=9) {
		        		if (lines[i].charAt(j)=='$' || lines[i].charAt(j)=='{' || lines[i].charAt(j)=='}') {
		        			break;
		        		} else {
		        			//Caso uma linha não esteja adequada, retorna erro para o usuário da linguagem e finaliza o interpretador;
		        			i++;
		        			System.out.println("Sintax error at line " + i + ".");
		        			return;
		        		}
		        	}
		  		}
            }
        }
           
    }
}
