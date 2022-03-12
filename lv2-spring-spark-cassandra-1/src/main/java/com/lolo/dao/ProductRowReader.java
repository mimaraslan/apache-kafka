package com.lolo.dao;

import java.io.Serializable;

import com.datastax.driver.core.Row;
import com.datastax.spark.connector.CassandraRowMetadata;
import com.datastax.spark.connector.ColumnRef;
import com.datastax.spark.connector.cql.TableDef;
import com.datastax.spark.connector.rdd.reader.RowReader;
import com.datastax.spark.connector.rdd.reader.RowReaderFactory;
import com.lolo.model.Product;

import scala.collection.IndexedSeq;

public class ProductRowReader extends GenericRowReader<Product> {
    private static final long serialVersionUID = 1L;
    private static RowReader<Product> reader = new ProductRowReader();

    public static class ProductRowReaderFactory implements RowReaderFactory<Product>, Serializable{
        private static final long serialVersionUID = 1L;

        @Override
        public RowReader<Product> rowReader(TableDef arg0, IndexedSeq<ColumnRef> arg1) {
            return reader;
        }

        @Override
        public Class<Product> targetClass() {
            return Product.class;
        }
    }

//    @Override
//    public Product read(Row row, String[] columnNames) {
//        Product product = new Product();        
//        product.setId(row.getInt(0));
//        product.setName(row.getString(1));
//        product.setParents(row.getList(2, Integer.class));
//        return product;
//    }

	@Override
	public Product read(Row row, CassandraRowMetadata rowMetaData) {
		 Product product = new Product();        
	        product.setId(row.getInt(0));
	        product.setName(row.getString(1));
	        product.setParents(row.getList(2, Integer.class));
	        return product;
	}
}