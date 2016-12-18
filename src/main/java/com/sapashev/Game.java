package com.sapashev;

import com.sapashev.threads.ThreadMonster;
import com.sapashev.threads.ThreadPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Describes main thread.
 * @author Arslan Sapashev
 * @since 15.12.2016
 * @version 1.0
 */
public class Game {
    public static void main (String[] args) {
        new Game().start(args);

    }

    private void start (String[] args) {
        int rows = Integer.parseInt(args[0]);
        int columns = Integer.parseInt(args[1]);
        int monstersCount = Integer.parseInt(args[2]);
        int repeatPeriod = Integer.parseInt(args[3]);
        int blocks = Integer.parseInt(args[4]);
        Actor freecell = new Freecell();
        final Logger LOG = LoggerFactory.getLogger(Game.class);

        //Checks the passed arguments
        checkArguments(rows,columns,monstersCount, blocks);
        //Creates game board
        Cell[][] board = createBoard(rows, columns, freecell);
        //Creates and puts blocks on the board
        putBlocks(createBlocks(blocks),board,freecell);
        //Creates and puts monsters on the board
        List<Monster> monsters = createMonsters(monstersCount);
        putMonsters(monsters,board,freecell);
        //Creates and puts player on the board
        Player player = new Player();
        putPlayer(player, board, freecell);

        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
        ScheduledFuture<?> futurePlayer = service.scheduleAtFixedRate(new ThreadPlayer(player, board, freecell), 1, 100, TimeUnit.MILLISECONDS);
        ScheduledFuture<?> futureMonster = service.scheduleAtFixedRate(new ThreadMonster(board, monsters,repeatPeriod, freecell),1,100, TimeUnit.MILLISECONDS);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            LOG.error(String.format("InterruptedException occurred at %s", LocalTime.now()),e);
        }
        futurePlayer.cancel(false);
        futureMonster.cancel(false);
        service.shutdownNow();
    }

    /**
     * Generates list of new monsters.
     * @param quantity - number of monsters on the board
     */
    private List<Monster> createMonsters (int quantity) {
        return Stream.generate(Monster::new).limit(quantity).collect(Collectors.toList());
    }

    /**
     * Fills up board with cells
     * @param rows - number of rows on the board.
     * @param columns - number of columns on the board.
     * @param freecell - object that will be assigned to each cell by default.
     * @return - filled up board.
     */
    private Cell[][] createBoard (int rows, int columns, Actor freecell) {
        Cell[][] board = new Cell[rows][columns];
        for (int x = 0; x < rows; x++){
            for (int y = 0; y < columns; y++){
                board[x][y] = new Cell(x,y, freecell);
                board[x][y].actor.compareAndSet(null, freecell);
            }
        }
        return board;
    }

    /**
     * Creates list of blocks generated by stream.
     * @param quantity - length of list.
     * @return - list of blocks.
     */
    private List<Block> createBlocks(int quantity){
        return Stream.generate(Block::new).limit(quantity).collect(Collectors.toList());
    }

    /**
     * Puts player to the random free cell
     * @param board - board with cells
     * @param freecell - reference to the freecell object
     */
    private void putPlayer (Player player, Cell[][] board, Actor freecell){
        player.setHostCell(getRandomCell(getFreeCells(board)));
        player.getHostCell().actor.compareAndSet(freecell, player);
    }

    /**
     * Puts blocks (forbidden cells) on the board.
     * @param blocks - list of blocks
     * @param board - board with cells
     */
    private void putBlocks(List<Block> blocks, Cell[][] board, Actor freecell){
        for (Block b : blocks){
            b.setHostCell(getRandomCell(getFreeCells(board)));
            b.getHostCell().actor.compareAndSet(freecell, b);
        }
    }

    /**
     * Puts monsters on the board
     * @param monsters - list of monsters
     * @param board - board
     * @param freecell - freecell
     */
    private void putMonsters(List<Monster> monsters, Cell[][] board, Actor freecell){
        for(Monster m : monsters){
            m.setHostCell(getRandomCell(getFreeCells(board)));
            m.getHostCell().actor.compareAndSet(freecell, m);
        }
    }

    /**
     * Returns list of free cells on the board
     * @param board - board with cells
     * @return - list of free cells
     */
    private List<Cell> getFreeCells(Cell[][] board){
        return Arrays.stream(board).flatMap(cells -> Arrays.stream(cells)).
                filter(cell -> cell.actor.get().getType() == Type.FREECELL).collect(Collectors.toList());
    }

    /**
     * Selects random cell from the list of free cells.
     * @param cells - list of free cells
     * @return - random free cell
     */
    private Cell getRandomCell(List<Cell> cells){
        return cells.get(new Random().nextInt(cells.size()));
    }

    /**
     * Checks the passed arguments for consistency.
     * @param rows - quantity of  rows
     * @param columns - quantity of columns
     * @param monsters - quantity of monsters
     * @param blocks - quantity of blocks
     */
    private void checkArguments(int rows, int columns, int monsters, int blocks){
        checkRowColumnsArguments(rows, columns);
        checkMonstersArguments(rows, columns, monsters, blocks);
        checkBlocksArgument(rows, columns, blocks);
    }

    /**
     * Throws IllegalArgumentException if quantity of blocks is less than zero or
     * quantity of blocks is greater than 1/15 of total cells on board.
     * @param rows - quantity of rows on board
     * @param columns - quantity of  columns on board
     * @param blocks - quantity of blocks on board
     */
    private void checkBlocksArgument (int rows, int columns, int blocks) {
        if(blocks >= ((rows * columns)/15) || blocks <= 0){
            throw new IllegalArgumentException("Illegal blocks quantity");
        }
    }

    /**
     * Throws IllegalArgumentException if quantity of monsters is less than zero or
     * greater than quantity of cells minus sum of blocks and player cells.
     * @param rows - rows on the board
     * @param columns - columns on the board
     * @param monsters - quantity of monsters
     * @param blocks - quantity of blocks
     */
    private void checkMonstersArguments (int rows, int columns, int monsters, int blocks) {
        if(monsters <= 0 || monsters >= ((rows * columns)-1-blocks)){
            throw new IllegalArgumentException("Illegal monsters quantity");
        }
    }

    /**
     * Throws IllegalArgumentException if quantity of rows or columns less than or equal to one.
     * @param rows - quantity of rows on board
     * @param columns - quantity of columns on board
     */
    private void checkRowColumnsArguments (int rows, int columns) {
        if(rows <= 1 || columns <= 1){
            throw new IllegalArgumentException("Wrong quantity of rows/columns on board");
        }
    }
}
