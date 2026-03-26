package refactoring.examples.TestBank.FormTemplateMethod;

//After
public class TextStatementA extends StatementA {
    String headerString() {
        return "Text Header\n";
    }
 
    String eachRentalString() {
        return "Text Rental Line\n";
    }
 
    String footerString() {
        return "Text Footer\n";
    }
}
 