import java.util.ArrayList;
import java.util.Random;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;

public class BoidUniverse implements Universe {

	//adapted from https://github.com/k5md/Boids (download 2022-10-01)
    KDTree kd;     //kd-tree structure is used to find bird's neighbours fast
    BirdSprite[] birds; 
    int N;         //number of boids to process
    int xRes;      //maximum x-coordinate of field
    int yRes;      //maximum y-coordinate of field
    
    protected static int vision = 10;
    protected static double cohesion = 100;
    protected static int alignment = 8;
    protected static double separation = 10;
	
	private boolean complete = false;	
	private DisplayableSprite player1 = null;
	private ArrayList<DisplayableSprite> sprites = new ArrayList<DisplayableSprite>();
	private ArrayList<Background> backgrounds = new ArrayList<Background>();

    /**
     * Initialize the array of bird-like objects with random coordinates within certain area,
     * determined by width and height, so each bird will be asigned position vector (from 0 to width, from 0 to height)
     * and zero velocity vector.
     *
     * @param  amount                 Number of boids to create. 
     *         width                  Maximum value of x-coordinate of position.
     *         height                 Maximum value of y-coordinate of position.
     */
    public BoidUniverse(int amount, int width, int height)
    {
		this.setXCenter(0);
		this.setYCenter(0);

		N = amount;
        xRes = width;
        yRes = height;
        kd =  new KDTree(2);
        birds = new BirdSprite[N];
        Random rand = new Random();
        
        for (int i = 0; i < N - 1; i++)   
        {
            birds[i] = new BirdSprite(new Vector(rand.nextInt(xRes),rand.nextInt(yRes)), new Vector(0,0)); 
            try{
            kd.insert(birds[i].position.data, birds[i]);
            sprites.add(birds[i]);            
            } catch (Exception e) {
                System.out.println("Init Exception caught: " + e);   
                e.printStackTrace();
            }
        }
        
        this.player1 = birds[0];
    }  
		
	public double getScale() {
		return 0.5;
	}

	public double getXCenter() {
		return 750;
	}

	public double getYCenter() {
		return 500;
	}

	public void setXCenter(double xCenter) {
	}

	public void setYCenter(double yCenter) {
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		complete = true;
	}

	public ArrayList<Background> getBackgrounds() {
		return backgrounds;
	}	

	public DisplayableSprite getPlayer1() {
		return player1;
	}

	public ArrayList<DisplayableSprite> getSprites() {
		return sprites;
	}

	public boolean centerOnPlayer() {
		return false;
	}		

	public void update(KeyboardInput keyboard, long actual_delta_time) {

		if (keyboard.keyDownOnce(27)) {
			complete = true;
		}
		move(vision, cohesion, alignment, separation);
		
	}

    /**
     * Updates each boid's position and velocity depending on it's neighbours.
     *
     * @param  distance               Number of neighbours, which positions and velocities are used to calculate 
     *                                corresponding vectors of cohesion, alignment, separation of a bird.
     *         cohesionCoefficient    Value affects speed at which bird moves towards the perceived centre of mass
     *                                e.g 100 means that in each iteration bird moves 1% to the perceived centre 
     *         alignmentCoefficient   Value affects velocity increase of bird with respect to the perceived centre 
     *                                of mass 
     *         separationCoefficient  If bird is within this distance from other birds, it will move away
     * @return No return value.
     */
    public void move(int distance, double cohesionCoefficient, int alignmentCoefficient, double separationCoefficient) 
    {
        try{
            for (int i = 0; i < N - 1; i++)  
            {
                double[] coords = birds[i].position.data;
                BirdSprite[] nbrs = new BirdSprite[distance];
                kd.nearest(coords, distance).toArray(nbrs); 
                try {
                    kd.delete(coords);
                } catch (Exception e) {
                    // we ignore this exception on purpose
                    System.out.println("KeyMissingException deleting caught: " + e + e.getMessage());
                }
                birds[i].updateVelocity(nbrs, xRes, yRes, cohesionCoefficient, alignmentCoefficient, separationCoefficient);
                birds[i].updatePosition();
                kd.insert(birds[i].position.data, birds[i]);
            }      
       
            //the implementation of deletion in KdTree does not actually delete nodes, 
            //but only marks them, that affects performance, so it's necessary to rebuild the tree
            //after long sequences of insertions and deletions
            kd = new KDTree(2);
            for (int i = 0; i < N - 1; i++)  
                kd.insert(birds[i].position.data, birds[i]);
        } catch (KeySizeException | KeyDuplicateException e) {
            System.out.println("KeySizeException/KeyDuplicateException caught: " + e + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unknown exception caught: ");   
            e.printStackTrace();
        } 
    }
	
	public String toString() {
		return "ShellUniverse";
	}

}
