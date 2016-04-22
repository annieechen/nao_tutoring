package tutoringproject.breaks;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by arsalan on 4/13/16.
 */
public class TicTacToeActivity extends Activity implements TutoringActivity {
    private squareState[][] board = new squareState[3][3];
    private Random gen = new Random();
    private strategy naoStrategy = strategy.RANDOM;

    private Button[][] boardButtons = new Button[3][3];
    private TextView instructions;
    private Button returnButton;

    private enum squareState { EMPTY, X, O }
    private enum strategy { RANDOM }

    public String START_MSG =
        "Awesome! You will be exes, and I will be ohs. You can go first. Click any square on the " +
        "board.";
    public String WIN_MSG =
        "Looks like you won! Congrats! Click the button at the bottom of the tablet to return to " +
        "the tutoring session.";
    public String TIE_MSG =
        "Looks like it's a tie! Good game! Click the button at the bottom of the tablet to " +
        "return to the tutoring session.";
    public String LOSS_MSG =
        "Looks like I won this time, but it was super close! Click the button at the bottom of " +
        "the tablet to return to the tutoring session.";
    public String[] NAO_TURN_MSGS = {
        "Alright, my turn now.",
        "Nice move! Let me think.",
        "Okay, my turn."
    };
    public String[] STUDENT_TURN_MSGS = {
        "Your turn again.",
        "Done! Back to you.",
        "All set!"
    };

    public String SQUARE_OCCUPIED_TEXT =
        "Sorry!\nThis square already has something in it.\nTry picking another square.";
    public String CLICK_RETURN_BUTTON_TEXT =
        "Click the button below to return to the tutoring session.";

    // Constructor =================================================================================

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tictactoe);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = squareState.EMPTY;
            }
        }

        boardButtons[0][0] = (Button)   findViewById(R.id.boardButton0);
        boardButtons[0][1] = (Button)   findViewById(R.id.boardButton1);
        boardButtons[0][2] = (Button)   findViewById(R.id.boardButton2);
        boardButtons[1][0] = (Button)   findViewById(R.id.boardButton3);
        boardButtons[1][1] = (Button)   findViewById(R.id.boardButton4);
        boardButtons[1][2] = (Button)   findViewById(R.id.boardButton5);
        boardButtons[2][0] = (Button)   findViewById(R.id.boardButton6);
        boardButtons[2][1] = (Button)   findViewById(R.id.boardButton7);
        boardButtons[2][2] = (Button)   findViewById(R.id.boardButton8);
        instructions       = (TextView) findViewById(R.id.instructions);
        returnButton       = (Button)   findViewById(R.id.returnButton);

        // Transfer control of TCP client from MathActivity to this activity.
        if (TCPClient.singleton != null ) {
            TCPClient.singleton.setSessionOwner(this);
        }

        if (TCPClient.singleton != null) {
            TCPClient.singleton.sendMessage("TICTACTOE-START;-1;-1;" + START_MSG);
        }
    }

    // Button handlers =============================================================================

    public void boardButtonPressed(View view) {
        // Identify the button that was pressed.
        Button button = (Button)view;
        int buttonRow = 0;
        int buttonCol = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (button == boardButtons[i][j]) {
                    buttonRow = i;
                    buttonCol = j;
                    break;
                }
            }
        }

        // Check if the square is already occupied.
        if (board[buttonRow][buttonCol] != squareState.EMPTY) {
            instructions.setText(SQUARE_OCCUPIED_TEXT);

        // If not,
        } else {
            // Place an X in the square.
            board[buttonRow][buttonCol] = squareState.X;
            button.setTextColor(Color.RED);
            button.setText("X");

            // Send a message to nao_server.py based on the state of the game.
            //
            // nao_server.py will send a message back to us containing the type of the message that
            // we sent. Our messageReceived() method will process the returned message and direct
            // the course of the activity accordingly.
            if (won(squareState.X)) {
                if (TCPClient.singleton != null) {
                    TCPClient.singleton.sendMessage("TICTACTOE-WIN;-1;-1;" + WIN_MSG);
                }
            } else if (full()) {
                if (TCPClient.singleton != null) {
                    TCPClient.singleton.sendMessage("TICTACTOE-TIE;-1;-1;" + TIE_MSG);
                }
            } else {
                String naoTurnMsg = NAO_TURN_MSGS[gen.nextInt(NAO_TURN_MSGS.length)];
                if (TCPClient.singleton != null) {
                    TCPClient.singleton.sendMessage("TICTACTOE-NAOTURN;-1;-1;" + naoTurnMsg);
                }
            }
        }
    }

    public void returnButtonPressed(View view) {
        finish();
    }

    // Game-playing methods ========================================================================

    /**
     * This method simulates Nao's turn. It's the complement to boardButtonPressed().
     */
    public void naoTurn() {
        // Pick an available square for Nao.
        int row = 0;
        int col = 0;
        if (naoStrategy == strategy.RANDOM) {
            int[] options = new int[9];
            int numOptions = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == squareState.EMPTY) {
                        options[numOptions] = i * 3 + j;
                        numOptions++;
                    }
                }
            }
            int choice = options[gen.nextInt(numOptions)];
            row = choice / 3;
            col = choice % 3;
        }

        // Place an O in the square.
        board[row][col] = squareState.O;
        boardButtons[row][col].setTextColor(Color.GREEN);
        boardButtons[row][col].setText("O");

        // Send a message to nao_server.py based on the state of the game.
        //
        // nao_server.py will send a message back to us containing the type of the message that we
        // sent. Our messageReceived() method will process the returned message and direct the
        // course of the activity accordingly.
        if (won(squareState.O)) {
            if (TCPClient.singleton != null) {
                TCPClient.singleton.sendMessage("TICTACTOE-LOSS;-1;-1;" + LOSS_MSG);
            }
        } else if (full()) {
            if (TCPClient.singleton != null) {
                TCPClient.singleton.sendMessage("TICTACTOE-TIE;-1;-1;" + TIE_MSG);
            }
        } else {
            String studentTurnMsg = STUDENT_TURN_MSGS[gen.nextInt(STUDENT_TURN_MSGS.length)];
            if (TCPClient.singleton != null) {
                TCPClient.singleton.sendMessage("TICTACTOE-STUDENTTURN;-1;-1;" + studentTurnMsg);
            }
        }
    }

    /**
     * This helper method returns true if the specified player (Xs or Os) has won.
     */
    public boolean won(squareState player) {
        // Check rows.
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true;
            }
        }
        // Check columns.
        for (int j = 0; j < 3; j++) {
            if (board[0][j] == player && board[1][j] == player && board[2][j] == player) {
                return true;
            }
        }
        // Check diagonals.
        if (   (board[0][0] == player && board[1][1] == player && board[2][2] == player)
            || (board[0][2] == player && board[1][1] == player && board[2][0] == player)) {
            return true;
        }
        return false;
    }

    /**
     * This helper method returns true if the board is full.
     */
    public boolean full() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == squareState.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    // Incoming message handler ====================================================================

    /**
     * This method directs the course of the activity based on the messages that it receives from
     * nao_server.py.
     */
    public void messageReceived(String msg) {
        System.out.println("[ TicTacToeActivity ] Received the following message: " + msg);

        if (   msg.equals("TICTACTOE-START")
            || msg.equals("TICTACTOE-STUDENTTURN")) {
            enableBoardButtons();

        } else if (   msg.equals("TICTACTOE-WIN")
                   || msg.equals("TICTACTOE-TIE")
                   || msg.equals("TICTACTOE-LOSS")) {
            enableReturnButton();
            instructions.setText(CLICK_RETURN_BUTTON_TEXT);

        } else if (msg.equals("TICTACTOE-NAOTURN")) {
            naoTurn();
        }
    }

    // Button disabling and enabling methods =======================================================

    public void disableButtons() {
        disableBoardButtons();
        disableReturnButton();
    }

    public void disableBoardButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardButtons[i][j].setEnabled(false);
            }
        }
    }

    public void enableBoardButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardButtons[i][j].setEnabled(true);
            }
        }
    }

    public void disableReturnButton() {
        returnButton.setEnabled(false);
    }

    public void enableReturnButton() {
        returnButton.setEnabled(true);
    }
}