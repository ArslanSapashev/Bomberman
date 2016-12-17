package com.sapashev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;


/**
 * Describes monster.
 * type - type of that actor. For monster should be Type.MONSTER.
 * stuckTime - time until which monster has been stuck (should repeat attempts to acquire target cell).
 * hostCell - hostCell which occupied by this monster.
 * failedCell - cell to which monster tried to move, but failed due to it wasn't free.
 * @author Arslan Sapashev
 * @since 15.12.2016
 * @version 1.0
 */
public class Monster implements Actor{
    private final Type type = Type.MONSTER;
    private final Logger LOG = LoggerFactory.getLogger(Monster.class);
    private LocalTime stuckTime = LocalTime.MIN;
    private Cell hostCell;
    private Cell failedCell;

    /**
     * Returns type of that object
     * @return - object Type (e.g. Type.MONSTER)
     */
    public Type getType () {
        return this.type;
    }

    /**
     * Moves monster to the target hostCell, if it's actor has Type.FREECELL;
     * @param target - hostCell to which player should move;
     * @param oldActor - to move on that hostCell oldActor should be Type.FREECELL;
     * @param newActor - new actor Type.MONSTER;
     * @return - true - monster moved to target hostCell, false - target hostCell is not free;
     */
    public boolean move (Cell target, Actor oldActor, Actor newActor) {
        return target.actor.compareAndSet(oldActor, newActor);
    }


    /**
     * Returns hostCell of this monster.
     * @return - hostCell which occupied by the monster.
     */
    public Cell getHostCell (){
        return this.hostCell;
    }

    /**
     * Sets target hostCell to the monster, if move operation succeeds.
     * @param target - target hostCell to which monster has been moved.
     */
    public void setHostCell (Cell target){
        this.hostCell = target;
    }

    /**
     * Returns time when monster has been stuck, trying to move forward.
     * @return - stuck time.
     */
    public LocalTime getStuckTime(){
        return this.stuckTime;
    }

    /**
     * Sets time when monster has been stuck.
     * @param stuckTime - stuck time
     */
    public void setStuckTime(LocalTime stuckTime){
        this.stuckTime = stuckTime;
    }

    /**
     * Returns cell to which monster has not been moved.
     * @return - cell to which monster has not been moved by last attempt.
     */
    public Cell getFailedCell(){
        return this.failedCell;
    }

    /**
     * Sets cell to which attempt to move monster failed, because it was occupied by another monster.
     * @param failed - target cell which was occupied at time of move.
     */
    public void setFailedCell(Cell failed){
        this.failedCell = failed;
    }

}
