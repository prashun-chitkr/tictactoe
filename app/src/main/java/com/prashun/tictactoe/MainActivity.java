package com.prashun.tictactoe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button[][] board = new Button[3][3];
    private String[][] tempBoard = new String[3][3];
    private boolean gameFinished = false;
    private String player = "x";
    private String computer = "o";
    private int playerScore = 0;
    private int computerScore = 0;
    private TextView playerText;
    private TextView computerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerText = findViewById(R.id.playerScore);
        computerText = findViewById(R.id.computerScore);
        Button resetButton = findViewById(R.id.resetButton);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tempBoard[i][j] = "";
                board[i][j] = findViewById(
                        getResources()
                                .getIdentifier(
                                        "button_" + i + j,
                                        "id",
                                        getPackageName()
                                )
                );
                board[i][j].setOnClickListener(this);
            }
        }
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.resetBoard();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("board", tempBoard);
        outState.putInt("playerScore", playerScore);
        outState.putInt("computerScore", computerScore);
        outState.putBoolean("gameFinished", gameFinished);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tempBoard = (String[][]) savedInstanceState.getSerializable("board");
        playerScore = savedInstanceState.getInt("playerScore");
        computerScore = savedInstanceState.getInt("computerScore");
        gameFinished = savedInstanceState.getBoolean("gameFinished");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j].setText(tempBoard[i][j]);
            }
        }
        playerText.setText("Player: " + playerScore);
        computerText.setText("Computer: " + computerScore);
    }

    @Override
    public void onClick(View v) {
        int moveVal;
        int bestVal = Integer.MIN_VALUE;
        BestMove bestMove = new BestMove(-1, -1);

        if (!gameFinished) {
            if (!((Button) v).getText().toString().equals(""))
                return;

            ((Button) v).setText(player);
            getBoardStatus();
            if (checkWin(tempBoard)) {
                gameFinished = true;
                playerText.setText("Player: " + playerScore);
                Toast.makeText(this, "Player Won!", Toast.LENGTH_LONG).show();
                return;
            }

            if (isMovesLeft(tempBoard)) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (tempBoard[i][j].equals("")) {

                            tempBoard[i][j] = computer;

                            moveVal = minMax(tempBoard, false);

                            tempBoard[i][j] = "";

                            if (moveVal > bestVal) {
                                bestMove.setI(i);
                                bestMove.setJ(j);
                                bestVal = moveVal;
                            }
                        }
                    }
                }
                board[bestMove.getI()][bestMove.getJ()].setText(computer);
                getBoardStatus();
                if (checkWin(tempBoard)) {
                    gameFinished = true;
                    computerText.setText("Computer: " + computerScore);
                    Toast.makeText(this, "Computer Won!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean checkWin(String[][] board) {
        for (int row = 0; row < 3; row++) {
            if (board[row][0].equals(board[row][1]) && board[row][1].equals(board[row][2])) {
                if (board[row][0].equals(player)) {
                    playerScore += 1;
                    return true;
                } else if (board[row][0].equals(computer)) {
                    computerScore += 1;
                    return true;
                }
            }
        }
        for (int col = 0; col < 3; col++) {
            if (board[0][col].equals(board[1][col]) && board[1][col].equals(board[2][col])) {
                if (board[0][col].equals(player)) {
                    playerScore += 1;
                    return true;
                } else if (board[0][col].equals(computer)) {
                    computerScore += 1;
                    return true;
                }
            }
        }
        if (board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2])) {
            if (board[1][1].equals(player)) {
                playerScore += 1;
                return true;
            } else if (board[1][1].equals(computer)) {
                computerScore += 1;
                return true;
            }
        }
        if (board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0])) {
            if (board[1][1].equals(player)) {
                playerScore += 1;
                return true;
            } else if (board[1][1].equals(computer)) {
                computerScore += 1;
                return true;
            }
        }
        return false;
    }

    void getBoardStatus() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                tempBoard[i][j] = board[i][j].getText().toString();
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                board[i][j].setText("");
                tempBoard[i][j] = "";
            }
        gameFinished = false;
    }

    private boolean isMovesLeft(String[][] board) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j].equals("")) return true;
        return false;
    }

    private short evaluate(String[][] board) {
        for (int row = 0; row < 3; row++) {
            if (board[row][0].equals(board[row][1]) && board[row][1].equals(board[row][2])) {
                if (board[row][0].equals(player)) return -10;
                else if (board[row][0].equals(computer)) return 10;
            }
        }
        for (int col = 0; col < 3; col++) {
            if (board[0][col].equals(board[1][col]) && board[1][col].equals(board[2][col])) {
                if (board[0][col].equals(player)) return -10;
                else if (board[0][col].equals(computer)) return 10;
            }
        }
        if (board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2])) {
            if (board[1][1].equals(player)) return -10;
            else if (board[1][1].equals(computer)) return 10;
        }
        if (board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0])) {
            if (board[1][1].equals(player)) return -10;
            else if (board[1][1].equals(computer)) return 10;
        }
        return 0;
    }

    private int minMax(String[][] board, boolean isMax) {
        int best;
        int score = evaluate(board);

        if (score == 10 || score == -10)
            return score;
        if (!isMovesLeft(board))
            return 0;
        if (isMax) {
            best = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    if (board[i][j].equals("")) {
                        board[i][j] = computer;
                        best = Math.max(best, minMax(board, !isMax));
                        board[i][j] = "";
                    }
        } else {
            best = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    if (board[i][j].equals("")) {
                        board[i][j] = player;
                        best = Math.min(best, minMax(board, !isMax));
                        board[i][j] = "";
                    }
        }
        return best;
    }
}
