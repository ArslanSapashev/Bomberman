package com.sapashev;

/**
 * Describes free cell on the board.
 * @author Arslan Sapashev
 * @since 15.12.2016
 * @version 1.0
 */
public class Freecell implements Actor {
    /**
     * Returns type of the actor
     * @return
     */
    @Override
    public Type getType () {
        return Type.FREECELL;
    }

    /**
     * Does nothing, due to the immobility of free cells.
     * @param target
     * @param oldActor
     * @param newActor
     * @return
     */
    @Override
    public boolean move (Cell target, Actor oldActor, Actor newActor) {
        return false;
    }

    @Override
    public boolean equals (Object obj) {
        if(obj == this){
            return true;
        }
        if (!(obj instanceof Freecell)){
            return false;
        }
        return this.getType() == ((Freecell)obj).getType();
    }

    @Override
    public int hashCode () {
        return 1000;
    }
}
