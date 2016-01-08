package com.vmware.horizon.auditing.db;

import java.util.List;

public interface ToolBoxDB {
	 public boolean isToolboxTableAvaiable() ;

		/**
		 * Delete items with the keys. No matter how many values for the key.
		 * Example, removeKey(a,b); all children under /a/b will be removed, /a/b itself will be removed too.
		 * @param keys; must not be null; at most 4 keys can be used
		 * @throws StorageExeption if keys is null or more than 4 keys
		 */
		public void removeKey(String... keys) throws StorageException;

		/**
		 * Get values with keys. When no value is found, return an empty list (not null);
		 * Example, getItems(a,b) return /a/b and all its children
		 * @param keys must not be null, at most 4 keys
		 * @return a List of StorageItem;
		 * @throws StorageExeption if keys is null or more than 4 keys
		 */
		public List<StorageItem> getItems(String... keys) throws StorageException;


		/**
		 * Add items. Each StorageItem has an attribute string[] keys and a value.
		 * By default, keys are not unique, since one key may have multiple values.
		 * For example, you have key /a/b/c with value "x" in storage, and you call this function to add key /a/b/c with value "x" again,
		 * Then the key"/a/b/c" have two values, both are "x"
		 * This is allowed by Toolbox Storage.
		 * If you don't want to have redundant data, please call removeKey first and then call addItems
		 * @param a list of StorageItem.
		 * @throws StorageExeption if items is null.
		 */
		public void addItems(List<StorageItem> items) throws StorageException;


		/**
		 * Add a single item. Each StorageItem has an attribute string[] keys and a value.
		 * By default, keys are not unique, since one key may have multiple values.
		 * For example, you have key /a/b/c with value "x" in storage, and you call this function to add key /a/b/c with value "x" again,
		 * Then you will have two keys "/a/b/c" with the same value "x"
		 * This is allowed by Toolbox Storage.
		 * If you don't want to have redundant data, please call removeKey first and then call addItem
		 * @param item
		 */
		public void addItem(StorageItem item) throws StorageException;
}
