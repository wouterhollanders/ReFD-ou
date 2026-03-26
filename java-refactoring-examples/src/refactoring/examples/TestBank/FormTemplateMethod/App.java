package refactoring.examples.TestBank.FormTemplateMethod;

public class App {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TextStatement textStmt = new TextStatement();
		HtmlStatement htmlStmt = new HtmlStatement();
 
        System.out.println(textStmt.value()); 
        System.out.println(htmlStmt.value());
        
        
        StatementA textStmtA = new TextStatementA();
        StatementA htmlStmtA = new HtmlStatementA();
 
        System.out.println(textStmtA.value()); 
        System.out.println(htmlStmtA.value()); 
	}

}
