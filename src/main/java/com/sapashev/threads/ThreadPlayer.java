package com.sapashev.threads;

import com.sapashev.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

/**
 * Describes Thread which manipulating player on the board.
 * @author Arslan Sapashev
 * @since 15.12.2016
 * @version 1.0
 */
public class ThreadPlayer implements Runnable {
    private final Player player;
    private final Cell[][] board;
    private final Actor freecell;
    private final Logger LOG = LoggerFactory.getLogger(ThreadPlayer.class);

    public ThreadPlayer(Player player, Cell[][] board, Actor freecell){
        this.player = player;
        this.board = board;
        this.freecell = freecell;
    }

    @Override
    public void run () {
        LOG.info(String.format("ThreadPlayer started at %s", LocalTime.now().toString()));
        Cell target = new Cell(0,0, freecell);
        makeMove(target);
    }

    /**
     * Makes move to the target cell. If move done it swaps cells, set degree of player cell to zero and
     * updates degree of all cells on the board accordingly to proximity to the player cell.
     * @param target - cell to which player tries to go.
     * @return true - move done, false - move failed, due to target cell is not free.
     */
    boolean makeMove(Cell target){
        boolean isMoveDone = player.move(target, freecell, player);
        if(isMoveDone){
            swapCells(target);
            player.getHostCell().setDegree(0);
            updateBoardDegree();
            LOG.info(String.format("Player has been moved successfully to the cell r:%s c:%", target.getRow()),target.getColumn());
        }
        return isMoveDone;
    }

    /**
     * Swaps the new and previous cells of the player. Should be invoked if only move of player succeeds.
     * It should be done to allow another monsters and actors to make move to the released cell.
     * First it assigns to the previous cell, occupied by player, status free cell.
     * Second it assigns to the hostCell field of the player reference to the just occupied cell.
     * @param target - new cell to which player has been moved.
     */
    private void swapCells (Cell target) {
        player.getHostCell().actor.compareAndSet(player, freecell);
        player.setHostCell(target);
        LOG.info(String.format("Swap of player cells succeeded"));
    }

    /**
     * Calculates and updates degree value of each cell on the board, according to proximity to the player cell.
     */
    private void updateBoardDegree (){
        int row = board.length;
        int column = board[0].length;
        for(int x = 0; x < row; x++){
            for (int y = 0; y < column; y++){
                Cell cell = board[x][y];
                int degree = Math.max(
                        Math.abs(cell.getRow() - player.getHostCell().getRow()),
                        Math.abs(cell.getColumn() - player.getHostCell().getColumn())
                );
                cell.setDegree(degree);
            }
        }
    }
}
