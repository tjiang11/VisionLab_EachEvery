package model;

import java.util.ArrayList;
import java.util.Random;
/**
 * @author Tony Jiang
 * 6-25-2015
 * 
 * Classes Related To:
 * (DotsGameController) > DotsPairGenerator > DotsPair > DotSet > Coordinate
 *                                          > Ratio
 *                                             
 * Generates 'DotsPair's with selected numbers of dots.
 * 
 * The quantity of dots in DotsPairs and consequently DotSets
 * are determined by selected a predefined ratio (Ratio)
 * and scaling that ratio until it is within a desirable range
 * (between MIN_DOTS and MAX_DOTS).
 * 
 * The predefined ratios are put into 'ratiosBucket'. For the next trial,
 * a ratio is choes at random from the ratiosBucket and removed. Once
 * the ratiosBucket is empty, the ratiosBucket is refilled with Ratios
 * based on the blockMode, which "block" the subject is currently in.
 * Whenever, a block is complete (based on number of rounds complete)
 * the ratiosBucket is immediately emptied.
 * 
 * A "DotsPair" in this assessment is the two sets of dots,
 * each of a different color, that is shown in the same space
 * in one trial.
 *
 */
public class DotsPairGenerator {
    
    /** Maximum number of total dots to be shown in one trial. */
    static final int MAX_DOTS = 20;
    /** Minimum number of total dots to be shown in one trial. */
    static final int MIN_DOTS = 10;
    
    /** Max number of times the same relative size (or control type) may be the correct choice. */
    static final int MAX_TIMES_SAME_SIZE_CORRECT = 3;
    
    /** Map from each block to an integer representation. */
    public static final int SOME_DOTS_BLOCK = 0;
    public static final int SOME_OF_THE_DOTS_BLOCK = 1;
    public static final int EACH_DOT_BLOCK = 2;
    public static final int EVERY_DOT_BLOCK = 3;    
    
    /** Random number generator. */
    Random randomGenerator = new Random();

    /** The most recent DotsPair produced by DotsPairGenerator. */
    private DotsPair dotsPair; 
    
    /** The current block setting */
    private int blockMode;
    
    /** List containing the blocks. */
    private ArrayList<Integer> blockSet;
    
    /** List containing Ratios for the current block. */
    private ArrayList<Ratio> ratiosBucket;
    
    /** A measure of how many times the same 'size' has been correct, meaning
     how many times has the cluster with bigger/smaller individual dots been correct.
     Depends on area control type --> 
         Equal Areas <--> Smaller Correct  
         Inverse Areas <--> Bigger Correct 
       See ControlType.java.*/
    private int sameSizeCorrect;
    
    /** True if the last correct choice was the cluster with bigger individual dots. */
    private boolean lastWasBig;
    
    /**
     * Constructor. 
     */
    public DotsPairGenerator() {
        this.setLastWasBig(false);
        this.blockSet = new ArrayList<Integer>();
        this.ratiosBucket = new ArrayList<Ratio>();
        this.fillBlockSet();
    }
    
    /**
     * Fill the block set in a random manner.
     */
    private void fillBlockSet() {
        ArrayList<Integer> tempSet = new ArrayList<Integer>();
        tempSet.add(SOME_DOTS_BLOCK);
        tempSet.add(SOME_OF_THE_DOTS_BLOCK);
        tempSet.add(EVERY_DOT_BLOCK);
        tempSet.add(EACH_DOT_BLOCK);
        int size = tempSet.size();
        for (int i = 0; i < size; i++) {
            this.blockSet.add((Integer) tempSet.remove(randomGenerator.nextInt(tempSet.size())));
            System.out.println(this.blockSet.toString());
        }
        this.blockMode = this.blockSet.get(0);
    }
    
    /** 
     * Get a new pair based on current mode. 
     */
    public void getNewModePair() {
        Ratio ratioCircles = this.decideRatio();
        Ratio ratioSquares = ratioCircles;
        this.getNewPair(ratioCircles, ratioSquares);
    }
    
    /**
     * Empty the ratio bucket. 
     */
    public void clearRatios() {
        this.ratiosBucket.clear();
    }
    
    /**
     * Pick a ratio at random from the ratiosBucket. If the ratiosBucket is empty, then refill it.
     * @return Ratio
     */
    private Ratio decideRatio() {
        if (this.ratiosBucket.isEmpty()) {
            fillRatiosBucket();
        }
        Ratio ratio = this.ratiosBucket.remove(randomGenerator.nextInt(this.ratiosBucket.size()));
        return ratio;
    }
    
    /**
     * Refill the ratiosBucket based on the current block.
     */
    private void fillRatiosBucket() {
        switch (this.blockMode) {
        case SOME_OF_THE_DOTS_BLOCK:
        case SOME_DOTS_BLOCK:
        case EVERY_DOT_BLOCK:
        case EACH_DOT_BLOCK:
            this.ratiosBucket.add(new Ratio(1,2));
            this.ratiosBucket.add(new Ratio(1,3));
            this.ratiosBucket.add(new Ratio(3,1));
            this.ratiosBucket.add(new Ratio(2,1));
            this.ratiosBucket.add(new Ratio(3,2));
            this.ratiosBucket.add(new Ratio(2,3));
            break;
        }
        System.out.println(this.ratiosBucket.toString());
    }
    
    /**
     * Get a new pair with a specified ratio of numbers of dots.
     * Scale the total number of dots to a range between MIN_DOTS and MAX_DOTS.
     * @param ratio
     */
    private void getNewPair(Ratio ratioCircles, Ratio ratioSquares) {
        int ratioCirclesNumOne = ratioCircles.getNumOne();
        int ratioCirclesNumTwo = ratioCircles.getNumTwo();
        int numCirclesOne = ratioCircles.getNumOne();
        int numCirclesTwo = ratioCircles.getNumTwo();
        while (numCirclesOne + numCirclesTwo < MIN_DOTS) {
            numCirclesOne += ratioCirclesNumOne;
            numCirclesTwo += ratioCirclesNumTwo;
        }
        int max = (MAX_DOTS - (numCirclesOne + numCirclesTwo)) / (ratioCirclesNumOne + ratioCirclesNumTwo);
        if (max <= 0) {
        	max = 1;
        }
        int randMax = randomGenerator.nextInt(max);
        for (int i = 0; i < randMax; i++) {
            numCirclesOne += ratioCirclesNumOne;
            numCirclesTwo += ratioCirclesNumTwo;
        }
        
        int ratioSquaresNumOne = ratioSquares.getNumOne();
        int ratioSquaresNumTwo = ratioSquares.getNumTwo();
        int numSquaresOne = ratioCircles.getNumOne();
        int numSquaresTwo = ratioCircles.getNumTwo();
        while (numCirclesOne + numCirclesTwo < MIN_DOTS) {
            numCirclesOne += ratioCirclesNumOne;
            numCirclesTwo += ratioCirclesNumTwo;
        }
        int maxT = (MAX_DOTS - (numCirclesOne + numCirclesTwo)) / (ratioCirclesNumOne + ratioCirclesNumTwo);
        if (max <= 0) {
        	max = 1;
        }
        int randMaxT = randomGenerator.nextInt(max);
        for (int i = 0; i < randMax; i++) {
            numSquaresOne += ratioSquaresNumOne;
            numSquaresTwo += ratioSquaresNumTwo;
        }
        
        this.checkAndSet(numCirclesOne, numCirclesTwo, numSquaresOne, numSquaresTwo);
    }
    
    /**
     * Set the dots pair.
     * @param dotSetOne number of dots in dot set one.
     * @param dotSetTwo number of dots in dot set two.
     */
    private void checkAndSet(int numCirclesOne, int numCirclesTwo, int numSquaresOne, int numSquaresTwo) {  
        ControlType controlTypeCandidate = generateAreaControlType(numCirclesOne, numCirclesTwo);
        this.setDotsPair(new DotsPair(numCirclesOne, numCirclesTwo,
        		numSquaresOne, numSquaresTwo, controlTypeCandidate));
    }
    
    /**
     * Perform checks.
     */
    private void performChecks(int dotSetOne, int dotSetTwo, ControlType controlTypeCandidate) {
        this.checkSameSize(controlTypeCandidate);
    }

    /** 
     * Generate the next ControlType.
     * @param dotSetOne
     * @param dotSetTwo
     * @return The next ControlType.
     */
    private ControlType generateAreaControlType(int dotSetOne, int dotSetTwo) {
        ControlType controlTypeCandidate = generateRandomAreaControlType();
        this.performChecks(dotSetOne, dotSetTwo, controlTypeCandidate);
        if (this.getSameSizeCorrect() >= MAX_TIMES_SAME_SIZE_CORRECT) {
            controlTypeCandidate = changeControlType(controlTypeCandidate);
        }
        return controlTypeCandidate;
    }
    
    /**
     * Generates a random control type. Either EQUAL_AREAS or INVERSE_AREAS.
     * @return random control type.
     */
    private ControlType generateRandomAreaControlType() {
        if (randomGenerator.nextBoolean()) {
            return ControlType.EQUAL_AREAS;
        } else {
            return ControlType.INVERSE_AREAS;
        }
    }
    
    /**
     * Swap the area of the controlTypeCandidate between EQUAL_AREAS and INVERSE_AREAS.
     * @param controlTypeCandidate
     * @return the opposite control type.
     */
    private ControlType changeControlType(ControlType controlTypeCandidate) {
        this.setSameSizeCorrect(0);
        this.toggleLastWasBig();
        if (controlTypeCandidate == ControlType.EQUAL_AREAS) {
            return ControlType.INVERSE_AREAS;
        } else if (controlTypeCandidate == ControlType.INVERSE_AREAS) {
            return ControlType.EQUAL_AREAS;
        }
        return ControlType.NONE;
    }
        
    /**
     * Check if the same relative size (or control type) is correct
     * as the last round. Inverse areas <--> Big correct. Equal areas <--> Small correct.
     * 
     * See ControlType.java
     * 
     * @param controlType the ControlType to be evaluated.
     */
    private void checkSameSize(ControlType controlType) {
        if (controlType == ControlType.INVERSE_AREAS) {
            if (this.lastWasBig) {
                this.incrementSameSizeCorrect();
            } else {
                this.setSameSizeCorrect(0);
            }
            this.lastWasBig = true;
        } else if (controlType == ControlType.EQUAL_AREAS) {
            if (!this.lastWasBig) {
                this.incrementSameSizeCorrect();
            } else {
                this.setSameSizeCorrect(0);
            }
            this.lastWasBig = false;
        }
    }
    
    /**
     * Toggles which of the last relative sizes was correct.
     */
    private void toggleLastWasBig() {
        if (this.lastWasBig) {
            this.lastWasBig = false;
        } else {
            this.lastWasBig = true;
        }   
    }
        
    /** 
     * Change to the next block. Clear the ratiosBucket.
     */
    public void changeBlock() {
        this.blockSet.remove(0);
        if (!this.blockSet.isEmpty()) {
            this.blockMode = this.blockSet.get(0);
        }
        this.ratiosBucket.clear();
    }

    public DotsPair getDotsPair() {
        return this.dotsPair;
    }

    public void setDotsPair(DotsPair dotsPair) {
        this.dotsPair = dotsPair;
    }
    
    public boolean isLastWasBig() {
        return lastWasBig;
    }

    public void setLastWasBig(boolean lastWasBig) {
        this.lastWasBig = lastWasBig;
    }

    public int getSameSizeCorrect() {
        return sameSizeCorrect;
    }

    public void setSameSizeCorrect(int sameSizeCorrect) {
        this.sameSizeCorrect = sameSizeCorrect;
    }
    
    public void incrementSameSizeCorrect() {
        this.sameSizeCorrect++;
    }

    public int getBlockMode() {
        return blockMode;
    }

    public void setBlockMode(int blockMode) {
        this.blockMode = blockMode;
    }
}
