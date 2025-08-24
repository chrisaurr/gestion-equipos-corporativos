package com.gestion.equipos.config;

import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.BasicBinder;
import org.hibernate.type.descriptor.jdbc.BasicExtractor;
import org.hibernate.type.descriptor.jdbc.JdbcType;

import java.sql.*;

public class PostgreSQLEnumJdbcType implements JdbcType {

    @Override
    public int getJdbcTypeCode() {
        return Types.OTHER;
    }

    @Override
    public <X> ValueBinder<X> getBinder(JavaType<X> javaType) {
        return new BasicBinder<X>(javaType, this) {
            @Override
            protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                if (value == null) {
                    st.setNull(index, Types.OTHER);
                } else {
                    st.setObject(index, ((Enum<?>) value).name(), Types.OTHER);
                }
            }

            @Override
            protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                if (value == null) {
                    st.setNull(name, Types.OTHER);
                } else {
                    st.setObject(name, ((Enum<?>) value).name(), Types.OTHER);
                }
            }
        };
    }

    @Override
    public <X> ValueExtractor<X> getExtractor(JavaType<X> javaType) {
        return new BasicExtractor<X>(javaType, this) {
            @Override
            protected X doExtract(ResultSet rs, int paramIndex, WrapperOptions options) throws SQLException {
                String value = rs.getString(paramIndex);
                if (value == null) {
                    return null;
                }
                return javaType.fromString(value);
            }

            @Override
            protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                String value = statement.getString(index);
                if (value == null) {
                    return null;
                }
                return javaType.fromString(value);
            }

            @Override
            protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                String value = statement.getString(name);
                if (value == null) {
                    return null;
                }
                return javaType.fromString(value);
            }
        };
    }
}