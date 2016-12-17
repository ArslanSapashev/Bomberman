package com.sapashev;


import java.util.concurrent.atomic.AtomicReference;

/**
 * Describes each cell of the game board.
 * cell - cell on a playground
 * degree - describes proximity to the cell Type.PLAYER.
 * @author Arslan Sapashev
 * @since 15.12.2016
 * @version 1.0
 */
public class Cell {
    public AtomicReference<Actor> actor;
    private final int row;
    private final int column;
    private int degree;

    public Cell(int row, int column, Actor freecell){
        this.actor = new AtomicReference<>(freecell);
        this.row = row;
        this.column = column;
        this.degree = 0;
    }

    /**
     * Returns row of the cell.
     * @return - row of the cell.
     */
    public int getRow(){
        return this.row;
    }

    /**
     * Returns column of the cell.
     * @return - column of the cell.
     */
    public int getColumn(){
        return this.column;
    }

    /**
     * Returns degree of the cell.
     * @return - degree equal or greater than zero.
     */
    public int getDegree(){
        return this.degree;
    }

    /**
     * Sets degree of the cell. Degree should be equal or greater than zero.
     * @param degree - degree to assign to the cell.
     * @return - true - new value assigned, false - assignment failed.
     */
    public boolean setDegree(int degree){
        boolean isDone = false;
        if(degree >= 0){
            this.degree = degree;
            isDone = true;
        }
        return isDone;
    }

    @Override
    public String toString () {
        return "The cell row: " + row + "column: " + column + "Type: " + actor.get().getType();
    }
}
