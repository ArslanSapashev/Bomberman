package com.sapashev;

/**
 * Describes block (forbidden) element of the board.
 * @author Arslan Sapashev
 * @since 15.12.2016
 * @version 1.0
 */
public class Block implements Actor{
    private Cell hostCell;
    private Type type = Type.BLOCK;

    /**
     * Returns type of the actor.
     * @return
     */
    @Override
    public Type getType () {
        return this.type;
    }

    /**
     * Does nothing, due to immobility of blocks in this version.
     * @param target
     * @param oldActor
     * @param newActor
     * @return
     */
    @Override
    public boolean move (Cell target, Actor oldActor, Actor newActor) {
        return false;
    }

    public Cell getHostCell (){
        return this.hostCell;
    }

    public void setHostCell (Cell target){
        this.hostCell = target;
    }
}
