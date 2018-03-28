
import java.awt.FlowLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * File: RequestParser.java Date: March 24, 2018 Purpose: Parse out conversion
 * request emails into a spreadsheet-friendly format
 *
 * @author Sarah Ball
 */
public class RequestParser extends JFrame {

    //GUI components
    JTextArea inputField, outputField;
    JScrollPane inputScroll, outputScroll;
    JButton parseButton;

    /**
     * Constructor
     */
    public RequestParser() {
        initComponents();
    }

    /**
     * Initialize GUI components and assign action listener to button
     */
    private void initComponents() {
        //frame setup
        setTitle("Parse Conversion Requests");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //right side output
        outputField = new JTextArea(25, 40);
        outputField.setMargin(new Insets(5, 5, 5, 5));
        outputField.setEditable(true);
        outputScroll = new JScrollPane(outputField);

        //left side input
        inputField = new JTextArea(25, 40);
        inputField.setMargin(new Insets(5, 5, 5, 5));
        inputField.setEditable(true);
        inputScroll = new JScrollPane(inputField);

        parseButton = new JButton("Parse text");
        parseButton.addActionListener(e -> outputField.setText(parseInput(inputField.getText())));

        setLayout(new FlowLayout());
        add(inputScroll);
        add(parseButton);
        add(outputScroll);
        pack();
        setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RequestParser rp = new RequestParser();
    }

    /**
     * Loops through input string and creates new tab and new line delimited
     * string
     *
     * @param sc - input string comprised of one or multiple request forms
     * @return - output string formatted for a spreadsheet
     */
    public String parseInput(String sc) {
        //Header of fields 
        String parsed = "Submitted by\tSubmit date\tTitle\tAuthor\tpISBN\teISBN\tPage count\tSpecial Requests\n";
        //Split by space or new line
        String[] tokens = sc.split(" |\\n");

        //Index counter
        int i = 0;

        while (i < tokens.length) {
            //NAME
            //advance until one token past "Name:"
            do {
                i++;
            } while (!tokens[i - 1].equals("Name:"));
            //concat until reaching next marker, "Email:"
            while (!tokens[i].equals("Email:")) {
                parsed += tokens[i] + " ";
                i++;
            }
            //DATE SUBMITTED
            //tab over twice to leave room date submitted to manually add later
            parsed += "\t\t";

            //TITLE
            //advance until one token past "Title:"
            while (!tokens[i - 1].equals("Title:")) {
                i++;
            }
            //concat Title names until reaching next marker, "Author" and "name:"
            //check for both to avoid errors from titles including "name" like "Finding my name: A memoir"
            while (!tokens[i].equals("Author") && !tokens[i + 1].equals("name:")) {
                parsed += tokens[i] + " ";
                i++;
            }
            //tab over 
            parsed += "\t";

            //AUTHOR
            i += 2; //advance past "Author" and "name:"
            //concat Authors until reaching "PrintISBN"
            while (!tokens[i].equals("PrintISBN:")) {
                parsed += tokens[i] + " ";
                i++;
            }
            //tab over
            parsed += "\t";

            //ISBNS
            i++;//advance past "printISBN:"
            parsed += removeDashes(tokens[i]) + "\t";
            i += 2;//advance past the isbn and "eISBN:"
            parsed += removeDashes(tokens[i]) + "\t";

            //PAGE COUNT
            //advance until one token past "edition:"
            while (!tokens[i - 1].equals("edition:")) {
                i++;
            }
            //add page number and tab
            parsed += tokens[i] + "\t";

            //SPECIAL INSTRUCTIONS
            //advance until one token past "Instructions:"
            while (!tokens[i - 1].equals("Instructions:")) {
                i++;
            }

            try {
                //look ahead for the token "#:" in "Order #:" and concat up 
                //until then. 11 tokens before "#:" is the start of
                //"The following Work Order"
                while (!tokens[i + 11].equals("#:")) {
                    parsed += tokens[i] + " ";
                    i++;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                //exceeded array bounds, concat rest of array contents to string
                //since it will be part of the last Additional Instructions
                //sections
                while (i < tokens.length) {
                    parsed += tokens[i] + " ";
                    i++;
                }
            }//end catch
            parsed += "\n"; //start new line
        }//end while loop
        return parsed;
    }

    /**
     * Remove dashes from the provided String
     */
    private String removeDashes(String st) {
        String[] splitString = st.split("");
        String dashRemoved = "";
        for (int i = 0; i < splitString.length; i++) {
            if (!splitString[i].equals("-")) {
                dashRemoved += splitString[i];
            }
        }
        return dashRemoved;
    }
}
