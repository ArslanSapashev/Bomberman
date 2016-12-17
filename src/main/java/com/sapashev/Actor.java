package com.sapashev;

/**
 * Describes actor object (any object that could be placed to the cell and could do any actions,
 * e.g. player, monster, block, bomb, etc. Block considered as actor too.)
 * @author Arslan Sapashev
 * @since 15.12.2016
 * @version 1.0
 */
public interface Actor {
    Type getType();
    boolean move(Cell target, Actor oldActor, Actor newActor);
}
