/*
Author: Jeffrey Holcomb
Date: 7/15/2020
Course: CMSC 330
Title: Project 1 - Recursive Descent Parser
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;

public class RecursiveDescentParser {

    // LinkedList to store input file as chars
    private static LinkedList workChars = new LinkedList();

    private static JTextField mainTextField = new JTextField();

    private static StringBuilder disp = new StringBuilder();
    private static String oprReg = null;
    private static double dispReg = 0d;
    private static boolean isFirst = true;

    // String arrays to hold compare keyword spelling and syntax, and identify type of keywords
    final static String[] KEYWORDS = new String[]{
            "Window", "Layout", "Flow", "Grid", "Panel", "Textfield", "Button", "Group", "Label", "Radio", "End", "."
    };
    final static String[] WIDGETS = new String[]{
            "Button", "Textfield", "Panel", "Group", "Label", "Radio"
    };
    final static String[] LAYOUTS = new String[]{
            "Flow", "Grid"
    };

    private static int totalChars;

    public static void main(String[] args) throws IOException {

        Charset encoding = Charset.defaultCharset();

        File file = new File(".\\rdp_input.txt");
        handleFile(file, encoding); // Parse input file into chars

        final LinkedList FULL_CHARS = workChars; // Create copy of char list
        totalChars = FULL_CHARS.size();

        try {
            String firstWord = findKeyword(new StringBuilder(), 0, workChars.size()); // Find first keyword

            if (firstWord.equals("Window")) { // Ensure first keyword is Window for new GUI

                String title = findString(new StringBuilder(), 0, workChars.size()); // find Title of JFrame
                Dimension dim = findDimensions(new StringBuilder(), 0, workChars.size()); // find Dimensions
                String checkLayoutKw = findKeyword(new StringBuilder(), 0, workChars.size()); // find Layout keyword

                if (!checkLayoutKw.equals("Layout")) {
                    throw new InvalidSyntaxException("Layout keyword expected in GUI syntax");
                }
                LayoutManager layout = findLayout(new StringBuilder(), 0, workChars.size()); // Find Layout Type

                if (checkIfGridLayout(layout)) { // If statement to determine how many chars to remove from list depending on constructor used for Grid Layout
                    removeCharsAfterGridLayout((MyGridLayout)layout);
                }

                if (String.valueOf(workChars.get(0)).equals(":")) { // If : symbol is found, create new JFrame with specified parameters
                    workChars.removeFirst();
                    JFrame frame = new JFrame(title);
                    frame.setPreferredSize(dim);
                    frame.setLayout(layout);

                    String widget = findKeyword(new StringBuilder(), 0, workChars.size()); // Find first widget
                    if (!isWidget(widget)) throw new InvalidSyntaxException("Widget required in GUI syntax");

                    // Do-while loop to add widget to Frame, and add any more if present
                    do {
                        frame.add(addWidget(widget));
                        widget = findKeyword(new StringBuilder(), 0, workChars.size());
                    } while (!widget.equals("End")); // Exit loop if "End" is found

                    if (widget.equals("End")) {

                        try {
                            String finalPeriod = String.valueOf(workChars.get(0));
                            if (finalPeriod.equals(".")) { // Ensure document ends with .
                                frame.pack();
                                frame.setLocationRelativeTo(null);
                                frame.setVisible(true);
                            }
                        }catch(IndexOutOfBoundsException ie){
                            throw new InvalidSyntaxException("Expected . symbol at end of code");
                        }
                    }
                }
            }
        } catch (InvalidSyntaxException ise) {
        }
    }

    // Recursive method for adding widgets
    private static JComponent addWidget(String str) throws InvalidSyntaxException {
        switch (str) {
            case "Textfield":
                int textFieldWidth = getTextFieldWidth(new StringBuilder(), 0, workChars.size());
                mainTextField.setColumns(textFieldWidth);
                mainTextField.setHorizontalAlignment(SwingConstants.RIGHT);
                return mainTextField;
            case "Button":
                String btnValue = findString(new StringBuilder(), 0, workChars.size());
                if(workChars.get(0).toString().equals(";")){
                    workChars.removeFirst();
                } else {
                    throw new InvalidSyntaxException("Expected ; symbol after Button string");
                }
                JButton jbtn = new JButton(btnValue);
                jbtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        try {
                            switch (btnValue) {
                                case "C":
                                    disp = new StringBuilder();
                                    mainTextField.setText(disp.toString());
                                    break;
                                case "+":
                                    dispReg = Double.parseDouble(mainTextField.getText());
                                    oprReg = "+";
                                    disp = new StringBuilder();
                                    break;
                                case "-":
                                    dispReg = Double.parseDouble(mainTextField.getText());
                                    oprReg = "-";
                                    disp = new StringBuilder();
                                    break;
                                case "*":
                                    dispReg = Double.parseDouble(mainTextField.getText());
                                    oprReg = "*";
                                    disp = new StringBuilder();
                                    break;
                                case "/":
                                    dispReg = Double.parseDouble(mainTextField.getText());
                                    oprReg = "/";
                                    disp = new StringBuilder();
                                    break;
                                case "=":
                                    double result = calculate(Double.parseDouble(mainTextField.getText()), oprReg);
                                    mainTextField.setText(Double.toString(result));
                                    disp = new StringBuilder();
                                    break;
                                default: // if button is a number
                                    if (isFirst) { // if first
                                        disp.append(btnValue);
                                        mainTextField.setText(disp.toString());
                                    } else {
                                        disp.append(btnValue);
                                        mainTextField.setText(disp.toString());
                                    }
                                    break;
                            }
                        }catch(NumberFormatException ne){
                            JOptionPane optionPane = new JOptionPane("Invalid Character detected", JOptionPane.ERROR_MESSAGE);
                            JDialog dialog = optionPane.createDialog("Error");
                            dialog.setAlwaysOnTop(true);
                            dialog.setVisible(true);
                        }catch(NullPointerException npe){
                            disp = new StringBuilder();
                        }
                    }
                });
                return jbtn;
            case "Group":
                ButtonGroup bg = new ButtonGroup();
                String radio = findKeyword(new StringBuilder(), 0, workChars.size());
                if(!radio.equals("Radio")){
                    throw new InvalidSyntaxException("Radio keyword expected after Group");
                }
                String buttonTitle = findString(new StringBuilder(), 0, workChars.size());
                JRadioButton jb;
                if(String.valueOf(workChars.get(0)).equals(";")){
                    workChars.removeFirst();
                    jb = new JRadioButton(buttonTitle);
                } else {
                    throw new InvalidSyntaxException("Symbol ; expected after Radio Button String");
                }
                String end = findKeyword(new StringBuilder(), 0, workChars.size());
                if (end.equals("End")){
                    if (String.valueOf(workChars.get(0)).equals(";")) {
                        workChars.removeFirst();
                        return jb;
                    } else {
                        throw new InvalidSyntaxException("Symbol ; expected after End");
                    }
                } else {
                    throw new InvalidSyntaxException("Keyword \"End\" expected after Radio Button String");
                }
            case "Label":
                String labelValue = findString(new StringBuilder(), 0, workChars.size());
                if(workChars.get(0).toString().equals(";")){
                    workChars.removeFirst();
                } else {
                    throw new InvalidSyntaxException("Expected ; symbol after Label string");
                }
                return new JLabel(labelValue);
            case "Panel":
                JPanel panel = new JPanel();
                String checkPanelLayoutKw = findKeyword(new StringBuilder(), 0, workChars.size());
                if (!checkPanelLayoutKw.equals("Layout")) {
                    throw new InvalidSyntaxException("Layout keyword expected in Panel syntax");
                }
                LayoutManager panelLayout = findLayout(new StringBuilder(), 0, workChars.size());
                if (checkIfGridLayout(panelLayout)) {
                    removeCharsAfterGridLayout((MyGridLayout)panelLayout);
                }
                if(String.valueOf(workChars.get(0)).equals(":")){
                    workChars.removeFirst();
                    panel.setLayout(panelLayout);
                    String widget = findKeyword(new StringBuilder(), 0, workChars.size());
                    if(isWidget(widget)){
                        do{
                            panel.add(addWidget(widget));
                            widget = findKeyword(new StringBuilder(), 0, workChars.size());
                        } while (isWidget(widget));
                        if(widget.equals("End")){
                            if(String.valueOf(workChars.get(0)).equals(";")){
                                workChars.removeFirst();
                                return panel;
                            }
                        }
                    }
                } else {
                    throw new InvalidSyntaxException("Expected : symbol after Layout type");
                }
            default:
                throw new InvalidSyntaxException("Widget Syntax Error");
        }
    }

    // Recursive method for finding Keywords
    private static String findKeyword(StringBuilder sb, int index, int remaining)
            throws InvalidSyntaxException {
        if (index > remaining-1) throw new InvalidSyntaxException("Token not recognized: " + sb.toString());
        sb.append(String.valueOf(workChars.get(index++)));
        String str = sb.toString();
        if (isKeyword(str)) {
            for (int i = 0; i < index; i++) {
                workChars.removeFirst();
            }
            return sb.toString();
        } else {
            findKeyword(sb, index, remaining);
        }
        return sb.toString();
    }

    // Recursive method for finding Layout
    private static LayoutManager findLayout(StringBuilder sb, int index, int remaining)
            throws InvalidSyntaxException {
        if (index > remaining - 1) throw new InvalidSyntaxException("Layout not found.");
        sb.append(String.valueOf(workChars.get(index++)));
        String str = sb.toString();
        if (isLayout(str)) {
            for (int i = 0; i < index; i++) {
                workChars.removeFirst();
            }
            return stringToLayout(sb.toString());
        } else {
            findLayout(sb, index, remaining);
        }
        return stringToLayout(sb.toString());
    }

    // Recursive method used in conjunction with findLayout method to determine properties of GridLayout
    private static LayoutManager stringToLayout(String str) throws InvalidSyntaxException {
        if (str.equals("Flow")) {
            return new FlowLayout();
        } else {
            boolean foundFirst = false;
            boolean foundSecond = false;
            boolean foundThird = false;
            boolean foundFourth = false;

            int x1 = -1;
            int x2 = -1;
            int x3 = -1;
            int x4;

            int commaCount = 0;
            StringBuilder sb = new StringBuilder();

            if (!String.valueOf(workChars.get(0)).equals("("))
                throw new InvalidSyntaxException("Grid Dimension must start with (");
            for (int i = 1; i < workChars.size(); i++) {
                char c = (Character) workChars.get(i);
                if (String.valueOf(c).equals(",")) commaCount++;
                if (String.valueOf(c).equals(",")) {
                    if (!foundFirst) {
                        x1 = Integer.parseInt(sb.toString());
                        sb = new StringBuilder();
                        foundFirst = true;
                        continue;
                    } else if (!foundSecond) {
                        x2 = Integer.parseInt(sb.toString());
                        sb = new StringBuilder();
                        foundSecond = true;
                        continue;
                    } else if (!foundThird) {
                        x3 = Integer.parseInt(sb.toString());
                        sb = new StringBuilder();
                        foundThird = true;
                        continue;
                    } else {
                        throw new InvalidSyntaxException("Error in Grid Dimension syntax: Should look like (120, 30, 50, 50) or (120, 30)");
                    }
                }
                if (String.valueOf(c).equals(")")) {
                    if (commaCount == 1) {
                        return new MyGridLayout(x1, x2);
                    } else {
                        x4 = Integer.parseInt(sb.toString());
                        return new MyGridLayout(x1, x2, x3, x4);
                    }
                }
                try {
                    Integer.parseInt(String.valueOf(c));
                    sb.append(String.valueOf(c));
                } catch (NumberFormatException nfe) {
                    throw new InvalidSyntaxException("Error in Grid Dimension syntax: Should look like (120, 30, 50, 50) or (120, 30)");
                }
            }
        }
        return null;
    }

    // Recursive method for finding String contents
    private static String findString(StringBuilder sb, int index, int remaining)
            throws InvalidSyntaxException {
        if((String.valueOf(workChars.get(0)).equals("\"")) && (String.valueOf(workChars.get(1)).equals("\""))){
            workChars.removeFirst();
            workChars.removeFirst();
            return "";
        }
        if (index == 0) {
            if (!String.valueOf(workChars.get(index).toString()).equals("\"")) {
                throw new InvalidSyntaxException("String must start with (\").");
            }
            workChars.removeFirst();
        }
        if ((index > remaining - 1)) throw new InvalidSyntaxException("No closing (\") found.");
        sb.append(String.valueOf(workChars.get(index++)));
        if (String.valueOf(workChars.get(index)).equals("\"")) {
            for (int i = 0; i < index + 1; i++) {
                workChars.removeFirst();
            }
            return sb.toString();
        } else {
            findString(sb, index, remaining);
        }
        return sb.toString();
    }

    // Recursive method for finding Dimensions of JFrame
    private static Dimension findDimensions(StringBuilder sb, int index, int remaining)
            throws InvalidSyntaxException {
        if ((index == 0) && (!String.valueOf(workChars.get(index).toString()).equals("(")))
            throw new InvalidSyntaxException("Dimension error: Must start with (");
        if ((index > remaining - 1)) throw new InvalidSyntaxException("Dimension error: No closing ) found.");
        sb.append(String.valueOf(workChars.get(index++)));
        if (String.valueOf(workChars.get(index)).equals(")")) {
            for (int i = 0; i < index + 1; i++) {
                workChars.removeFirst();
            }
            sb.append(")");
            int[] dimVals = stringToDimensions(sb.toString());
            return new Dimension(dimVals[0], dimVals[1]);
        }
        findDimensions(sb, index, remaining);
        int[] dimVals = stringToDimensions(sb.toString());
        return new Dimension(dimVals[0], dimVals[1]);
    }

    // Method used in conjunction with findDimensions for finding attributes of Dimension
    private static int[] stringToDimensions(String str) throws InvalidSyntaxException {
        StringBuilder sb = new StringBuilder();
        int x1 = -1;
        int x2 = -1;
        boolean firstValFound = false;
        for (int i = 1; i < str.length(); i++) {

            char c = str.charAt(i);

            if (String.valueOf(c).equals(",")) {
                if (!firstValFound) {
                    try {
                        x1 = Integer.parseInt(sb.toString());
                        sb = new StringBuilder();
                        firstValFound = true;
                    } catch (NumberFormatException ne){
                        throw new InvalidSyntaxException("Non-integer type found in dimension statement");
                    }
                    continue;
                }
            }
            if ((String.valueOf(c).equals(")") && (firstValFound))) {
                x2 = Integer.parseInt(sb.toString());
                break;
            }
            try {
                Integer.parseInt(String.valueOf(i));
                sb.append(String.valueOf(c));
                continue;
            } catch (NumberFormatException nf) {
                throw new InvalidSyntaxException("Invalid Dimension syntax");
            }
        }
        if ((x1 == -1) || (x2 == -1)) {
            throw new InvalidSyntaxException("Problem with Dimension syntax: Dimension should look like (150, 200)");
        }
        return new int[]{x1, x2};
    }

    // Method to find if String is a keyword
    private static boolean isKeyword(String str) {
        for (String kw : KEYWORDS) {
            if (str.equals(kw)) return true;
        }
        return false;
    }

    // Method to find if String is a widget
    private static boolean isWidget(String str) {
        for (String kw : WIDGETS) {
            if (str.equals(kw)) return true;
        }
        return false;
    }

    // Method for finding width of Textfield
    private static int getTextFieldWidth(StringBuilder sb, int index, int remaining)
            throws InvalidSyntaxException {
        if (String.valueOf(workChars.get(index)).equals(";")) {
            for (int i = 0; i < index + 1; i++) {
                workChars.removeFirst();
            }
            try {
                return Integer.parseInt(sb.toString());
            } catch (NumberFormatException ne) {
                throw new InvalidSyntaxException("Integer value expected after Textfield keyword");
            }
        }
        try {
            Integer.parseInt(String.valueOf(workChars.get(index)));
            sb.append(Integer.parseInt(String.valueOf(workChars.get(index))));
            getTextFieldWidth(sb, ++index, remaining);
        } catch (NumberFormatException ne) {
            throw new InvalidSyntaxException("Integer value not found after Textfield keyword");
        }
        return Integer.parseInt(sb.toString());
    }

    // Method for finding if String is a Layout option
    private static boolean isLayout(String str) {
        for (String kw : LAYOUTS) {
            if (str.equals(kw)) return true;
        }
        return false;
    }

    // Method for printing out remaining characters (used for debugging)
    private static void printCharList() {
        for (Object ch : workChars) {
            System.out.print(ch);
        }
        System.out.print("\n");
    }

    // Method to handle File reading
    private static void handleFile(File file, Charset encoding)
            throws IOException {
        try (InputStream in = new FileInputStream(file);
             Reader reader = new InputStreamReader(in, encoding);
             // buffer for efficiency
             Reader buffer = new BufferedReader(reader)) {
            handleCharacters(buffer);
        }
    }

    // Method to add all chars to LinkedList, ignoring whitespace
    private static void handleCharacters(Reader reader)
            throws IOException {
        int r;
        while ((r = reader.read()) != -1) {
            char ch = (char) r;
            if (!Character.isWhitespace(ch)) {
                workChars.add(ch);
            }
        }
    }

    // Method to find which constructor was used for GridLayout
    private static boolean checkIfGridLayout(LayoutManager layout) {
        if (layout.getClass() == new MyGridLayout(1, 1).getClass()) {
            return true;
        }
        return false;
    }

    // Method to remove chars from LinkedList depending on which constructor was used
    private static void removeCharsAfterGridLayout(MyGridLayout layout) {
        MyGridLayout temp = layout;
        if (temp.getParams() == 2) {
            for (int i = 0; i < 9; i++) {
                workChars.removeFirst();
            }
        } else {
            for (int i = 0; i < 5; i++) {
                workChars.removeFirst();
            }
        }
    }

    // Method for calculating expression when = button is pushed
    private static double calculate(double operandTwo, String operator){
        switch (operator){
            case "+":
                return dispReg + operandTwo;
            case "-":
                return dispReg - operandTwo;
            case "*":
                return dispReg * operandTwo;
            case "/":
                return dispReg / operandTwo;
            default:
                return -1;
        }
    }
}