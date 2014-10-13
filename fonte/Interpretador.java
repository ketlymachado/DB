class Interpretador {
    private String lines[];
    
    public void interpret(String l[]) {
    
        this.lines = l;
        
        for(int i=0;i<this.lines.length;i++) {
        	if (this.lines[i]!=null) {
            	for (int j=0;j<this.lines[i].length();j++) {
		        	if (lines[i].charAt(j)!=32 && lines[i].charAt(j)!=9) {
		        		System.out.print(lines[i].charAt(j));
		        	}
		        }
		        System.out.println();
            }
        }
    }
}
