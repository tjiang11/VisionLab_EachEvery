package model;

import java.util.Random;
import java.util.ArrayList;

import model.Coordinate.Shape;
import view.SetUp;
import config.Config;

/** 
 * Object to represent a set of dots.
 * 
 * Classes related to:
 *  -Coordinate.java
 *      Contains an array of Coordinates to represent dot locations.
 * 
 * @author Tony Jiang
 * 6-25-2015
 * 
 */

public class DotSet {
    
    /** AVERAGE_RADIUS_CONTROL true if each dotset should have the same average radius. */
    /** If TOTAL_AREA_CONTROL is true in DotsPair, then the average radius control property will not hold. */
    static boolean AVERAGE_RADIUS_CONTROL;
    /** The average diameter for each dot set with average radius control on */
    static int AVERAGE_DIAMETER_ARC;
    /** The maximum variance in diameter for each dot set with average radius control on */
    static int MAX_DIAMETER_VARIANCE_ARC;

    /** Properties without radius control on;
     * each dot has a random diameter between MIN_DIAMETER and MAX_DIAMETER
     * independent of other dots. */
    static int MIN_DIAMETER;
    static int MAX_DIAMETER;
    
    /** Total number of circles and squares this dotSet will have */
    private int totalNumDots;
    /** Total number of circles this dotSet will have */
    private int totalNumCircles;
    /** Total number of squares this dotSet will have */
    private int totalNumSquares;
    
    /** Positions of every dot with respect to the canvas it is in */
    private ArrayList<Coordinate> positions;
    
    /** Respective diameters of the dots in the dotSet */
    private ArrayList<Double> diameters;
    
    /** The total area of the dotSet to be calculated after painting all dots.
     * Used for TOTAL_AREA_CONTROL. */
    private double totalArea;
    
    /** Random number generator */
    private Random randomGenerator = new Random();
    
    /** The minimum distance in pixels two dots can be from each other. */
    static final int MIN_DISTANCE_BETWEEN_DOTS = 3;
    
    /**
     * Constructor for DotSet with a specified number of total dots to contain. 
     * @param numDots total number of dots this dotSet will have.
     */
    public DotSet(int numDots) {
        
        loadConfig();
        
        this.setTotalNumDots(numDots);
        this.positions = new ArrayList<Coordinate>();
        this.diameters = new ArrayList<Double>(); 
        
        this.totalArea = 0;
        this.fillDots();
    }
    
    /**
     * Constructor for DotSet with a specified number of total dots to contain
     * and to not overlap with another DotSet.
     * @param numDots total number of dots this dotSet will have.
     * @param otherDotSet other DotSet to not overlap with.
     */
    public DotSet(int numDots, DotSet otherDotSet) {
        loadConfig();
        this.setTotalNumDots(numDots);
        this.positions = new ArrayList<Coordinate>();
        this.diameters = new ArrayList<Double>(); 
        this.totalArea = 0;
        this.fillDots(otherDotSet);
    }
    
    public DotSet(int numCircles, int numSquares) {
    	loadConfig();
    	this.totalNumCircles = numCircles;
    	this.totalNumSquares = numSquares;
    	this.totalNumDots = numSquares + numCircles;
    	this.positions = new ArrayList<Coordinate>();
        this.diameters = new ArrayList<Double>(); 
        this.totalArea = 0;
        this.fillDots();
    }
    
    public DotSet(int numCircles, int numSquares, DotSet otherDotSet) {
    	loadConfig();
    	this.totalNumCircles = numCircles;
    	this.totalNumSquares = numSquares;
    	this.totalNumDots = numSquares + numCircles;
    	this.positions = new ArrayList<Coordinate>();
        this.diameters = new ArrayList<Double>(); 
        this.totalArea = 0;
        this.fillDots(otherDotSet);
    }
    
    private void loadConfig() {
        new Config();
        AVERAGE_RADIUS_CONTROL = Config.getPropertyBoolean("average.radius.control");
        AVERAGE_DIAMETER_ARC = Config.getPropertyInt("average.diameter.arc");
        MAX_DIAMETER_VARIANCE_ARC = Config.getPropertyInt("max.diameter.variance.arc");
        
        MIN_DIAMETER = Config.getPropertyInt("min.diameter");
        MAX_DIAMETER = Config.getPropertyInt("max.diameter");
    }
    
    /**
     * Populate the dotSet with dots that have random diameters between a default MIN_DIAMETER, MAX_DIAMETER.
     */
    private void fillDots() {
        int i = 0;
        while (i < this.totalNumDots) {
            int x = randomGenerator.nextInt(SetUp.DOTS_CANVAS_WIDTH - MAX_DIAMETER);
            int y = randomGenerator.nextInt(SetUp.DOTS_CANVAS_HEIGHT - MAX_DIAMETER);
            int diameter = randomGenerator.nextInt(MAX_DIAMETER - MIN_DIAMETER) + MIN_DIAMETER; 
            
            if (!overLapsOther(x, y, diameter)) {
            	if (i < this.totalNumCircles) {
                    this.addDotAndDiameterAndArea(x, y, diameter, Shape.CIRCLE);
            	} else {
            		this.addDotAndDiameterAndArea(x, y, diameter, Shape.SQUARE);
            	}
                i++;
            }
        }
    }
    
    private void fillDots(DotSet otherDotSet) {
        int i = 0;
        while (i < this.totalNumDots) {
            int x = randomGenerator.nextInt(SetUp.DOTS_CANVAS_WIDTH - MAX_DIAMETER);
            int y = randomGenerator.nextInt(SetUp.DOTS_CANVAS_HEIGHT - MAX_DIAMETER);
            int diameter = randomGenerator.nextInt(MAX_DIAMETER - MIN_DIAMETER) + MIN_DIAMETER; 
            
            if (!overLapsOther(x, y, diameter) 
                    && !overLapsOtherInOtherDotSet(x, y, diameter, otherDotSet)) {
            		if (i < this.totalNumCircles) {
            			this.addDotAndDiameterAndArea(x, y, diameter, Shape.CIRCLE);
            		} else {
            			this.addDotAndDiameterAndArea(x, y, diameter, Shape.SQUARE);
            		}
                    i++;
            }
        }
	}

    /**
     * Checks if a dot overlaps another dot in the dotSet.
     * @param x X position of the dot to be checked.
     * @param y Y position of the dot to be checked.
     * @param diameter Diameter of the dot to be checked.
     * @return true if the dot overlaps another dot in the dotSet.
     */
    private boolean overLapsOther(int x, int y, double diameter) {
        double radius = diameter / 2.0;
        double centerX = x + radius;
        double centerY = y + radius;

        for (int i = 0; i < positions.size(); i++) {
            Coordinate otherPosition = positions.get(i);
            double otherDiameter = diameters.get(i);
            double otherRadius = otherDiameter/2;
            double otherCenterX = otherPosition.x + otherRadius;
            double otherCenterY = otherPosition.y + otherRadius;

            double dx = centerX - otherCenterX;
            double dy = centerY - otherCenterY;
            double distance = Math.hypot(dx, dy);
            if (distance < radius + otherRadius + MIN_DISTANCE_BETWEEN_DOTS) {
                return true;
            }
        }
        return false;
    }
    
    private boolean overLapsOtherInOtherDotSet(int x, int y, int diameter,
            DotSet otherDotSet) {
        double radius = diameter / 2.0;
        double centerX = x + radius;
        double centerY = y + radius;

        for (int i = 0; i < otherDotSet.positions.size(); i++) {
            Coordinate otherPosition = otherDotSet.positions.get(i);
            double otherDiameter = otherDotSet.diameters.get(i);
            double otherRadius = otherDiameter/2;
            double otherCenterX = otherPosition.x + otherRadius;
            double otherCenterY = otherPosition.y + otherRadius;

            double dx = centerX - otherCenterX;
            double dy = centerY - otherCenterY;
            double distance = Math.hypot(dx, dy);
            if (distance < radius + otherRadius + MIN_DISTANCE_BETWEEN_DOTS) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Add a dot the dotSet by storing its position and diameter.
     * Also, update the total area.
     * 
     * @param x X coordinate of the dot
     * @param y Y coordinate of the dot
     * @param diameter Diameter of the dot
     */
    public void addDotAndDiameterAndArea(int x, int y, double diameter, Shape shape) {
        this.positions.add(new Coordinate(x, y, shape));
        this.diameters.add(diameter);
        if (shape == Shape.CIRCLE) {
            this.totalArea += Math.PI * Math.pow((diameter / 2), 2);
        } else if (shape == Shape.SQUARE) {
        	this.totalArea += Math.pow(diameter, 2);
        }
    }
    
    /**
     * Match the area of this dotSet to the area of the other dotSet by scaling the area of this dotSet.
     * 
     * Given a dot set X with N dots, dot set Y with M dots, then we need to multiply each diameter in dotSet X
     * by the square root of the ratio of Total Area Y / Total Area X.
     * 
     * Note: Should always scale down to avoid overlapping of dots. (otherTotalArea < this.getTotalArea())
     * 
     * @param otherTotalArea The area of the other dotSet to be matched.
     */
    public void matchArea(double otherTotalArea) {
        
        double resizeRatio = Math.sqrt(otherTotalArea / this.getTotalArea());
        
        for (int diameterIndex = 0; diameterIndex < this.totalNumDots; diameterIndex++) {
            double diameterToScale = this.diameters.get(diameterIndex);
            double scaledDiameter = diameterToScale * resizeRatio;
            this.diameters.set(diameterIndex, scaledDiameter);
        }
        this.recalcArea();
    }
    
    /**
     * Takes the area of this dot set X (with less area than other dot set Y) and further scales it area down by the factor
     * that other dot set Y would have had to scale down to match this dot set X. Opposite of matchArea().
     * 
     * (otherTotalArea > this.getTotalArea())
     * 
     * @param otherTotalArea
     */
    public void inverseMatchArea(double otherTotalArea) {
        
        double inverseResizeRatio = Math.sqrt(this.getTotalArea() / otherTotalArea);
        
        for (int diameterIndex = 0; diameterIndex < this.totalNumDots; diameterIndex++) {
            double diameterToScale = this.diameters.get(diameterIndex);
            double scaledDiameter = diameterToScale * inverseResizeRatio;
            this.diameters.set(diameterIndex, scaledDiameter);
        }
        this.recalcArea();
    }

    /**
     * Recompute and set the total area of the dotset.
     */
    private void recalcArea() {
        this.totalArea = 0;
        for (Double diameter : this.diameters) {
            totalArea += Math.PI * Math.pow((diameter / 2), 2);
        }
    }
    
    public ArrayList<Coordinate> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<Coordinate> positions) {
        this.positions = positions;
    }
    
    public double getTotalArea() {
        return this.totalArea;
    }
    
    public int getTotalNumDots() {
        return this.totalNumDots;
    }

    public ArrayList<Double> getDiameters() {
        return diameters;
    }

    public void setDiameters(ArrayList<Double> diameters) {
        this.diameters = diameters;
    }

    public void setTotalNumDots(int totalNumDots) {
        this.totalNumDots = totalNumDots;
    }

	public int getTotalNumCircles() {
		return totalNumCircles;
	}

	public void setTotalNumCircles(int totalNumCircles) {
		this.totalNumCircles = totalNumCircles;
	}

	public int getTotalNumSquares() {
		return totalNumSquares;
	}

	public void setTotalNumSquares(int totalNumSquares) {
		this.totalNumSquares = totalNumSquares;
	}


}
