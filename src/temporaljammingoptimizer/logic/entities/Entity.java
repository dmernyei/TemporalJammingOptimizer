package temporaljammingoptimizer.logic.entities;

import temporaljammingoptimizer.logic.geometry.Vector2;

/**
 * Created by Daniel Mernyei
 */
public abstract class Entity {

    private static int idGenerator = 0;

    protected Vector2 position;

    private int id;

    public Entity(Vector2 position){
        this.position = position;
        id = ++Entity.idGenerator;
    }

    public int getId(){
        return id;
    }

    public static void resetIdGenerator(){
        Entity.idGenerator = 0;
    }

    public Vector2 getPosition(){
        return position;
    }

    @Override
    public boolean equals(Object o){
        if (null == o || !(o instanceof Entity))
            return false;

        Entity e = (Entity)o;
        return e.getId() == id;
    }

    @Override
    public int hashCode(){
        return id;
    }

    @Override
    public String toString(){
        return position.toString();
    }
}
