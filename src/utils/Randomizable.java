package utils;

import java.util.Random;

/**
 * A class that can take many values randomly
 * @author Corentin
 * @param <E>
 */
public class Randomizable<E> {
    
    private static final Random random = new Random();
    private final E[] elements;
    
    public Randomizable(E... elements) {
        this.elements = elements;
    }
    
    public E random() {
        return elements[random.nextInt(elements.length)];
    }
}
