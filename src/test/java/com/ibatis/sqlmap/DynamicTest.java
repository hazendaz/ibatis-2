/*
 * Copyright 2004-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibatis.sqlmap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import testdomain.Account;

class DynamicTest extends BaseSqlMap {

  @BeforeEach
  void setUp() throws Exception {
    initSqlMap("com/ibatis/sqlmap/maps/SqlMapConfig.xml", null);
    initScript("scripts/account-init.sql");
  }

  // PARAMETER PRESENT

  @Test
  void testIsParameterPresentTrue() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsParameterPresent", Integer.valueOf(1));
    assertAccount1((Account) list.get(0));
    assertEquals(1, list.size());
  }

  @Test
  void testIsParameterPresentFalse() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsParameterPresent", null);
    assertEquals(5, list.size());
  }

  // EMPTY

  @Test
  void testIsNotEmptyTrue() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsNotEmpty", "Clinton");
    assertAccount1((Account) list.get(0));
    assertEquals(1, list.size());
  }

  @Test
  void testIsNotEmptyFalse() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsNotEmpty", "");
    assertEquals(5, list.size());
  }

  // EQUAL

  @Test
  void testIsEqualTrue() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsEqual", "Clinton");
    assertAccount1((Account) list.get(0));
    assertEquals(1, list.size());
  }

  @Test
  void testIsEqualFalse() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsEqual", "BLAH!");
    assertEquals(5, list.size());
  }

  // GREATER

  @Test
  void testIsGreaterTrue() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsGreater", Integer.valueOf(5));
    assertAccount1((Account) list.get(0));
    assertEquals(1, list.size());
  }

  @Test
  void testIsGreaterFalse() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsGreater", Integer.valueOf(1));
    assertEquals(5, list.size());
  }

  // GREATER EQUAL

  @Test
  void testIsGreaterEqualTrue() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsGreaterEqual", Integer.valueOf(3));
    assertAccount1((Account) list.get(0));
    assertEquals(1, list.size());
  }

  @Test
  void testIsGreaterEqualFalse() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsGreaterEqual", Integer.valueOf(1));
    assertEquals(5, list.size());
  }

  // LESS

  @Test
  void testIsLessTrue() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsLess", Integer.valueOf(1));
    assertAccount1((Account) list.get(0));
    assertEquals(1, list.size());
  }

  @Test
  void testIsLessFalse() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsLess", Integer.valueOf(5));
    assertEquals(5, list.size());
  }

  // LESS EQUAL

  @Test
  void testIsLessEqualTrue() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsLessEqual", Integer.valueOf(3));
    assertAccount1((Account) list.get(0));
    assertEquals(1, list.size());
  }

  @Test
  void testIsLessEqualFalse() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsLessEqual", Integer.valueOf(5));
    assertEquals(5, list.size());
  }

  // NULL

  @Test
  void testIsNotNullTrue() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsNotNull", "");
    assertAccount1((Account) list.get(0));
    assertEquals(1, list.size());
  }

  @Test
  void testIsNotNullFalse() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsNotNull", null);
    assertEquals(5, list.size());
  }

  // PROPERTY AVAILABLE

  @Test
  void testIsPropertyAvailableTrue() throws SQLException {
    List<?> list = sqlMap.queryForList("dynamicIsPropertyAvailable", "1");
    assertAccount1((Account) list.get(0));
    assertEquals(1, list.size());
  }

  @Test
  void testEmptyParameterObject() throws SQLException {
    Account account = new Account();
    account.setId(-1);
    List<?> list = sqlMap.queryForList("dynamicQueryByExample", account);
    assertAccount1((Account) list.get(0));
    assertEquals(5, list.size());
  }

  @Test
  void testComplexDynamicQuery() throws SQLException {
    Account account = new Account();
    account.setId(2);
    account.setFirstName("Jim");
    account.setLastName("Smith");
    account.setEmailAddress("jim.smith@somewhere.com");
    List<?> list = sqlMap.queryForList("complexDynamicQueryByExample", account);
    assertAccount2((Account) list.get(0));
    assertEquals(1, list.size());
  }

  @Test
  void testComplexDynamicQueryLiteral() throws SQLException {
    Account account = new Account();
    account.setId(2);
    account.setFirstName("Jim");
    account.setLastName("Smith");
    account.setEmailAddress("jim.smith@somewhere.com");
    List<?> list = sqlMap.queryForList("complexDynamicQueryByExampleLiteral", account);
    assertAccount2((Account) list.get(0));
    assertEquals(1, list.size());
  }

  // COMPLETE STATEMENT SUBSTITUTION

  // -- No longer supported. Conflicted with and deemed less valuable than
  // -- iterative $substitutions[]$.
  //
  // @Test
  // void testCompleteStatementSubst() throws SQLException {
  // String statement = "select" +
  // " ACC_ID as id," +
  // " ACC_FIRST_NAME as firstName," +
  // " ACC_LAST_NAME as lastName," +
  // " ACC_EMAIL as emailAddress" +
  // " from ACCOUNT" +
  // " WHERE ACC_ID = #id#";
  // Integer id = Integer.valueOf(1);
  //
  // Map params = new HashMap<>();
  // params.put("id", id);
  // params.put("statement", statement);
  //
  // List list = sqlMap.queryForList("dynamicSubst", params);
  // assertAccount1((Account) list.get(0));
  // assertEquals(1, list.size());
  // }

  // Query By Example w/Prepend

  @Test
  void testQueryByExample() throws SQLException {
    Account account = new Account();

    account.setId(1);
    account = (Account) sqlMap.queryForObject("dynamicQueryByExample", account);
    assertAccount1(account);

    account = new Account();
    account.setFirstName("Clinton");
    account = (Account) sqlMap.queryForObject("dynamicQueryByExample", account);
    assertAccount1(account);

    account = new Account();
    account.setLastName("Begin");
    account = (Account) sqlMap.queryForObject("dynamicQueryByExample", account);
    assertAccount1(account);

    account = new Account();
    account.setEmailAddress("clinton");
    account = (Account) sqlMap.queryForObject("dynamicQueryByExample", account);
    assertAccount1(account);

    account = new Account();
    account.setId(1);
    account.setFirstName("Clinton");
    account.setLastName("Begin");
    account.setEmailAddress("clinton");
    account = (Account) sqlMap.queryForObject("dynamicQueryByExample", account);
    assertAccount1(account);

  }

  @Test
  void testRemappableResults() throws SQLException {
    Account account = new Account();

    account.setId(1);
    account = (Account) sqlMap.queryForObject("testRemappableResults", Integer.valueOf(1));

    assertAccount1(account);

    account = new Account();
    account.setId(5);
    account = (Account) sqlMap.queryForObject("testRemappableResults", Integer.valueOf(77));

    assertEquals(0, account.getId());
    assertEquals("Jim", account.getFirstName());
  }

  @Test
  void testIsPropertyAvailable() throws Exception {
    Map<String, Comparable> account = new HashMap<>();

    account.put("id", Integer.valueOf(1));
    account.put("name", "Clinton");
    account = (Map<String, Comparable>) sqlMap.queryForObject("selectIfPropertyAvailable", account);

    assertEquals(Integer.valueOf(1), account.get("ACC_ID"));
    assertEquals("Clinton", account.get("ACC_FIRST_NAME"));
  }
}
