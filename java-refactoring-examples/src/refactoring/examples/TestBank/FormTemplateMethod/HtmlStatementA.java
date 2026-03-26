package refactoring.examples.TestBank.FormTemplateMethod;

//After
public class HtmlStatementA extends StatementA {
    String headerString() {
        return "<h1>HTML Header</h1>\n";
    }
 
    String eachRentalString() {
        return "<p>HTML Rental Line</p>\n";
    }
 
    String footerString() {
        return "<p>HTML Footer</p>\n";
    }
}