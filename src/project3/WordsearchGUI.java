package project3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class WordsearchGUI implements ActionListener {
	private static boolean[][] buttonstate; //to get button states
	private static StringBuilder clicked; // to get String of clicked letters
	private static JLabel label; // To display clicked letters //needed to declare as class variable so that another method can use it.
	private static JTextArea textArea; // To display the found words
	private static List<String> wordlist; // To store words from words.txt
	private static List<String> foundWords = new ArrayList<>(); // To store found words


	
public static void main(String[] args) {
		
		
		Random prob = new Random();
		
		
		//Using JOptionpane, prompt user to enter the grid dimensions and minimum word length
		String rowInput = JOptionPane.showInputDialog(null, "Enter number of rows: ");
		int row = Integer.parseInt(rowInput) ;//Convert input to integer
		String colInput = JOptionPane.showInputDialog(null, "Enter number of columns: ");
		int col = Integer.parseInt(colInput);
		String wlengthInput = JOptionPane.showInputDialog(null,"Enter the minimum word length: ");
		int wlength = Integer.parseInt(wlengthInput);
		
		
		
		//Define arrays for vowels and consonants
		char[] vowels = {'A','E','I','O','U'};
		char[] consonants = {'B','C','D','F','G','H','J','K','L','M','N','P','Q','R','S','T','V','W','X','Y','Z'};
		
		//Initialize a 2D grid
		char[][] grid = new char[row][col];
		//populate the grid
		for(int i=0; i<row;i++) {
			for (int j=0; j<col; j++) {
				double chance = prob.nextDouble();
				//Higher chance of consonants (70%)
				if (chance > 0.3) {grid[i][j] = consonants[prob.nextInt(consonants.length)];
				}
				//Lower chance of vowels(30%)
				else {
					grid[i][j] = vowels[prob.nextInt(vowels.length)];
		}
			}
	}
		//From project 1 with modification of GUI
		//Initialize the wordlist and read from file		
		wordlist = new ArrayList<>();//Initialize the wordlist
		String filename = "words.txt"; //File containing the list of words

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))){
			String word;
			
			while ((word = reader.readLine()) != null) {
				wordlist.add(word);//Add each word to the list.
			}
		}catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File Not Found: ");
		}catch (IOException e) {
			JOptionPane.showMessageDialog(null,"There was an error reading the file");
			}
		
		//Filter the word list using the grid and minimum word length
		List<String> validWords = filterWordList(wordlist, grid, wlength);
		if (validWords.isEmpty()) {
		    JOptionPane.showMessageDialog(null, "No valid words can be formed in the grid. Try again with a different grid or settings.");
		    System.exit(0); // Exit if there are no valid words
		}
		setup(grid, validWords, wlength);

		
} //end of main



//Method to to setup GUI window
//Step by step modifications will be done to this method to avoid confusion in the main
public static void setup(char[][] grid, List<String> validWords, int wlength) {
	//1-Setup JFrame
	JFrame frame = new JFrame("Wordsearch Grid"); 
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//default close operation
	frame.setSize(600,400);//Setting size, can change later if needed
	frame.setLayout(new BorderLayout());//Border layout looks better
	
	//2-Panel for grid of buttons 
	JPanel panel = new JPanel(new GridLayout(grid.length, grid[0].length));//The main panel for buttons
	label = new JLabel(" "); //make the label appear even before the letters are clicked
	label.setHorizontalAlignment(SwingConstants.CENTER); // Center align the text
    label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
    frame.add(label, BorderLayout.NORTH); // Place the label in the south of the frame

    //JTextArea for displaying found words
    textArea = new JTextArea(5, 20); // 5 rows, 20 columns (can be adjusted)
    JScrollPane scrollPane = new JScrollPane(textArea); // Add scrolling for longer lists
    frame.add(scrollPane, BorderLayout.SOUTH); // Add it to the south of the frame

    
	clicked = new StringBuilder(); //Initialize clicked
	buttonstate = new boolean[grid.length][grid[0].length]; // Initialize button state with grid dimension.

	
	//3-Populate the Jframe grid with buttons
	for(int i=0; i<grid.length; i++) {
		for(int j=0; j<grid[i].length; j++) {
			
			final int row = i;
			final int col = j;
			
			JButton button = new JButton(String.valueOf(grid[i][j]));//from console grid to setup grid.	
			
			// 4-implementing ActionListener
			button.addActionListener(e -> { 
			    if (!buttonstate[row][col]) { // Check if the button is not already clicked
			        button.setBackground(Color.PINK); // Highlight button on click
			        button.setOpaque(true); // Ensure the background is visible
			        button.setBorderPainted(false); // Optional: Remove button border
			        buttonstate[row][col] = true;

			        // Add the clicked letter to the clicked string
			        clicked.append(button.getText());
			        label.setText(clicked.toString());

			        //5-Validate the clicked word
			        String current = clicked.toString();
			        if (validWords.contains(current) && current.length() >= wlength) {
			            if (!foundWords.contains(current)) { // Check if the word is not already in the foundWords list
			                foundWords.add(current); // Add the word to the foundWords list
			                textArea.append(current + "\n"); // Append the found word to the JTextArea
			                
			                //6-Check if all words are found
			                if (foundWords.size() == validWords.size()) {
			                    JOptionPane.showMessageDialog(null, "Congratulations! You found all the words!");
			                    System.exit(0);
			                }

			                reset(panel, grid); // Reset buttons and clicked letters
			            }
			        }
			    } else {
			        reset(panel, grid); // Reset the buttons when clicked again
			    }
			});

			panel.add(button); // Add each button to the button panel
		}
	}
	//7-Add panel and label to the main frame
	frame.add(panel,BorderLayout.CENTER);//Add the button panel to the center
	frame.setVisible(true);//Make frame visible 
}

@Override
public void actionPerformed(ActionEvent e) {
	JButton clickedButton = (JButton) e.getSource();
	clickedButton.setBackground(Color.pink);//change to pink when clicked :) I like pink
	//MacOS requirement
	clickedButton.setOpaque( true ) ;
	clickedButton.setContentAreaFilled( true ) ;
	clickedButton.setBorderPainted( false );
}

//Method to reset buttons
private static void reset(JPanel panel, char[][] grid) {
    // Reset all button states
    for (int i = 0; i < buttonstate.length; i++) {
        for (int j = 0; j < buttonstate[i].length; j++) {
            buttonstate[i][j] = false; // Reset button states
        }
    }
    // Clear the clicked letters and label
    clicked.setLength(0); 
    label.setText(" ");//Reset the label
    
    // Reset the appearance of all buttons
    for (Component c : panel.getComponents()) {
        if (c instanceof JButton) {
            JButton button = (JButton) c;
            button.setBackground(null); // Reset the button to default
            button.setOpaque(true);
            button.setBorderPainted(true);
            button.setFocusPainted(false);
        }
    }
}

//Reuse from Project 1 with modifications
private static List<String> filterWordList(List<String> wordlist, char[][] grid, int minLength) {
 List<String> validWords = new ArrayList<>();
 //Direction vectors for searching 8 directions
 int[] dx = {-1, -1, -1, 0, 1, 1, 1, 0};
 int[] dy = {-1, 0, 1, 1, 1, 0, -1, -1};

 for (String w : wordlist) {
     if (w.length() >= minLength) {//Check if the word meets minimum length
         boolean found = false;
         for (int r = 0; r < grid.length && !found; r++) {
             for (int c = 0; c < grid[0].length && !found; c++) {
                 for (int dir = 0; dir < 8 && !found; dir++) { //Iterate through all 8 directions
                     int newr = r, newc = c, index = 0;
                     while (index < w.length()) {
                         if (newr < 0 || newr >= grid.length || newc < 0 || newc >= grid[0].length || grid[newr][newc] != w.charAt(index)) {
                             break; // Out of bounds or letter mismatch
                         }
                         newr += dx[dir];
                         newc += dy[dir];
                         index++;
                     }
                     // If all letters match, add the word
                     if (index == w.length()) {
                         validWords.add(w);
                         found = true; // Mark as found to stop further search for this word
                     }
                 }
             }
         }
     }
 }
 return validWords;
}


}







