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
package com.ibatis.sqlmap.engine.type;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Types;

/**
 * The Class BlobTypeHandlerCallback.
 */
public class BlobTypeHandlerCallback implements TypeHandlerCallback {

  @Override
  public Object getResult(ResultGetter getter) throws SQLException {
    Blob blob = getter.getBlob();
    byte[] returnValue;
    if (!getter.wasNull()) {
      returnValue = blob.getBytes(1, (int) blob.length());
    } else {
      returnValue = null;
    }
    return returnValue;
  }

  @Override
  public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
    if (null != parameter) {
      byte[] bytes = (byte[]) parameter;
      ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
      setter.setBinaryStream(bis, bytes.length);
    } else {
      setter.setNull(Types.BLOB);
    }
  }

  @Override
  public Object valueOf(String s) {
    return s;
  }

}
