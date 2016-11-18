package com.test.server.hibernate;

import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.pagination.*;
import org.hibernate.engine.spi.RowSelection;

import java.util.Locale;

/**
 * Custom dialect which provides a MSSQL 2000-proof top limit handler.
 */
public class MSSQL2000Dialect extends SQLServerDialect {


    private LimitHandler LIMIT_HANDLER;

    @Override
    public LimitHandler getLimitHandler() {

        if(LIMIT_HANDLER == null){
            LIMIT_HANDLER = new MSSQL2000TopLimitHandler(false, false);
        }
        return LIMIT_HANDLER;
    }


    /**
     * Top Limit Handler for MSSQL 2000. Avoids offset error
     */
    private class MSSQL2000TopLimitHandler extends TopLimitHandler{

        public MSSQL2000TopLimitHandler(boolean supportsVariableLimit, boolean bindLimitParametersFirst) {
            super(supportsVariableLimit, bindLimitParametersFirst);
        }

        @Override
        public String processSql(String sql, RowSelection selection) {

            //int top = getMaxOrLimit(selection);
            final int selectIndex = sql.toLowerCase(Locale.ROOT).indexOf( "select" );
            final int selectDistinctIndex = sql.toLowerCase(Locale.ROOT).indexOf( "select distinct" );
            final int insertionPoint = selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);

            return new StringBuilder( sql.length() + 8 )
                    .append( sql )
                    .insert( insertionPoint, " TOP ? " )
                    .toString();
        }
    }





}
