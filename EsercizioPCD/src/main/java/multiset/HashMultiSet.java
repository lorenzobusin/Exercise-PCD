package multiset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * <p>A MultiSet models a data structure containing elements along with their frequency count i.e., </p>
 * <p>the number of times an element is present in the set.</p>
 * <p>HashMultiSet is a Map-based concrete implementation of the MultiSet concept.</p>
 *
 * <p>MultiSet a = <{1:2}, {2:2}, {3:4}, {10:1}></p>
 * */
public final class HashMultiSet<T, V> {

	private HashMap<T, V> multiset ;

	/**
	 *XXX: data structure backing this MultiSet implementation.
	 */

	/**
	 * Sole constructor of the class.
	 **/
	public HashMultiSet() {
	    multiset = new HashMap<>();
    }


	/**
	 * If not present, adds the element to the data structure, otherwise
	 * simply increments its frequency.
	 *
	 * @param t T: element to include in the multiset
	 *
	 * @return V: frequency count of the element in the multiset
	 * */
	public V addElement(T t) {
                if(!isPresent(t)){
                    Integer i = 1;
                    V added = multiset.put(t, (V)i);
                    return added;
                }   
                else{
                    Integer i = (Integer)multiset.get(t) + 1;
                    V added = multiset.replace(t, (V)i );
                    return added;
                }
	}

	/**
	 * Check whether the elements is present in the multiset.
	 *
	 * @param t T: element
	 *
	 * @return V: true if the element is present, false otherwise.
	 * */
	public boolean isPresent(T t) {
           return multiset.containsKey(t);
	}

	/**
	 * @param t T: element
	 * @return V: frequency count of parameter t ('0' if not present)
	 * */
	public V getElementFrequency(T t) {
            if(isPresent(t))
                return multiset.get(t);
            else
                return null;
	}


	/**
	 * Builds a multiset from a source data file. The source data file contains
	 * a number comma separated elements.
	 * Example_1: ab,ab,ba,ba,ac,ac -->  <{ab:2},{ba:2},{ac:2}>
	 * Example 2: 1,2,4,3,1,3,4,7 --> <{1:2},{2:1},{3:2},{4:2},{7:1}>
	 *
	 * @param source Path: source of the multiset
	 * */
	public void buildFromFile(Path source) {
            try {
                Files.lines(source).map(x -> x.split(",")).forEach(obj -> addElement((T) obj));
            } catch (IOException e) {
                throw new IllegalArgumentException("Method should be invoked with a non null file path");
            }
    }

	/**
	 * Same as before with the difference being the source type.
	 * @param source List<T>: source of the multiset
	 * */
	public void buildFromCollection(List<? extends T> source) {
            if(source == null) 
                throw new IllegalArgumentException("Method should be invoked with a non null file path");
            else
                source.stream().forEach(obj -> addElement((T)obj));
	}

	/**
	 * Produces a linearized, un ordered version of the MultiSet data structure.
	 * Example: <{1:2},{2:1}, {3:3}> -> 1 1 2 3 3 3 3
	 *
	 * @return List<T>: linearized version of the multiset represented by this object.
	 */
	public List<T> linearize() {
            List<T> linearized = new ArrayList<>();
            multiset.forEach((t,v)->{
                                        for(Integer i = 0; i < (Integer)v;i++)
                                            linearized.add(t);
            });
            
            return linearized;
	}


}
