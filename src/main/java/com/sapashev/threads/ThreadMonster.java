package com.sapashev.threads;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sapashev.*;

/**
 * Describes Thread which manipulating monsters on the board.
 * @author Arslan Sapashev
 * @since 15.12.2016
 * @version 1.0
 */
public class ThreadMonster implements Runnable{
    private final Cell[][] board;
    private final List<Monster> monsters;
    private Actor freecell;
    private int repeatPeriod = 5;
    private final Logger LOG = LoggerFactory.getLogger(ThreadMonster.class);


    public ThreadMonster (Cell[][] board, List<Monster> monsters, int repeatPeriod, Actor freecell){
        this.board = board;
        this.monsters = monsters;
        if(repeatPeriod >= 0){
            this.repeatPeriod = repeatPeriod;
        }
        this.freecell = freecell;
    }

    @Override
    public void run () {
        try {
            monsters.forEach(m -> makeMove(m));
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
    }


    /**
     * Tries to make next move, chasing the player.
     * Each cell has degree field which describes proximity to the player current location.
     * Each monster has a stuckTime field - if monster hasn't been stuck, it's value = LocalTime.MIN,
     * otherwise this field indicates time when monster tried to move, but failed + repeatPeriod.
     * If current time is after (greater than) stuckTime, this mean that monster can move to the new cell
     * otherwise it repeat attempts to get previously failed cell.
     * @param monster - monster that should make move.
     */
    void makeMove(Monster monster){
        if(LocalTime.now().isAfter(monster.getStuckTime())){          //If repeat period exceeded, choose next cell.
            makeNewMove(monster);
        } else {
            repeatFailedMove(monster);                                //If repeat period wasn't exceeded, try it again.
        }
    }

    /**
     * Tries to move monster to the new cell. If succeeded swaps the cells, otherwise sets monster's stuckTime and
     * cell to which it has tried to move as failed cell.
     * @param monster - monster to move
     */
    private void makeNewMove (Monster monster) {
        Cell target = chooseNextMove(monster);
        if(monster.move(target,freecell,monster)){
            LOG.info(String.format("Monster %s moved successfully to the cell r:%s c:%s at %s",
                    monster,target.getRow(),target.getColumn(), LocalTime.now().toString()));
            swapCells(monster, target);
        } else{
            monster.setStuckTime(LocalTime.now().plusSeconds(repeatPeriod));
            monster.setFailedCell(target);
            LOG.info(String.format("Monster %s stuck trying to move to cell r:%s c:%s at %s",
                    monster, target.getRow(), target.getColumn(), LocalTime.now().toString()));
        }
    }

    /**
     * Swaps the new and previous cells of the monster. Should be invoked if only move of monster succeeds.
     * It should be done to allow another monsters and actors to make move to the released cell.
     * First it assigns to the previous cell, occupied by monster, status free cell.
     * Second it assigns to the hostCell field of the monster reference to the just occupied cell.
     * @param monster - monster, that done move.
     * @param target - new cell to which monster has been moved.
     */
    private void swapCells (Monster monster, Cell target) {
        monster.getHostCell().actor.compareAndSet(monster,freecell);
        monster.setHostCell(target);
    }

    /**
     * Conducts another attempt to move to the same cell.
     * If move succeeds it swaps the cells, sets stuck time to 00:00 and erases failed cell reference.
     * If move wasn't succeed does nothing.
     * @param monster - monster who is failed to get the cell at the last time.
     */
    private void repeatFailedMove (Monster monster) {
        if(monster.move(monster.getFailedCell(),freecell,monster)){
            swapCells(monster,monster.getFailedCell());
            monster.setFailedCell(null);
            monster.setStuckTime(LocalTime.MIN);
            LOG.info(String.format("Monster %s acquired cell after previous fail. Cell r:%s c:%s at %s",
                    monster, monster.getHostCell().getRow(), monster.getHostCell().getColumn(), LocalTime.now().toString()));
        }
    }

    /**
     * Selects cell with minimum degree among nearby cells.
     * In case of two or more cells in the list have the same degree,
     * first occurrence with minimum degree will be chosen.
     * @param monster - monster which should decide where to go next.
     * @return - cell with minimum degree.
     */
    Cell chooseNextMove (Monster monster){
        List<Cell> cells = removeBlocks(getNearbyCells(monster));
        return cells.stream().min(Comparator.comparing(Cell::getDegree)).get();
    }

    /**
     * Removes cells which have Type.BLOCK from the list .
     * Because of monster can't move to the Type.BLOCK cell.
     * @param cells - list of free cells
     * @return - list of cells without blocks
     */
    List<Cell> removeBlocks(List<Cell> cells){
        cells.removeIf(cell -> cell.actor.get().getType()==Type.BLOCK);
        return cells;
    }

    /**
     * Creates list filled up with cells nearby to the argument.
     * Cells above, below, to the right and to the left from monster.
     * Among those cells after filtration, next cell will be chosen.
     * @param monster - monster which nearby cells will be listed.
     * @return - list of nearby cells.
     */
    List<Cell> getNearbyCells(Monster monster){
        int row = monster.getHostCell().getRow();
        int column = monster.getHostCell().getColumn();

        List<Cell> cells = new ArrayList<>(4);
        if(column - 1 >= 0){
            cells.add(board[row][column - 1]);
        }
        if(column + 1 < board[0].length) {
            cells.add(board[row][column + 1]);
        }
        if(row - 1 >= 0) {
            cells.add(board[row - 1][column]);
        }
        if(row + 1 < board.length){
            cells.add(board[row + 1][column]);
        }
        return cells;
    }
}
