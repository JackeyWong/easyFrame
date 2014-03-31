package com.easy4j.easydao.base;

import java.io.Serializable;
import java.util.List;

public interface DAO<T> {

	/**
	 * Insert a row.
	 * 
	 * @param t
	 * @return inserted autoincrement key if ID column's autoincrement is true, otherwise return effect column count.
	 */
	int insert(T entity);

	/**
	 * Update a row.
	 * 
	 * @param t
	 * @param whereClause
	 *            the optional WHERE clause to apply when updating. Passing null
	 *            will update all rows.
	 * @param whereArgs
	 *            You may include ?s in the where clause, which will be replaced
	 *            by the values from whereArgs. The values will be bound as
	 *            Strings.
	 * @return the number of rows affected
	 */
	int update(T entity, String whereClause, String[] whereArgs);

	/**
	 * delete a row.
	 * 
	 * @param key
	 * @return the number of rows affected
	 */
	int delete(Serializable key);

	/**
	 * @param whereClause
	 *            the optional WHERE clause to apply when deleting. Passing null
	 *            will delete all rows.
	 * @param whereArgs
	 *            You may include ?s in the where clause, which will be replaced
	 *            by the values from whereArgs. The values will be bound as
	 *            Strings.
	 * @return
	 */
	int delete(String whereClause, String[] whereArgs);

	/**
	 * query special row.
	 * 
	 * @return
	 */
	List<T> query(String orderBy, int offset , int pageSize);
	/**
	 * query all row.
	 * 
	 * @return
	 */
	List<T> query(String orderBy);

	/**
	 * Retrieve a row.
	 * 
	 * @param key
	 * @return
	 */
	T get(Serializable key);

	/**
	 * total row number.
	 * 
	 * @return
	 */
	int getTotal();
}
