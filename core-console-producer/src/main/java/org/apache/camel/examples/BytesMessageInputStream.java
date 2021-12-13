/*
 * Copyright 2020 Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.examples;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import javax.jms.BytesMessage;
import javax.jms.JMSException;

public class BytesMessageInputStream extends InputStream {

  private final BytesMessage message;
  
  public BytesMessageInputStream(BytesMessage message) {
    this.message = Objects.requireNonNull(message, "The msg parameter must not be null.");
  }

  public BytesMessage getMessage() {
    return message;
  }

  @Override
  public int read() throws IOException {
    try {
      return message.readByte();
    } catch (JMSException e) {
      throw new IOException(e);
    }
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    if (off != 0) {
      throw new IllegalArgumentException("The off parameter must be 0.");
    }
    try {
      return message.readBytes(b, len);
    } catch (JMSException e) {
      throw new IOException(e);
    }
  }
}
