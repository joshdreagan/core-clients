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
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Component
public class CamelRouteConfiguration extends RouteBuilder {

  @Bean
  @Autowired
  private ComponentCustomizer<JmsComponent> jmsComponentCustomizer(CamelContext camelContext, ConnectionFactory jmsConnectionFactory) {
    return (component) -> {
      /*
      ActiveMQConnectionFactory artemisJmsConnectionFactory = null;
      if (jmsConnectionFactory instanceof CachingConnectionFactory) {
        artemisJmsConnectionFactory = (ActiveMQConnectionFactory) ((CachingConnectionFactory) jmsConnectionFactory).getTargetConnectionFactory();
      } else if (jmsConnectionFactory instanceof JmsPoolConnectionFactory) {
        artemisJmsConnectionFactory = (ActiveMQConnectionFactory) ((JmsPoolConnectionFactory) jmsConnectionFactory).getConnectionFactory();
      } else if (jmsConnectionFactory instanceof ActiveMQConnectionFactory) {
        artemisJmsConnectionFactory = (ActiveMQConnectionFactory) jmsConnectionFactory;
      }
      if (artemisJmsConnectionFactory != null) {
        artemisJmsConnectionFactory.setCompressLargeMessage(true);
        artemisJmsConnectionFactory.setMinLargeMessageSize(10240);
      }
      */

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

    from("file:{{file.directory:target/input/}}?delete=true")
      .log(LoggingLevel.DEBUG, log, "Sending file: ${headers.CamelFileName}")
      .to("direct:enqueue")
    ;

    from("stream:in?promptMessage=RAW(> )&initialPromptDelay=0")
      .filter(simple("${body} == ${null} || ${body} == ''"))
        .stop()
      .end()
      .log(LoggingLevel.DEBUG, log, "Sending message: ${body}")
      .to("direct:enqueue")
    ;

    from("direct:enqueue")
      .filter(simple("${properties:jms.compression-enabled:false}"))
        .log(LoggingLevel.DEBUG, log, "Compressing message")
        .marshal().gzip()
      .end()
      .choice()
        .when(simple("${properties:jms.delivery-mode:persistent} =~ 'non-persistent'"))
          .log(LoggingLevel.DEBUG, log, "Setting JMSDeliveryMode=NON_PERSISTENT...")
          .setHeader("JMSDeliveryMode", constant(1))
        .when(simple("${properties:jms.delivery-mode:persistent} =~ 'persistent'"))
          .log(LoggingLevel.DEBUG, log, "Setting JMSDeliveryMode=PERSISTENT...")
          .setHeader("JMSDeliveryMode", constant(2))
      .end()
      .to(ExchangePattern.InOnly, "jms:{{jms.destination.type:queue}}://{{jms.destination.name}}?preserveMessageQos=true")
    ;
  }
}
