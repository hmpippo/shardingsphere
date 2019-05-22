/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.core.parse;

import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.core.constant.DatabaseType;
import org.apache.shardingsphere.core.metadata.table.ShardingTableMetaData;
import org.apache.shardingsphere.core.parse.cache.ParsingResultCache;
import org.apache.shardingsphere.core.parse.sql.statement.SQLStatement;
import org.apache.shardingsphere.core.rule.EncryptRule;

/**
 * SQL parse engine for encrypt.
 *
 * @author panjuan
 */
@RequiredArgsConstructor
public final class EncryptSQLParseEngine {
    
    private final DatabaseType dbType;
    
    private final EncryptRule encryptRule;
    
    private final ShardingTableMetaData shardingTableMetaData;
    
    private final ParsingResultCache parsingResultCache = new ParsingResultCache();
    
    /**
     * Parse SQL.
     *
     * @param sql SQL
     * @param useCache use cache or not
     * @return parsed SQL statement
     */
    public SQLStatement parse(final String sql, final boolean useCache) {
        Optional<SQLStatement> cachedSQLStatement = getSQLStatementFromCache(sql, useCache);
        if (cachedSQLStatement.isPresent()) {
            return cachedSQLStatement.get();
        }
        SQLStatement result = new SQLParseEngine(dbType, sql, encryptRule, shardingTableMetaData).parse();
        if (useCache) {
            parsingResultCache.put(sql, result);
        }
        return result;
    }
    
    private Optional<SQLStatement> getSQLStatementFromCache(final String sql, final boolean useCache) {
        return useCache ? Optional.fromNullable(parsingResultCache.getSQLStatement(sql)) : Optional.<SQLStatement>absent();
    }
}
