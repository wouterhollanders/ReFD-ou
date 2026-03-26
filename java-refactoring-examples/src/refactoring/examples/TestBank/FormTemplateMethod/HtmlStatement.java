package refactoring.examples.TestBank.FormTemplateMethod;

public class HtmlStatement extends Statement {
    public String value() {
        String result = headerString();
        result += eachRentalString();
        result += footerString();
        return result;
    }
 
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
 