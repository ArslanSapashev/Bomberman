package com.sapashev;


/**
 * Describes actor controlled by player.
 * @author Arslan Sapashev
 * @since 15.12.2016
 * @version 1.0
 */
public class Player implements Actor {
    private final Type type = Type.PLAYER;
    private Cell hostCell;

    /**
     * Returns type of that object
     * @return - object Type (e.g. Type.PLAYER)
     */
    public Type getType () {
        return this.type;
    }

    /**
     * Moves player to the target hostCell, if it's actor has Type.FREECELL;
     * @param target - hostCell to which player should move;
     * @param oldActor - to move on that hostCell oldType should be Type.FREECELL;
     * @param newActor - new actor should be Type.PLAYER;
     * @return - true - player moved to target hostCell, false - target hostCell is not free;
     */
    public boolean move (Cell target, Actor oldActor, Actor newActor) {
        return target.actor.compareAndSet(oldActor, newActor);
    }

    /**
     * Returns hostCell of this player.
     * @return - hostCell which occupied by the player.
     */
    public Cell getHostCell(){
        return this.hostCell;
    }

    /**
     * Sets target hostCell to the player, if move operation succeeds.
     * @param target - target hostCell to which player has been moved.
     */
    public void setHostCell(Cell target){
        this.hostCell = target;
    }
}
