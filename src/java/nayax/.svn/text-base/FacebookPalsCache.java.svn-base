/**
 * 
 */
package nayax;

import java.util.LinkedHashMap;

/**
 * @author Srinivas
 *
 */
public class FacebookPalsCache extends LinkedHashMap {

	private final int capacity;

	public FacebookPalsCache(int capacity) {
		super(capacity + 1, 1.1f, true);
		this.capacity = capacity;
	}

	protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
		return size() > capacity;
	}
}

