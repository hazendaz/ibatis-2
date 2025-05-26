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
package com.ibatis.sqlmap.engine.transaction;

/**
 * The Class TransactionException.
 */
public class TransactionException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new transaction exception.
   */
  public TransactionException() {
  }

  /**
   * Instantiates a new transaction exception.
   *
   * @param msg
   *          the msg
   */
  public TransactionException(String msg) {
    super(msg);
  }

  /**
   * Instantiates a new transaction exception.
   *
   * @param cause
   *          the cause
   */
  public TransactionException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new transaction exception.
   *
   * @param msg
   *          the msg
   * @param cause
   *          the cause
   */
  public TransactionException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
