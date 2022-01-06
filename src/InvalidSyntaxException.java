import javax.swing.*;

public class InvalidSyntaxException extends Exception {

    public InvalidSyntaxException(String msg){
        super(msg);
        JOptionPane.showMessageDialog(new JFrame(),
                msg,
                "Invalid Syntax Error",
                JOptionPane.ERROR_MESSAGE);
    }

}
