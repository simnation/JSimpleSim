/**
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.scheduling;

/**
 * This class provides basic cache functionality.
 *
 * @param T type of items to be cached
 *
 * @author Rene Kuhlemann
 *
 */
final class ItemCache<T> {

	private final Object cache[]; // array for caching items

	private int index=0; // cache index

	/**
	 * Class constructor taking the size of the cache as parameter
	 *
	 * @param chacheSize size of internal element cache, set to 0 to switch of
	 *                   caching.
	 */
	ItemCache(int chacheSize) {
		cache=new Object[chacheSize];
	}

	ItemCache() {
		this(0);
	}

	/**
	 * Takes unused item for reuse by {@link createEntry}. If cache size is zero or
	 * cache is already full, the entry is disposed.
	 *
	 * @param idle item to be cached
	 */
	void recycle(T item) {
		if (index<cache.length) cache[index++]=item;
	}

	/**
	 * Provides a new item for reuse.
	 *
	 * @return recycled item or null if cache is empty
	 */
	@SuppressWarnings("unchecked")
	T reuse() {
		if (index<=0) return null;
		return (T) cache[--index];
	}

}
