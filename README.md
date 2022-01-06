A recursive descent parser which reads a text input file and builds a user interface. 

This Java application reads an input text file and produces a user interface with mathematical calculation functionality. The input string is read from rdp_input.txt, which, in this repo, is:

```
Window "Calculator" (220, 200) Layout Flow:
 Textfield 17;
 Panel Layout Grid (4, 4, 5, 5):
 Button "7";
 Button "8";
 Button "9";
 Button "+";
 Button "4";
 Button "5";
 Button "6";
 Button "-";
 Button "1";
 Button "2";
 Button "3";
 Button "*";
 Button "C";
 Button "0";
 Button "/";
 Button "=";
 End;
End.
```

The first line tells the parser that it should create a JWindow with the title "Calculator with the size 220x200 with a flow layout. The second line produces a textfield of width 17. The third line produces a panel with a grid layout of (4, 4, 5, 5). The following lines populate the grid layout with buttons, each titled with the string inside of the quotation marks. Lastly, the first "End" command informs the parser that the panel should end, and the second "End" command finishes the Window.

The generated user interface looks like this: 

![alt text](https://github.com/holjeffcomb/recursive-descent-parser/blob/main/images/output.JPG?raw=true)

The calculator app also has full functionality.
