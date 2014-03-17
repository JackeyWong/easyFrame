package com.easy4j.easydao.base;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.easy4j.easydao.annotation.Column;
import com.easy4j.easydao.annotation.ID;
import com.easy4j.easydao.annotation.Table;

/**
 * Dao常用操作支持类
 *</br> Copyright: Copyright (c) 2003 
 *</br> Company: Epin Solution(Beijing)Technology Co.,Ltd. 
 * @author <a href="mailto:wangjie2013@126.com">Jackwang<a>
 * @version 1.0 
 * @since 2014年3月6日  下午6:11:44
 */ 
public abstract class DAOSupport<T> implements DAO<T> {

	private final class RowMapperImpl implements RowMapper<T> {
		@Override
		public T mapRow(ResultSet rs, int rowNum) throws SQLException {
			T instance = getEntityInstance();
			resultSet2Entity(rs,instance);
			return instance;
		}
	}

	protected DataSource dataSource;
	protected JdbcTemplate jdbcTemplate;

	public DAOSupport(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	@Override
	public int insert(T entity) {
		Map<String, Object> pair = new HashMap<String, Object>();
		entity2Map(entity, pair);
		int size = (pair != null && pair.size() > 0) ? pair.size() : 0;
		Object[] args = null;
		if (size > 0) {
			args = new Object[size];
			StringBuffer sql = new StringBuffer();
			sql.append("INSERT");
			sql.append(" INTO ");
			sql.append(getTableName());
			sql.append('(');
			int i = 0;
			for (String colName : pair.keySet()) {
				sql.append((i > 0) ? "," : "");
				sql.append(colName);
				args[i++] = pair.get(colName);
			}
			sql.append(')');
			sql.append(" VALUES (");
			for (i = 0; i < size; i++) {
				sql.append((i > 0) ? ",?" : "?");
			}
			sql.append(')');
			return this.jdbcTemplate.update(sql.toString(), args);
		} else {
			return 0;
		}

	}

	private void entity2Map(T entity, Map<String, Object> pair) {
		Field[] fields = getEntityInstance().getClass().getDeclaredFields();
		try {
			for (Field field : fields) {
				Column column = field.getAnnotation(Column.class);
				ID id = field.getAnnotation(ID.class);
				if(column != null){
					if(id != null && id.Autoincrement()) continue;
					field.setAccessible(true);
					pair.put(column.value(), field.get(entity));
				};
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	protected String getTableName() {
		Table table = getEntityInstance().getClass().getAnnotation(Table.class);
		if(table != null){
			return table.value();
		}
		throw new RuntimeException("Can't found Annotation Table.");
	}

	@Override
	public int update(T entity, String whereClause, String[] whereArgs) {
		Map<String, Object> pair = new HashMap<String, Object>();
		entity2Map(entity, pair);
		int setValuesSize = (pair != null && pair.size() > 0) ? pair.size() + 1
				: 1;
		int bindArgsSize = (whereArgs == null) ? setValuesSize
				: (setValuesSize + whereArgs.length);
		Object[] args = null;
		if (setValuesSize > 1) {
			args = new Object[setValuesSize];
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE ");
			sql.append(getTableName());
			sql.append(" SET ");
			int i = 0;
			for (String colName : pair.keySet()) {
				sql.append(i > 0 ? "," : "");
				sql.append(colName);
				sql.append("=?");
				args[i++] = pair.get(colName);
			}
			sql.append(" WHERE ");
			if (whereArgs != null && whereClause != null
					&& !"".equals(whereClause.trim())) {
				sql.append(whereClause);
				for (i = setValuesSize; i < bindArgsSize; i++) {
					args[i] = whereArgs[i - setValuesSize];
				}
			} else {
				sql.append(getKeyColumn());
				sql.append("= ?");
				args[setValuesSize - 1] = getKeyValue(entity);
			}
			return jdbcTemplate.update(sql.toString(), args);
		} else {
			return 0;
		}
	}

	protected Serializable getKeyValue(T entity) {
		Field[] fields = getEntityInstance().getClass().getDeclaredFields();
		try {
			for (Field field : fields) {
				Column column = field.getAnnotation(Column.class);
				ID id = field.getAnnotation(ID.class);
				if (id != null && column != null) {
					field.setAccessible(true);
					return (Serializable) field.get(entity);
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Can't found Annotation ID from entity.");
	}

	protected String getKeyColumn() {
		Field[] fields = getEntityInstance().getClass().getDeclaredFields();
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			ID id = field.getAnnotation(ID.class);
			if (id != null && column != null) {
				return column.value();
			}
		}
		return "";
	}

	@Override
	public int delete(Serializable key) {
		String sql = "DELETE FROM " + getTableName() + " WHERE " + getKeyColumn() +"=?";
		return jdbcTemplate.update(sql,key);
	}
	
	@Override
	public int delete(String whereClause, String[] whereArgs) {
		String sql = "DELETE FROM " + getTableName() +
				(whereClause!=null && !whereClause.trim().equals("") ? " WHERE " + whereClause : "");
		return jdbcTemplate.update(sql, (Object[])whereArgs);
	}

	@Override
	public List<T> query(String orderBy, int offset , int pageSize){
		StringBuffer sql = new StringBuffer("SELECT * FROM "+getTableName());
		List<Object> params = new ArrayList<Object>();
		if(orderBy != null && !orderBy.trim().equals("")){
			sql.append(" ORDER BY ?");
			params.add(orderBy);
		}
		if(offset > -1 && pageSize > -1){
			sql.append(" LIMIT ?,?");
			params.add(offset);
			params.add(pageSize);
		}
		return this.jdbcTemplate.query(sql.toString(),params.toArray(), new RowMapperImpl());
	}

	@Override
	public List<T> query(String orderBy){
		return query(orderBy, -1, -1);
	}
	
	protected void resultSet2Entity(ResultSet rs, T instance) {
		Field[] fields = instance.getClass().getDeclaredFields();
		try {
			for (Field field : fields) {
				Column coloum = field.getAnnotation(Column.class);
				if(coloum != null){
					String columnLabel = coloum.value();
					Object value = rs.getObject(columnLabel);
					/*if (type == int.class || type == Integer.class) {
						value = rs.getInt(columnLabel);
					}else if(type == long.class || type == Long.class){
						value = rs.getLong(columnLabel);
					}else if(type == String.class){
						value = rs.getString(columnLabel);
					}else if(type == Date.class || type == java.sql.Date.class 
							|| type == Timestamp.class){
						value = rs.getTimestamp(columnLabel);
					}else {
						value = rs.getObject(columnLabel);
					}*/
					field.setAccessible(true);
					field.set(instance, value);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected T getEntityInstance() {
		Type superclass = getClass().getGenericSuperclass();
		try {
			if(superclass instanceof ParameterizedType){
				Type actualType = ((ParameterizedType)superclass).getActualTypeArguments()[0];
				return (T) ((Class)actualType).newInstance();
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		throw new RuntimeException(this.getClass().getSimpleName() + " can't found Generic Type.");
	}

	@Override
	public T get(Serializable key) {
		String sql = "SELECT * FROM "+ getTableName() +" WHERE "+getKeyColumn() +"=?";
		List<T> result = jdbcTemplate.query(sql,new Object[]{key}, new RowMapperImpl());
		return result != null && result.size() > 0 ? result.get(0) : null;
	}

	@Override
	public int getTotal() {
		String sql = "SELECT COUNT(*) FROM "+ getTableName();
		return jdbcTemplate.queryForInt(sql);
	}
}
