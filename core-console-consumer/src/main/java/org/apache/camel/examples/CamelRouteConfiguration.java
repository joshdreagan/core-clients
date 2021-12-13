/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.examples;

import java.io.InputStream;
import javax.jms.BytesMessage;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.apache.camel.CamelContext;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Component
public class CamelRouteConfiguration extends RouteBuilder {

  private static final Logger log = LoggerFactory.getLogger(CamelRouteConfiguration.class);

  @Bean
  @Autowired
  private ComponentCustomizer<JmsComponent> jmsComponentCustomizer(CamelContext camelContext, ConnectionFactory jmsConnectionFactory) {
    return (component) -> {
      component.setConnectionFactory(jmsConnectionFactory);
      /*
      component.setMessageConverter(new MessageConverter() {
        @Override
        public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
          Message message = null;
          Object convertedObject = camelContext.getTypeConverter().tryConvertTo(InputStream.class, object);
          if (convertedObject != null) {
            message = session.createBytesMessage();
            message.setObjectProperty("JMS_AMQ_InputStream", convertedObject);
          } else {
            throw new UnsupportedOperationException("Not yet supported.");
          }
          return message;
        }

        @Override
        public Object fromMessage(Message message) throws JMSException, MessageConversionException {
          Object object = null;
          if (message instanceof BytesMessage) {
            object = new BytesMessageInputStream((BytesMessage) message);
          } else {
            throw new UnsupportedOperationException("Not yet supported.");
          }
          return object;
        }
      });
      */
    };
  }

  @Override
  public void configure() {

    from("jms:{{jms.destination.type:queue}}://{{jms.destination.name}}?disableReplyTo=true")
      .to("stream:out")
      //.to("file:target/output/")
    ;
  }
}
